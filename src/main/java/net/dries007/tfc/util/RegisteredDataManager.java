/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util;

import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nullable;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;

import net.dries007.tfc.TerraFirmaCraft;

/**
 * An implementation of a json reload listener, which has an internal backing registry which is then populated via json.
 * Elements can be registered to this manager, and then the manager will error if those elements are not populated by json entries.
 */
public class RegisteredDataManager<T> extends SimpleJsonResourceReloadListener
{
    public static final Logger LOGGER = LogManager.getLogger();

    protected final BiMap<ResourceLocation, Entry<T>> types;
    protected final BiFunction<ResourceLocation, JsonObject, T> factory;
    protected final Function<ResourceLocation, T> fallbackFactory;
    protected final String typeName;

    public RegisteredDataManager(BiFunction<ResourceLocation, JsonObject, T> factory, Function<ResourceLocation, T> fallbackFactory, String domain, String typeName)
    {
        super(new Gson(), TerraFirmaCraft.MOD_ID + "/" + domain);

        this.types = HashBiMap.create();
        this.factory = factory;
        this.fallbackFactory = fallbackFactory;
        this.typeName = typeName;
    }

    public Entry<T> register(ResourceLocation id)
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
