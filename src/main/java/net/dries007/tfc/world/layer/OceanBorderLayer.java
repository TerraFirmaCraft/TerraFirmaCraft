/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.layer;

import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.layer.traits.ICastleTransformer;

/**
 * Creates oceans on borders between land and deep ocean
 */
public enum OceanBorderLayer implements ICastleTransformer
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
        }
        return center;
    }
}