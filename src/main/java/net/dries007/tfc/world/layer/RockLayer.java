/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.layer;

import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.layer.traits.IAreaTransformer0;

public class RockLayer implements IAreaTransformer0
{
    private final int totalRocks;

    public RockLayer(int totalRocks)
    {
        this.totalRocks = totalRocks;
    }

    @Override
    public int applyPixel(INoiseRandom context, int x, int z)
    {
        return context.nextRandom(totalRocks);
    }
}