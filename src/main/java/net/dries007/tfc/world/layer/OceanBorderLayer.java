/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.layer;

import net.dries007.tfc.world.layer.framework.AdjacentTransformLayer;
import net.dries007.tfc.world.layer.framework.AreaContext;

import static net.dries007.tfc.world.layer.TFCLayers.DEEP_OCEAN;
import static net.dries007.tfc.world.layer.TFCLayers.OCEAN;

/**
 * Creates oceans on borders between land and deep ocean
 */
public enum OceanBorderLayer implements AdjacentTransformLayer
{
    INSTANCE;

    @Override
    public int apply(AreaContext context, int north, int east, int south, int west, int center)
    {
        if (center == DEEP_OCEAN)
        {
            // Add ocean to land - deep ocean borders
            if (!TFCLayers.isOceanOrMarker(north) || !TFCLayers.isOceanOrMarker(east) || !TFCLayers.isOceanOrMarker(south) || !TFCLayers.isOceanOrMarker(west))
            {
                return OCEAN;
            }
        }
        else if (center == OCEAN)
        {
            // And in the reverse, in large sections of ocean, add deep ocean in fully ocean-locked area
            if (TFCLayers.isOceanOrMarker(north) && TFCLayers.isOceanOrMarker(east) && TFCLayers.isOceanOrMarker(west) && TFCLayers.isOceanOrMarker(south))
            {
                return DEEP_OCEAN;
            }
        }
        return center;
    }
}