/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.layer;

import java.util.function.IntPredicate;

import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.layer.traits.ICastleTransformer;

public enum BiomeRiverWidenLayer implements ICastleTransformer
{
    MEDIUM(TFCLayerUtil.RIVER, value -> TFCLayerUtil.isLow(value) || value == TFCLayerUtil.ROLLING_HILLS || value == TFCLayerUtil.CANYONS),
    LOW(TFCLayerUtil.RIVER, TFCLayerUtil::isLow);

    private final int river;
    private final IntPredicate expansion;

    BiomeRiverWidenLayer(int river, IntPredicate expansion)
    {
        this.river = river;
        this.expansion = expansion;
    }

    @Override
    public int apply(INoiseRandom context, int north, int west, int south, int east, int center)
    {
        if (expansion.test(center) && center != river)
        {
            if (north == river && (expansion.test(west) && expansion.test(east) && expansion.test(south)))
            {
                return river;
            }
            else if (east == river && (expansion.test(west) && expansion.test(north) && expansion.test(south)))
            {
                return river;
            }
            else if (west == river && (expansion.test(north) && expansion.test(east) && expansion.test(south)))
            {
                return river;
            }
            else if (south == river && (expansion.test(west) && expansion.test(east) && expansion.test(north)))
            {
                return river;
            }
        }
        return center;
    }
}