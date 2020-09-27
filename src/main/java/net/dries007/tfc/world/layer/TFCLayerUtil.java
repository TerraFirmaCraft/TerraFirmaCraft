/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.layer;

import java.util.ArrayList;
import java.util.List;
import java.util.function.LongFunction;
import java.util.function.Supplier;

import net.minecraft.util.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.LazyAreaLayerContext;
import net.minecraft.world.gen.area.IAreaFactory;
import net.minecraft.world.gen.area.LazyArea;
import net.minecraft.world.gen.layer.SmoothLayer;
import net.minecraft.world.gen.layer.ZoomLayer;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistry;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.dries007.tfc.common.types.RockManager;
import net.dries007.tfc.world.biome.BiomeVariants;
import net.dries007.tfc.world.biome.TFCBiomes;

public class TFCLayerUtil
{
    static final Int2ObjectMap<BiomeVariants> REGISTRY = new Int2ObjectOpenHashMap<>();
    private static int ID = -1;

    /**
     * These are the int IDs that are used for layer generation
     * They are mapped to registry keys here
     * When the biome is requested, the json biome is requested
     */
    public static final int OCEAN = makeLayerId(TFCBiomes.OCEAN);
    public static final int DEEP_OCEAN = makeLayerId(TFCBiomes.DEEP_OCEAN);
    public static final int PLAINS = makeLayerId(TFCBiomes.PLAINS);
    public static final int HILLS = makeLayerId(TFCBiomes.HILLS);
    public static final int LOWLANDS = makeLayerId(TFCBiomes.LOWLANDS);
    public static final int LOW_CANYONS = makeLayerId(TFCBiomes.LOW_CANYONS);
    public static final int ROLLING_HILLS = makeLayerId(TFCBiomes.ROLLING_HILLS);
    public static final int BADLANDS = makeLayerId(TFCBiomes.BADLANDS);
    public static final int PLATEAU = makeLayerId(TFCBiomes.PLATEAU);
    public static final int OLD_MOUNTAINS = makeLayerId(TFCBiomes.OLD_MOUNTAINS);
    public static final int MOUNTAINS = makeLayerId(TFCBiomes.MOUNTAINS);
    public static final int FLOODED_MOUNTAINS = makeLayerId(TFCBiomes.FLOODED_MOUNTAINS);
    public static final int CANYONS = makeLayerId(TFCBiomes.CANYONS);
    public static final int SHORE = makeLayerId(TFCBiomes.SHORE);
    public static final int LAKE = makeLayerId(TFCBiomes.LAKE);
    public static final int RIVER = makeLayerId(TFCBiomes.RIVER);

    public static BiomeVariants getFromLayerId(int id)
    {
        return REGISTRY.get(id);
    }

    public static IAreaFactory<LazyArea> createOverworldBiomeLayer(long seed, int landFrequency, int biomeSize)
    {
        LongFunction<LazyAreaLayerContext> contextFactory = seedModifier -> new LazyAreaLayerContext(25, seed, seedModifier);
        IAreaFactory<LazyArea> mainLayer, riverLayer;

        // Ocean / Continents

        mainLayer = new IslandLayer(landFrequency).run(contextFactory.apply(1000L));
        mainLayer = ZoomLayer.FUZZY.run(contextFactory.apply(1001L), mainLayer);
        mainLayer = AddIslandLayer.NORMAL.run(contextFactory.apply(1002L), mainLayer);
        mainLayer = ZoomLayer.NORMAL.run(contextFactory.apply(1003L), mainLayer);
        mainLayer = AddIslandLayer.NORMAL.run(contextFactory.apply(1004L), mainLayer);

        for (int i = 0; i < 2; i++)
        {
            mainLayer = AddIslandLayer.HEAVY.run(contextFactory.apply(1005L + 2 * i), mainLayer);
            mainLayer = SmoothLayer.INSTANCE.run(contextFactory.apply(1006L + 2 * i), mainLayer);
        }

        // Oceans and Continents => Elevation Mapping
        mainLayer = ElevationLayer.INSTANCE.run(contextFactory.apply(1009L), mainLayer);
        mainLayer = ZoomLayer.NORMAL.run(contextFactory.apply(1010L), mainLayer);

        // Elevation Mapping => Rivers
        riverLayer = ZoomLayer.NORMAL.run(contextFactory.apply(1011L), mainLayer);

        for (int i = 0; i < biomeSize; i++)
        {
            riverLayer = ZoomLayer.NORMAL.run(contextFactory.apply(1012L + i), riverLayer);
        }

        riverLayer = RiverLayer.INSTANCE.run(contextFactory.apply(1018L), riverLayer);
        riverLayer = ZoomLayer.NORMAL.run(contextFactory.apply(1019L), riverLayer);

        // Elevation Mapping => Biomes
        mainLayer = BiomeLayer.INSTANCE.run(contextFactory.apply(1011L), mainLayer);
        mainLayer = ZoomLayer.NORMAL.run(contextFactory.apply(1012L), mainLayer);
        mainLayer = AddIslandLayer.NORMAL.run(contextFactory.apply(1013L), mainLayer);
        mainLayer = ZoomLayer.NORMAL.run(contextFactory.apply(1014L), mainLayer);
        mainLayer = RemoveOceanLayer.INSTANCE.run(contextFactory.apply(1015L), mainLayer);
        mainLayer = OceanLayer.INSTANCE.run(contextFactory.apply(1016L), mainLayer);
        mainLayer = EdgeBiomeLayer.INSTANCE.run(contextFactory.apply(1017L), mainLayer);
        mainLayer = AddLakeLayer.INSTANCE.run(contextFactory.apply(1018L), mainLayer);

        for (int i = 0; i < 2; i++)
        {
            mainLayer = ZoomLayer.NORMAL.run(contextFactory.apply(1019L), mainLayer);
        }

        mainLayer = ShoreLayer.CASTLE.run(contextFactory.apply(1023L), mainLayer);

        for (int i = 0; i < 4; i++)
        {
            mainLayer = ZoomLayer.NORMAL.run(contextFactory.apply(1024L), mainLayer);
        }

        mainLayer = SmoothLayer.INSTANCE.run(contextFactory.apply(1025L), mainLayer);
        mainLayer = MixRiverLayer.INSTANCE.run(contextFactory.apply(1026L), mainLayer, riverLayer);
        mainLayer = BiomeRiverWidenLayer.MEDIUM.run(contextFactory.apply(1027L), mainLayer);
        mainLayer = BiomeRiverWidenLayer.LOW.run(contextFactory.apply(1028L), mainLayer);
        return mainLayer;
    }

    public static List<IAreaFactory<LazyArea>> createOverworldRockLayers(long seed)
    {
        LongFunction<LazyAreaLayerContext> contextFactory = seedModifier -> new LazyAreaLayerContext(25, seed, seedModifier);

        List<IAreaFactory<LazyArea>> completedLayers = new ArrayList<>(3);
        IAreaFactory<LazyArea> seedLayer;

        int numRocks = RockManager.INSTANCE.getKeys().size();

        // Seed Areas
        for (int j = 0; j < 3; j++)
        {
            RandomLayer randomLayer = new RandomLayer(numRocks);
            RockManager.INSTANCE.addCallback(() -> randomLayer.setLimit(RockManager.INSTANCE.getKeys().size()));
            seedLayer = randomLayer.run(contextFactory.apply(1000L));

            // The following results were obtained about the number of applications of this layer. (over 10 M samples each time)
            // None => 95.01% of adjacent pairs were equal (which lines up pretty good with theoretical predictions)
            // 1x => 98.49%
            // 2x => 99.42%
            // 3x => 99.54%
            // 4x => 99.55%
            // And thus we only apply once, as it's the best result to reduce adjacent pairs without too much effort / performance cost
            RandomizeNeighborsLayer randomNeighborLayer = new RandomizeNeighborsLayer(numRocks);
            RockManager.INSTANCE.addCallback(() -> randomNeighborLayer.setLimit(RockManager.INSTANCE.getKeys().size()));
            seedLayer = randomNeighborLayer.run(contextFactory.apply(1001L), seedLayer);

            for (int i = 0; i < 2; i++)
            {
                seedLayer = ExactZoomLayer.INSTANCE.run(contextFactory.apply(1001L), seedLayer);
                seedLayer = ZoomLayer.NORMAL.run(contextFactory.apply(1001L), seedLayer);
                seedLayer = SmoothLayer.INSTANCE.run(contextFactory.apply(1001L), seedLayer);
            }

            // todo: config option through biome provider codec?
            for (int i = 0; i < 7; i++)
            {
                seedLayer = ZoomLayer.NORMAL.run(contextFactory.apply(1001L), seedLayer);
            }

            completedLayers.add(seedLayer);
        }
        return completedLayers;
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
        return value == OCEAN || value == DEEP_OCEAN;
    }

    static boolean isMountains(int value)
    {
        return value == MOUNTAINS || value == FLOODED_MOUNTAINS || value == OLD_MOUNTAINS;
    }

    static boolean isLow(int value)
    {
        return value == PLAINS || value == HILLS || value == LOW_CANYONS || value == LOWLANDS;
    }

    private static int makeLayerId(BiomeVariants variants)
    {
        ID++;
        REGISTRY.put(ID, variants);
        return ID;
    }
}