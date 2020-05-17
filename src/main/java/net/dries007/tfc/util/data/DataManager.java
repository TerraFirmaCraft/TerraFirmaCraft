package net.dries007.tfc.util.data;

import java.util.*;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.minecraft.client.resources.JsonReloadListener;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.dries007.tfc.TerraFirmaCraft;

public abstract class DataManager<T> extends JsonReloadListener
{
    public static final Logger LOGGER = LogManager.getLogger();

    protected final Gson gson;
    protected final BiMap<ResourceLocation, T> types;
    protected final Object2IntMap<T> typeIds;
    protected final List<T> orderedTypes;

    protected final List<Runnable> callbacks;
    protected final String typeName;

    public DataManager(Gson gson, String domain, String typeName)
    {
        super(gson, TerraFirmaCraft.MOD_ID + "/" + domain);

        this.gson = gson;
        this.types = HashBiMap.create();
        this.typeIds = new Object2IntOpenHashMap<>();
        this.orderedTypes = new ArrayList<>();
        this.callbacks = new ArrayList<>();
        this.typeName = typeName;
    }

    @Nullable
    public T get(ResourceLocation id)
    {
        return types.get(id);
    }

    public T getOrDefault(ResourceLocation id)
    {
        if (types.containsKey(id))
        {
            return types.get(id);
        }
        if (!orderedTypes.isEmpty())
        {
            return orderedTypes.get(0);
        }
        throw new IllegalStateException("Tried to get default but there were none!");
    }

    @Nullable
    public ResourceLocation getName(T type)
    {
        return types.inverse().get(type);
    }

    public int getId(T type)
    {
        return typeIds.getInt(type);
    }

    public T get(int id)
    {
        return orderedTypes.get(id);
    }

    @Nonnull
    public Set<T> getValues()
    {
        return types.values();
    }

    @Nonnull
    public Set<ResourceLocation> getKeys()
    {
        return types.keySet();
    }

    @Nonnull
    public List<T> getOrderedValues()
    {
        return orderedTypes;
    }

    public void addCallback(Runnable callback)
    {
        callbacks.add(callback);
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonObject> resources, IResourceManager resourceManager, IProfiler profiler)
    {
        types.clear();
        typeIds.clear();
        orderedTypes.clear();
        for (Map.Entry<ResourceLocation, JsonObject> entry : resources.entrySet())
        {
            ResourceLocation name = entry.getKey();
            JsonObject json = entry.getValue();
            try
            {
                if (CraftingHelper.processConditions(json, "conditions"))
                {
                    T object = read(name, json);
                    types.put(name, object);
                    orderedTypes.add(object);
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

        // Setup entry -> id map from sorted names
        orderedTypes.sort(Comparator.comparing(types.inverse()::get));
        for (int i = 0; i < orderedTypes.size(); i++)
        {
            typeIds.put(orderedTypes.get(i), i);
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
}
