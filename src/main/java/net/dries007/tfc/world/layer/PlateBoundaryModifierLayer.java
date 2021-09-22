/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.layer;

import net.dries007.tfc.world.layer.framework.AdjacentTransformLayer;
import net.dries007.tfc.world.layer.framework.AreaContext;

import static net.dries007.tfc.world.layer.TFCLayers.*;

/**
 * Modifies various types of plate tectonic boundaries after initial assignment from plate layers
 */
public enum PlateBoundaryModifierLayer implements AdjacentTransformLayer
{
    INSTANCE;

    @Override
    public int apply(AreaContext context, int north, int east, int south, int west, int center)
    {
        // Expand ocean-continent diverging boundaries - create two types of areas, continental shelf, and oceanic diverging boundary
        if (center == OCEAN_CONTINENT_DIVERGING)
        {
            return CONTINENTAL_SHELF;
        }

        if (north == OCEAN_CONTINENT_DIVERGING || east == OCEAN_CONTINENT_DIVERGING || west == OCEAN_CONTINENT_DIVERGING || south == OCEAN_CONTINENT_DIVERGING)
        {
            if (TFCLayers.isContinental(center))
            {
                return CONTINENTAL_SHELF;
            }
            else if (center == OCEANIC)
            {
                return OCEAN_OCEAN_DIVERGING;
            }
        }

        return center;
    }
}
