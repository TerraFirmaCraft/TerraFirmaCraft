/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.layer;

import java.util.Random;
import org.apache.commons.lang3.mutable.MutableInt;

import net.dries007.tfc.util.IArtist;
import net.dries007.tfc.world.Seed;
import net.dries007.tfc.world.biome.BiomeExtension;
import net.dries007.tfc.world.biome.TFCBiomes;
import net.dries007.tfc.world.layer.framework.AreaFactory;
import net.dries007.tfc.world.layer.framework.TypedAreaFactory;
import net.dries007.tfc.world.noise.OpenSimplex2D;
import net.dries007.tfc.world.region.Region;
import net.dries007.tfc.world.region.RegionGenerator;
import net.dries007.tfc.world.region.Units;

public class TFCLayers
{
    private static final BiomeExtension[] BIOME_LAYERS = new BiomeExtension[128];
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
    public static final int MUD_FLATS = idFor(TFCBiomes.MUD_FLATS);
    public static final int SALT_FLATS = idFor(TFCBiomes.SALT_FLATS);
    public static final int DUNE_SEA = idFor(TFCBiomes.DUNE_SEA);
    public static final int GRASSY_DUNES = idFor(TFCBiomes.GRASSY_DUNES);
    public static final int WHORLED_CANYONS = idFor(TFCBiomes.WHORLED_CANYONS);
    public static final int STAIR_STEP_CANYONS = idFor(TFCBiomes.STAIR_STEP_CANYONS);
    public static final int MESAS = idFor(TFCBiomes.MESAS);
    public static final int BUTTES = idFor(TFCBiomes.BUTTES);
    public static final int HOODOOS = idFor(TFCBiomes.HOODOOS);
    public static final int ROCKY_PLATEAU = idFor(TFCBiomes.ROCKY_PLATEAU);
    public static final int TOWER_KARST_PLAINS = idFor(TFCBiomes.TOWER_KARST_PLAINS);
    public static final int TOWER_KARST_CANYONS = idFor(TFCBiomes.TOWER_KARST_CANYONS);
    public static final int TOWER_KARST_HILLS = idFor(TFCBiomes.TOWER_KARST_HILLS);
    public static final int TOWER_KARST_HIGHLANDS = idFor(TFCBiomes.TOWER_KARST_HIGHLANDS);
    public static final int TOWER_KARST_LAKE = idFor(TFCBiomes.TOWER_KARST_LAKE);
    public static final int TOWER_KARST_BAY = idFor(TFCBiomes.TOWER_KARST_BAY);
    public static final int BURREN_PLATEAU = idFor(TFCBiomes.BURREN_PLATEAU);
    public static final int BURREN_BADLANDS = idFor(TFCBiomes.BURREN_BADLANDS);
    public static final int BURREN_BADLANDS_TALL = idFor(TFCBiomes.BURREN_BADLANDS_TALL);
    public static final int BURREN_ROCHE_MOUTONEE = idFor(TFCBiomes.BURREN_ROCHE_MOUTONEE);
    public static final int BURREN_PLAINS = idFor(TFCBiomes.BURREN_PLAINS);
    public static final int SHILIN_PLAINS = idFor(TFCBiomes.SHILIN_PLAINS);
    public static final int SHILIN_CANYONS = idFor(TFCBiomes.SHILIN_CANYONS);
    public static final int SHILIN_HILLS = idFor(TFCBiomes.SHILIN_HILLS);
    public static final int SHILIN_HIGHLANDS = idFor(TFCBiomes.SHILIN_HIGHLANDS);
    public static final int SHILIN_PLATEAU = idFor(TFCBiomes.SHILIN_PLATEAU);
    public static final int DOLINE_PLAINS = idFor(TFCBiomes.DOLINE_PLAINS);
    public static final int DOLINE_HILLS = idFor(TFCBiomes.DOLINE_HILLS);
    public static final int DOLINE_ROLLING_HILLS = idFor(TFCBiomes.DOLINE_ROLLING_HILLS);
    public static final int DOLINE_HIGHLANDS = idFor(TFCBiomes.DOLINE_HIGHLANDS);
    public static final int DOLINE_PLATEAU = idFor(TFCBiomes.DOLINE_PLATEAU);
    public static final int DOLINE_CANYONS = idFor(TFCBiomes.DOLINE_CANYONS);
    public static final int CENOTE_PLAINS = idFor(TFCBiomes.CENOTE_PLAINS);
    public static final int CENOTE_HILLS = idFor(TFCBiomes.CENOTE_HILLS);
    public static final int CENOTE_ROLLING_HILLS = idFor(TFCBiomes.CENOTE_ROLLING_HILLS);
    public static final int CENOTE_CANYONS = idFor(TFCBiomes.CENOTE_CANYONS);
    public static final int CENOTE_HIGHLANDS = idFor(TFCBiomes.CENOTE_HIGHLANDS);
    public static final int CENOTE_PLATEAU = idFor(TFCBiomes.CENOTE_PLATEAU);
    public static final int EXTREME_DOLINE_PLATEAU = idFor(TFCBiomes.EXTREME_DOLINE_PLATEAU);
    public static final int EXTREME_DOLINE_MOUNTAINS = idFor(TFCBiomes.EXTREME_DOLINE_MOUNTAINS);
    public static final int ACTIVE_SHIELD_VOLCANO = idFor(TFCBiomes.ACTIVE_SHIELD_VOLCANO);
    public static final int DORMANT_SHIELD_VOLCANO = idFor(TFCBiomes.DORMANT_SHIELD_VOLCANO);
    public static final int EXTINCT_SHIELD_VOLCANO = idFor(TFCBiomes.EXTINCT_SHIELD_VOLCANO);
    public static final int ANCIENT_SHIELD_VOLCANO = idFor(TFCBiomes.ANCIENT_SHIELD_VOLCANO);
    public static final int SUNKEN_SHIELD_VOLCANO = idFor(TFCBiomes.SUNKEN_SHIELD_VOLCANO);
    public static final int SHIELD_VOLCANO_SHORE = idFor(TFCBiomes.SHIELD_VOLCANO_SHORE);
    public static final int OLD_SHIELD_VOLCANO_SHORE = idFor(TFCBiomes.OLD_SHIELD_VOLCANO_SHORE);
    public static final int ICE_SHEET = idFor(TFCBiomes.ICE_SHEET);
    public static final int ICE_SHEET_MOUNTAINS = idFor(TFCBiomes.ICE_SHEET_MOUNTAINS);
    public static final int ICE_SHEET_OCEANIC_MOUNTAINS = idFor(TFCBiomes.ICE_SHEET_OCEANIC_MOUNTAINS);
    public static final int ICE_SHEET_SHIELD_VOLCANO = idFor(TFCBiomes.ICE_SHEET_SHIELD_VOLCANO);
    public static final int ICE_SHEET_TUYAS = idFor(TFCBiomes.ICE_SHEET_TUYAS);
    public static final int SUBGLACIAL_LAKE = idFor(TFCBiomes.SUBGLACIAL_LAKE);

    public static final int ICE_SHEET_EDGE = idFor(TFCBiomes.ICE_SHEET_EDGE);
    public static final int ICE_SHEET_TUYAS_EDGE = idFor(TFCBiomes.ICE_SHEET_TUYAS_EDGE);
    public static final int ICE_SHEET_OCEANIC = idFor(TFCBiomes.ICE_SHEET_OCEANIC);
    public static final int ICE_SHEET_OCEANIC_MOUNTAINS_EDGE = idFor(TFCBiomes.ICE_SHEET_OCEANIC_MOUNTAINS_EDGE);
    public static final int ICE_SHEET_MOUNTAINS_EDGE = idFor(TFCBiomes.ICE_SHEET_MOUNTAINS_EDGE);
    public static final int GLACIATED_MOUNTAINS = idFor(TFCBiomes.GLACIATED_MOUNTAINS);
    public static final int GLACIATED_OCEANIC_MOUNTAINS = idFor(TFCBiomes.GLACIATED_OCEANIC_MOUNTAINS);
    public static final int MELTWATER_LAKE = idFor(TFCBiomes.MELTWATER_LAKE);
    public static final int GLACIATED_SHIELD_VOLCANO = idFor(TFCBiomes.GLACIATED_SHIELD_VOLCANO);
    public static final int ICE_SHEET_SHORE = idFor(TFCBiomes.ICE_SHEET_SHORE);

    public static final int GLACIALLY_CARVED_MOUNTAINS = idFor(TFCBiomes.GLACIALLY_CARVED_MOUNTAINS);
    public static final int GLACIALLY_CARVED_OCEANIC_MOUNTAINS = idFor(TFCBiomes.GLACIALLY_CARVED_OCEANIC_MOUNTAINS);
    public static final int DRUMLINS = idFor(TFCBiomes.DRUMLINS);
    public static final int TUYAS = idFor(TFCBiomes.TUYAS);
    public static final int KNOB_AND_KETTLE = idFor(TFCBiomes.KNOB_AND_KETTLE);
    public static final int PATTERNED_GROUND = idFor(TFCBiomes.PATTERNED_GROUND);
    public static final int INVERTED_PATTERNED_GROUND = idFor(TFCBiomes.INVERTED_PATTERNED_GROUND);
    public static final int STONE_CIRCLES = idFor(TFCBiomes.STONE_CIRCLES);


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

        layer = new ForestInitLayer(new OpenSimplex2D(random.nextInt()).spread(0.25f)).apply(random.nextLong());
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

    public static AreaFactory createRegionBiomeLayer(RegionGenerator generator, Seed seed)
    {
        final TypedAreaFactory<Region.Point> regionLayer = new RegionLayer(generator).apply(seed.next());

        AreaFactory mainLayer;

        mainLayer = RegionBiomeLayer.INSTANCE.apply(regionLayer);

        // Grid scale

        mainLayer = RegionEdgeBiomeLayer.INSTANCE.apply(seed.next(), mainLayer);
        mainLayer = ZoomLayer.NORMAL.apply(seed.next(), mainLayer);


        // 4x4 Chunk Scale
        mainLayer = ShoreLayer.INSTANCE.apply(seed.next(), mainLayer);
        mainLayer = MoreShoresLayer.INSTANCE.apply(seed.next(), mainLayer);
        mainLayer = IceSheetEdgeLayer.INSTANCE.apply(seed.next(), mainLayer);
        mainLayer = ZoomLayer.NORMAL.apply(seed.next(), mainLayer);
        mainLayer = ZoomLayer.NORMAL.apply(seed.next(), mainLayer);

        // Chunk scale

        mainLayer = ZoomLayer.NORMAL.apply(seed.next(), mainLayer);
        mainLayer = ZoomLayer.NORMAL.apply(seed.next(), mainLayer);

        // Quart scale

        mainLayer = SmoothLayer.INSTANCE.apply(seed.next(), mainLayer);

        return mainLayer;
    }

    public static AreaFactory createUniformLayer(Seed seed, int zoomLevels)
    {
        AreaFactory layer;

        layer = UniformLayer.INSTANCE.apply(seed.next());
        for (int i = 0; i < zoomLevels; i++)
        {
            layer = ZoomLayer.NORMAL.apply(seed.next(), layer);
            layer = SmoothLayer.INSTANCE.apply(seed.next(), layer);
        }

        return layer;
    }

    public static boolean hasShore(int value)
    {
        return value != LOWLANDS && value != SALT_MARSH && value != LOW_CANYONS && value != CANYONS && value != OCEANIC_MOUNTAINS && value != VOLCANIC_OCEANIC_MOUNTAINS
            && value != TOWER_KARST_BAY && value != SUNKEN_SHIELD_VOLCANO && value != GLACIALLY_CARVED_OCEANIC_MOUNTAINS && value != GLACIATED_OCEANIC_MOUNTAINS && value != ICE_SHEET_OCEANIC_MOUNTAINS_EDGE
            && value != ICE_SHEET_SHIELD_VOLCANO && value != GLACIATED_SHIELD_VOLCANO;
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
        if (value == TOWER_KARST_LAKE)
        {
            return TOWER_KARST_BAY;
        }
        if (value == ACTIVE_SHIELD_VOLCANO)
        {
            return SHIELD_VOLCANO_SHORE;
        }
        if (value == DORMANT_SHIELD_VOLCANO || value == EXTINCT_SHIELD_VOLCANO || value == ANCIENT_SHIELD_VOLCANO)
        {
            return OLD_SHIELD_VOLCANO_SHORE;
        }
        if (isFlatIceSheet(value) || value == ICE_SHEET_EDGE || value == ICE_SHEET_OCEANIC)
        {
            return ICE_SHEET_SHORE;
        }
        if (value == ICE_SHEET_OCEANIC_MOUNTAINS)
        {
            return ICE_SHEET_OCEANIC_MOUNTAINS_EDGE;
        }
        if (value == GLACIALLY_CARVED_OCEANIC_MOUNTAINS || value == GLACIALLY_CARVED_MOUNTAINS)
        {
            return GLACIATED_OCEANIC_MOUNTAINS;
        }
        return SHORE;
    }

    public static boolean hasLake(int value)
    {
        return (!isOcean(value) && value != BADLANDS && value != ACTIVE_SHIELD_VOLCANO && value != DORMANT_SHIELD_VOLCANO
            && value != EXTINCT_SHIELD_VOLCANO && value != ANCIENT_SHIELD_VOLCANO && value != ICE_SHEET_MOUNTAINS
            && value != ICE_SHEET_MOUNTAINS_EDGE && value != ICE_SHEET_OCEANIC_MOUNTAINS && value != ICE_SHEET_OCEANIC_MOUNTAINS_EDGE
            && value != ICE_SHEET_SHIELD_VOLCANO && value != ICE_SHEET_SHORE && value != GLACIATED_SHIELD_VOLCANO
            && value != GLACIATED_MOUNTAINS && value != GLACIATED_OCEANIC_MOUNTAINS && value != GLACIALLY_CARVED_MOUNTAINS
            && value != GLACIALLY_CARVED_OCEANIC_MOUNTAINS);
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
        if (isFlatIceSheet(value))
        {
            return SUBGLACIAL_LAKE;
        }
        if (value == ICE_SHEET_EDGE)
        {
            return MELTWATER_LAKE;
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
        return value == PLAINS || value == HILLS || value == LOW_CANYONS || value == LOWLANDS || value == SALT_MARSH || value == MUD_FLATS || value == SALT_FLATS || value == DUNE_SEA;
    }

    public static boolean isFlats(int value)
    {
        return value == MUD_FLATS || value == SALT_FLATS;
    }

    public static boolean isFlatIceSheet(int value)
    {
        return value == ICE_SHEET || value == ICE_SHEET_TUYAS || value == SUBGLACIAL_LAKE;
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