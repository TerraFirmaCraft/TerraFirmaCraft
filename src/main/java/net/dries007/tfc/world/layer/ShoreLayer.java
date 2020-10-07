/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.layer;

import java.util.function.IntPredicate;
import java.util.function.Predicate;

import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.layer.traits.ICastleTransformer;

public enum ShoreLayer implements ICastleTransformer
{
    INSTANCE;

    @Override
    public int apply(INoiseRandom context, int north, int west, int south, int east, int center)
    {
        Predicate<IntPredicate> matcher = p -> p.test(north) || p.test(west) || p.test(south) || p.test(east);
        if (!TFCLayerUtil.isOcean(center) && TFCLayerUtil.isShoreCompatible(center))
        {
            if (matcher.test(TFCLayerUtil::isOcean))
            {
                return TFCLayerUtil.SHORE;
            }
        }
        return center;
    }
}