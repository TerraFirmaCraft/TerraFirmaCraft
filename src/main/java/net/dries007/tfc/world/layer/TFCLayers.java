/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.layer;

import java.util.Random;
import net.minecraft.util.RandomSource;
import org.apache.commons.lang3.mutable.MutableInt;

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
    private static final BiomeExtension[] BIOME_LAYERS = new BiomeExtension[64];
    private static final MutableInt BIOME_LAYER_INDEX = new MutableInt(0);

    /**
     * These are the int IDs that are used for biome layer generation
     * They are mapped to {@link BiomeExtension} through the internal registry
     */
    public static final int OCEAN = idFor(TFCBiomes.OCEAN);
    public static final int OCEAN_REEF = idFor(TFCBiomes.OCEAN_REEF);
    public static final int DEEP_OCEAN = idFor(TFCBiomes.DEEP_OCEAN);
    public static final int DEEP_OCEAN_TRENCH = idFor(TFCBiomes.DEEP_OCEAN_TRENCH);
    public static final int PLAINS = idFor(TFCBiomes.PLAINS);
    public static final int HILLS = idFor(TFCBiomes.HILLS);
    public static final int LOWLANDS = idFor(TFCBiomes.LOWLANDS);
    public static final int SALT_MARSH = idFor(TFCBiomes.SALT_MARSH);
    public static final int LOW_CANYONS = idFor(TFCBiomes.LOW_CANYONS);
    public static final int ROLLING_HILLS = idFor(TFCBiomes.ROLLING_HILLS);
    public static final int HIGHLANDS = idFor(TFCBiomes.HIGHLANDS);
    public static final int BADLANDS = idFor(TFCBiomes.BADLANDS);
    public static final int INVERTED_BADLANDS = idFor(TFCBiomes.INVERTED_BADLANDS);
    public static final int PLATEAU = idFor(TFCBiomes.PLATEAU);
    public static final int OLD_MOUNTAINS = idFor(TFCBiomes.OLD_MOUNTAINS);
    public static final int MOUNTAINS = idFor(TFCBiomes.MOUNTAINS);
    public static final int VOLCANIC_MOUNTAINS = idFor(TFCBiomes.VOLCANIC_MOUNTAINS);
    public static final int OCEANIC_MOUNTAINS = idFor(TFCBiomes.OCEANIC_MOUNTAINS);
    public static final int VOLCANIC_OCEANIC_MOUNTAINS = idFor(TFCBiomes.VOLCANIC_OCEANIC_MOUNTAINS);
    public static final int CANYONS = idFor(TFCBiomes.CANYONS);
    public static final int SHORE = idFor(TFCBiomes.SHORE);
    public static final int TIDAL_FLATS = idFor(TFCBiomes.TIDAL_FLATS);
    public static final int LAKE = idFor(TFCBiomes.LAKE);
    public static final int RIVER = idFor(TFCBiomes.RIVER);
    public static final int MOUNTAIN_LAKE = idFor(TFCBiomes.MOUNTAIN_LAKE);
    public static final int VOLCANIC_MOUNTAIN_LAKE = idFor(TFCBiomes.VOLCANIC_MOUNTAIN_LAKE);
    public static final int OLD_MOUNTAIN_LAKE = idFor(TFCBiomes.OLD_MOUNTAIN_LAKE);
    public static final int OCEANIC_MOUNTAIN_LAKE = idFor(TFCBiomes.OCEANIC_MOUNTAIN_LAKE);
    public static final int VOLCANIC_OCEANIC_MOUNTAIN_LAKE = idFor(TFCBiomes.VOLCANIC_OCEANIC_MOUNTAIN_LAKE);
    public static final int PLATEAU_LAKE = idFor(TFCBiomes.PLATEAU_LAKE);

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

    public static int idFor(BiomeExtension extension)
    {
        final int index = BIOME_LAYER_INDEX.getAndIncrement();
        if (index >= BIOME_LAYERS.length)
        {
            throw new IllegalStateException("Tried to register layer id " + index + " but only had space for " + BIOME_LAYERS.length + " layers");
        }
        BIOME_LAYERS[index] = extension;
        return index;
    }
}