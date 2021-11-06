/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util;

import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
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
import net.minecraftforge.common.crafting.CraftingHelper;

import net.dries007.tfc.TerraFirmaCraft;

/**
 * An implementation of a typical json reload manager.
 */
public abstract class DataManager<T> extends SimpleJsonResourceReloadListener
{
    public static final Logger LOGGER = LogManager.getLogger();

    protected final Gson gson;
    protected final BiMap<ResourceLocation, T> types;
    protected final String typeName;

    protected DataManager(String domain, String typeName)
    {
        this(new Gson(), domain, typeName);
    }

    protected DataManager(Gson gson, String domain, String typeName)
    {
        super(gson, TerraFirmaCraft.MOD_ID + "/" + domain);

        this.gson = gson;
        this.types = HashBiMap.create();
        this.typeName = typeName;
    }

    @Nullable
    public T get(ResourceLocation id)
    {
        return types.get(id);
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

    @Nullable
    public ResourceLocation getId(T type)
    {
        return types.inverse().get(type);
    }

    public Set<T> getValues()
    {
        return types.values();
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
                if (CraftingHelper.processConditions(json, "conditions"))
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
            }
        }

        LOGGER.info("Loaded {} {}(s).", types.size(), typeName);
        postProcess();
    }

    protected abstract T read(ResourceLocation id, JsonObject obj);

    /**
     * Here for subclasses to override
     */
    protected void postProcess() {}

    public static class Instance<T> extends DataManager<T>
    {
        private final BiFunction<ResourceLocation, JsonObject, T> factory;

        public Instance(BiFunction<ResourceLocation, JsonObject, T> factory, String domain, String typeName)
        {
            super(domain, typeName);
            this.factory = factory;
        }

        @Override
        protected T read(ResourceLocation id, JsonObject json)
        {
            return factory.apply(id, json);
        }
    }
}