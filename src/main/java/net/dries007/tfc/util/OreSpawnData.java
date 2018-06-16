/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.annotations.SerializedName;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.objects.Ore;
import net.dries007.tfc.objects.Rock;
import net.dries007.tfc.objects.Rock.Category;

import static net.dries007.tfc.Constants.GSON;

// todo: someone look through assets/tfc/config/tfc_ore_spawn_data.json and verify that everything looks good  -Alex (alcatrazEscapee)
public class OreSpawnData
{
    public static ImmutableList<OreEntry> ORE_SPAWN_DATA;
    public static double TOTAL_WEIGHT;

    public static File configDir;
    private static String genData;


    public static void preInit()
    {
        TerraFirmaCraft.getLog().info("Loading or creating ore generation config file");

        File tfcDir = new File(configDir, "/tfc/");

        if (!tfcDir.exists())
        {
            try
            {
                if (!tfcDir.mkdir())
                {
                    throw new Error("Problem creating TFC World gen directory: (unknown error)");
                }
            }
            catch (Exception e)
            {
                TerraFirmaCraft.getLog().fatal("Problem creating TFC World gen directory:", e);
                return;
            }
        }
        File genFile = new File(tfcDir, "tfc_ore_spawn_data.json");
        Path genPath = genFile.toPath();
        try
        {
            if (genFile.createNewFile())
            {
                FileUtils.copyFile("assets/tfc/config/tfc_ore_spawn_data.json", genFile);
                TerraFirmaCraft.getLog().info("Created standard generation json.");
            }
            else if (!genFile.exists())
            {
                throw new Error("Problem creating TFC world gen json: (unspecified error).");
            }
        }
        catch (Exception e)
        {
            TerraFirmaCraft.getLog().fatal("Problem creating TFC world gen json: ", e);
        }
        try
        {
            genData = new String(Files.readAllBytes(genPath));
        }
        catch (IOException e)
        {
            TerraFirmaCraft.getLog().fatal("Unable to read world gen json.", e);
        }

        TerraFirmaCraft.getLog().info("Complete.");
    }

    // todo: test that all the exceptions and try statements catch problems with json
    public static void reloadOreGen()
    {
        Set<Map.Entry<String, JsonElement>> entries;
        ImmutableList.Builder<OreEntry> builder = new ImmutableList.Builder<>();
        TOTAL_WEIGHT = 0.0;

        try
        {
            JsonElement rootJson = new JsonParser().parse(genData);
            JsonObject rootObject = rootJson.getAsJsonObject();

            entries = rootObject.entrySet();
        }
        catch (Exception e)
        {
            // Problems
            return;
        }

        for (Map.Entry<String, JsonElement> entry : entries)
        {
            OreJson genEntry;
            Ore ore;
            IBlockState state;
            final SpawnSize size;
            final SpawnType shape;
            final int rarity;
            final int minY;
            final int maxY;
            final int density;
            List<String> baseStrings;

            try
            {
                genEntry = GSON.fromJson(entry.getValue(), OreJson.class);

                size = SpawnSize.valueOf(genEntry.size.toUpperCase());
                shape = SpawnType.valueOf(genEntry.shape.toUpperCase());

                rarity = genEntry.rarity;
                minY = genEntry.minY;
                maxY = genEntry.maxY;
                density = genEntry.density;

                baseStrings = genEntry.baseRocks;

            }
            catch (Exception e)
            {
                //Problems
                TerraFirmaCraft.getLog().warn("Problem parsing data for ore generation entry with key: \"" + entry.getKey() + "\" Skipping.");
                continue;
            }

            try
            {
                ore = Ore.valueOf(genEntry.ore.toUpperCase());
                if (ore == Ore.UNKNOWN_ORE)
                    throw new Exception("Can't assign to unknown ore");
                state = null;
            }
            catch (Exception e1)
            {
                try
                {
                    String blockName = genEntry.ore;
                    Block block = Block.getBlockFromName(blockName);
                    if (block != null)
                    {
                        state = block.getDefaultState();
                        ore = Ore.UNKNOWN_ORE;
                    }
                    else
                    {
                        TerraFirmaCraft.getLog().warn("Problem parsing IBlockState: block doesn't exist for ore generation entry with key: \"" + entry.getKey() + "\" Skipping.");
                        continue;
                    }
                }
                catch (Exception e2)
                {
                    TerraFirmaCraft.getLog().warn("Problem parsing Ore / IBlockState for ore generation entry with key: \"" + entry.getKey() + "\" Skipping.");
                    continue;
                }
            }

            ImmutableList.Builder<Rock> b = new ImmutableList.Builder<>();
            for (String s : baseStrings)
            {
                try
                {
                    Rock rock = Rock.valueOf(s.toUpperCase());
                    b.add(rock);
                }
                catch (IllegalArgumentException e1)
                {
                    try
                    {
                        Category category = Category.valueOf(s.toUpperCase());
                        for (Rock rock : Rock.values())
                        {
                            if (rock.category == category)
                            {
                                b.add(rock);
                            }
                        }
                    }
                    catch (IllegalArgumentException e2)
                    {
                        TerraFirmaCraft.getLog().warn("Problem parsing base rock \"" + s + "\" for ore generation entry with key+\"" + entry.getKey() + "\" Skipping");
                    }
                }
            }

            builder.add(new OreEntry(ore, state, size, shape, b.build(), rarity, minY, maxY, density));
            TOTAL_WEIGHT += 1.0D / (double) rarity;
            TerraFirmaCraft.getLog().debug("Added ore generation entry for " + entry.getKey());
        }
        TerraFirmaCraft.getLog().debug("Total weight for ore generator = " + TOTAL_WEIGHT);
        ORE_SPAWN_DATA = builder.build();
    }

        /*Config genData = ConfigFactory.parseFile(genFile);
        ImmutableList.Builder<OreEntry> builder = new ImmutableList.Builder<>();

        for (Map.Entry<String, ConfigValue> genEntry : genData.root().entrySet())
        {
            Config entryData;
            Ore ore;
            IBlockState state;
            final SpawnSize size;
            final SpawnType shape;
            final int rarity;
            final int minY;
            final int maxY;
            final int density;
            List<String> baseStrings;
            try
            {
                entryData = genData.getConfig(genEntry.getKey());

                //ore = Ore.valueOf(entryData.getString("ore").toUpperCase());
                size = SpawnSize.valueOf(entryData.getString("size").toUpperCase());
                shape = SpawnType.valueOf(entryData.getString("shape").toUpperCase());

                rarity = entryData.getInt("rarity");
                minY = entryData.getInt("minimum_height");
                maxY = entryData.getInt("maximum_height");
                density = entryData.getInt("density");

                baseStrings = entryData.getStringList("base_rocks");

            }
            catch (Exception e)
            {
                TerraFirmaCraft.getLog().warn("Problem parsing data for ore generation entry with key: \"" + genEntry.getKey() + "\" Skipping.");
                continue;
            }

            try
            {
                ore = Ore.valueOf(entryData.getString("ore").toUpperCase());
                if (ore == Ore.UNKNOWN_ORE)
                    throw new Exception("Can't assign to unknown ore");
                state = null;
            }
            catch (Exception e1)
            {
                try
                {
                    String blockName = entryData.getString("ore");
                    Block block = Block.getBlockFromName(blockName);
                    if (block != null)
                    {
                        state = block.getDefaultState();
                        ore = Ore.UNKNOWN_ORE;
                    }
                    else
                    {
                        TerraFirmaCraft.getLog().warn("Problem parsing IBlockState: block doesn't exist for ore generation entry with key: \\\"\"+genEntry.getKey()+\"\\\" Skipping.\"");
                        continue;
                    }
                }
                catch (Exception e2)
                {
                    TerraFirmaCraft.getLog().warn("Problem parsing Ore / IBlockState for ore generation entry with key: \\\"\"+genEntry.getKey()+\"\\\" Skipping.\"");
                    continue;
                }
            }

            ImmutableList.Builder<Rock> b = new ImmutableList.Builder<>();
            for (String s : baseStrings)
            {
                try
                {
                    Rock rock = Rock.valueOf(s.toUpperCase());
                    b.add(rock);
                }
                catch (IllegalArgumentException e1)
                {
                    try
                    {
                        Category category = Category.valueOf(s.toUpperCase());
                        for (Rock rock : Rock.values())
                        {
                            if (rock.category == category)
                            {
                                b.add(rock);
                            }
                        }
                    }
                    catch (IllegalArgumentException e2)
                    {
                        TerraFirmaCraft.getLog().warn("Problem parsing base rock \"" + s + "\" for ore generation entry with key+\"" + genEntry.getKey() + "\" Skipping");
                    }
                }
            }

            builder.add(new OreEntry(ore, state, size, shape, b.build(), rarity, minY, maxY, density));
            TOTAL_WEIGHT += 1.0D / (double) rarity;
            TerraFirmaCraft.getLog().debug("Added ore generation entry for " + genEntry.getKey());*/
    //}

    //ORE_SPAWN_DATA = builder.build();
    //}

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
        public final IBlockState state;
        public final SpawnType type;
        public final SpawnSize size;
        public final ImmutableList<Rock> baseRocks;
        public final int minY;
        public final int maxY;
        public final double weight;
        public final double density;

        final int rarity;

        private OreEntry(@Nonnull Ore ore, @Nullable IBlockState state, SpawnSize size, SpawnType type, ImmutableList<Rock> baseRocks, int rarity, int minY, int maxY, int density)
        {
            this.ore = ore;
            this.state = state;
            this.size = size;
            this.type = type;
            this.baseRocks = baseRocks;

            this.rarity = rarity;
            this.weight = 1.0D / (double) rarity;
            this.minY = minY;
            this.maxY = maxY;
            this.density = 0.01D * (double) density; // For debug purposes, removing the 0.01D will lead to ore veins being full size, easy to see shapes

            if (ore == Ore.UNKNOWN_ORE && state == null)
                throw new IllegalStateException("Ore Entry has neither a IBlockState or a Ore type");
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

    private class OreJson
    {

        private String ore;
        private String size;
        private String shape;

        private int rarity;
        private int density;
        @SerializedName("minimum_height")
        private int minY;
        @SerializedName("maximum_height")
        private int maxY;

        @SerializedName("base_rocks")
        private List<String> baseRocks;
    }
}
