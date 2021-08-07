/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.layer;

import net.dries007.tfc.world.layer.framework.AdjacentTransformLayer;
import net.dries007.tfc.world.layer.framework.AreaContext;

import static net.dries007.tfc.world.layer.TFCLayerUtil.DEEP_OCEAN;
import static net.dries007.tfc.world.layer.TFCLayerUtil.OCEAN;

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
            if (!TFCLayerUtil.isOceanOrMarker(north) || !TFCLayerUtil.isOceanOrMarker(east) || !TFCLayerUtil.isOceanOrMarker(south) || !TFCLayerUtil.isOceanOrMarker(west))
            {
                return OCEAN;
            }
        }
        else if (center == OCEAN)
        {
            // And in the reverse, in large sections of ocean, add deep ocean in fully ocean-locked area
            if (TFCLayerUtil.isOceanOrMarker(north) && TFCLayerUtil.isOceanOrMarker(east) && TFCLayerUtil.isOceanOrMarker(west) && TFCLayerUtil.isOceanOrMarker(south))
            {
                return DEEP_OCEAN;
            }
        }
        return center;
    }
}