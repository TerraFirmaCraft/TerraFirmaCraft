/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.gen.layer;

import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.layer.traits.IC1Transformer;

@ParametersAreNonnullByDefault
public enum ElevationLayer implements IC1Transformer
{
    INSTANCE;

    @Override
    public int apply(INoiseRandom context, int value)
    {
        if (!TFCLayerUtil.isOcean(value))
        {
            int rand = context.random(4);
            if (rand == 0)
            {
                return TFCLayerUtil.PLAINS;
            }
            else if (rand == 1)
            {
                return TFCLayerUtil.HILLS;
            }
            else if (rand == 2)
            {
                return TFCLayerUtil.MOUNTAINS;
            }
        }
        return value;
    }
}
