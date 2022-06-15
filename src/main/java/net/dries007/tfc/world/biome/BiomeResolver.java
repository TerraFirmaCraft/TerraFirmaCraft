/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.biome;

import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;

@FunctionalInterface
public interface BiomeResolver
{
    Holder<Biome> sample(BiomeExtension variants);
}
