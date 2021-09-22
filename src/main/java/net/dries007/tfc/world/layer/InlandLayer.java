/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.layer;

import net.dries007.tfc.world.layer.framework.AdjacentTransformLayer;
import net.dries007.tfc.world.layer.framework.AreaContext;

import static net.dries007.tfc.world.layer.TFCLayers.INLAND_MARKER;
import static net.dries007.tfc.world.layer.TFCLayers.NULL_MARKER;

/**
 * This layer pipes the initial biome generation, and marks specific areas as inland, which are allowed to generate lakes
 * It prevents lakes from generating and replacing biomes at the edge of a biome-ocean border, or near one
 */
public enum InlandLayer implements AdjacentTransformLayer
{
    INSTANCE;

    @Override
    public int apply(AreaContext context, int north, int east, int south, int west, int center)
    {
        if (TFCLayers.isOceanOrMarker(north) || TFCLayers.isOceanOrMarker(east) || TFCLayers.isOceanOrMarker(south) || TFCLayers.isOceanOrMarker(west))
        {
            return NULL_MARKER;
        }
        return INLAND_MARKER;
    }
}
