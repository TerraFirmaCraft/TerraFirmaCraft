/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.layer;

import java.util.Random;
import java.util.function.Supplier;
import net.minecraft.util.RandomSource;
import org.apache.commons.lang3.mutable.MutableInt;

import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.IArtist;
import net.dries007.tfc.world.biome.BiomeExtension;
import net.dries007.tfc.world.biome.TFCBiomes;
import net.dries007.tfc.world.chunkdata.ForestType;
import net.dries007.tfc.world.layer.framework.AreaFactory;
import net.dries007.tfc.world.layer.framework.TypedAreaFactory;
import net.dries007.tfc.world.noise.OpenSimplex2D;
import net.dries007.tfc.world.region.Region;
import net.dries007.tfc.world.region.RegionGenerator;
import net.dries007.tfc.world.region.Units;

public class TFCLayers
{
    /**
     * These are the int IDs that are used for forest layer generation
     */
    public static final int FOREST_NONE = 0;
    public static final int FOREST_SPARSE = 1;
    public static final int FOREST_EDGE = 2;
    public static final int FOREST_NORMAL = 3;
    public static final int FOREST_OLD = 4;

    /**
     * These are the int IDs that are used for biome layer generation
     * They are mapped to {@link BiomeExtension} through the internal registry
     */
    public static final int OCEAN;
    public static final int OCEAN_REEF;
    public static final int DEEP_OCEAN;
    public static final int DEEP_OCEAN_TRENCH;
    public static final int PLAINS;
    public static final int HILLS;
    public static final int LOWLANDS;
    public static final int SALT_MARSH;
    public static final int LOW_CANYONS;
    public static final int ROLLING_HILLS;
    public static final int HIGHLANDS;
    public static final int BADLANDS;
    public static final int INVERTED_BADLANDS;
    public static final int PLATEAU;
    public static final int OLD_MOUNTAINS;
    public static final int MOUNTAINS;
    public static final int VOLCANIC_MOUNTAINS;
    public static final int OCEANIC_MOUNTAINS;
    public static final int VOLCANIC_OCEANIC_MOUNTAINS;
    public static final int CANYONS;
    public static final int SHORE;
    public static final int TIDAL_FLATS;
    public static final int LAKE;
    public static final int RIVER;
    public static final int MOUNTAIN_LAKE;
    public static final int VOLCANIC_MOUNTAIN_LAKE;
    public static final int OLD_MOUNTAIN_LAKE;
    public static final int OCEANIC_MOUNTAIN_LAKE;
    public static final int VOLCANIC_OCEANIC_MOUNTAIN_LAKE;
    public static final int PLATEAU_LAKE;

    private static final BiomeExtension[] BIOME_LAYERS = new BiomeExtension[64];
    private static final MutableInt BIOME_LAYER_INDEX = new MutableInt(0);

    static
    {
        OCEAN = register(() -> TFCBiomes.OCEAN);
        OCEAN_REEF = register(() -> TFCBiomes.OCEAN_REEF);
        DEEP_OCEAN = register(() -> TFCBiomes.DEEP_OCEAN);
        DEEP_OCEAN_TRENCH = register(() -> TFCBiomes.DEEP_OCEAN_TRENCH);
        PLAINS = register(() -> TFCBiomes.PLAINS);
        HILLS = register(() -> TFCBiomes.HILLS);
        LOWLANDS = register(() -> TFCBiomes.LOWLANDS);
        SALT_MARSH = register(() -> TFCBiomes.SALT_MARSH);
        LOW_CANYONS = register(() -> TFCBiomes.LOW_CANYONS);
        ROLLING_HILLS = register(() -> TFCBiomes.ROLLING_HILLS);
        HIGHLANDS = register(() -> TFCBiomes.HIGHLANDS);
        BADLANDS = register(() -> TFCBiomes.BADLANDS);
        INVERTED_BADLANDS = register(() -> TFCBiomes.INVERTED_BADLANDS);
        PLATEAU = register(() -> TFCBiomes.PLATEAU);
        OLD_MOUNTAINS = register(() -> TFCBiomes.OLD_MOUNTAINS);
        MOUNTAINS = register(() -> TFCBiomes.MOUNTAINS);
        VOLCANIC_MOUNTAINS = register(() -> TFCBiomes.VOLCANIC_MOUNTAINS);
        OCEANIC_MOUNTAINS = register(() -> TFCBiomes.OCEANIC_MOUNTAINS);
        VOLCANIC_OCEANIC_MOUNTAINS = register(() -> TFCBiomes.VOLCANIC_OCEANIC_MOUNTAINS);
        CANYONS = register(() -> TFCBiomes.CANYONS);
        SHORE = register(() -> TFCBiomes.SHORE);
        TIDAL_FLATS = register(() -> TFCBiomes.TIDAL_FLATS);
        LAKE = register(() -> TFCBiomes.LAKE);
        RIVER = register(() -> TFCBiomes.RIVER);
        MOUNTAIN_LAKE = register(() -> TFCBiomes.MOUNTAIN_LAKE);
        VOLCANIC_MOUNTAIN_LAKE = register(() -> TFCBiomes.VOLCANIC_MOUNTAIN_LAKE);
        OLD_MOUNTAIN_LAKE = register(() -> TFCBiomes.OLD_MOUNTAIN_LAKE);
        OCEANIC_MOUNTAIN_LAKE = register(() -> TFCBiomes.OCEANIC_MOUNTAIN_LAKE);
        VOLCANIC_OCEANIC_MOUNTAIN_LAKE = register(() -> TFCBiomes.VOLCANIC_OCEANIC_MOUNTAIN_LAKE);
        PLATEAU_LAKE = register(() -> TFCBiomes.PLATEAU_LAKE);
    }

    public static BiomeExtension getFromLayerId(int id)
    {
        final BiomeExtension v = BIOME_LAYERS[id];
        if (v == null)
        {
            throw new NullPointerException("Layer id = " + id + " returned null!");
        }
        return v;
    }

    public static AreaFactory createOverworldForestLayer(long seed, IArtist<AreaFactory> artist)
    {
        final Random random = new Random(seed);

        AreaFactory layer;

        layer = new ForestInitLayer(new OpenSimplex2D(random.nextInt()).spread(0.3f)).apply(random.nextLong());
        artist.draw("forest", 1, layer);
        layer = ForestRandomizeLayer.INSTANCE.apply(random.nextLong(), layer);
        artist.draw("forest", 2, layer);
        layer = ZoomLayer.FUZZY.apply(random.nextLong(), layer);
        artist.draw("forest", 3, layer);
        layer = ForestRandomizeLayer.INSTANCE.apply(random.nextLong(), layer);
        artist.draw("forest", 4, layer);
        layer = ZoomLayer.FUZZY.apply(random.nextLong(), layer);
        artist.draw("forest", 5, layer);
        layer = ZoomLayer.NORMAL.apply(random.nextLong(), layer);
        artist.draw("forest", 6, layer);
        layer = ForestEdgeLayer.INSTANCE.apply(random.nextLong(), layer);
        artist.draw("forest", 7, layer);
        layer = ForestRandomizeSmallLayer.INSTANCE.apply(random.nextLong(), layer);
        artist.draw("forest", 8, layer);

        for (int i = 0; i < 2; i++)
        {
            layer = ZoomLayer.NORMAL.apply(random.nextLong(), layer);
            artist.draw("forest", 9 + i, layer);
        }

        return layer;
    }

    public static AreaFactory createOverworldRockLayer(RegionGenerator generator, long seed)
    {
        final Random random = new Random(seed);
        final TypedAreaFactory<Region.Point> regionLayer = new RegionLayer(generator).apply(random.nextLong());

        AreaFactory layer;

        layer = RegionRockLayer.INSTANCE.apply(regionLayer); // Grid scale (128x)
        for (int i = 0; i < Units.GRID_BITS - 1; i++)
        {
            layer = ZoomLayer.NORMAL.apply(seed, layer);
        }
        layer = SmoothLayer.INSTANCE.apply(seed, layer);
        layer = ZoomLayer.NORMAL.apply(seed, layer);
        layer = SmoothLayer.INSTANCE.apply(seed, layer);

        return layer;
    }

    public static AreaFactory createRegionBiomeLayer(RegionGenerator generator, long seed)
    {
        final Random random = new Random(seed);
        final TypedAreaFactory<Region.Point> regionLayer = new RegionLayer(generator).apply(random.nextLong());

        AreaFactory mainLayer;

        mainLayer = RegionBiomeLayer.INSTANCE.apply(regionLayer);

        // Grid scale

        mainLayer = RegionEdgeBiomeLayer.INSTANCE.apply(random.nextLong(), mainLayer);
        mainLayer = ZoomLayer.NORMAL.apply(random.nextLong(), mainLayer);

        mainLayer = ShoreLayer.INSTANCE.apply(random.nextLong(), mainLayer);
        mainLayer = MoreShoresLayer.INSTANCE.apply(random.nextLong(), mainLayer);
        mainLayer = ZoomLayer.NORMAL.apply(random.nextLong(), mainLayer);
        mainLayer = ZoomLayer.NORMAL.apply(random.nextLong(), mainLayer);

        // Chunk scale

        mainLayer = ZoomLayer.NORMAL.apply(random.nextLong(), mainLayer);
        mainLayer = ZoomLayer.NORMAL.apply(random.nextLong(), mainLayer);

        // Quart scale

        mainLayer = SmoothLayer.INSTANCE.apply(random.nextLong(), mainLayer);

        return mainLayer;
    }

    public static AreaFactory createUniformLayer(RandomSource random, int zoomLevels)
    {
        AreaFactory layer;

        layer = UniformLayer.INSTANCE.apply(random.nextLong());
        for (int i = 0; i < zoomLevels; i++)
        {
            layer = ZoomLayer.NORMAL.apply(random.nextLong(), layer);
            layer = SmoothLayer.INSTANCE.apply(random.nextLong(), layer);
        }

        return layer;
    }

    public static boolean hasShore(int value)
    {
        return value != LOWLANDS && value != SALT_MARSH && value != LOW_CANYONS && value != CANYONS && value != OCEANIC_MOUNTAINS && value != VOLCANIC_OCEANIC_MOUNTAINS;
    }

    public static int shoreFor(int value)
    {
        if (value == MOUNTAINS)
        {
            return OCEANIC_MOUNTAINS;
        }
        if (value == VOLCANIC_MOUNTAINS)
        {
            return VOLCANIC_OCEANIC_MOUNTAINS;
        }
        return SHORE;
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

    public static boolean isOcean(int value)
    {
        return value == OCEAN || value == DEEP_OCEAN || value == DEEP_OCEAN_TRENCH || value == OCEAN_REEF;
    }

    public static boolean isMountains(int value)
    {
        return value == MOUNTAINS || value == OCEANIC_MOUNTAINS || value == OLD_MOUNTAINS || value == VOLCANIC_MOUNTAINS || value == VOLCANIC_OCEANIC_MOUNTAINS;
    }

    public static boolean isLow(int value)
    {
        return value == PLAINS || value == HILLS || value == LOW_CANYONS || value == LOWLANDS || value == SALT_MARSH;
    }

    public static int register(Supplier<BiomeExtension> variants)
    {
        final int index = BIOME_LAYER_INDEX.getAndIncrement();
        if (index >= BIOME_LAYERS.length)
        {
            throw new IllegalStateException("Tried to register layer id " + index + " but only had space for " + BIOME_LAYERS.length + " layers");
        }
        BIOME_LAYERS[index] = Helpers.BOOTSTRAP_ENVIRONMENT ? null : variants.get();
        return index;
    }
}