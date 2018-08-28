/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.util;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.*;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.io.FileUtils;
import net.minecraft.util.ResourceLocation;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.api.registries.TFCRegistries;
import net.dries007.tfc.api.types.Ore;
import net.dries007.tfc.api.types.Rock;
import net.dries007.tfc.api.types.RockCategory;

import static net.dries007.tfc.Constants.GSON;
import static net.dries007.tfc.api.util.TFCConstants.MOD_ID;

public class OreSpawnData
{
    private static List<OreEntry> oreSpawnEntries;
    private static double totalWeight;
    private static File genFile;

    public static List<OreEntry> getOreSpawnEntries()
    {
        return oreSpawnEntries;
    }

    public static double getTotalWeight()
    {
        return totalWeight;
    }

    public static void preInit(File dir)
    {
        TerraFirmaCraft.getLog().info("Loading or creating ore generation config file");

        File tfcDir = new File(dir, MOD_ID);

        if (!tfcDir.exists() && !tfcDir.mkdir()) throw new Error("Problem creating TFC config directory.");

        genFile = new File(tfcDir, "ore_spawn_data.json");
    }

    public static void reloadOreGen()
    {
        String str = null;
        if (genFile.exists())
        {
            try
            {
                str = FileUtils.readFileToString(genFile, Charset.defaultCharset());
            }
            catch (IOException e)
            {
                throw new Error("Error reading config file.", e);
            }
        }
        if (Strings.isNullOrEmpty(str))
        {
            try
            {
                FileUtils.copyInputStreamToFile(OreSpawnData.class.getResourceAsStream("/assets/tfc/config/ore_spawn_data.json"), genFile);
                str = FileUtils.readFileToString(genFile, Charset.defaultCharset());
                if (Strings.isNullOrEmpty(str)) throw new RuntimeException("Default entry is empty... wut did u do");
            }
            catch (IOException e)
            {
                throw new Error("Error providing default config file.", e);
            }
        }

        if (Strings.isNullOrEmpty(str)) throw new Error("The config file is empty");
        Map<String, OreJson> configMap = GSON.fromJson(str, new TypeToken<Map<String, OreJson>>() {}.getType());
        if (configMap == null) throw new Error("Error reading config file.");
        totalWeight = 0.0;
        oreSpawnEntries = Collections.unmodifiableList(configMap.entrySet().stream().map(entry ->
        {
            final String name = entry.getKey();
            final OreJson json = entry.getValue();

            Ore ore = TFCRegistries.ORES.getValue(json.ore);
            if (ore == null)
            {
                TerraFirmaCraft.getLog().warn("Problem parsing ore entry '" + name + "'. Ore is not defined. Skipping.");
                return null;
            }
            SpawnSize size;
            SpawnType shape;
            try
            {
                size = SpawnSize.valueOf(json.size.toUpperCase());
                shape = SpawnType.valueOf(json.shape.toUpperCase());
            }
            catch (IllegalArgumentException e)
            {
                TerraFirmaCraft.getLog().warn("Problem parsing ore entry '" + name + "'. Size / shape is not defined. Skipping.", e);
                return null;
            }
            List<Rock> blocks = new ArrayList<>();
            json.baseRocks.forEach(s ->
            {
                Rock rock = TFCRegistries.ROCKS.getValue(s);
                if (rock == null)
                {
                    RockCategory category = TFCRegistries.ROCK_CATEGORIES.getValue(s);
                    if (category == null)
                        TerraFirmaCraft.getLog().warn("Problem parsing ore entry '" + name + "'. Rock / Rock Category '" + s + "' is not defined. Skipping.");
                    else
                        blocks.addAll(category.getRocks());
                }
                else
                {
                    blocks.add(rock);
                }
            });
            totalWeight += 1.0D / (double) json.rarity;
            return new OreEntry(ore, size, shape, blocks, json.rarity, json.minY, json.maxY, json.density);
        }).filter(Objects::nonNull).collect(Collectors.toList()));
    }

    public enum SpawnType
    {
        SCATTERED_CLUSTER(2, 5), // This is the default. It creates scattered spheriods
        SINGLE_CLUSTER(1, 1); // This is to create a single spheriod

        public final int minClusters;
        public final int maxClusters;

        SpawnType(int minClusters, int maxClusters)
        {
            this.minClusters = minClusters;
            this.maxClusters = maxClusters;
        }
    }

    public enum SpawnSize
    {
        SMALL(8.0F, 0.7F),
        MEDIUM(12.0F, 0.6F),
        LARGE(16.0F, 0.5F);

        public final float radius;
        public final float densityModifier;

        SpawnSize(float radius, float densityModifier)
        {
            this.radius = radius;
            this.densityModifier = densityModifier;
        }
    }

    public static final class OreEntry
    {
        public final Ore ore;
        public final SpawnType type;
        public final SpawnSize size;
        public final ImmutableSet<Rock> baseRocks;
        public final int minY;
        public final int maxY;
        public final double weight;
        public final double density;
        public final int rarity;

        private OreEntry(@Nonnull Ore ore, SpawnSize size, SpawnType type, Collection<Rock> baseRocks, int rarity, int minY, int maxY, int density)
        {
            this.ore = ore;
            this.size = size;
            this.type = type;
            this.baseRocks = ImmutableSet.copyOf(baseRocks);

            this.rarity = rarity;
            this.weight = 1.0D / (double) rarity;
            this.minY = minY;
            this.maxY = maxY;
            this.density = 0.01D * (double) density; // For debug purposes, removing the 0.01D will lead to ore veins being full size, easy to see shapes
        }

        @Override
        public String toString()
        {
            return "OreSpawnData{" +
                "ore=" + ore +
                ", type=" + type +
                ", size=" + size +
                ", rarity=" + rarity +
                ", baseRocks=" + baseRocks +
                ", minY=" + minY +
                ", maxY=" + maxY +
                ", density=" + density +
                '}';
        }
    }

    @SuppressWarnings("unused")
    private final class OreJson
    {
        // todo: replace with the actual enums, GSON can handle it fine IIRC
        private ResourceLocation ore;
        private String size;
        private String shape;

        private int rarity;
        private int density;
        @SerializedName("minimum_height")
        private int minY;
        @SerializedName("maximum_height")
        private int maxY;

        @SerializedName("base_rocks")
        private List<ResourceLocation> baseRocks;
    }
}
