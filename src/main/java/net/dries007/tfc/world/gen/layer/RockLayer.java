/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.gen.layer;

import java.util.function.Predicate;

import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.layer.traits.IAreaTransformer0;

import net.dries007.tfc.api.registries.TFCRegistries;
import net.dries007.tfc.api.types.Rock;

public class RockLayer implements IAreaTransformer0
{
    private final Rock[] rocks;

    public RockLayer(Predicate<Rock> predicate)
    {
        this.rocks = TFCRegistries.ROCKS.getValues().stream().filter(predicate).toArray(Rock[]::new);
    }

    @Override
    public int apply(INoiseRandom context, int x, int z)
    {
        return TFCRegistries.ROCKS.getID(rocks[context.random(rocks.length)]);
    }
}
