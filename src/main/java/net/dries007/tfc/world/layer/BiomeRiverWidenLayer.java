/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.layer;

import java.util.function.IntPredicate;

import net.dries007.tfc.world.layer.framework.AdjacentTransformLayer;
import net.dries007.tfc.world.layer.framework.AreaContext;

import static net.dries007.tfc.world.layer.TFCLayerUtil.RIVER;

public enum BiomeRiverWidenLayer implements AdjacentTransformLayer
{
    MEDIUM(value -> TFCLayerUtil.isLow(value) || value == TFCLayerUtil.ROLLING_HILLS || value == TFCLayerUtil.CANYONS),
    LOW(TFCLayerUtil::isLow);

    private final IntPredicate expansion;

    BiomeRiverWidenLayer(IntPredicate expansion)
    {
        this.expansion = expansion;
    }

    @Override
    public int apply(AreaContext context, int north, int east, int south, int west, int center)
    {
        // The center must be non-river and expandable
        if (center != RIVER && expansion.test(center))
        {
            // Check if adjacent to at least one river
            if (north == RIVER || east == RIVER || west == RIVER || south == RIVER)
            {
                // Check if all surrounding pixels are either river, or valid for expansion
                if ((north == RIVER || expansion.test(north)) && (east == RIVER || expansion.test(east)) && (west == RIVER || expansion.test(west)) && (south == RIVER || expansion.test(south)))
                {
                    return RIVER;
                }
            }
        }
        return center;
    }
}