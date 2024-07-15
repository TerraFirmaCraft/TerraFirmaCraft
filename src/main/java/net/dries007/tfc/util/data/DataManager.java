/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapCodec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import net.dries007.tfc.util.SelfTests;


// todo: add a int ID mechanism, sync int IDs as part of normal sync, and then use int IDs for stream codec references
public class DataManager<T> extends SimpleJsonResourceReloadListener
{
    public static final MapCodec<ResourceLocation> ID = ExtraCodecs.retrieveContext(ops ->
        ops instanceof DataManager.IdAware<?> idAware && idAware.id != null ? DataResult.success(idAware.id) : DataResult.error(() -> "Not ID aware"));


    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Gson GSON = new Gson();

    protected final BiMap<ResourceLocation, T> types;
    protected final String typeName;
    protected final String registryName;

    protected final Codec<T> codec;
    protected final @Nullable StreamCodec<RegistryFriendlyByteBuf, T> streamCodec;

    private final Codec<Reference<T>> byIdCodec = ResourceLocation.CODEC.xmap(this::getReference, Reference::id);
    private final StreamCodec<ByteBuf, Reference<T>> byIdStreamCodec = ResourceLocation.STREAM_CODEC.map(this::getReference, Reference::id);

    private final Map<ResourceLocation, Reference<T>> references;
    private final Object referencesLock = new Object();

    /**
     * Create a {@link DataManager} that is not synced to client
     */
    public DataManager(ResourceLocation domain, String typeName, Codec<T> codec)
    {
        this(domain, typeName, codec, null);
    }

    /**
     * Create a {@link DataManager} that is synced to client
     */
    public DataManager(ResourceLocation domain, String typeName, Codec<T> codec, @Nullable StreamCodec<RegistryFriendlyByteBuf, T> streamCodec)
    {
        super(GSON, domain.getNamespace() + "/" + domain.getPath());

        this.types = HashBiMap.create();
        this.typeName = typeName;
        this.registryName = domain.getPath();
        this.codec = codec;
        this.streamCodec = streamCodec;
        this.references = new HashMap<>();
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
        return Objects.requireNonNull(types.get(id), () -> "Unregistered id: " + id);
    }

    public ResourceLocation getIdOrThrow(T t)
    {
        return Objects.requireNonNull(types.inverse().get(t), "Unregistered element");
    }

    /**
     * Returns a reference to an element of this data manager, by id. This can be used to reference an element before the data itself
     * is loaded. Once the data is loaded, this will throw an error if it was not provided, and can be safely unboxed after the fact.
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

    public Map<ResourceLocation, T> getElements()
    {
        return Collections.unmodifiableMap(types);
    }

    public Set<T> getValues()
    {
        return types.values();
    }

    public boolean isSynced()
    {
        return streamCodec != null;
    }

    /**
     * @return A codec that can return a reference to an element via an ID, that does not require elements to be loaded
     */
    public final Codec<Reference<T>> byIdReferenceCodec()
    {
        return byIdCodec;
    }

    /**
     * @return A stream codec used to write references to elements to the network
     * N.B. We just write the element by resource location. This is a bit wasteful, but we would need proper registry ID sync which is difficult to manage
     */
    public final StreamCodec<ByteBuf, Reference<T>> byIdStreamCodec()
    {
        return byIdStreamCodec;
    }

    /**
     * @return The codec used to write individual data manager elements to the network. Should only be used by the initial sync packet!
     * @throws NullPointerException if {@link #isSynced()} is {@code false}
     */
    public final StreamCodec<RegistryFriendlyByteBuf, T> streamCodec()
    {
        return Objects.requireNonNull(streamCodec);
    }

    public void onSync(boolean isMemoryConnection, Map<ResourceLocation, T> elements)
    {
        if (isMemoryConnection)
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

        final IdAware<JsonElement> ops = new IdAware<>(getRegistryLookup().createSerializationContext(JsonOps.INSTANCE));
        for (Map.Entry<ResourceLocation, JsonElement> entry : elements.entrySet())
        {
            final ResourceLocation id = entry.getKey();
            try
            {
                ops.id = id;
                types.put(id, codec.parse(ops, entry.getValue()).getOrThrow(JsonParseException::new));
            }
            catch (IllegalArgumentException | JsonParseException e)
            {
                LOGGER.error("{} '{}' failed to parse. {}: {}", typeName, id, e.getClass().getSimpleName(), e.getMessage());
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

    static class IdAware<T> extends RegistryOps<T>
    {
        @Nullable ResourceLocation id = null;

        IdAware(RegistryOps<T> other)
        {
            super(other);
        }
    }
}