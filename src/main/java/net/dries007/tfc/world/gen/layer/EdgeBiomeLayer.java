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
public enum EdgeBiomeLayer implements ICastleTransformer
{
    INSTANCE;

    @Override
    public int apply(INoiseRandom context, int north, int west, int south, int east, int center)
    {
        Predicate<IntPredicate> matcher = p -> p.test(north) || p.test(west) || p.test(south) || p.test(east);
        if (center == TFCLayerUtil.PLATEAU || center == TFCLayerUtil.BADLANDS)
        {
            if (matcher.test(i -> i == TFCLayerUtil.LOW_CANYONS || i == TFCLayerUtil.LOWLANDS))
            {
                return TFCLayerUtil.CANYONS;
            }
            else if (matcher.test(i -> i == TFCLayerUtil.PLAINS || i == TFCLayerUtil.HILLS))
            {
                return TFCLayerUtil.ROLLING_HILLS;
            }
            else if (matcher.test(i -> i == TFCLayerUtil.ROLLING_HILLS))
            {
                return TFCLayerUtil.BADLANDS;
            }
        }
        else if (TFCLayerUtil.isMountains(center))
        {
            if (matcher.test(TFCLayerUtil::isLow))
            {
                return TFCLayerUtil.ROLLING_HILLS;
            }
        }
        else if (center == TFCLayerUtil.DEEP_OCEAN_RIDGE)
        {
            // No inverse, as we can't replace ocean with anything at this point
            if (matcher.test(i -> i == TFCLayerUtil.OCEAN))
            {
                return TFCLayerUtil.DEEP_OCEAN;
            }
        }
        // Inverses of above conditions
        else if (center == TFCLayerUtil.LOWLANDS || center == TFCLayerUtil.LOW_CANYONS)
        {
            if (matcher.test(i -> i == TFCLayerUtil.PLATEAU || i == TFCLayerUtil.BADLANDS))
            {
                return TFCLayerUtil.CANYONS;
            }
            else if (matcher.test(TFCLayerUtil::isMountains))
            {
                return TFCLayerUtil.ROLLING_HILLS;
            }
        }
        else if (center == TFCLayerUtil.PLAINS || center == TFCLayerUtil.HILLS)
        {
            if (matcher.test(i -> i == TFCLayerUtil.PLATEAU || i == TFCLayerUtil.BADLANDS))
            {
                return TFCLayerUtil.HILLS;
            }
            else if (matcher.test(TFCLayerUtil::isMountains))
            {
                return TFCLayerUtil.ROLLING_HILLS;
            }
        }
        else if (center == TFCLayerUtil.ROLLING_HILLS)
        {
            if (matcher.test(i -> i == TFCLayerUtil.PLATEAU || i == TFCLayerUtil.BADLANDS))
            {
                return TFCLayerUtil.BADLANDS;
            }
        }
        return center;
    }
}
