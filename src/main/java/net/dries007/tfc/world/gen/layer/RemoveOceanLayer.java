/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.gen.layer;

import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.layer.traits.ICastleTransformer;

@ParametersAreNonnullByDefault
public enum RemoveOceanLayer implements ICastleTransformer
{
    INSTANCE;

    public int apply(INoiseRandom context, int top, int right, int bottom, int left, int center)
    {
        if (TFCLayerUtil.isOcean(top) && TFCLayerUtil.isOcean(right) && TFCLayerUtil.isOcean(bottom) && TFCLayerUtil.isOcean(left) && context.random(32) == 0)
        {
            if (context.random(3) == 0)
            {
                return TFCLayerUtil.HILLS;
            }
            return TFCLayerUtil.PLAINS;
        }
        if (TFCLayerUtil.isOcean(center))
        {
            int replacement = center, count = 0;
            if (!TFCLayerUtil.isOcean(top))
            {
                replacement = top;
                count++;
            }
            if (!TFCLayerUtil.isOcean(left))
            {
                replacement = left;
                count++;
            }
            if (!TFCLayerUtil.isOcean(right))
            {
                replacement = right;
                count++;
            }
            if (!TFCLayerUtil.isOcean(bottom))
            {
                replacement = bottom;
                count++;
            }
            if (count == 4 || (count == 3 && context.random(3) == 0))
            {
                return replacement;
            }
        }
        return center;
    }
}
