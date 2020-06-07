/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.layer;

import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.layer.traits.ICastleTransformer;

public class RandomizeNeighborsLayer extends CallbackLimitLayer implements ICastleTransformer
{
    public RandomizeNeighborsLayer(int limit)
    {
        super(limit);
    }

    @Override
    public int apply(INoiseRandom context, int north, int west, int south, int east, int center)
    {
        if (north == center || west == center || south == center || east == center)
        {
            // Pick a different random
            return context.random(limit);
        }
        return center;
    }
}
