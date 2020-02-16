/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package test;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.LongFunction;

import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.LazyAreaLayerContext;
import net.minecraft.world.gen.area.IArea;
import net.minecraft.world.gen.area.IAreaFactory;
import net.minecraft.world.gen.area.LazyArea;
import net.minecraft.world.gen.layer.SmoothLayer;
import net.minecraft.world.gen.layer.VoroniZoomLayer;
import net.minecraft.world.gen.layer.ZoomLayer;
import net.minecraft.world.gen.layer.traits.IAreaTransformer0;

import imageutil.Images;
import net.dries007.tfc.world.gen.layer.*;
import net.dries007.tfc.world.gen.rock.RockCategory;

import static net.dries007.tfc.world.gen.layer.TFCLayerUtil.*;

/**
 * Test class for overworld biome layer generation
 */
@SuppressWarnings("ALL")
public class LayerTests
{
    public static final Images<IAreaFactory<LazyArea>> IMAGES = Images.get(af -> {
        IArea area = af.make();
        return (x, y) -> area.getValue((int) x, (int) y);
    });
    public static boolean isTestMode = false;

    static
    {
        IMAGES.size(1000).color(Images.Colors.LINEAR_GRAY).disable();
    }

    public static void main(String[] args)
    {
        isTestMode = true;
        IMAGES.enable();

        boolean testBiomes = false, testRocks = true, findSpawnBiomes = false, drawHugeArea = false;

        long seed = System.currentTimeMillis();

        if (testBiomes)
        {
            List<IAreaFactory<LazyArea>> layers = generateOverworldLayers(seed, new FakeSettings());

            if (findSpawnBiomes)
            {
                LazyArea actual = layers.get(1).make();
                System.out.println("Biome at (0, 0): " + biomeName(actual.getValue(0, 0)));
                System.out.println("Biome at (-256, 0): " + biomeName(actual.getValue(-256, 0)));
                System.out.println("Biome at (256, 0): " + biomeName(actual.getValue(256, 0)));
                System.out.println("Biome at (0, -256): " + biomeName(actual.getValue(0, -256)));
                System.out.println("Biome at (0, 256): " + biomeName(actual.getValue(0, 256)));
            }

            if (drawHugeArea)
            {
                IMAGES.draw("biomes_actual_10km", layers.get(0), 0, 0, -5000, -5000, 5000, 5000);
            }
        }

        if (testRocks)
        {
            List<IAreaFactory<LazyArea>> layers = generateSeedLayers(seed, new FakeSettings());

            if (drawHugeArea)
            {
                IMAGES.draw("rocks_actual_10km", layers.get(0), 0, 0, -5000, -5000, 5000, 5000);
            }
        }
    }

    public static List<IAreaFactory<LazyArea>> generateOverworldLayers(long seed, FakeSettings settings)
    {
        LongFunction<LazyAreaLayerContext> contextFactory = seedModifier -> new LazyAreaLayerContext(25, seed, seedModifier);

        IAreaFactory<LazyArea> mainLayer, riverLayer;
        int layerCount = 0;

        IMAGES.color(LayerTests::landColor).size(20);

        // Ocean / Continents

        mainLayer = new IslandLayer(settings.getIslandFrequency()).apply(contextFactory.apply(1000L));
        IMAGES.draw("layer_land_" + ++layerCount, mainLayer, 0, 0, -10, -10, 10, 10);

        mainLayer = ZoomLayer.FUZZY.apply(contextFactory.apply(1001L), mainLayer);
        IMAGES.size(40).draw("layer_land_" + ++layerCount, mainLayer, 0, 0, -20, -20, 20, 20);

        mainLayer = AddIslandLayer.NORMAL.apply(contextFactory.apply(1002L), mainLayer);
        IMAGES.draw("layer_land_" + ++layerCount, mainLayer, 0, 0, -20, -20, 20, 20);

        mainLayer = ZoomLayer.NORMAL.apply(contextFactory.apply(1003L), mainLayer);
        IMAGES.size(80).draw("layer_land_" + ++layerCount, mainLayer, 0, 0, -40, -40, 40, 40);

        mainLayer = AddIslandLayer.NORMAL.apply(contextFactory.apply(1004L), mainLayer);
        IMAGES.draw("layer_land_" + ++layerCount, mainLayer, 0, 0, -40, -40, 40, 40);

        for (int i = 0; i < 2; i++)
        {
            mainLayer = AddIslandLayer.HEAVY.apply(contextFactory.apply(1005L + 2 * i), mainLayer);
            IMAGES.draw("layer_land_" + ++layerCount, mainLayer, 0, 0, -40, -40, 40, 40);

            mainLayer = SmoothLayer.INSTANCE.apply(contextFactory.apply(1006L + 2 * i), mainLayer);
            IMAGES.draw("layer_land_" + ++layerCount, mainLayer, 0, 0, -40, -40, 40, 40);
        }

        // Oceans and Continents => Elevation Mapping

        IMAGES.color(LayerTests::elevationColor);
        layerCount = 0;

        mainLayer = ElevationLayer.INSTANCE.apply(contextFactory.apply(1009L), mainLayer);
        IMAGES.draw("layer_elevation_" + ++layerCount, mainLayer, 0, 0, -40, -40, 40, 40);

        mainLayer = ZoomLayer.NORMAL.apply(contextFactory.apply(1010L), mainLayer);
        IMAGES.size(160).draw("layer_elevation_" + ++layerCount, mainLayer, -80, -80, -80, -80, 80, 80);

        // Elevation Mapping => Rivers
        layerCount = 0;

        riverLayer = ZoomLayer.NORMAL.apply(contextFactory.apply(1011L), mainLayer);
        IMAGES.draw("layer_river_" + ++layerCount, riverLayer, 0, 0, -80, -80, 80, 80);

        for (int i = 0; i < 6; i++)
        {
            riverLayer = ZoomLayer.NORMAL.apply(contextFactory.apply(1012L + i), riverLayer);
            IMAGES.draw("layer_river_" + ++layerCount, riverLayer, 0, 0, -80, -80, 80, 80);
        }

        riverLayer = RiverLayer.INSTANCE.apply(contextFactory.apply(1018L), riverLayer);
        IMAGES.size(640).color(LayerTests::riverColor).draw("layer_river_" + ++layerCount, riverLayer, 0, 0, -320, -320, 320, 320);

        riverLayer = ZoomLayer.NORMAL.apply(contextFactory.apply(1019L), riverLayer);
        IMAGES.draw("layer_river_" + ++layerCount, riverLayer, 0, 0, -80, -80, 80, 80);

        // Elevation Mapping => Biomes

        IMAGES.color(LayerTests::biomeColor);
        layerCount = 0;

        mainLayer = BiomeLayer.INSTANCE.apply(contextFactory.apply(1011L), mainLayer);
        IMAGES.size(160).draw("layer_biome_" + ++layerCount, mainLayer, 0, 0, -80, -80, 80, 80);

        mainLayer = ZoomLayer.NORMAL.apply(contextFactory.apply(1012L), mainLayer);
        IMAGES.size(320).draw("layer_biome_" + ++layerCount, mainLayer, 0, 0, -160, -160, 160, 160);

        mainLayer = AddIslandLayer.NORMAL.apply(contextFactory.apply(1013L), mainLayer);
        IMAGES.draw("layer_biome_" + ++layerCount, mainLayer, 0, 0, -160, -160, 160, 160);

        mainLayer = ZoomLayer.NORMAL.apply(contextFactory.apply(1014L), mainLayer);
        IMAGES.size(640).draw("layer_biome_" + ++layerCount, mainLayer, 0, 0, -320, -320, 320, 320);

        mainLayer = RemoveOceanLayer.INSTANCE.apply(contextFactory.apply(1015L), mainLayer);
        IMAGES.draw("layer_biome_" + ++layerCount, mainLayer, 0, 0, -320, -320, 320, 320);

        mainLayer = OceanLayer.INSTANCE.apply(contextFactory.apply(1016L), mainLayer);
        IMAGES.draw("layer_biome_" + ++layerCount, mainLayer, 0, 0, -320, -320, 320, 320);

        mainLayer = EdgeBiomeLayer.INSTANCE.apply(contextFactory.apply(1017L), mainLayer);
        IMAGES.draw("layer_biome_" + ++layerCount, mainLayer, 0, 0, -320, -320, 320, 320);

        mainLayer = AddLakeLayer.INSTANCE.apply(contextFactory.apply(1018L), mainLayer);
        IMAGES.draw("layer_biome_" + ++layerCount, mainLayer, 0, 0, -320, -320, 320, 320);

        for (int i = 0; i < settings.getBiomeZoomLevel(); i++)
        {
            mainLayer = ZoomLayer.NORMAL.apply(contextFactory.apply(1019L), mainLayer);
            IMAGES.draw("layer_biome_" + ++layerCount, mainLayer, 0, 0, -320, -320, 320, 320);
        }

        mainLayer = ShoreLayer.INSTANCE.apply(contextFactory.apply(1023L), mainLayer);
        IMAGES.draw("layer_biome_" + ++layerCount, mainLayer, 0, 0, -320, -320, 320, 320);

        for (int i = 0; i < 2; i++)
        {
            mainLayer = ZoomLayer.NORMAL.apply(contextFactory.apply(1024L), mainLayer);
            IMAGES.draw("layer_biome_" + ++layerCount, mainLayer, 0, 0, -320, -320, 320, 320);
        }

        mainLayer = SmoothLayer.INSTANCE.apply(contextFactory.apply(1025L), mainLayer);
        IMAGES.draw("layer_biome_" + ++layerCount, mainLayer, 0, 0, -320, -320, 320, 320);

        mainLayer = MixRiverLayer.INSTANCE.apply(contextFactory.apply(1026L), mainLayer, riverLayer);
        IMAGES.draw("layer_biome_" + ++layerCount, mainLayer, 0, 0, -320, -320, 320, 320);

        mainLayer = BiomeRiverWidenLayer.MEDIUM.apply(contextFactory.apply(1027L), mainLayer);
        IMAGES.draw("layer_biome_" + ++layerCount, mainLayer, 0, 0, -320, -320, 320, 320);

        mainLayer = BiomeRiverWidenLayer.LOW.apply(contextFactory.apply(1028L), mainLayer);
        IMAGES.draw("layer_biome_" + ++layerCount, mainLayer, 0, 0, -320, -320, 320, 320);

        IAreaFactory<LazyArea> areaFactoryActual = VoroniZoomLayer.INSTANCE.apply(contextFactory.apply(1029L), mainLayer);
        IMAGES.size(1280).color(Images.Colors.DISCRETE_20).draw("layer_biome_" + ++layerCount, areaFactoryActual, 0, 0, -640, -640, 640, 640);

        return Arrays.asList(mainLayer, areaFactoryActual);
    }

    public static List<IAreaFactory<LazyArea>> generateSeedLayers(long seed, FakeSettings settings)
    {
        LongFunction<LazyAreaLayerContext> contextFactory = seedModifier -> new LazyAreaLayerContext(25, seed, seedModifier);

        IAreaFactory<LazyArea> seedLayer, biomeLayer;
        List<IAreaFactory<LazyArea>> completedLayers = new ArrayList<>(3);
        int layerCount = 0;

        Images.ColorMap moduloDiscrete20 = (v, m, x) -> Images.Colors.DISCRETE_20.apply(v / Integer.MAX_VALUE * 4, 1, 0);
        IMAGES.color(moduloDiscrete20);

        // Seed Areas
        seedLayer = RandomLayer.INSTANCE.apply(contextFactory.apply(1000L));
        IMAGES.size(32).draw("layer_seed" + ++layerCount, seedLayer, 0, 20, -16, -16, 16, 16);

        IMAGES.size(640);
        for (int i = 0; i < 3; i++)
        {
            seedLayer = ZoomLayer.NORMAL.apply(contextFactory.apply(1001L), seedLayer);
            IMAGES.draw("layer_seed" + ++layerCount, seedLayer, 0, 20, -320, -320, 320, 320);

            seedLayer = SmoothLayer.INSTANCE.apply(contextFactory.apply(1001L), seedLayer);
            IMAGES.draw("layer_seed" + ++layerCount, seedLayer, 0, 20, -320, -320, 320, 320);
        }

        for (int i = 0; i < 5; i++)
        {
            seedLayer = ZoomLayer.NORMAL.apply(contextFactory.apply(1001L), seedLayer);
            IMAGES.draw("layer_seed" + ++layerCount, seedLayer, 0, 20, -320, -320, 320, 320);
        }

        seedLayer = new ModuloLayer(20).apply(contextFactory.apply(1003L), seedLayer);
        IMAGES.color(Images.Colors.DISCRETE_20);

        seedLayer = VoroniZoomLayer.INSTANCE.apply(contextFactory.apply(1003L), seedLayer);
        IMAGES.size(1280).draw("layer_seed" + ++layerCount, seedLayer, 0, 20, -640, -640, 640, 640);

        return null;
    }

    public static Color biomeColor(double val, double min, double max)
    {
        int biome = (int) Math.round(val);
        if (biome == OCEAN) return new Color(0, 0, 255);
        if (biome == DEEP_OCEAN) return new Color(0, 0, 180);
        if (biome == DEEP_OCEAN_RIDGE) return new Color(0, 0, 120);

        if (biome == PLAINS) return new Color(0, 230, 120);
        if (biome == HILLS) return new Color(0, 180, 20);
        if (biome == LOWLANDS) return new Color(160, 200, 120);
        if (biome == LOW_CANYONS) return new Color(200, 100, 0);

        if (biome == ROLLING_HILLS) return new Color(0, 160, 0);
        if (biome == BADLANDS) return new Color(255, 160, 0);
        if (biome == PLATEAU) return new Color(240, 150, 100);
        if (biome == OLD_MOUNTAINS) return new Color(140, 170, 140);

        if (biome == MOUNTAINS) return new Color(140, 140, 140);
        if (biome == FLOODED_MOUNTAINS) return new Color(110, 110, 110);
        if (biome == CANYONS) return new Color(160, 60, 0);

        if (biome == SHORE) return new Color(230, 210, 100);
        if (biome == STONE_SHORE) return new Color(210, 190, 80);

        if (biome == MOUNTAINS_EDGE) return new Color(180, 180, 180);
        if (biome == LAKE) return new Color(0, 100, 255);
        if (biome == RIVER) return new Color(0, 200, 255);
        return Color.BLACK;
    }

    public static Color landColor(double val, double min, double max)
    {
        int i = (int) Math.round(val);
        if (i == PLAINS) return Color.GREEN;
        if (i == DEEP_OCEAN) return Color.BLUE;
        return Color.BLACK;
    }

    public static Color riverColor(double val, double min, double max)
    {
        int i = (int) Math.round(val);
        if (i == RIVER) return Color.CYAN;
        return Color.BLACK;
    }

    public static Color elevationColor(double val, double min, double max)
    {
        int i = (int) Math.round(val);
        if (i == PLAINS) return new Color(100, 200, 100);
        if (i == HILLS) return new Color(255, 200, 0);
        if (i == MOUNTAINS) return new Color(255, 100, 0);
        if (i == DEEP_OCEAN) return Color.BLUE;
        return Color.BLACK;
    }

    public static Color rockCategoryColor(double val, double min, double max)
    {
        int i = (int) Math.round(val);
        if (i == 0) return Color.BLUE;
        if (i == 1) return Color.CYAN;
        if (i == 2) return Color.RED;
        if (i == 3) return Color.ORANGE;
        return Color.BLACK;
    }

    public static Color rockColor(double val, double min, double max)
    {
        return Images.Colors.DISCRETE_20.apply(val, 0, Stone.values().length);
    }

    public static String biomeName(int biome)
    {
        if (biome == OCEAN) return "OCEAN";
        if (biome == DEEP_OCEAN) return "DEEP_OCEAN";
        if (biome == DEEP_OCEAN_RIDGE) return "DEEP_OCEAN_RIDGE";

        if (biome == PLAINS) return "PLAINS";
        if (biome == HILLS) return "HILLS";
        if (biome == LOWLANDS) return "LOWLANDS";
        if (biome == LOW_CANYONS) return "LOW_CANYONS";

        if (biome == ROLLING_HILLS) return "ROLLING_HILLS";
        if (biome == BADLANDS) return "BADLANDS";
        if (biome == PLATEAU) return "PLATEAU";
        if (biome == OLD_MOUNTAINS) return "OLD_MOUNTAINS";

        if (biome == MOUNTAINS) return "MOUNTAINS";
        if (biome == FLOODED_MOUNTAINS) return "FLOODED_MOUNTAINS";
        if (biome == CANYONS) return "CANYONS";

        if (biome == SHORE) return "SHORE";
        if (biome == STONE_SHORE) return "STONE_SHORE";

        if (biome == MOUNTAINS_EDGE) return "MOUNTAINS_EDGE";
        if (biome == LAKE) return "LAKE";
        return "UNKNOWN";
    }

    enum Stone
    {
        GRANITE(RockCategory.IGNEOUS_INTRUSIVE),
        DIORITE(RockCategory.IGNEOUS_INTRUSIVE),
        GABBRO(RockCategory.IGNEOUS_INTRUSIVE),
        SHALE(RockCategory.SEDIMENTARY),
        CLAYSTONE(RockCategory.SEDIMENTARY),
        ROCKSALT(RockCategory.SEDIMENTARY),
        LIMESTONE(RockCategory.SEDIMENTARY),
        CONGLOMERATE(RockCategory.SEDIMENTARY),
        DOLOMITE(RockCategory.SEDIMENTARY),
        CHERT(RockCategory.SEDIMENTARY),
        CHALK(RockCategory.SEDIMENTARY),
        RHYOLITE(RockCategory.IGNEOUS_EXTRUSIVE),
        BASALT(RockCategory.IGNEOUS_EXTRUSIVE),
        ANDESITE(RockCategory.IGNEOUS_EXTRUSIVE),
        DACITE(RockCategory.IGNEOUS_EXTRUSIVE),
        QUARTZITE(RockCategory.METAMORPHIC),
        SLATE(RockCategory.METAMORPHIC),
        PHYLLITE(RockCategory.METAMORPHIC),
        SCHIST(RockCategory.METAMORPHIC),
        GNEISS(RockCategory.METAMORPHIC),
        MARBLE(RockCategory.METAMORPHIC);

        private final RockCategory cat;

        Stone(RockCategory cat)
        {
            this.cat = cat;
        }
    }

    enum InitRockLayer implements IAreaTransformer0
    {
        INSTANCE;

        @Override
        public int apply(INoiseRandom context, int x, int z)
        {
            return context.random(20);
        }
    }

    static class FakeSettings
    {
        public int getIslandFrequency()
        {
            return 6;
        }

        public int getBiomeZoomLevel()
        {
            return 5;
        }
    }
}
