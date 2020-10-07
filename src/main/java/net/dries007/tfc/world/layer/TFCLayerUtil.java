/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.layer;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.LongFunction;
import java.util.function.Supplier;

import net.minecraft.world.gen.LazyAreaLayerContext;
import net.minecraft.world.gen.area.IAreaFactory;
import net.minecraft.world.gen.area.LazyArea;
import net.minecraft.world.gen.layer.SmoothLayer;
import net.minecraft.world.gen.layer.ZoomLayer;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.dries007.tfc.common.types.RockManager;
import net.dries007.tfc.world.biome.BiomeVariants;
import net.dries007.tfc.world.biome.TFCBiomeProvider;
import net.dries007.tfc.world.biome.TFCBiomes;
import net.dries007.tfc.world.layer.traits.ITypedAreaFactory;
import net.dries007.tfc.world.layer.traits.LazyTypedAreaLayerContext;
import net.dries007.tfc.world.noise.VoronoiNoise2D;

public class TFCLayerUtil
{
    /**
     * These IDs are used during plate tectonic layer generation
     * They're declared here as compile time constants so they can be used optimally in switch statements later
     */
    public static final int OCEANIC = 0;
    public static final int CONTINENTAL_LOW = 1;
    public static final int CONTINENTAL_MID = 2;
    public static final int CONTINENTAL_HIGH = 3;
    public static final int OCEAN_OCEAN_DIVERGING = 4;
    public static final int OCEAN_OCEAN_CONVERGING = 5;
    public static final int OCEAN_CONTINENT_DIVERGING = 6;
    public static final int OCEAN_CONTINENT_CONVERGING = 7;
    public static final int CONTINENT_CONTINENT_DIVERGING = 8;
    public static final int CONTINENT_CONTINENT_CONVERGING = 9;

    private static final Int2ObjectMap<BiomeVariants> ID_TO_BIOME_VARIANTS = new Int2ObjectOpenHashMap<>();
    private static int LAYER_ID = -1;

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
    /**
     * These IDs are used as markers for biomes. They should all be removed by the time the biome layers are finished
     */
    public static final int LARGE_LAKE_MARKER = ++LAYER_ID;
    public static final int OCEAN_OCEAN_CONVERGING_MARKER = ++LAYER_ID;
    public static final int RIVER_MARKER = ++LAYER_ID;
    public static final int NULL_MARKER = ++LAYER_ID;

    public static BiomeVariants getFromLayerId(int id)
    {
        return ID_TO_BIOME_VARIANTS.get(id);
    }

    public static IAreaFactory<LazyArea> createOverworldBiomeLayer(long seed, TFCBiomeProvider.LayerSettings layerSettings)
    {
        final Random random = new Random(seed);
        final Supplier<LazyTypedAreaLayerContext<Plate>> plateContext = () -> new LazyTypedAreaLayerContext<>(25, seed, random.nextLong());
        final Supplier<LazyAreaLayerContext> layerContext = () -> new LazyAreaLayerContext(25, seed, random.nextLong());

        ITypedAreaFactory<Plate> plateLayer;
        IAreaFactory<LazyArea> mainLayer, riverLayer;

        // Tectonic Plates - generate plates and annotate border regions with converging / diverging boundaries
        plateLayer = new PlateGenerationLayer(new VoronoiNoise2D(random.nextLong()), 0.2f, layerSettings.getOceanPercent()).apply(plateContext.get());
        plateLayer = TypedZoomLayer.<Plate>fuzzy().run(plateContext.get(), plateLayer);
        mainLayer = PlateBoundaryLayer.INSTANCE.run(layerContext.get(), plateLayer);

        // Rivers
        riverLayer = new FloatNoiseLayer(new VoronoiNoise2D(random.nextLong()).spread(0.12f)).run(layerContext.get());
        riverLayer = ZoomLayer.FUZZY.run(layerContext.get(), riverLayer);
        riverLayer = ZoomLayer.NORMAL.run(layerContext.get(), riverLayer);
        riverLayer = ZoomLayer.FUZZY.run(layerContext.get(), riverLayer);

        for (int i = 0; i < 3; i++)
        {
            riverLayer = ZoomLayer.NORMAL.run(layerContext.get(), riverLayer);
        }

        riverLayer = RiverLayer.INSTANCE.run(layerContext.get(), riverLayer);
        riverLayer = ZoomLayer.NORMAL.run(layerContext.get(), riverLayer);

        // Biomes
        mainLayer = PlateBiomeLayer.INSTANCE.run(layerContext.get(), mainLayer);
        mainLayer = OceanBorderLayer.INSTANCE.run(layerContext.get(), mainLayer);

        // Add biome level features - large + small lakes, island chains, shores
        mainLayer = AddLakeLayer.LARGE.run(layerContext.get(), mainLayer);
        mainLayer = LargeLakeLayer.INSTANCE.run(layerContext.get(), mainLayer);
        mainLayer = ZoomLayer.NORMAL.run(layerContext.get(), mainLayer);
        mainLayer = ArchipelagoLayer.INSTANCE.run(layerContext.get(), mainLayer);
        mainLayer = ZoomLayer.NORMAL.run(layerContext.get(), mainLayer);
        mainLayer = EdgeBiomeLayer.INSTANCE.run(layerContext.get(), mainLayer);
        mainLayer = AddLakeLayer.SMALL.run(layerContext.get(), mainLayer);
        mainLayer = ZoomLayer.NORMAL.run(layerContext.get(), mainLayer);
        mainLayer = ShoreLayer.INSTANCE.run(layerContext.get(), mainLayer);

        for (int i = 0; i < 4; i++)
        {
            mainLayer = ZoomLayer.NORMAL.run(layerContext.get(), mainLayer);
        }

        mainLayer = SmoothLayer.INSTANCE.run(layerContext.get(), mainLayer);

        // Mix rivers and expand them in low biomes
        mainLayer = MixRiverLayer.INSTANCE.run(layerContext.get(), mainLayer, riverLayer);
        mainLayer = BiomeRiverWidenLayer.MEDIUM.run(layerContext.get(), mainLayer);
        mainLayer = BiomeRiverWidenLayer.LOW.run(layerContext.get(), mainLayer);

        return mainLayer;
    }

    public static List<IAreaFactory<LazyArea>> createOverworldRockLayers(long seed, TFCBiomeProvider.LayerSettings layerSettings)
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

            for (int i = 0; i < layerSettings.getRockLayerScale(); i++)
            {
                seedLayer = ZoomLayer.NORMAL.run(contextFactory.apply(1001L), seedLayer);
            }

            completedLayers.add(seedLayer);
        }
        return completedLayers;
    }

    static boolean isShoreCompatible(int value)
    {
        return value != LOWLANDS && value != LOW_CANYONS && value != CANYONS && value != FLOODED_MOUNTAINS && value != LAKE;
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
        LAYER_ID++;
        ID_TO_BIOME_VARIANTS.put(LAYER_ID, variants);
        return LAYER_ID;
    }
}