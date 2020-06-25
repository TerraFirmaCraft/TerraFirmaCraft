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
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistry;

import net.dries007.tfc.objects.types.RockManager;
import net.dries007.tfc.world.TFCGenerationSettings;
import net.dries007.tfc.world.biome.TFCBiomes;

public class TFCLayerUtil
{
    public static boolean DEBUG = false;
    public static int ID = 0;


    /* Biomes */
    public static final int OCEAN = DEBUG ? ID++ : getId(TFCBiomes.OCEAN);
    public static final int DEEP_OCEAN = DEBUG ? ID++ : getId(TFCBiomes.DEEP_OCEAN);
    public static final int DEEP_OCEAN_RIDGE = DEBUG ? ID++ : getId(TFCBiomes.DEEP_OCEAN_RIDGE);
    public static final int PLAINS = DEBUG ? ID++ : getId(TFCBiomes.PLAINS);
    public static final int HILLS = DEBUG ? ID++ : getId(TFCBiomes.HILLS);
    public static final int LOWLANDS = DEBUG ? ID++ : getId(TFCBiomes.LOWLANDS);
    public static final int LOW_CANYONS = DEBUG ? ID++ : getId(TFCBiomes.LOW_CANYONS);
    public static final int ROLLING_HILLS = DEBUG ? ID++ : getId(TFCBiomes.ROLLING_HILLS);
    public static final int BADLANDS = DEBUG ? ID++ : getId(TFCBiomes.BADLANDS);
    public static final int PLATEAU = DEBUG ? ID++ : getId(TFCBiomes.PLATEAU);
    public static final int OLD_MOUNTAINS = DEBUG ? ID++ : getId(TFCBiomes.OLD_MOUNTAINS);
    public static final int MOUNTAINS = DEBUG ? ID++ : getId(TFCBiomes.MOUNTAINS);
    public static final int FLOODED_MOUNTAINS = DEBUG ? ID++ : getId(TFCBiomes.FLOODED_MOUNTAINS);
    public static final int CANYONS = DEBUG ? ID++ : getId(TFCBiomes.CANYONS);
    public static final int SHORE = DEBUG ? ID++ : getId(TFCBiomes.SHORE);
    public static final int STONE_SHORE = DEBUG ? ID++ : getId(TFCBiomes.STONE_SHORE);
    public static final int MOUNTAINS_EDGE = DEBUG ? ID++ : getId(TFCBiomes.MOUNTAINS_EDGE);
    public static final int LAKE = DEBUG ? ID++ : getId(TFCBiomes.LAKE);
    public static final int RIVER = DEBUG ? ID++ : getId(TFCBiomes.RIVER);

    public static IAreaFactory<LazyArea> createOverworldBiomeLayer(long seed, TFCGenerationSettings settings)
    {
        LongFunction<LazyAreaLayerContext> contextFactory = seedModifier -> new LazyAreaLayerContext(25, seed, seedModifier);

        IAreaFactory<LazyArea> mainLayer, riverLayer;

        // Ocean / Continents

        mainLayer = new IslandLayer(settings.getIslandFrequency()).apply(contextFactory.apply(1000L));
        mainLayer = ZoomLayer.FUZZY.apply(contextFactory.apply(1001L), mainLayer);
        mainLayer = AddIslandLayer.NORMAL.apply(contextFactory.apply(1002L), mainLayer);
        mainLayer = ZoomLayer.NORMAL.apply(contextFactory.apply(1003L), mainLayer);
        mainLayer = AddIslandLayer.NORMAL.apply(contextFactory.apply(1004L), mainLayer);

        for (int i = 0; i < 2; i++)
        {
            mainLayer = AddIslandLayer.HEAVY.apply(contextFactory.apply(1005L + 2 * i), mainLayer);
            mainLayer = SmoothLayer.INSTANCE.apply(contextFactory.apply(1006L + 2 * i), mainLayer);
        }

        // Oceans and Continents => Elevation Mapping
        mainLayer = ElevationLayer.INSTANCE.apply(contextFactory.apply(1009L), mainLayer);
        mainLayer = ZoomLayer.NORMAL.apply(contextFactory.apply(1010L), mainLayer);

        // Elevation Mapping => Rivers
        riverLayer = ZoomLayer.NORMAL.apply(contextFactory.apply(1011L), mainLayer);

        for (int i = 0; i < 6; i++)
        {
            riverLayer = ZoomLayer.NORMAL.apply(contextFactory.apply(1012L + i), riverLayer);
        }

        riverLayer = RiverLayer.INSTANCE.apply(contextFactory.apply(1018L), riverLayer);
        riverLayer = ZoomLayer.NORMAL.apply(contextFactory.apply(1019L), riverLayer);

        // Elevation Mapping => Biomes
        mainLayer = BiomeLayer.INSTANCE.apply(contextFactory.apply(1011L), mainLayer);
        mainLayer = ZoomLayer.NORMAL.apply(contextFactory.apply(1012L), mainLayer);
        mainLayer = AddIslandLayer.NORMAL.apply(contextFactory.apply(1013L), mainLayer);
        mainLayer = ZoomLayer.NORMAL.apply(contextFactory.apply(1014L), mainLayer);
        mainLayer = RemoveOceanLayer.INSTANCE.apply(contextFactory.apply(1015L), mainLayer);
        mainLayer = OceanLayer.INSTANCE.apply(contextFactory.apply(1016L), mainLayer);
        mainLayer = EdgeBiomeLayer.INSTANCE.apply(contextFactory.apply(1017L), mainLayer);
        mainLayer = AddLakeLayer.INSTANCE.apply(contextFactory.apply(1018L), mainLayer);

        for (int i = 0; i < settings.getBiomeZoomLevel(); i++)
        {
            mainLayer = ZoomLayer.NORMAL.apply(contextFactory.apply(1019L), mainLayer);
        }

        mainLayer = ShoreLayer.INSTANCE.apply(contextFactory.apply(1023L), mainLayer);

        for (int i = 0; i < 2; i++)
        {
            mainLayer = ZoomLayer.NORMAL.apply(contextFactory.apply(1024L), mainLayer);
        }

        mainLayer = SmoothLayer.INSTANCE.apply(contextFactory.apply(1025L), mainLayer);
        mainLayer = MixRiverLayer.INSTANCE.apply(contextFactory.apply(1026L), mainLayer, riverLayer);
        mainLayer = BiomeRiverWidenLayer.MEDIUM.apply(contextFactory.apply(1027L), mainLayer);
        mainLayer = BiomeRiverWidenLayer.LOW.apply(contextFactory.apply(1028L), mainLayer);
        return mainLayer;
    }

    public static List<IAreaFactory<LazyArea>> createOverworldRockLayers(long seed, TFCGenerationSettings settings)
    {
        LongFunction<LazyAreaLayerContext> contextFactory = seedModifier -> new LazyAreaLayerContext(25, seed, seedModifier);

        List<IAreaFactory<LazyArea>> completedLayers = new ArrayList<>(3);
        IAreaFactory<LazyArea> seedLayer;
        int layerCount = 0;

        int numRocks = RockManager.INSTANCE.getKeys().size();

        // Seed Areas
        for (int j = 0; j < 3; j++)
        {
            RandomLayer randomLayer = new RandomLayer(numRocks);
            RockManager.INSTANCE.addCallback(() -> randomLayer.setLimit(RockManager.INSTANCE.getKeys().size()));
            seedLayer = randomLayer.apply(contextFactory.apply(1000L));

            // The following results were obtained about the number of applications of this layer. (over 10 M samples each time)
            // None => 95.01% of adjacent pairs were equal (which lines up pretty good with theoretical predictions)
            // 1x => 98.49%
            // 2x => 99.42%
            // 3x => 99.54%
            // 4x => 99.55%
            // And thus we only apply once, as it's the best result to reduce adjacent pairs without too much effort / performance cost
            RandomizeNeighborsLayer randomNeighborLayer = new RandomizeNeighborsLayer(numRocks);
            RockManager.INSTANCE.addCallback(() -> randomNeighborLayer.setLimit(RockManager.INSTANCE.getKeys().size()));
            seedLayer = randomNeighborLayer.apply(contextFactory.apply(1001L), seedLayer);

            for (int i = 0; i < 2; i++)
            {
                seedLayer = ExactZoomLayer.INSTANCE.apply(contextFactory.apply(1001L), seedLayer);
                seedLayer = ZoomLayer.NORMAL.apply(contextFactory.apply(1001L), seedLayer);
                seedLayer = SmoothLayer.INSTANCE.apply(contextFactory.apply(1001L), seedLayer);
            }

            for (int i = 0; i < settings.getRockZoomLevel(j); i++)
            {
                seedLayer = ZoomLayer.NORMAL.apply(contextFactory.apply(1001L), seedLayer);
            }

            completedLayers.add(seedLayer);
        }
        return completedLayers;
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

    private static <T extends Biome> int getId(Supplier<T> biome)
    {
        return ((ForgeRegistry<Biome>) ForgeRegistries.BIOMES).getID(biome.get());
    }
}
