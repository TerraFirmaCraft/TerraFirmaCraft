/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.layer;

import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.layer.traits.IAreaTransformer0;

public class RandomLayer extends CallbackLimitLayer implements IAreaTransformer0
{
    public RandomLayer(int limit)
    {
        super(limit);
    }

    @Override
    public int apply(INoiseRandom context, int x, int z)
    {
        return context.random(limit);
    }
}
