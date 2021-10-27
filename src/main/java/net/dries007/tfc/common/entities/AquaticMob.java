package net.dries007.tfc.common.entities;

import net.minecraft.world.level.material.Fluid;

public interface AquaticMob
{
    boolean canSpawnIn(Fluid fluid);
}
