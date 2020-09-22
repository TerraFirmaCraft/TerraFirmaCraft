/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.layer;

import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.layer.traits.ICastleTransformer;

public enum AddLakeLayer implements ICastleTransformer
{
    INSTANCE;

    @Override
    public int apply(INoiseRandom context, int north, int west, int south, int east, int center)
    {
        if (TFCLayerUtil.isLakeCompatible(north) && TFCLayerUtil.isLakeCompatible(west) && TFCLayerUtil.isLakeCompatible(south) && TFCLayerUtil.isLakeCompatible(east) && TFCLayerUtil.isLakeCompatible(center))
        {
            if (context.nextRandom(15) == 0)
            {
                return TFCLayerUtil.LAKE;
            }
        }
        return center;
    }
}