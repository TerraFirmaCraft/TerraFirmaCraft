/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
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
    public int apply(INoiseRandom context, int north, int east, int south, int west, int center)
    {
        if (north == center || east == center || south == center || west == center)
        {
            // Pick a different random
            return context.random(limit);
        }
        return center;
    }
}