/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.layer;

import net.dries007.tfc.world.layer.framework.AreaContext;
import net.dries007.tfc.world.layer.framework.DiagonalTransformLayer;

import static net.dries007.tfc.world.layer.TFCLayerUtil.RIVER_MARKER;

/**
 * Expands harsh river vertexes by filling in acute angles
 * This avoids sudden sections of very thin rivers which can lead to bottoming off / cut offs in noise generation later
 */
public enum RiverAcuteVertexLayer implements DiagonalTransformLayer
{
    INSTANCE;

    @Override
    public int apply(AreaContext context, int southWest, int southEast, int northWest, int northEast, int center)
    {
        if (center != RIVER_MARKER)
        {
            int riverCount = 0;
            if (southWest == RIVER_MARKER)
            {
                riverCount++;
            }
            if (southEast == RIVER_MARKER)
            {
                riverCount++;
            }
            if (northWest == RIVER_MARKER)
            {
                riverCount++;
            }
            if (northEast == RIVER_MARKER)
            {
                riverCount++;
            }
            if (riverCount >= 3)
            {
                return RIVER_MARKER;
            }
        }
        return center;
    }
}
