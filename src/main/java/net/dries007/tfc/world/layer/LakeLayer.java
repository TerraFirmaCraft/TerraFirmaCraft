/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.layer;

import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.layer.traits.IC1Transformer;

/**
 * Adds instances of lake markers to a layer randomly
 */
public enum LakeLayer implements IC1Transformer
{
    SMALL(40, TFCLayerUtil.LAKE_MARKER),
    LARGE(210, TFCLayerUtil.LARGE_LAKE_MARKER);

    private final int chance;
    private final int lake;

    LakeLayer(int chance, int lake)
    {
        this.chance = chance;
        this.lake = lake;
    }

    @Override
    public int apply(INoiseRandom context, int value)
    {
        if (context.nextRandom(chance) == 0)
        {
            return lake;
        }
        return value;
    }
}