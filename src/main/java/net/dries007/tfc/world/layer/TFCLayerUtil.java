/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.layer;

import java.util.ArrayList;
import java.util.List;
import java.util.function.LongFunction;
import java.util.function.Supplier;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.IExtendedNoiseRandom;
import net.minecraft.world.gen.LazyAreaLayerContext;
import net.minecraft.world.gen.area.IArea;
import net.minecraft.world.gen.area.IAreaFactory;
import net.minecraft.world.gen.area.LazyArea;
import net.minecraft.world.gen.layer.SmoothLayer;
import net.minecraft.world.gen.layer.ZoomLayer;
import net.minecraft.world.gen.layer.traits.IAreaTransformer1;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistry;

import imageutil.Images;
import net.dries007.tfc.world.TFCGenerationSettings;
import net.dries007.tfc.world.biome.TFCBiomes;

import static net.dries007.tfc.world.layer.LayerDrawingUtil.IMAGES;

public class TFCLayerUtil
{
    /* Cheeky hacks to avoid classloading some vanilla classes. this WILL BREAK EVERYTHING */
    public static boolean isDebugMode = false;
    public static int id = 0;

    /* Biomes */
    public static final int OCEAN = isDebugMode ? id++ : getId(TFCBiomes.OCEAN);
    public static final int DEEP_OCEAN = isDebugMode ? id++ : getId(TFCBiomes.DEEP_OCEAN);
    public static final int DEEP_OCEAN_RIDGE = isDebugMode ? id++ : getId(TFCBiomes.DEEP_OCEAN_RIDGE);
    public static final int PLAINS = isDebugMode ? id++ : getId(TFCBiomes.PLAINS);
    public static final int HILLS = isDebugMode ? id++ : getId(TFCBiomes.HILLS);
    public static final int LOWLANDS = isDebugMode ? id++ : getId(TFCBiomes.LOWLANDS);
    public static final int LOW_CANYONS = isDebugMode ? id++ : getId(TFCBiomes.LOW_CANYONS);
    public static final int ROLLING_HILLS = isDebugMode ? id++ : getId(TFCBiomes.ROLLING_HILLS);
    public static final int BADLANDS = isDebugMode ? id++ : getId(TFCBiomes.BADLANDS);
    public static final int PLATEAU = isDebugMode ? id++ : getId(TFCBiomes.PLATEAU);
    public static final int OLD_MOUNTAINS = isDebugMode ? id++ : getId(TFCBiomes.OLD_MOUNTAINS);
    public static final int MOUNTAINS = isDebugMode ? id++ : getId(TFCBiomes.MOUNTAINS);
    public static final int FLOODED_MOUNTAINS = isDebugMode ? id++ : getId(TFCBiomes.FLOODED_MOUNTAINS);
    public static final int CANYONS = isDebugMode ? id++ : getId(TFCBiomes.CANYONS);
    public static final int SHORE = isDebugMode ? id++ : getId(TFCBiomes.SHORE);
    public static final int STONE_SHORE = isDebugMode ? id++ : getId(TFCBiomes.STONE_SHORE);
    public static final int MOUNTAINS_EDGE = isDebugMode ? id++ : getId(TFCBiomes.MOUNTAINS_EDGE);
    public static final int LAKE = isDebugMode ? id++ : getId(TFCBiomes.LAKE);
    public static final int RIVER = isDebugMode ? id++ : getId(TFCBiomes.RIVER);

    public static IAreaFactory<LazyArea> createOverworldBiomeLayer(long seed, TFCGenerationSettings settings)
    {
        LongFunction<LazyAreaLayerContext> contextFactory = seedModifier -> new LazyAreaLayerContext(25, seed, seedModifier);

        IAreaFactory<LazyArea> mainLayer, riverLayer;
        int layerCount = 0;

        IMAGES.color(LayerDrawingUtil::landColor).size(20);

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

        IMAGES.color(LayerDrawingUtil::elevationColor);
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
        IMAGES.size(640).color(LayerDrawingUtil::riverColor).draw("layer_river_" + ++layerCount, riverLayer, 0, 0, -320, -320, 320, 320);

        riverLayer = ZoomLayer.NORMAL.apply(contextFactory.apply(1019L), riverLayer);
        IMAGES.draw("layer_river_" + ++layerCount, riverLayer, 0, 0, -80, -80, 80, 80);

        // Elevation Mapping => Biomes

        IMAGES.color(LayerDrawingUtil::biomeColor);
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

        return mainLayer;
    }

    public static IAreaFactory<LazyArea> createOverworldRockLayers(long seed, TFCGenerationSettings settings)
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

        seedLayer = ZoomLayer.NORMAL.apply(contextFactory.apply(1003L), seedLayer);
        IMAGES.size(1280).draw("layer_seed" + ++layerCount, seedLayer, 0, 20, -640, -640, 640, 640);

        return seedLayer;
    }

    public static <A extends IArea, C extends IExtendedNoiseRandom<A>> IAreaFactory<A> repeat(IAreaTransformer1 transformer, int count, IAreaFactory<A> originalLayer, Supplier<C> contextSupplier)
    {
        IAreaFactory<A> newFactory = originalLayer;
        for (int i = 0; i < count; ++i)
        {
            newFactory = transformer.apply(contextSupplier.get(), newFactory);
        }
        return newFactory;
    }

    public static <A extends IArea, C extends IExtendedNoiseRandom<A>> IAreaFactory<A> repeat(IAreaTransformer1 transformer, int count, IAreaFactory<A> originalLayer, LongFunction<C> contextFactory, long seed)
    {
        IAreaFactory<A> newFactory = originalLayer;
        for (int i = 0; i < count; ++i)
        {
            newFactory = transformer.apply(contextFactory.apply(seed + i), newFactory);
        }
        return newFactory;
    }

    static boolean isShoreCompatible(int value)
    {
        return value != LOWLANDS && value != LOW_CANYONS && value != CANYONS;
    }

    static boolean isLakeCompatible(int value)
    {
        return isLow(value) || value == CANYONS || value == ROLLING_HILLS;
    }

    static boolean isOcean(int value)
    {
        return value == OCEAN || value == DEEP_OCEAN || value == DEEP_OCEAN_RIDGE;
    }

    static boolean isShallowOcean(int value)
    {
        return value == OCEAN;
    }

    static boolean isMountains(int value)
    {
        return value == MOUNTAINS || value == FLOODED_MOUNTAINS || value == MOUNTAINS_EDGE || value == OLD_MOUNTAINS;
    }

    static boolean isLow(int value)
    {
        return value == PLAINS || value == HILLS || value == LOW_CANYONS || value == LOWLANDS;
    }

    private static <T extends Biome> int getId(RegistryObject<T> biome)
    {
        return ((ForgeRegistry<Biome>) ForgeRegistries.BIOMES).getID(biome.get());
    }
}
