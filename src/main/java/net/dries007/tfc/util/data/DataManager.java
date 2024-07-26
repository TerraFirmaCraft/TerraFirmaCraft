/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import net.dries007.tfc.util.SelfTests;


public class DataManager<T> extends SimpleJsonResourceReloadListener
{
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Gson GSON = new Gson();

    private final String registryName;
    private final Codec<T> codec;
    private final @Nullable StreamCodec<RegistryFriendlyByteBuf, T> streamCodec;

    private Map<ResourceLocation, T> byKey = Map.of();
    private final Map<T, ResourceLocation> toKey = new IdentityHashMap<>(); // Allow equal values to map to unique keys

    private final Codec<Reference<T>> byIdCodec = ResourceLocation.CODEC.xmap(this::getReference, Reference::id);
    private final StreamCodec<ByteBuf, Reference<T>> byIdStreamCodec = ResourceLocation.STREAM_CODEC.map(this::getReference, Reference::id);

    private final Map<ResourceLocation, Reference<T>> references = new HashMap<>();
    private final Object referencesLock = new Object();

    /**
     * Create a {@link DataManager} that is not synced to client
     */
    public DataManager(ResourceLocation domain, Codec<T> codec)
    {
        this(domain, codec, null);
    }

    /**
     * Create a {@link DataManager} that is synced to client
     */
    public DataManager(ResourceLocation domain, Codec<T> codec, @Nullable StreamCodec<RegistryFriendlyByteBuf, T> streamCodec)
    {
        super(GSON, domain.getNamespace() + "/" + domain.getPath());

        this.registryName = domain.getPath();
        this.codec = codec;
        this.streamCodec = streamCodec;
    }

    /**
     * @return An element of this data manager, by id. Returns {@code null} if the element does not exist.
     */
    @Nullable
    public T get(ResourceLocation id)
    {
        return byKey.get(id);
    }

    @Nullable
    public ResourceLocation getId(T value)
    {
        return toKey.get(value);
    }

    /**
     * @return An element of this data manager, by id. Throws an exception if the element does not exist.
     */
    public T getOrThrow(ResourceLocation id)
    {
        return Objects.requireNonNull(byKey.get(id));
    }

    public ResourceLocation getIdOrThrow(T value)
    {
        return Objects.requireNonNull(toKey.get(value));
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
            ref = references.computeIfAbsent(id, key -> new Reference<>(key, byKey.get(key)));
        }
        return ref;
    }

    public Map<ResourceLocation, T> getElements()
    {
        return byKey;
    }

    public Collection<T> getValues()
    {
        return byKey.values();
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
     * @return A codec used to encode objects. Note that this will not maintain references, and should not be used by data loading code
     * @see #byIdReferenceCodec()
     */
    public final Codec<T> codec()
    {
        return codec;
    }

    /**
     * @return The codec used to write individual data manager elements to the network. Should only be used by the initial sync packet!
     * @throws NullPointerException if {@link #isSynced()} is {@code false}
     * @see #byIdStreamCodec()
     */
    public final StreamCodec<RegistryFriendlyByteBuf, T> streamCodec()
    {
        return Objects.requireNonNull(streamCodec);
    }

    /**
     * Updates the data manager with the state of the networked elements. Only called on physical client connecting to a physical server,
     * and in test environments where we want to create values from external data.
     */
    public void bindValues(Map<ResourceLocation, T> elements)
    {
        // Sync received from physical server
        byKey = ImmutableMap.copyOf(elements);
        updateReferences();
        LOGGER.info("Received {} {}(s) from physical server", byKey.size(), registryName);
    }

    /**
     * @return The registry name (excluding namespace) of this data manager
     */
    @Override
    public final String getName()
    {
        return registryName;
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> elements, ResourceManager resourceManagerIn, ProfilerFiller profilerIn)
    {
        final ImmutableMap.Builder<ResourceLocation, T> builder = ImmutableMap.builder();
        final RegistryOps<JsonElement> ops = getRegistryLookup().createSerializationContext(JsonOps.INSTANCE);
        for (Map.Entry<ResourceLocation, JsonElement> entry : elements.entrySet())
        {
            final ResourceLocation id = entry.getKey();
            try
            {
                builder.put(id, codec.parse(ops, entry.getValue()).getOrThrow(JsonParseException::new));
            }
            catch (IllegalArgumentException | JsonParseException e)
            {
                LOGGER.error("{} '{}' failed to parse. {}: {}", registryName, id, e.getClass().getSimpleName(), e.getMessage());
                SelfTests.reportExternalError();
            }
        }

        byKey = builder.build();
        updateReferences();

        LOGGER.info("Loaded {} {}(s).", byKey.size(), registryName);
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
                LOGGER.error("There were {} '{}' that were used but not defined: {}", unboundReferences.size(), registryName, unboundReferences);
                SelfTests.reportExternalError();
            }
        }

        toKey.clear();
        byKey.forEach((id, value) -> toKey.put(value, id));
    }

    public static class Reference<T> implements Supplier<T>
    {
        private final ResourceLocation id;
        private Optional<T> value;

        public Reference(ResourceLocation id)
        {
            this(id, null);
        }

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

        public void set(T value)
        {
            assert this.value.isEmpty() : "Assigned a duplicate value!";
            this.value = Optional.of(value);
        }
    }
}