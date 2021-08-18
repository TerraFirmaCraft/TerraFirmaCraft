/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util.data;

import java.util.*;
import java.util.function.BiFunction;
import javax.annotation.Nullable;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.gson.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.common.crafting.CraftingHelper;

import net.dries007.tfc.TerraFirmaCraft;

public abstract class DataManager<T> extends SimpleJsonResourceReloadListener
{
    public static final Logger LOGGER = LogManager.getLogger();

    protected final Gson gson;
    protected final BiMap<ResourceLocation, T> types;

    protected final List<Runnable> callbacks;
    protected final String typeName;

    protected final boolean allowNone;
    @Nullable protected T defaultValue;
    protected boolean loaded;

    protected DataManager(Gson gson, String domain, String typeName, boolean allowNone)
    {
        super(gson, TerraFirmaCraft.MOD_ID + "/" + domain);

        this.gson = gson;
        this.types = HashBiMap.create();
        this.callbacks = new ArrayList<>();
        this.typeName = typeName;
        this.allowNone = allowNone;
        this.defaultValue = null;
        this.loaded = false;
    }

    @Nullable
    public T get(ResourceLocation id)
    {
        return types.get(id);
    }

    public T getOrDefault(ResourceLocation id)
    {
        return types.getOrDefault(id, getDefault());
    }

    public T getDefault()
    {
        return Objects.requireNonNull(defaultValue, "Tried to get the default " + typeName + " but none existed! This DataManager has allowNone = " + allowNone);
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

    public Set<ResourceLocation> getKeys()
    {
        return types.keySet();
    }

    public void addCallback(Runnable callback)
    {
        callbacks.add(callback);
    }

    public boolean isLoaded()
    {
        return loaded;
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> objectIn, ResourceManager resourceManagerIn, ProfilerFiller profilerIn)
    {
        types.clear();
        for (Map.Entry<ResourceLocation, JsonElement> entry : objectIn.entrySet())
        {
            ResourceLocation name = entry.getKey();
            JsonObject json = GsonHelper.convertToJsonObject(entry.getValue(), "root");
            try
            {
                if (CraftingHelper.processConditions(json, "conditions"))
                {
                    T object = read(name, json);
                    types.put(name, object);
                }
                else
                {
                    LOGGER.info("Skipping loading {} '{}' as it's conditions were not met", typeName, name);
                }
            }
            catch (IllegalArgumentException | JsonParseException e)
            {
                LOGGER.error("{} '{}' failed to parse. {}: {}", typeName, name, e.getClass().getSimpleName(), e.getMessage());
            }
        }

        LOGGER.info("Loaded {} {}(s).", types.size(), typeName);
        loaded = true;
        defaultValue = types.values().stream().findFirst().orElse(null);
        if (defaultValue == null && !allowNone)
        {
            throw new IllegalStateException("There must be at least one loaded " + typeName + '!');
        }
        postProcess();
    }

    protected abstract T read(ResourceLocation id, JsonObject obj);

    /**
     * Here for subclasses to override
     */
    protected void postProcess()
    {
        for (Runnable callback : callbacks)
        {
            callback.run();
        }
    }

    public static class Instance<T> extends DataManager<T>
    {
        private final BiFunction<ResourceLocation, JsonObject, T> factory;

        public Instance(BiFunction<ResourceLocation, JsonObject, T> factory, String domain, String typeName, boolean allowNone)
        {
            super(new GsonBuilder().create(), domain, typeName, allowNone);
            this.factory = factory;
        }

        @Override
        protected T read(ResourceLocation id, JsonObject json)
        {
            return factory.apply(id, json);
        }
    }
}