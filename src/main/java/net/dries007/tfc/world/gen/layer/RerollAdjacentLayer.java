/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.gen.layer;

import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.layer.traits.IC0Transformer;
import net.minecraft.world.gen.layer.traits.ICastleTransformer;

public class RerollAdjacentLayer implements ICastleTransformer
{
    private final IC0Transformer baseLayer;

    public RerollAdjacentLayer(IC0Transformer baseLayer)
    {
        this.baseLayer = baseLayer;
    }

    @Override
    public int apply(INoiseRandom context, int north, int west, int south, int east, int center)
    {
        if (north == center || west == center || south == center || east == center)
        {
            return baseLayer.apply(context, center);
        }
        return center;
    }
}
