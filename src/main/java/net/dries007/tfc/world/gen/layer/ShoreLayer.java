/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.gen.layer;

import java.util.function.IntPredicate;
import java.util.function.Predicate;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.layer.traits.ICastleTransformer;

@ParametersAreNonnullByDefault
public enum ShoreLayer implements ICastleTransformer
{
    INSTANCE;

    @Override
    public int apply(INoiseRandom context, int north, int west, int south, int east, int center)
    {
        Predicate<IntPredicate> matcher = p -> p.test(north) || p.test(west) || p.test(south) || p.test(east);
        if (TFCLayerUtil.isMountains(center))
        {
            if (matcher.test(TFCLayerUtil::isOcean))
            {
                return TFCLayerUtil.STONE_SHORE;
            }
            else if (matcher.test(i -> !TFCLayerUtil.isMountains(i)))
            {
                return TFCLayerUtil.MOUNTAINS_EDGE;
            }
        }
        else if (!TFCLayerUtil.isOcean(center) && TFCLayerUtil.isShoreCompatible(center))
        {
            if (matcher.test(TFCLayerUtil::isOcean))
            {
                return TFCLayerUtil.SHORE;
            }
        }
        else if (center == TFCLayerUtil.DEEP_OCEAN || center == TFCLayerUtil.DEEP_OCEAN_RIDGE)
        {
            if (matcher.test(i -> i == TFCLayerUtil.OCEAN))
            {
                return TFCLayerUtil.OCEAN;
            }
        }
        return center;
    }
}
