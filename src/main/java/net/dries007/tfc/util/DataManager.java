/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util;

import java.util.HashMap;
import java.util.Map;
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
import com.mojang.logging.LogUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.util.Lazy;
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

    public DataManager(ResourceLocation domain, String typeName, BiFunction<ResourceLocation, JsonObject, T> factory)
    {
        this(domain, typeName, factory, null, null, null);
    }

    public DataManager(ResourceLocation domain, String typeName, BiFunction<ResourceLocation, JsonObject, T> factory, @Nullable BiFunction<ResourceLocation, FriendlyByteBuf, T> networkFactory, @Nullable BiConsumer<T, FriendlyByteBuf> networkEncoder, @Nullable Supplier<? extends DataManagerSyncPacket<T>> networkPacketFactory)
    {
        super(GSON, domain.getNamespace() + "/" + domain.getPath());

        assertUniquePacketTypes(this, networkPacketFactory);

        this.factory = factory;
        this.networkFactory = networkFactory;
        this.networkEncoder = networkEncoder;
        this.networkPacketFactory = networkPacketFactory;

        this.types = HashBiMap.create();
        this.typeName = typeName;
    }

    @Nullable
    public T get(ResourceLocation id)
    {
        return types.get(id);
    }

    public Supplier<T> getLazyOrThrow(ResourceLocation id)
    {
        return Lazy.of(() -> getOrThrow(id));
    }

    public T getOrThrow(ResourceLocation id)
    {
        final T t = types.get(id);
        if (t == null)
        {
            throw new IllegalArgumentException("No " + typeName + " with id " + id);
        }
        return t;
    }

    public ResourceLocation getIdOrThrow(T type)
    {
        final ResourceLocation id = getId(type);
        if (id == null)
        {
            throw new IllegalArgumentException("No id for " + typeName + ": " + type);
        }
        return id;
    }

    @Nullable
    public ResourceLocation getId(T type)
    {
        return types.inverse().get(type);
    }

    public Set<T> getValues()
    {
        return types.values();
    }

    public void toNetwork(T element, FriendlyByteBuf buffer)
    {
        buffer.writeResourceLocation(getIdOrThrow(element));
    }

    public Supplier<T> fromNetwork(FriendlyByteBuf buffer)
    {
        final ResourceLocation id = buffer.readResourceLocation();
        return Suppliers.memoize(() -> getOrThrow(id));
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
            LOGGER.info("Received {} {}(s) from physical server", types.size(), typeName);
        }
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> elements, ResourceManager resourceManagerIn, ProfilerFiller profilerIn)
    {
        types.clear();
        for (Map.Entry<ResourceLocation, JsonElement> entry : elements.entrySet())
        {
            ResourceLocation name = entry.getKey();
            JsonObject json = GsonHelper.convertToJsonObject(entry.getValue(), typeName);
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
                SelfTests.reportExternalDataManagerError();
            }
        }
        LOGGER.info("Loaded {} {}(s).", types.size(), typeName);
    }
}