/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.util.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.minecraft.client.resources.JsonReloadListener;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;

import net.dries007.tfc.TerraFirmaCraft;

public abstract class DataManager<T> extends JsonReloadListener
{
    public static final Logger LOGGER = LogManager.getLogger();

    protected final Gson gson;
    protected final BiMap<ResourceLocation, T> types;

    protected final List<Runnable> callbacks;
    protected final String typeName;

    protected T defaultValue;
    protected boolean loaded;

    public DataManager(Gson gson, String domain, String typeName)
    {
        super(gson, TerraFirmaCraft.MOD_ID + "/" + domain);

        this.gson = gson;
        this.types = HashBiMap.create();
        this.callbacks = new ArrayList<>();
        this.typeName = typeName;
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
        return defaultValue;
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
    protected void apply(Map<ResourceLocation, JsonElement> objectIn, IResourceManager resourceManagerIn, IProfiler profilerIn)
    {
        types.clear();
        for (Map.Entry<ResourceLocation, JsonElement> entry : objectIn.entrySet())
        {
            ResourceLocation name = entry.getKey();
            JsonObject json = JSONUtils.convertToJsonObject(entry.getValue(), "root");
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
                LOGGER.warn("{} '{}' failed to parse. Cause: {}", typeName, name, e.getMessage());
                LOGGER.debug("Error: ", e);
            }
        }

        LOGGER.info("Registered {} {}(s) Successfully.", types.size(), typeName);
        loaded = true;
        defaultValue = types.values().stream().findFirst().orElseThrow(() -> new IllegalStateException("There must be at least one registered " + typeName + '!'));
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
}