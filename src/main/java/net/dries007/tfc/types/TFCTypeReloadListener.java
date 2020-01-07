package net.dries007.tfc.types;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

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

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.api.types.TFCType;

@ParametersAreNonnullByDefault
public class TFCTypeReloadListener<T extends TFCType> extends JsonReloadListener
{
    private static final Logger LOGGER = LogManager.getLogger();

    private final BiMap<ResourceLocation, T> types;
    private final List<T> orderedTypes;
    private final Gson gson;
    private final Class<T> resourceClass;

    public TFCTypeReloadListener(Gson gson, String domain, Class<T> resourceClass)
    {
        super(gson, TerraFirmaCraft.MOD_ID + "/" + domain);

        this.types = HashBiMap.create();
        this.orderedTypes = new ArrayList<>();
        this.gson = gson;
        this.resourceClass = resourceClass;
    }

    @Nullable
    public T get(ResourceLocation id)
    {
        return types.get(id);
    }

    @Nullable
    public ResourceLocation getId(T type)
    {
        return types.inverse().get(type);
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

    @Override
    protected void apply(Map<ResourceLocation, JsonObject> resources, IResourceManager resourceManager, IProfiler profiler)
    {
        types.clear();
        orderedTypes.clear();
        for (Map.Entry<ResourceLocation, JsonObject> entry : resources.entrySet())
        {
            ResourceLocation name = entry.getKey();
            JsonObject json = entry.getValue();
            try
            {
                if (CraftingHelper.processConditions(json, "conditions"))
                {
                    T object = gson.fromJson(json, resourceClass);
                    object.setId(name);
                    types.put(name, object);
                    orderedTypes.add(object);
                }
                else
                {
                    LOGGER.info("Skipping loading type '{}' as it's conditions were not met", name);
                }
            }
            catch (IllegalArgumentException | JsonParseException e)
            {
                LOGGER.warn("Type '{}' failed to parse. This is most likely caused by incorrectly specified JSON.", entry.getKey());
                LOGGER.warn("Error: ", e);
            }
        }

        LOGGER.info("Registered {} Types Successfully.", types.size());
    }
}
