package net.dries007.tfc.world;

import net.minecraft.world.level.biome.Climate;

public enum NoopClimateSampler implements Climate.Sampler
{
    INSTANCE;

    private static final Climate.TargetPoint TARGET = new Climate.TargetPoint(0, 0, 0, 0, 0, 0);

    @Override
    public Climate.TargetPoint sample(int x, int y, int z)
    {
        return TARGET;
    }
}
