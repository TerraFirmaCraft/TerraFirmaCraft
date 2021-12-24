/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

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
