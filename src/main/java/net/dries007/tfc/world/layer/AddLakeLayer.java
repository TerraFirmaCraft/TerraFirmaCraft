/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.layer;

import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.layer.traits.ICastleTransformer;

public enum AddLakeLayer implements ICastleTransformer
{
    SMALL(24, TFCLayerUtil.LAKE),
    LARGE(180, TFCLayerUtil.LARGE_LAKE_MARKER);

    private final int chance;
    private final int lake;

    AddLakeLayer(int chance, int lake)
    {
        this.chance = chance;
        this.lake = lake;
    }

    @Override
    public int apply(INoiseRandom context, int north, int west, int south, int east, int center)
    {
        if (TFCLayerUtil.isLakeCompatible(north) && TFCLayerUtil.isLakeCompatible(west) && TFCLayerUtil.isLakeCompatible(south) && TFCLayerUtil.isLakeCompatible(east) && TFCLayerUtil.isLakeCompatible(center))
        {
            if (context.nextRandom(chance) == 0)
            {
                return lake;
            }
        }
        return center;
    }
}