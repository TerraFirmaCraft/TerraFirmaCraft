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
 * Operates on the {@link TFCLayers#OCEAN_REEF_MARKER} markers
 * Borders reef - land with ocean, and adds ocean to reef - deep ocean borders
 */
public enum ReefBorderLayer implements AdjacentTransformLayer
{
    INSTANCE;

    @Override
    public int apply(AreaContext context, int north, int east, int south, int west, int center)
    {
        if (center == OCEAN_REEF_MARKER)
        {
            if (!TFCLayers.isOceanOrMarker(north) || !TFCLayers.isOceanOrMarker(east) || !TFCLayers.isOceanOrMarker(south) || !TFCLayers.isOceanOrMarker(west))
            {
                return OCEAN;
            }
            return OCEAN_REEF;
        }
        else if (TFCLayers.isOceanOrMarker(center) && (north == OCEAN_REEF_MARKER || east == OCEAN_REEF_MARKER || south == OCEAN_REEF_MARKER || west == OCEAN_REEF_MARKER))
        {
            return OCEAN;
        }
        return center;
    }
}
