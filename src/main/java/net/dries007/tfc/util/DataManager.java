/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.datafixers.util.Either;
import com.mojang.logging.LogUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.network.NetworkEvent;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import net.dries007.tfc.network.DataManagerSyncPacket;

/**
 * An implementation of a typical json reload manager.
 */
public class DataManager<T> extends SimpleJsonResourceReloadListener
{
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final Gson GSON = new Gson();

    private static final Map<Class<?>, DataManager<?>> NETWORK_TYPES = new HashMap<>();

    private static <T> void assertUniquePacketTypes(DataManager<?> instance, @Nullable Supplier<? extends DataManagerSyncPacket<T>> networkPacketFactory)
    {
        if (Helpers.ASSERTIONS_ENABLED && networkPacketFactory != null)
        {
            final Class<?> packetType = networkPacketFactory.get().getClass();
            final DataManager<?> old = NETWORK_TYPES.put(packetType, instance);
            if (old != null)
            {
                throw new IllegalStateException("Packet class " + packetType.getSimpleName() + " registered for managers for " + old.typeName + " and " + instance.typeName);
            }
        }
    }

    protected final BiMap<ResourceLocation, T> types;
    protected final String typeName;

    @Nullable protected final BiFunction<ResourceLocation, FriendlyByteBuf, T> networkFactory;
    @Nullable protected final BiConsumer<T, FriendlyByteBuf> networkEncoder;
    @Nullable protected final Supplier<? extends DataManagerSyncPacket<T>> networkPacketFactory;

    private final BiFunction<ResourceLocation, JsonObject, T> factory;
    private final Map<ResourceLocation, Reference<T>> references;
    private final Object referencesLock = new Object();

    public DataManager(ResourceLocation domain, String typeName, BiFunction<ResourceLocation, JsonObject, T> factory)
    {
        this(domain, typeName, factory, null, null, null);
    }

    public DataManager(ResourceLocation domain, String typeName, BiFunction<ResourceLocation, JsonObject, T> factory, @Nullable BiFunction<ResourceLocation, FriendlyByteBuf, T> networkFactory, @Nullable BiConsumer<T, FriendlyByteBuf> networkEncoder, @Nullable Supplier<? extends DataManagerSyncPacket<T>> networkPacketFactory)
    {
        super(GSON, domain.getNamespace() + "/" + domain.getPath());

        assertUniquePacketTypes(this, networkPacketFactory);

        this.factory = factory;
        this.references = new HashMap<>();
        this.networkFactory = networkFactory;
        this.networkEncoder = networkEncoder;
        this.networkPacketFactory = networkPacketFactory;

        this.types = HashBiMap.create();
        this.typeName = typeName;
    }

    /**
     * @return An element of this data manager, by id. Returns {@code null} if the element does not exist.
     */
    @Nullable
    public T get(ResourceLocation id)
    {
        return types.get(id);
    }

    /**
     * @return An element of this data manager, by id. Throws an exception if the element does not exist.
     */
    public T getOrThrow(ResourceLocation id)
    {
        final T t = types.get(id);
        if (t == null)
        {
            throw new IllegalArgumentException("No " + typeName + " with id " + id);
        }
        return t;
    }

    /**
     * Returns a reference to an element of this data manager, by id.
     * <p>
     * This can be used to reference an element before the data itself is loaded. Once the data is loaded, this will throw an error if it was not provided, and can be safely unboxed after the fact.
     * <p>
     * This method can be called concurrently from i.e. recipe loading.
     */
    public Reference<T> getReference(ResourceLocation id)
    {
        final Reference<T> ref;
        synchronized(referencesLock)
        {
            ref = references.computeIfAbsent(id, key -> new Reference<>(key, types.get(key)));
        }
        return ref;
    }

    public Set<T> getValues()
    {
        return types.values();
    }

    public DataManagerSyncPacket<T> createSyncPacket()
    {
        return createEmptyPacket().with(types);
    }

    public DataManagerSyncPacket<T> createEmptyPacket()
    {
        assert networkPacketFactory != null;
        return networkPacketFactory.get();
    }

    public T read(ResourceLocation id, JsonObject obj)
    {
        return factory.apply(id, obj);
    }

    public void rawToNetwork(FriendlyByteBuf buffer, T element)
    {
        assert networkEncoder != null;
        networkEncoder.accept(element, buffer);
    }

    public T rawFromNetwork(ResourceLocation id, FriendlyByteBuf buffer)
    {
        assert networkFactory != null;
        return networkFactory.apply(id, buffer);
    }

    public void onSync(NetworkEvent.Context context, Map<ResourceLocation, T> elements)
    {
        if (context.getNetworkManager().isMemoryConnection())
        {
            LOGGER.info("Ignored {}(s) sync from logical server", typeName);
        }
        else
        {
            // Sync received from physical server
            types.clear();
            types.putAll(elements);
            updateReferences();
            LOGGER.info("Received {} {}(s) from physical server", types.size(), typeName);
        }
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> elements, ResourceManager resourceManagerIn, ProfilerFiller profilerIn)
    {
        types.clear();
        for (Map.Entry<ResourceLocation, JsonElement> entry : elements.entrySet())
        {
            final ResourceLocation name = entry.getKey();
            final JsonObject json = GsonHelper.convertToJsonObject(entry.getValue(), typeName);
            try
            {
                if (CraftingHelper.processConditions(json, "conditions", ICondition.IContext.EMPTY))
                {
                    T object = read(name, json);
                    types.put(name, object);
                }
                else
                {
                    LOGGER.debug("Skipping loading {} '{}' as it's conditions were not met", typeName, name);
                }
            }
            catch (IllegalArgumentException | JsonParseException e)
            {
                LOGGER.error("{} '{}' failed to parse. {}: {}", typeName, name, e.getClass().getSimpleName(), e.getMessage());
                SelfTests.reportExternalError();
            }
        }

        updateReferences();

        LOGGER.info("Loaded {} {}(s).", types.size(), typeName);
    }

    private void updateReferences()
    {
        synchronized (referencesLock)
        {
            final List<ResourceLocation> unboundReferences = new ArrayList<>();
            for (Map.Entry<ResourceLocation, Reference<T>> entry : references.entrySet())
            {
                final T value = get(entry.getKey());
                if (value == null)
                {
                    unboundReferences.add(entry.getKey());
                }

                // Always update the reference
                entry.getValue().value = Optional.ofNullable(value);
            }

            if (!unboundReferences.isEmpty())
            {
                LOGGER.error("There were {} '{}' that were used but not defined: {}", unboundReferences.size(), typeName, unboundReferences);
                SelfTests.reportExternalError();
            }
        }
    }

    public static class Reference<T> implements Supplier<T>
    {
        private final ResourceLocation id;
        private Optional<T> value;

        Reference(ResourceLocation id, @Nullable T value)
        {
            this.id = id;
            this.value = Optional.ofNullable(value);
        }

        public ResourceLocation id()
        {
            return id;
        }

        @Override
        public T get()
        {
            return value.orElseThrow(() -> new IllegalStateException("Referencing value before loaded"));
        }
    }
}