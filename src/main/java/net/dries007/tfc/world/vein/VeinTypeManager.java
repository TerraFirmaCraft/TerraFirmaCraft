package net.dries007.tfc.world.vein;

import java.util.*;
import javax.annotation.Nullable;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.minecraft.block.BlockState;
import net.minecraft.client.resources.JsonReloadListener;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;

import net.dries007.tfc.util.collections.IWeighted;
import net.dries007.tfc.util.collections.Weighted;
import net.dries007.tfc.util.json.*;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

public class VeinTypeManager extends JsonReloadListener
{
    public static final VeinTypeManager INSTANCE;

    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = new GsonBuilder()
        .registerTypeAdapter(new TypeToken<IWeighted<BlockState>>() {}.getType(), new WeightedDeserializer<>(BlockState.class))
        .registerTypeAdapter(new TypeToken<IWeighted<VeinType.Indicator>>() {}.getType(), new WeightedDeserializer<>(VeinType.Indicator.class))
        .registerTypeAdapter(new TypeToken<List<BlockState>>() {}.getType(), new LenientListDeserializer<>(BlockState.class, Collections::singletonList, ArrayList::new))
        .registerTypeAdapter(BlockState.class, BlockStateDeserializer.INSTANCE)
        .registerTypeAdapter(VeinType.Indicator.class, VeinIndicatorDeserializer.INSTANCE)
        .registerTypeAdapter(VeinType.class, VeinTypeDeserializer.INSTANCE)
        .disableHtmlEscaping()
        .create();

    static
    {
        // Constructor call must come after GSON declaration
        INSTANCE = new VeinTypeManager();
    }

    private final BiMap<ResourceLocation, VeinType> veinTypes;
    private final List<IWeighted<VeinType>> veinTypeGroups;

    private VeinTypeManager()
    {
        super(GSON, MOD_ID + "/veins");
        this.veinTypes = HashBiMap.create();
        this.veinTypeGroups = new ArrayList<>();
    }

    public Collection<VeinType> getVeins()
    {
        return veinTypes.values();
    }

    public Set<ResourceLocation> getKeys()
    {
        return veinTypes.keySet();
    }

    @Nullable
    public VeinType getVein(ResourceLocation key)
    {
        return veinTypes.get(key);
    }

    public ResourceLocation getName(VeinType key)
    {
        return veinTypes.inverse().get(key);
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonObject> resources, IResourceManager manager, IProfiler profiler)
    {
        Map<ResourceLocation, IWeighted<VeinType>> groupNames = new HashMap<>();

        for (Map.Entry<ResourceLocation, JsonObject> entry : resources.entrySet())
        {
            ResourceLocation name = entry.getKey();
            JsonObject json = entry.getValue();
            try
            {
                if (CraftingHelper.processConditions(json, "conditions"))
                {
                    VeinType veinType = GSON.fromJson(json, VeinType.class);
                    veinTypes.put(name, veinType);
                    ResourceLocation groupName = veinType.getGroup();
                    if (groupName != null)
                    {
                        // Find the existing group, or add one to the names map + groups list
                        IWeighted<VeinType> group = groupNames.computeIfAbsent(groupName, key -> {
                            IWeighted<VeinType> result = new Weighted<>();
                            veinTypeGroups.add(result);
                            return result;
                        });
                        group.add(veinType.getGroupWeight(), veinType);
                    }
                    else
                    {
                        // Add the vein as a singleton group
                        veinTypeGroups.add(IWeighted.singleton(veinType));
                    }
                    veinTypes.put(name, GSON.fromJson(json, VeinType.class));
                }
                else
                {
                    LOGGER.info("Skipping loading vein '{}' as it's conditions were not met", name);
                }
            }
            catch (IllegalArgumentException | JsonParseException e)
            {
                LOGGER.warn("Vein '{}' failed to parse. This is most likely caused by incorrectly specified JSON.", entry.getKey());
                LOGGER.warn("Error: ", e);
            }
        }

        LOGGER.info("Registered {} Veins Successfully.", veinTypes.size());

        // After Veins have Reloaded
        //VeinsFeature.resetChunkRadius();
    }
}
