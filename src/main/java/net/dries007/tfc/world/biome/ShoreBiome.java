/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.biome;

import net.minecraft.world.biome.Biome;

import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.world.noise.INoise2D;
import net.dries007.tfc.world.noise.SimplexNoise2D;

public class ShoreBiome extends TFCBiome
{
    public ShoreBiome(BiomeTemperature temperature, BiomeRainfall rainfall)
    {
        super(new Biome.Builder().category(Category.BEACH), temperature, rainfall);
    }

    @Override
    public INoise2D createNoiseLayer(long seed)
    {
        int seaLevel = TFCConfig.COMMON.seaLevel.get();
        return new SimplexNoise2D(seed).octaves(2).spread(0.17f).scaled(seaLevel, seaLevel + 1.8f);
    }
}
