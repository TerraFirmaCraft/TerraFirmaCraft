/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.gen.layer;

import java.util.function.Predicate;

import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.layer.traits.ICastleTransformer;

import net.dries007.tfc.api.registries.TFCRegistries;
import net.dries007.tfc.api.types.Rock;

public class RockMixLayer implements ICastleTransformer
{
    private final Rock[] rocks;

    public RockMixLayer(Predicate<Rock> predicate)
    {
        this.rocks = TFCRegistries.ROCKS.getValues().stream().filter(predicate).toArray(Rock[]::new);
    }

    @Override
    public int apply(INoiseRandom context, int north, int west, int south, int east, int center)
    {
        if (north == center || west == center || south == center || east == center)
        {
            return TFCRegistries.ROCKS.getID(rocks[context.random(rocks.length)]);
        }
        return center;
    }
}
