/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util;

import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;

import com.mojang.logging.LogUtils;
import net.dries007.tfc.network.DataManagerSyncPacket;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

/**
 * An implementation of a json reload listener, which has an internal backing registry which is then populated via json.
 * Elements can be registered to this manager, and then the manager will error if those elements are not populated by json entries.
 */
public class RegisteredDataManager<T> extends DataManager<RegisteredDataManager.Entry<T>>
{
    public static final Logger LOGGER = LogUtils.getLogger();

    @Nullable
    private static <T> BiFunction<ResourceLocation, FriendlyByteBuf, Entry<T>> fixNetworkFactory(@Nullable BiFunction<ResourceLocation, FriendlyByteBuf, T> networkFactory)
    {
        return networkFactory != null ? networkFactory.andThen(Entry::of) : null;
    }

    @Nullable
    private static <T> BiConsumer<Entry<T>, FriendlyByteBuf> fixNetworkEncoder(@Nullable BiConsumer<T, FriendlyByteBuf> networkEncoder)
    {
        return networkEncoder != null ? (e, buf) -> networkEncoder.accept(e.value, buf) : null;
    }


    protected final BiFunction<ResourceLocation, JsonObject, T> factory;
    protected final Function<ResourceLocation, T> fallbackFactory;
    protected final String typeName;

    public RegisteredDataManager(BiFunction<ResourceLocation, JsonObject, T> factory, Function<ResourceLocation, T> fallbackFactory, String domain, String typeName)
    {
        this(factory, fallbackFactory, domain, typeName, null, null, null);
    }

    public RegisteredDataManager(BiFunction<ResourceLocation, JsonObject, T> factory, Function<ResourceLocation, T> fallbackFactory, String domain, String typeName, @Nullable BiFunction<ResourceLocation, FriendlyByteBuf, T> networkFactory, @Nullable BiConsumer<T, FriendlyByteBuf> networkEncoder, @Nullable Supplier<? extends DataManagerSyncPacket<Entry<T>>> networkPacketFactory)
    {
        super(domain, typeName, (res, id) -> null, fixNetworkFactory(networkFactory), fixNetworkEncoder(networkEncoder), networkPacketFactory);

        this.factory = factory;
        this.fallbackFactory = fallbackFactory;
        this.typeName = typeName;
    }

    public synchronized Entry<T> register(ResourceLocation id)
    {
        return types.computeIfAbsent(id, key -> new Entry<>());
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> elements, ResourceManager resourceManager, ProfilerFiller profiler)
    {
        types.values().forEach(e -> e.value = null);

        int valid = 0;
        for (Map.Entry<ResourceLocation, JsonElement> entry : elements.entrySet())
        {
            final ResourceLocation name = entry.getKey();
            final Entry<T> typeEntry = types.get(name);
            if (typeEntry == null)
            {
                LOGGER.error("Ignoring {} '{}' as it was not registered.", typeName, name);
            }
            else
            {
                try
                {
                    final JsonObject json = GsonHelper.convertToJsonObject(entry.getValue(), typeName);
                    typeEntry.value = factory.apply(name, json);
                    valid++;
                }
                catch (IllegalArgumentException | JsonParseException e)
                {
                    LOGGER.error("{} '{}' failed to parse. {}: {}", typeName, name, e.getClass().getSimpleName(), e.getMessage());
                }
            }
        }

        LOGGER.info("Loaded {} / {} {}(s).", valid, types.size(), typeName);

        for (Map.Entry<ResourceLocation, Entry<T>> entry : types.entrySet())
        {
            final ResourceLocation id = entry.getKey();
            final Entry<T> typeEntry = entry.getValue();
            if (typeEntry.value == null)
            {
                LOGGER.error("Missing required {} '{}'. Using fallback factory.", typeName, id);
                typeEntry.value = fallbackFactory.apply(id);
            }
        }
    }

    public static class Entry<T> implements Supplier<T>
    {
        private static <T> Entry<T> of(T value)
        {
            final Entry<T> entry = new Entry<>();
            entry.value = value;
            return entry;
        }

        @Nullable private T value = null;

        @Override
        public T get()
        {
            if (value == null)
            {
                throw new IllegalStateException("Value requested before data has been loaded");
            }
            return value;
        }
    }
}
