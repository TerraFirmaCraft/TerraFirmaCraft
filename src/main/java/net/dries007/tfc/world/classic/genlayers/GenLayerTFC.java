/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.classic.genlayers;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.function.Function;
import java.util.function.IntFunction;
import javax.imageio.ImageIO;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.layer.GenLayer;
import net.minecraftforge.fml.common.FMLCommonHandler;

import net.dries007.tfc.ConfigTFC;
import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.api.types.RockCategory;
import net.dries007.tfc.world.classic.biomes.BiomeTFC;
import net.dries007.tfc.world.classic.biomes.BiomesTFC;
import net.dries007.tfc.world.classic.genlayers.biome.*;
import net.dries007.tfc.world.classic.genlayers.datalayers.rock.GenLayerRockInit;
import net.dries007.tfc.world.classic.genlayers.datalayers.stability.GenLayerStabilityInit;
import net.dries007.tfc.world.classic.genlayers.river.GenLayerRiverInitTFC;
import net.dries007.tfc.world.classic.genlayers.river.GenLayerRiverMixTFC;
import net.dries007.tfc.world.classic.genlayers.river.GenLayerRiverTFC;

public abstract class GenLayerTFC extends GenLayer
{
    // Distinct colors for debug map gen
    private static final Color[] COLORS = new Color[] {
        new Color(0xFFB300),    // Vivid Yellow
        new Color(0x803E75),    // Strong Purple
        new Color(0xFF6800),    // Vivid Orange
        new Color(0xA6BDD7),    // Very Light Blue
        new Color(0xC10020),    // Vivid Red
        new Color(0xCEA262),    // Grayish Yellow
        new Color(0x817066),    // Medium Gray
        new Color(0x007D34),    // Vivid Green
        new Color(0xF6768E),    // Strong Purplish Pink
        new Color(0x00538A),    // Strong Blue
        new Color(0xFF7A5C),    // Strong Yellowish Pink
        new Color(0x53377A),    // Strong Violet
        new Color(0xFF8E00),    // Vivid Orange Yellow
        new Color(0xB32851),    // Strong Purplish Red
        new Color(0xF4C800),    // Vivid Greenish Yellow
        new Color(0x7F180D),    // Strong Reddish Brown
        new Color(0x93AA00),    // Vivid Yellowish Green
        new Color(0x593315),    // Deep Yellowish Brown
        new Color(0xF13A13),    // Vivid Reddish Orange
        new Color(0x232C16),    // Dark Olive Green
    };

    public static GenLayerTFC[] initializeBiomes(long seed)
    {
        // Continent generator
        GenLayerTFC continent = new GenLayerIslandTFC(1L);
        continent = new GenLayerFuzzyZoomTFC(2000L, continent);
        continent = new GenLayerAddIslandTFC(1L, continent);
        continent = new GenLayerZoomTFC(2001L, continent);
        continent = new GenLayerAddIslandTFC(2L, continent);
        continent = new GenLayerZoomTFC(2002L, continent);
        continent = new GenLayerAddIslandTFC(3L, continent);
        continent = new GenLayerZoomTFC(2003L, continent);
        continent = new GenLayerAddIslandTFC(4L, continent);
        continent = new GenLayerDeepOcean(4L, continent);
        // At this point, the output of continent only contains PLAINS, OCEAN and DEEP OCEAN.
        drawImageBiomes(1024, continent, "continent");

        // Create Biomes
        GenLayerTFC biomes = new GenLayerBiomeTFC(200L, continent);
        biomes = new GenLayerLakes(200L, biomes);
        biomes = GenLayerZoomTFC.magnify(1000L, biomes, 2);
        biomes = new GenLayerBiomeEdge(1000L, biomes);
        biomes = new GenLayerZoomTFC(1000, biomes);
        biomes = new GenLayerAddIslandTFC(3L, biomes);
        biomes = new GenLayerZoomTFC(1001, biomes);
        biomes = new GenLayerShoreTFC(1000L, biomes);
        biomes = new GenLayerZoomTFC(1002, biomes);
        biomes = new GenLayerZoomTFC(1003, biomes);
        biomes = new GenLayerSmoothTFC(1000L, biomes);
        // Now we have a full on biome map
        drawImageBiomes(1024, biomes, "biomes");

        // Create Rivers
        GenLayerTFC rivers = GenLayerZoomTFC.magnify(1000L, continent, 2);
        rivers = new GenLayerRiverInitTFC(100L, rivers);
        rivers = GenLayerZoomTFC.magnify(1000L, rivers, 6);
        rivers = new GenLayerRiverTFC(1L, rivers);
        rivers = new GenLayerSmoothTFC(1000L, rivers);
        // Rivers should only have plains or rivers.
        drawImageBiomes(1024, rivers, "rivers");

        // Mix the biomes and rivers
        GenLayerRiverMixTFC riverMix = new GenLayerRiverMixTFC(100L, biomes, rivers);
        riverMix.initWorldGenSeed(seed);
        drawImageBiomes(1024, riverMix, "mixed");

        GenLayerTFC zoomed = GenLayerZoomTFC.magnify(1000L, riverMix, 2);
        zoomed = new GenLayerSmoothTFC(1001L, zoomed);
        zoomed.initWorldGenSeed(seed);
        drawImageBiomes(1024, zoomed, "zoomed");

        return new GenLayerTFC[] {riverMix, zoomed};
    }

    public static GenLayerTFC initializeRock(long seed, RockCategory.Layer level)
    {
        GenLayerTFC layer = new GenLayerRockInit(1L, level);
        layer = new GenLayerFuzzyZoomTFC(2000L, layer);
        layer = new GenLayerZoomTFC(2001L, layer);
        layer = new GenLayerZoomTFC(2002L, layer);
        layer = new GenLayerZoomTFC(2003L, layer);
        layer = new GenLayerSmoothTFC(1000L, layer);
        layer = new GenLayerZoomTFC(1000, layer);
        layer = new GenLayerZoomTFC(1001, layer);
        layer = new GenLayerZoomTFC(1002, layer);
        layer = new GenLayerZoomTFC(1003, layer);
        layer = new GenLayerZoomTFC(1004, layer);
        layer = new GenLayerSmoothTFC(1000L, layer);
        layer = new GenLayerVoronoiZoomTFC(10L, layer);
        layer.initWorldGenSeed(seed);
        drawImage(1024, layer, "rock" + level.name());
        return layer;
    }

    public static GenLayerTFC initializeStability(long seed)
    {
        GenLayerTFC continent = new GenLayerStabilityInit(1L + seed);
        continent = new GenLayerFuzzyZoomTFC(2000L, continent);
        continent = new GenLayerZoomTFC(2001L, continent);
        continent = new GenLayerZoomTFC(2002L, continent);
        continent = new GenLayerZoomTFC(2003L, continent);
        continent = GenLayerZoomTFC.magnify(1000L, continent, 2);
        continent = new GenLayerSmoothTFC(1000L, continent);
        continent = new GenLayerZoomTFC(1000, continent);
        continent = new GenLayerZoomTFC(1001, continent);
        continent = new GenLayerZoomTFC(1002, continent);
        continent = new GenLayerZoomTFC(1003, continent);
        continent = new GenLayerSmoothTFC(1000L, continent);
        continent = new GenLayerVoronoiZoomTFC(10L, continent);
        continent.initWorldGenSeed(seed);
        drawImage(1024, continent, "stability");
        return continent;
    }

    public static void drawImageBiomes(int size, GenLayerTFC genlayer, String name)
    {
        Function<Biome, Color> colorize = (x) -> x instanceof BiomeTFC ? ((BiomeTFC) x).debugColor : Color.BLACK;
        drawImage(size, genlayer, name, (i) -> colorize.apply(Biome.getBiomeForId(i)));
    }

    public static void drawImage(int size, GenLayerTFC genlayer, String name)
    {
        drawImage(size, genlayer, name, (i) -> COLORS[i % COLORS.length]);
    }

    public static void drawImage(int size, GenLayerTFC genlayer, String name, IntFunction<Color> gibColor)
    {
        if (!ConfigTFC.General.DEBUG.debugWorldGenSafe) return;
        if (FMLCommonHandler.instance().getEffectiveSide().isClient()) return;
        try
        {
            int[] ints = genlayer.getInts(-size / 2, -size / 2, size, size);
            BufferedImage outBitmap = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics = (Graphics2D) outBitmap.getGraphics();
            graphics.clearRect(0, 0, size, size);
            for (int x = 0; x < size; x++)
            {
                for (int z = 0; z < size; z++)
                {
                    int i = ints[x * size + z];
                    if (i == -1 || x == size / 2 || z == size / 2)
                    {
                        graphics.setColor(Color.WHITE);
                    }
                    else
                    {
                        graphics.setColor(gibColor.apply(i));
                    }
                    //noinspection SuspiciousNameCombination
                    graphics.drawRect(z, x, 1, 1);
                }
            }
            name = "_" + name + ".png";
            TerraFirmaCraft.getLog().info("Worldgen debug image {}", name);
            ImageIO.write(outBitmap, "PNG", new File(name));
        }
        catch (Exception e)
        {
            TerraFirmaCraft.getLog().catching(e);
        }
    }

    // Doing this lookup only once is quite a bit faster.
    protected final int oceanID = Biome.getIdForBiome(BiomesTFC.OCEAN);
    protected final int plainsID = Biome.getIdForBiome(BiomesTFC.PLAINS);
    protected final int highPlainsID = Biome.getIdForBiome(BiomesTFC.HIGH_PLAINS);
    protected final int deepOceanID = Biome.getIdForBiome(BiomesTFC.DEEP_OCEAN);
    protected final int lakeID = Biome.getIdForBiome(BiomesTFC.LAKE);
    protected final int riverID = Biome.getIdForBiome(BiomesTFC.RIVER);
    protected final int swamplandID = Biome.getIdForBiome(BiomesTFC.SWAMPLAND);
    protected final int highHillsID = Biome.getIdForBiome(BiomesTFC.HIGH_HILLS);
    protected final int highHillsEdgeID = Biome.getIdForBiome(BiomesTFC.HIGH_HILLS_EDGE);
    protected final int beachID = Biome.getIdForBiome(BiomesTFC.BEACH);
    protected final int gravelBeachID = Biome.getIdForBiome(BiomesTFC.GRAVEL_BEACH);
    protected final int mountainsID = Biome.getIdForBiome(BiomesTFC.MOUNTAINS);
    protected final int mountainsEdgeID = Biome.getIdForBiome(BiomesTFC.MOUNTAINS_EDGE);

    public GenLayerTFC(long seed)
    {
        super(seed);
    }

    public boolean isOceanicBiome(int id)
    {
        return oceanID == id || deepOceanID == id;
    }

    public boolean isMountainBiome(int id)
    {
        return mountainsID == id || mountainsEdgeID == id;
    }

    public boolean isBeachBiome(int id)
    {
        return beachID == id || gravelBeachID == id;
    }
}
