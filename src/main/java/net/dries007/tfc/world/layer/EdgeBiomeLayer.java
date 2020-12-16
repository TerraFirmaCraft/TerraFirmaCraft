/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.layer;

import java.util.function.IntPredicate;
import java.util.function.Predicate;

import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.layer.traits.ICastleTransformer;

import static net.dries007.tfc.world.layer.TFCLayerUtil.*;

public enum EdgeBiomeLayer implements ICastleTransformer
{
    INSTANCE;

    @Override
    public int apply(INoiseRandom context, int north, int east, int south, int west, int center)
    {
        Predicate<IntPredicate> matcher = p -> p.test(north) || p.test(east) || p.test(south) || p.test(west);
        if (center == PLATEAU || center == BADLANDS)
        {
            if (matcher.test(i -> i == LOW_CANYONS || i == LOWLANDS))
            {
                return HILLS;
            }
            else if (matcher.test(i -> i == PLAINS || i == HILLS))
            {
                return ROLLING_HILLS;
            }
        }
        else if (TFCLayerUtil.isMountains(center))
        {
            if (matcher.test(TFCLayerUtil::isLow))
            {
                return ROLLING_HILLS;
            }
        }
        // Inverses of above conditions
        else if (center == LOWLANDS || center == LOW_CANYONS)
        {
            if (matcher.test(i -> i == PLATEAU || i == BADLANDS))
            {
                return HILLS;
            }
            else if (matcher.test(TFCLayerUtil::isMountains))
            {
                return ROLLING_HILLS;
            }
        }
        else if (center == PLAINS || center == HILLS)
        {
            if (matcher.test(i -> i == PLATEAU || i == BADLANDS))
            {
                return HILLS;
            }
            else if (matcher.test(TFCLayerUtil::isMountains))
            {
                return ROLLING_HILLS;
            }
        }
        return center;
    }
}