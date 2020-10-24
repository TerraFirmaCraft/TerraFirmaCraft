/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.layer;

import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.layer.traits.ICastleTransformer;

import static net.dries007.tfc.world.layer.TFCLayerUtil.NULL_MARKER;
import static net.dries007.tfc.world.layer.TFCLayerUtil.RIVER_MARKER;

public enum RiverLayer implements ICastleTransformer
{
    INSTANCE;

    public int apply(INoiseRandom context, int north, int west, int south, int east, int center)
    {
        if (center != north || center != south || center != east || center != west)
        {
            return RIVER_MARKER;
        }
        return NULL_MARKER;
    }
}