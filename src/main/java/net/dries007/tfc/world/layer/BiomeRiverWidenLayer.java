/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.layer;

import java.util.function.IntPredicate;

import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.layer.traits.ICastleTransformer;

import static net.dries007.tfc.world.layer.TFCLayerUtil.RIVER;

public enum BiomeRiverWidenLayer implements ICastleTransformer
{
    MEDIUM(value -> TFCLayerUtil.isLow(value) || value == TFCLayerUtil.ROLLING_HILLS || value == TFCLayerUtil.CANYONS),
    LOW(TFCLayerUtil::isLow);

    private final IntPredicate expansion;

    BiomeRiverWidenLayer(IntPredicate expansion)
    {
        this.expansion = expansion;
    }

    @Override
    public int apply(INoiseRandom context, int north, int west, int south, int east, int center)
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