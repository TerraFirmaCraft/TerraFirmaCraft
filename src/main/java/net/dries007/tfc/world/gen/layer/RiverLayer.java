/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.gen.layer;

import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.layer.traits.ICastleTransformer;

import static net.dries007.tfc.world.gen.layer.TFCLayerUtil.OCEAN;
import static net.dries007.tfc.world.gen.layer.TFCLayerUtil.RIVER;

@ParametersAreNonnullByDefault
public enum RiverLayer implements ICastleTransformer
{
    INSTANCE;

    public int apply(INoiseRandom context, int north, int west, int south, int east, int center)
    {
        if (center != north || center != south || center != east || center != west)
        {
            return RIVER;
        }
        return OCEAN;
    }
}
