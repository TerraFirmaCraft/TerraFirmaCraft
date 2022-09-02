/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world;

import java.util.List;

import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.levelgen.DensityFunctions;

public final class NoopClimateSampler
{
    public static final Climate.Sampler INSTANCE = new Climate.Sampler(DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero(), List.of());
}
