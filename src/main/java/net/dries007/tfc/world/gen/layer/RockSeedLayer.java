package net.dries007.tfc.world.gen.layer;

import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.layer.traits.IAreaTransformer0;

public enum RockSeedLayer implements IAreaTransformer0
{
    INSTANCE;

    @Override
    public int apply(INoiseRandom context, int x, int z)
    {
        return context.random(Integer.MAX_VALUE);
    }
}
