/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.gen.layer;

import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.layer.traits.ICastleTransformer;

@ParametersAreNonnullByDefault
public enum OceanLayer implements ICastleTransformer
{
    INSTANCE;

    @Override
    public int apply(INoiseRandom context, int north, int west, int south, int east, int center)
    {
        if (TFCLayerUtil.isOcean(center))
        {
            if (!TFCLayerUtil.isOcean(north) || !TFCLayerUtil.isOcean(west) || !TFCLayerUtil.isOcean(south) || !TFCLayerUtil.isOcean(east))
            {
                return TFCLayerUtil.OCEAN;
            }
            else if (north == TFCLayerUtil.DEEP_OCEAN_RIDGE || west == TFCLayerUtil.DEEP_OCEAN_RIDGE || south == TFCLayerUtil.DEEP_OCEAN_RIDGE || east == TFCLayerUtil.DEEP_OCEAN_RIDGE)
            {
                if (context.random(3) == 0)
                {
                    return TFCLayerUtil.DEEP_OCEAN_RIDGE;
                }
            }
        }
        return center;
    }
}
