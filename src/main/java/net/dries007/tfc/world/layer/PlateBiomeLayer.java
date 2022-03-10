/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.layer;

import net.dries007.tfc.world.layer.framework.AreaContext;
import net.dries007.tfc.world.layer.framework.CenterTransformLayer;

import static net.dries007.tfc.world.layer.TFCLayers.*;

public enum PlateBiomeLayer implements CenterTransformLayer
{
    INSTANCE;

    private static final int[] SUBDUCTION_BIOMES = {VOLCANIC_OCEANIC_MOUNTAINS, VOLCANIC_OCEANIC_MOUNTAINS, VOLCANIC_MOUNTAINS, VOLCANIC_MOUNTAINS, MOUNTAINS, PLATEAU};
    private static final int[] OROGENY_BIOMES = {MOUNTAINS, MOUNTAINS, MOUNTAINS, OLD_MOUNTAINS, PLATEAU};
    private static final int[] RIFT_BIOMES = {CANYONS, CANYONS, CANYONS, CANYONS, ROLLING_HILLS, OLD_MOUNTAINS};
    private static final int[] CONTINENT_LOW_BIOMES = {PLAINS, PLAINS, HILLS, ROLLING_HILLS, LOW_CANYONS, LOW_CANYONS, LOWLANDS, LOWLANDS};
    private static final int[] CONTINENT_MID_BIOMES = {PLAINS, HILLS, ROLLING_HILLS, INVERTED_BADLANDS, BADLANDS, PLATEAU, LOW_CANYONS, LOWLANDS};
    private static final int[] CONTINENT_HIGH_BIOMES = {HILLS, ROLLING_HILLS, ROLLING_HILLS, INVERTED_BADLANDS, BADLANDS, PLATEAU, PLATEAU, OLD_MOUNTAINS, OLD_MOUNTAINS};

    @Override
    public int apply(AreaContext context, int value)
    {
        switch (value)
        {
            case OCEANIC:
                // Main oceanic plate body - generate deep oceans
                return DEEP_OCEAN;
            case CONTINENTAL_LOW:
                // Normal biomes
                return context.choose(CONTINENT_LOW_BIOMES);
            case CONTINENTAL_MID:
                // Mid scale height biomes
                return context.choose(CONTINENT_MID_BIOMES);
            case CONTINENTAL_HIGH:
                // High height biomes
                return context.choose(CONTINENT_HIGH_BIOMES);
            case OCEAN_OCEAN_DIVERGING:
                // Oceanic diverging - new plate being generated
                // Return a marker that will get replaced with sporadic flooded mountains (non-volcanic) or ocean
                return OCEAN_OCEAN_DIVERGING_MARKER;
            case OCEAN_OCEAN_CONVERGING_LOWER:
                // The subducting plate of an oceanic converging boundary. Creates a trench on the subducting side and volcanic islands on the upper side
                // This is the trench side, so generate deep ocean trench
                return DEEP_OCEAN_TRENCH;
            case OCEAN_OCEAN_CONVERGING_UPPER:
                // The upper of two subducting plates. Creates an series of oceanic volcanic islands.
                return OCEAN_OCEAN_CONVERGING_MARKER;
            case OCEAN_CONTINENT_CONVERGING_LOWER:
                // A subducting oceanic plate under a continental plate. Creates a deep trench adjacent to the shore
                return DEEP_OCEAN_TRENCH;
            case OCEAN_CONTINENT_CONVERGING_UPPER:
                // Continental subduction biomes. Highly volcanic mountain areas
                return context.choose(SUBDUCTION_BIOMES);
            case CONTINENT_CONTINENT_DIVERGING:
                // Diverging areas create volcanoes, rifts, and rift valleys. This is a very varied set of biomes with a lot of volcanic activity
                return context.choose(RIFT_BIOMES);
            case CONTINENT_CONTINENT_CONVERGING:
                // Non-volcanic mountain building
                return context.choose(OROGENY_BIOMES);
            case CONTINENTAL_SHELF:
                // Continental shelf, for continental plate area that is still underwater
                // This is generated as a replacement for ocean-continental diverging boundaries, and helps create better mid-ocean ridges
                return OCEAN;
        }
        throw new IllegalStateException("What is this: " + value);
    }
}
