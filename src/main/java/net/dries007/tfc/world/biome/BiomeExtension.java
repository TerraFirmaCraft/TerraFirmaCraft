/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.biome;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;

/**
 * This is a wrapper class around extra data, which is assigned to particular biomes
 * This extension is tracked in {@link TFCBiomes} by registry key.
 */
public record BiomeExtension(ResourceKey<Biome> key, BiomeTemperature temperature, BiomeRainfall rainfall, BiomeVariants variants)
{
    @SuppressWarnings("ConstantConditions")
    public static final BiomeExtension EMPTY = new BiomeExtension(null, BiomeTemperature.NORMAL, BiomeRainfall.NORMAL, null);
}
