/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.layer;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.function.IntFunction;
import java.util.function.Supplier;

import net.minecraft.util.IntIdentityHashBiMap;
import net.minecraft.world.gen.area.IArea;
import net.minecraft.world.gen.area.IAreaFactory;
import net.minecraft.world.gen.layer.SmoothLayer;
import net.minecraft.world.gen.layer.ZoomLayer;

import net.dries007.tfc.util.IArtist;
import net.dries007.tfc.world.biome.BiomeVariants;
import net.dries007.tfc.world.biome.TFCBiomeProvider;
import net.dries007.tfc.world.biome.TFCBiomes;
import net.dries007.tfc.world.chunkdata.ForestType;
import net.dries007.tfc.world.chunkdata.PlateTectonicsClassification;
import net.dries007.tfc.world.layer.traits.FastArea;
import net.dries007.tfc.world.layer.traits.FastAreaContext;
import net.dries007.tfc.world.layer.traits.ITypedAreaFactory;
import net.dries007.tfc.world.layer.traits.TypedAreaContext;
import net.dries007.tfc.world.noise.Cellular2D;
import net.dries007.tfc.world.noise.INoise2D;
import net.dries007.tfc.world.noise.OpenSimplex2D;

public class TFCLayerUtil
{
    /**
     * These IDs are used during plate tectonic layer generation
     * They're declared here as compile time constants so they can be used optimally in switch statements later
     *
     * @see PlateTectonicsClassification
     */
    public static final int OCEANIC = 0;
    public static final int CONTINENTAL_LOW = 1;
    public static final int CONTINENTAL_MID = 2;
    public static final int CONTINENTAL_HIGH = 3;
    public static final int OCEAN_OCEAN_DIVERGING = 4;
    public static final int OCEAN_OCEAN_CONVERGING_LOWER = 5;
    public static final int OCEAN_OCEAN_CONVERGING_UPPER = 6;
    public static final int OCEAN_CONTINENT_CONVERGING_LOWER = 7;
    public static final int OCEAN_CONTINENT_CONVERGING_UPPER = 8;
    public static final int OCEAN_CONTINENT_DIVERGING = 9;
    public static final int CONTINENT_CONTINENT_DIVERGING = 10;
    public static final int CONTINENT_CONTINENT_CONVERGING = 11;
    public static final int CONTINENTAL_SHELF = 12;

    /**
     * These are the int IDs that are used for forest layer generation
     */
    public static final int FOREST_NONE = ForestType.NONE.ordinal();
    public static final int FOREST_NORMAL = ForestType.NORMAL.ordinal();
    public static final int FOREST_SPARSE = ForestType.SPARSE.ordinal();
    public static final int FOREST_EDGE = ForestType.EDGE.ordinal();
    public static final int FOREST_OLD = ForestType.OLD_GROWTH.ordinal();

    /**
     * These are the int IDs that are used for biome layer generation
     * They are mapped to {@link BiomeVariants} through the internal registry
     */
    public static final int OCEAN;
    public static final int OCEAN_REEF;
    public static final int DEEP_OCEAN;
    public static final int DEEP_OCEAN_TRENCH;
    public static final int PLAINS;
    public static final int HILLS;
    public static final int LOWLANDS;
    public static final int LOW_CANYONS;
    public static final int ROLLING_HILLS;
    public static final int BADLANDS;
    public static final int PLATEAU;
    public static final int OLD_MOUNTAINS;
    public static final int MOUNTAINS;
    public static final int VOLCANIC_MOUNTAINS;
    public static final int OCEANIC_MOUNTAINS;
    public static final int VOLCANIC_OCEANIC_MOUNTAINS;
    public static final int CANYONS;
    public static final int SHORE;
    public static final int LAKE;
    public static final int RIVER;
    public static final int MOUNTAIN_RIVER;
    public static final int VOLCANIC_MOUNTAIN_RIVER;
    public static final int OLD_MOUNTAIN_RIVER;
    public static final int OCEANIC_MOUNTAIN_RIVER;
    public static final int VOLCANIC_OCEANIC_MOUNTAIN_RIVER;
    public static final int MOUNTAIN_LAKE;
    public static final int VOLCANIC_MOUNTAIN_LAKE;
    public static final int OLD_MOUNTAIN_LAKE;
    public static final int OCEANIC_MOUNTAIN_LAKE;
    public static final int VOLCANIC_OCEANIC_MOUNTAIN_LAKE;
    public static final int PLATEAU_LAKE;

    /**
     * These IDs are used as markers for biomes. They should all be removed by the time the biome layers are finished
     */
    public static final int OCEAN_OCEAN_CONVERGING_MARKER;
    public static final int OCEAN_OCEAN_DIVERGING_MARKER;
    public static final int LAKE_MARKER;
    public static final int RIVER_MARKER;
    public static final int NULL_MARKER;
    public static final int INLAND_MARKER;
    public static final int OCEAN_REEF_MARKER;

    private static final IntIdentityHashBiMap<BiomeVariants> REGISTRY = new IntIdentityHashBiMap<>(32);

    static
    {
        OCEAN = register(TFCBiomes.OCEAN);
        OCEAN_REEF = register(TFCBiomes.OCEAN_REEF);
        DEEP_OCEAN = register(TFCBiomes.DEEP_OCEAN);
        DEEP_OCEAN_TRENCH = register(TFCBiomes.DEEP_OCEAN_TRENCH);
        PLAINS = register(TFCBiomes.PLAINS);
        HILLS = register(TFCBiomes.HILLS);
        LOWLANDS = register(TFCBiomes.LOWLANDS);
        LOW_CANYONS = register(TFCBiomes.LOW_CANYONS);
        ROLLING_HILLS = register(TFCBiomes.ROLLING_HILLS);
        BADLANDS = register(TFCBiomes.BADLANDS);
        PLATEAU = register(TFCBiomes.PLATEAU);
        OLD_MOUNTAINS = register(TFCBiomes.OLD_MOUNTAINS);
        MOUNTAINS = register(TFCBiomes.MOUNTAINS);
        VOLCANIC_MOUNTAINS = register(TFCBiomes.VOLCANIC_MOUNTAINS);
        OCEANIC_MOUNTAINS = register(TFCBiomes.OCEANIC_MOUNTAINS);
        VOLCANIC_OCEANIC_MOUNTAINS = register(TFCBiomes.VOLCANIC_OCEANIC_MOUNTAINS);
        CANYONS = register(TFCBiomes.CANYONS);
        SHORE = register(TFCBiomes.SHORE);
        LAKE = register(TFCBiomes.LAKE);
        RIVER = register(TFCBiomes.RIVER);
        MOUNTAIN_RIVER = register(TFCBiomes.MOUNTAIN_RIVER);
        VOLCANIC_MOUNTAIN_RIVER = register(TFCBiomes.VOLCANIC_MOUNTAIN_RIVER);
        OLD_MOUNTAIN_RIVER = register(TFCBiomes.OLD_MOUNTAIN_RIVER);
        OCEANIC_MOUNTAIN_RIVER = register(TFCBiomes.OCEANIC_MOUNTAIN_RIVER);
        VOLCANIC_OCEANIC_MOUNTAIN_RIVER = register(TFCBiomes.VOLCANIC_OCEANIC_MOUNTAIN_RIVER);
        MOUNTAIN_LAKE = register(TFCBiomes.MOUNTAIN_LAKE);
        VOLCANIC_MOUNTAIN_LAKE = register(TFCBiomes.VOLCANIC_MOUNTAIN_LAKE);
        OLD_MOUNTAIN_LAKE = register(TFCBiomes.OLD_MOUNTAIN_LAKE);
        OCEANIC_MOUNTAIN_LAKE = register(TFCBiomes.OCEANIC_MOUNTAIN_LAKE);
        VOLCANIC_OCEANIC_MOUNTAIN_LAKE = register(TFCBiomes.VOLCANIC_OCEANIC_MOUNTAIN_LAKE);
        PLATEAU_LAKE = register(TFCBiomes.PLATEAU_LAKE);

        OCEAN_OCEAN_CONVERGING_MARKER = registerDummy();
        OCEAN_OCEAN_DIVERGING_MARKER = registerDummy();
        LAKE_MARKER = registerDummy();
        RIVER_MARKER = registerDummy();
        NULL_MARKER = registerDummy();
        INLAND_MARKER = registerDummy();
        OCEAN_REEF_MARKER = registerDummy();
    }

    public static BiomeVariants getFromLayerId(int id)
    {
        return Objects.requireNonNull(REGISTRY.byId(id), "Layer ID = " + id + " was null!");
    }

    public static IAreaFactory<FastArea> createOverworldBiomeLayer(long seed, TFCBiomeProvider.LayerSettings layerSettings, IArtist<ITypedAreaFactory<Plate>> plateArtist, IArtist<IAreaFactory<? extends IArea>> layerArtist)
    {
        final Random random = new Random(seed);
        final Supplier<TypedAreaContext<Plate>> plateContext = () -> new TypedAreaContext<>(seed, random.nextLong());
        final Supplier<FastAreaContext> layerContext = () -> new FastAreaContext(seed, random.nextLong());

        final List<Long> zoomLayerSeedModifiers = new ArrayList<>();
        final IntFunction<FastAreaContext> zoomLayerContext = i -> {
            while (zoomLayerSeedModifiers.size() <= i)
            {
                zoomLayerSeedModifiers.add(random.nextLong());
            }
            return new FastAreaContext(seed, zoomLayerSeedModifiers.get(i));
        };

        ITypedAreaFactory<Plate> plateLayer;
        IAreaFactory<FastArea> mainLayer, riverLayer, lakeLayer;

        // Tectonic Plates - generate plates and annotate border regions with converging / diverging boundaries
        plateLayer = new PlateGenerationLayer(new Cellular2D(random.nextInt()).spread(0.2f), layerSettings.getOceanPercent()).apply(plateContext.get());
        plateArtist.draw("plate_generation", 1, plateLayer);
        plateLayer = TypedZoomLayer.<Plate>fuzzy().run(plateContext.get(), plateLayer);
        plateArtist.draw("plate_generation", 2, plateLayer);

        mainLayer = PlateBoundaryLayer.INSTANCE.run(layerContext.get(), plateLayer);
        layerArtist.draw("plate_boundary", 1, mainLayer);
        mainLayer = SmoothLayer.INSTANCE.run(layerContext.get(), mainLayer);
        layerArtist.draw("plate_boundary", 2, mainLayer);
        mainLayer = PlateBoundaryModifierLayer.INSTANCE.run(layerContext.get(), mainLayer);
        layerArtist.draw("plate_boundary", 3, mainLayer);

        // Plates -> Biomes
        mainLayer = PlateBiomeLayer.INSTANCE.run(layerContext.get(), mainLayer);
        layerArtist.draw("biomes", 1, mainLayer);

        // Initial Biomes -> Lake Setup
        lakeLayer = InlandLayer.INSTANCE.run(layerContext.get(), mainLayer);
        layerArtist.draw("lake", 1, lakeLayer);
        lakeLayer = ZoomLayer.NORMAL.run(zoomLayerContext.apply(0), lakeLayer);
        layerArtist.draw("lake", 2, lakeLayer);

        // Lakes
        lakeLayer = AddLakesLayer.LARGE.run(layerContext.get(), lakeLayer);
        layerArtist.draw("lake", 3, lakeLayer);
        lakeLayer = ZoomLayer.NORMAL.run(zoomLayerContext.apply(1), lakeLayer);
        layerArtist.draw("lake", 4, lakeLayer);
        lakeLayer = AddLakesLayer.SMALL.run(layerContext.get(), lakeLayer);
        layerArtist.draw("lake", 5, lakeLayer);
        lakeLayer = ZoomLayer.NORMAL.run(zoomLayerContext.apply(2), lakeLayer);
        layerArtist.draw("lake", 6, lakeLayer);

        // Biome level features - ocean borders, lakes, island chains, edge biomes, shores
        // Apply lakes back to biomes
        mainLayer = OceanBorderLayer.INSTANCE.run(layerContext.get(), mainLayer);
        layerArtist.draw("biomes", 2, mainLayer);
        mainLayer = ZoomLayer.NORMAL.run(zoomLayerContext.apply(0), mainLayer);
        layerArtist.draw("biomes", 3, mainLayer);
        mainLayer = ArchipelagoLayer.INSTANCE.run(layerContext.get(), mainLayer);
        layerArtist.draw("biomes", 4, mainLayer);
        mainLayer = ReefBorderLayer.INSTANCE.run(layerContext.get(), mainLayer);
        layerArtist.draw("biomes", 5, mainLayer);
        mainLayer = ZoomLayer.NORMAL.run(zoomLayerContext.apply(1), mainLayer);
        layerArtist.draw("biomes", 6, mainLayer);
        mainLayer = EdgeBiomeLayer.INSTANCE.run(layerContext.get(), mainLayer);
        layerArtist.draw("biomes", 7, mainLayer);
        mainLayer = ZoomLayer.NORMAL.run(zoomLayerContext.apply(2), mainLayer);
        layerArtist.draw("biomes", 8, mainLayer);
        mainLayer = MixLakeLayer.INSTANCE.run(layerContext.get(), mainLayer, lakeLayer);
        layerArtist.draw("biomes", 9, mainLayer);
        mainLayer = ShoreLayer.INSTANCE.run(layerContext.get(), mainLayer);
        layerArtist.draw("biomes", 10, mainLayer);

        for (int i = 0; i < 4; i++)
        {
            mainLayer = ZoomLayer.NORMAL.run(layerContext.get(), mainLayer);
            layerArtist.draw("biomes", 11 + i, mainLayer);
        }

        mainLayer = SmoothLayer.INSTANCE.run(layerContext.get(), mainLayer);
        layerArtist.draw("biomes", 15, mainLayer);

        // River Setup
        final float riverScale = 1.7f;
        final float riverSpread = 0.15f;
        final INoise2D riverNoise = new Cellular2D(random.nextLong()).spread(0.072f).warped(
            new OpenSimplex2D(random.nextLong()).spread(riverSpread).scaled(-riverScale, riverScale),
            new OpenSimplex2D(random.nextLong()).spread(riverSpread).scaled(-riverScale, riverScale)
        ).terraces(5);

        // River Noise
        riverLayer = new FloatNoiseLayer(riverNoise).run(layerContext.get());
        layerArtist.draw("river", 1, riverLayer);

        for (int i = 0; i < 4; i++)
        {
            riverLayer = ZoomLayer.NORMAL.run(layerContext.get(), riverLayer);
            layerArtist.draw("river", 2 + i, riverLayer);
        }

        // River shape and modifications
        riverLayer = RiverLayer.INSTANCE.run(layerContext.get(), riverLayer);
        layerArtist.draw("river", 6, riverLayer);
        riverLayer = RiverAcuteVertexLayer.INSTANCE.run(layerContext.get(), riverLayer);
        layerArtist.draw("river", 7, riverLayer);
        riverLayer = ZoomLayer.NORMAL.run(layerContext.get(), riverLayer);
        layerArtist.draw("river", 8, riverLayer);
        riverLayer = SmoothLayer.INSTANCE.run(layerContext.get(), riverLayer);
        layerArtist.draw("river", 9, riverLayer);

        // Apply rivers
        mainLayer = MixRiverLayer.INSTANCE.run(layerContext.get(), mainLayer, riverLayer);
        layerArtist.draw("biomes", 16, mainLayer);
        mainLayer = BiomeRiverWidenLayer.MEDIUM.run(layerContext.get(), mainLayer);
        layerArtist.draw("biomes", 17, mainLayer);
        mainLayer = BiomeRiverWidenLayer.LOW.run(layerContext.get(), mainLayer);
        layerArtist.draw("biomes", 18, mainLayer);

        return mainLayer;
    }

    public static IAreaFactory<FastArea> createOverworldForestLayer(long seed, TFCBiomeProvider.LayerSettings settings, IArtist<IAreaFactory<? extends IArea>> artist)
    {
        final Random random = new Random(seed);
        final Supplier<FastAreaContext> layerContext = () -> new FastAreaContext(seed, random.nextLong());

        IAreaFactory<FastArea> mainLayer;

        mainLayer = ForestInitLayer.INSTANCE.run(layerContext.get());
        artist.draw("forest", 1, mainLayer);
        mainLayer = ForestRandomizeLayer.INSTANCE.run(layerContext.get(), mainLayer);
        artist.draw("forest", 2, mainLayer);
        mainLayer = ZoomLayer.FUZZY.run(layerContext.get(), mainLayer);
        artist.draw("forest", 3, mainLayer);
        mainLayer = ForestRandomizeLayer.INSTANCE.run(layerContext.get(), mainLayer);
        artist.draw("forest", 4, mainLayer);
        mainLayer = ZoomLayer.FUZZY.run(layerContext.get(), mainLayer);
        artist.draw("forest", 5, mainLayer);
        mainLayer = ZoomLayer.NORMAL.run(layerContext.get(), mainLayer);
        artist.draw("forest", 6, mainLayer);
        mainLayer = ForestEdgeLayer.INSTANCE.run(layerContext.get(), mainLayer);
        artist.draw("forest", 7, mainLayer);
        mainLayer = ForestRandomizeSmallLayer.INSTANCE.run(layerContext.get(), mainLayer);
        artist.draw("forest", 8, mainLayer);
        mainLayer = ZoomLayer.NORMAL.run(layerContext.get(), mainLayer);
        artist.draw("forest", 9, mainLayer);

        return mainLayer;
    }

    public static IAreaFactory<FastArea> createOverworldPlateTectonicInfoLayer(long seed, TFCBiomeProvider.LayerSettings layerSettings)
    {
        final Random random = new Random(seed);
        final Supplier<TypedAreaContext<Plate>> plateContext = () -> new TypedAreaContext<>(seed, random.nextLong());
        final Supplier<FastAreaContext> layerContext = () -> new FastAreaContext(seed, random.nextLong());

        ITypedAreaFactory<Plate> plateLayer;
        IAreaFactory<FastArea> mainLayer;

        // Tectonic Plates - generate plates and annotate border regions with converging / diverging boundaries
        plateLayer = new PlateGenerationLayer(new Cellular2D(random.nextInt()).spread(0.2f), layerSettings.getOceanPercent()).apply(plateContext.get());
        plateLayer = TypedZoomLayer.<Plate>fuzzy().run(plateContext.get(), plateLayer);
        mainLayer = PlateBoundaryLayer.INSTANCE.run(layerContext.get(), plateLayer);

        for (int i = 0; i < 5; i++)
        {
            mainLayer = ZoomLayer.NORMAL.run(layerContext.get(), mainLayer);
        }

        return mainLayer;
    }

    public static List<IAreaFactory<FastArea>> createOverworldRockLayers(long seed, TFCBiomeProvider.LayerSettings layerSettings)
    {
        final Random random = new Random(seed);
        final Supplier<FastAreaContext> contextFactory = () -> new FastAreaContext(seed, random.nextLong());
        final List<IAreaFactory<FastArea>> completedLayers = new ArrayList<>(3);

        IAreaFactory<FastArea> seedLayer;
        int numRocks = layerSettings.getRocks().size();

        // Seed Areas
        for (int j = 0; j < 3; j++)
        {
            seedLayer = new RockLayer(numRocks).run(contextFactory.get());

            // The following results were obtained about the number of applications of this layer. (over 10 M samples each time)
            // None => 95.01% of adjacent pairs were equal (which lines up pretty good with theoretical predictions)
            // 1x => 98.49%
            // 2x => 99.42%
            // 3x => 99.54%
            // 4x => 99.55%
            // And thus we only apply once, as it's the best result to reduce adjacent pairs without too much effort / performance cost
            seedLayer = new RandomizeNeighborsLayer(numRocks).run(contextFactory.get(), seedLayer);

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

    public static boolean isContinental(int value)
    {
        return value == CONTINENTAL_LOW || value == CONTINENTAL_MID || value == CONTINENTAL_HIGH;
    }

    public static boolean hasShore(int value)
    {
        return value != LOWLANDS && value != LOW_CANYONS && value != CANYONS && value != OCEANIC_MOUNTAINS && value != VOLCANIC_OCEANIC_MOUNTAINS;
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
        if (value == VOLCANIC_MOUNTAINS)
        {
            return VOLCANIC_MOUNTAIN_LAKE;
        }
        if (value == OLD_MOUNTAINS)
        {
            return OLD_MOUNTAIN_LAKE;
        }
        if (value == OCEANIC_MOUNTAINS)
        {
            return OCEANIC_MOUNTAIN_LAKE;
        }
        if (value == VOLCANIC_OCEANIC_MOUNTAINS)
        {
            return VOLCANIC_OCEANIC_MOUNTAIN_LAKE;
        }
        if (value == PLATEAU)
        {
            return PLATEAU_LAKE;
        }
        return LAKE;
    }

    public static boolean hasRiver(int value)
    {
        return !isOcean(value) && !isLake(value);
    }

    public static int riverFor(int value)
    {
        if (value == MOUNTAINS)
        {
            return MOUNTAIN_RIVER;
        }
        if (value == VOLCANIC_MOUNTAINS)
        {
            return VOLCANIC_MOUNTAIN_RIVER;
        }
        if (value == OLD_MOUNTAINS)
        {
            return OLD_MOUNTAIN_RIVER;
        }
        if (value == OCEANIC_MOUNTAINS)
        {
            return OCEANIC_MOUNTAIN_RIVER;
        }
        if (value == VOLCANIC_OCEANIC_MOUNTAINS)
        {
            return VOLCANIC_OCEANIC_MOUNTAIN_RIVER;
        }
        return RIVER;
    }

    public static boolean isOcean(int value)
    {
        return value == OCEAN || value == DEEP_OCEAN || value == DEEP_OCEAN_TRENCH || value == OCEAN_REEF;
    }

    public static boolean isOceanOrMarker(int value)
    {
        return isOcean(value) || value == OCEAN_OCEAN_CONVERGING_MARKER || value == OCEAN_OCEAN_DIVERGING_MARKER || value == OCEAN_REEF_MARKER;
    }

    public static boolean isLake(int value)
    {
        return value == LAKE || value == OCEANIC_MOUNTAIN_LAKE || value == OLD_MOUNTAIN_LAKE || value == MOUNTAIN_LAKE || value == VOLCANIC_OCEANIC_MOUNTAIN_LAKE || value == VOLCANIC_MOUNTAIN_LAKE || value == PLATEAU_LAKE;
    }

    public static boolean isRiver(int value)
    {
        return value == RIVER || value == OCEANIC_MOUNTAIN_RIVER || value == OLD_MOUNTAIN_RIVER || value == MOUNTAIN_RIVER || value == VOLCANIC_OCEANIC_MOUNTAIN_RIVER || value == VOLCANIC_MOUNTAIN_RIVER;
    }

    public static boolean isMountains(int value)
    {
        return value == MOUNTAINS || value == OCEANIC_MOUNTAINS || value == OLD_MOUNTAINS || value == VOLCANIC_MOUNTAINS || value == VOLCANIC_OCEANIC_MOUNTAINS;
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