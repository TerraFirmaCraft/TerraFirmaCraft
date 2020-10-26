/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.layer;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.function.Supplier;

import net.minecraft.util.IntIdentityHashBiMap;
import net.minecraft.world.gen.LazyAreaLayerContext;
import net.minecraft.world.gen.area.IAreaFactory;
import net.minecraft.world.gen.area.LazyArea;
import net.minecraft.world.gen.layer.SmoothLayer;
import net.minecraft.world.gen.layer.ZoomLayer;

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

    /**
     * These are the int IDs that are used for layer generation
     * They are mapped to {@link BiomeVariants} through the internal registry
     */
    public static final int OCEAN;
    public static final int DEEP_OCEAN;
    public static final int PLAINS;
    public static final int HILLS;
    public static final int LOWLANDS;
    public static final int LOW_CANYONS;
    public static final int ROLLING_HILLS;
    public static final int BADLANDS;
    public static final int PLATEAU;
    public static final int OLD_MOUNTAINS;
    public static final int MOUNTAINS;
    public static final int FLOODED_MOUNTAINS;
    public static final int CANYONS;
    public static final int SHORE;
    public static final int LAKE;
    public static final int RIVER;
    public static final int MOUNTAIN_RIVER;
    public static final int OLD_MOUNTAIN_RIVER;
    public static final int FLOODED_MOUNTAIN_RIVER;
    public static final int MOUNTAIN_LAKE;
    public static final int OLD_MOUNTAIN_LAKE;
    public static final int FLOODED_MOUNTAIN_LAKE;
    public static final int PLATEAU_LAKE;

    /**
     * These IDs are used as markers for biomes. They should all be removed by the time the biome layers are finished
     */
    public static final int LAKE_MARKER;
    public static final int LARGE_LAKE_MARKER;
    public static final int OCEAN_OCEAN_CONVERGING_MARKER;
    public static final int RIVER_MARKER;
    public static final int NULL_MARKER;

    private static final IntIdentityHashBiMap<BiomeVariants> REGISTRY = new IntIdentityHashBiMap<>(32);

    static
    {
        OCEAN = register(TFCBiomes.OCEAN);
        DEEP_OCEAN = register(TFCBiomes.DEEP_OCEAN);
        PLAINS = register(TFCBiomes.PLAINS);
        HILLS = register(TFCBiomes.HILLS);
        LOWLANDS = register(TFCBiomes.LOWLANDS);
        LOW_CANYONS = register(TFCBiomes.LOW_CANYONS);
        ROLLING_HILLS = register(TFCBiomes.ROLLING_HILLS);
        BADLANDS = register(TFCBiomes.BADLANDS);
        PLATEAU = register(TFCBiomes.PLATEAU);
        OLD_MOUNTAINS = register(TFCBiomes.OLD_MOUNTAINS);
        MOUNTAINS = register(TFCBiomes.MOUNTAINS);
        FLOODED_MOUNTAINS = register(TFCBiomes.FLOODED_MOUNTAINS);
        CANYONS = register(TFCBiomes.CANYONS);
        SHORE = register(TFCBiomes.SHORE);
        LAKE = register(TFCBiomes.LAKE);
        RIVER = register(TFCBiomes.RIVER);
        MOUNTAIN_RIVER = register(TFCBiomes.MOUNTAIN_RIVER);
        OLD_MOUNTAIN_RIVER = register(TFCBiomes.OLD_MOUNTAIN_RIVER);
        FLOODED_MOUNTAIN_RIVER = register(TFCBiomes.FLOODED_MOUNTAIN_RIVER);
        MOUNTAIN_LAKE = register(TFCBiomes.MOUNTAIN_LAKE);
        OLD_MOUNTAIN_LAKE = register(TFCBiomes.OLD_MOUNTAIN_LAKE);
        FLOODED_MOUNTAIN_LAKE = register(TFCBiomes.FLOODED_MOUNTAIN_LAKE);
        PLATEAU_LAKE = register(TFCBiomes.PLATEAU_LAKE);

        LAKE_MARKER = registerDummy();
        LARGE_LAKE_MARKER = registerDummy();
        OCEAN_OCEAN_CONVERGING_MARKER = registerDummy();
        RIVER_MARKER = registerDummy();
        NULL_MARKER = registerDummy();
    }

    public static BiomeVariants getFromLayerId(int id)
    {
        return Objects.requireNonNull(REGISTRY.byId(id), "Layer ID = " + id + " was null!");
    }

    public static IAreaFactory<LazyArea> createOverworldBiomeLayer(long seed, TFCBiomeProvider.LayerSettings layerSettings)
    {
        final Random random = new Random(seed);
        final Supplier<LazyTypedAreaLayerContext<Plate>> plateContext = () -> new LazyTypedAreaLayerContext<>(25, seed, random.nextLong());
        final Supplier<LazyAreaLayerContext> layerContext = () -> new LazyAreaLayerContext(25, seed, random.nextLong());

        ITypedAreaFactory<Plate> plateLayer;
        IAreaFactory<LazyArea> mainLayer, riverLayer, lakeLayer;

        // Tectonic Plates - generate plates and annotate border regions with converging / diverging boundaries
        plateLayer = new PlateGenerationLayer(new VoronoiNoise2D(random.nextLong()), 0.2f, layerSettings.getOceanPercent()).apply(plateContext.get());
        plateLayer = TypedZoomLayer.<Plate>fuzzy().run(plateContext.get(), plateLayer);
        mainLayer = PlateBoundaryLayer.INSTANCE.run(layerContext.get(), plateLayer);

        // Rivers
        riverLayer = new FloatNoiseLayer(new VoronoiNoise2D(random.nextLong()).spread(0.12f)).run(layerContext.get());

        for (int i = 0; i < 4; i++)
        {
            riverLayer = ZoomLayer.NORMAL.run(layerContext.get(), riverLayer);
        }

        riverLayer = RiverLayer.INSTANCE.run(layerContext.get(), riverLayer);
        riverLayer = RiverAcuteVertexLayer.INSTANCE.run(layerContext.get(), riverLayer);
        riverLayer = ZoomLayer.NORMAL.run(layerContext.get(), riverLayer);

        // Biomes
        mainLayer = PlateBiomeLayer.INSTANCE.run(layerContext.get(), mainLayer);
        mainLayer = OceanBorderLayer.INSTANCE.run(layerContext.get(), mainLayer);

        // Lakes
        lakeLayer = NullLayer.INSTANCE.run(layerContext.get());
        lakeLayer = LakeLayer.LARGE.run(layerContext.get(), lakeLayer);
        lakeLayer = LargeLakeLayer.INSTANCE.run(layerContext.get(), lakeLayer);
        for (int i = 0; i < 2; i++)
        {
            lakeLayer = ZoomLayer.NORMAL.run(layerContext.get(), lakeLayer);
        }
        lakeLayer = LakeLayer.SMALL.run(layerContext.get(), lakeLayer);

        // Add biome level features - lakes, island chains, edge biomes, shores
        mainLayer = ZoomLayer.NORMAL.run(layerContext.get(), mainLayer);
        mainLayer = ArchipelagoLayer.INSTANCE.run(layerContext.get(), mainLayer);
        mainLayer = ZoomLayer.NORMAL.run(layerContext.get(), mainLayer);
        mainLayer = EdgeBiomeLayer.INSTANCE.run(layerContext.get(), mainLayer);
        mainLayer = ZoomLayer.NORMAL.run(layerContext.get(), mainLayer);
        mainLayer = MixLakeLayer.INSTANCE.run(layerContext.get(), mainLayer, lakeLayer);
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
        final Random random = new Random(seed);
        final Supplier<LazyAreaLayerContext> contextFactory = () -> new LazyAreaLayerContext(25, seed, random.nextLong());
        final List<IAreaFactory<LazyArea>> completedLayers = new ArrayList<>(3);

        IAreaFactory<LazyArea> seedLayer;
        int numRocks = RockManager.INSTANCE.getKeys().size();

        // Seed Areas
        for (int j = 0; j < 3; j++)
        {
            final RandomLayer randomLayer = new RandomLayer(numRocks);
            RockManager.INSTANCE.addCallback(() -> randomLayer.setLimit(RockManager.INSTANCE.getKeys().size()));
            seedLayer = randomLayer.run(contextFactory.get());

            // The following results were obtained about the number of applications of this layer. (over 10 M samples each time)
            // None => 95.01% of adjacent pairs were equal (which lines up pretty good with theoretical predictions)
            // 1x => 98.49%
            // 2x => 99.42%
            // 3x => 99.54%
            // 4x => 99.55%
            // And thus we only apply once, as it's the best result to reduce adjacent pairs without too much effort / performance cost
            RandomizeNeighborsLayer randomNeighborLayer = new RandomizeNeighborsLayer(numRocks);
            RockManager.INSTANCE.addCallback(() -> randomNeighborLayer.setLimit(RockManager.INSTANCE.getKeys().size()));
            seedLayer = randomNeighborLayer.run(contextFactory.get(), seedLayer);

            for (int i = 0; i < 2; i++)
            {
                seedLayer = ExactZoomLayer.INSTANCE.run(contextFactory.get(), seedLayer);
                seedLayer = ZoomLayer.NORMAL.run(contextFactory.get(), seedLayer);
                seedLayer = SmoothLayer.INSTANCE.run(contextFactory.get(), seedLayer);
            }

            for (int i = 0; i < layerSettings.getRockLayerScale(); i++)
            {
                seedLayer = ZoomLayer.NORMAL.run(contextFactory.get(), seedLayer);
            }

            completedLayers.add(seedLayer);
        }
        return completedLayers;
    }

    public static boolean hasShore(int value)
    {
        return value != LOWLANDS && value != LOW_CANYONS && value != CANYONS && value != FLOODED_MOUNTAINS && value != LAKE;
    }

    public static boolean hasLake(int value)
    {
        return !isOcean(value) && value != BADLANDS;
    }

    public static int lakeFor(int value)
    {
        if (value == MOUNTAINS)
        {
            return MOUNTAIN_LAKE;
        }
        if (value == OLD_MOUNTAINS)
        {
            return OLD_MOUNTAIN_LAKE;
        }
        if (value == FLOODED_MOUNTAINS)
        {
            return FLOODED_MOUNTAIN_LAKE;
        }
        if (value == PLATEAU)
        {
            return PLATEAU_LAKE;
        }
        return LAKE;
    }

    public static boolean hasRiver(int value)
    {
        return !isOcean(value) && value != LAKE;
    }

    public static int riverFor(int value)
    {
        if (value == MOUNTAINS)
        {
            return MOUNTAIN_RIVER;
        }
        if (value == OLD_MOUNTAINS)
        {
            return OLD_MOUNTAIN_RIVER;
        }
        if (value == FLOODED_MOUNTAINS)
        {
            return FLOODED_MOUNTAIN_RIVER;
        }
        return RIVER;
    }

    public static boolean isOcean(int value)
    {
        return value == OCEAN || value == DEEP_OCEAN;
    }

    public static boolean isMountains(int value)
    {
        return value == MOUNTAINS || value == FLOODED_MOUNTAINS || value == OLD_MOUNTAINS;
    }

    public static boolean isLow(int value)
    {
        return value == PLAINS || value == HILLS || value == LOW_CANYONS || value == LOWLANDS;
    }

    public static int register(BiomeVariants variants)
    {
        return REGISTRY.add(variants);
    }

    @SuppressWarnings("ConstantConditions")
    public static int registerDummy()
    {
        return REGISTRY.add(null);
    }
}