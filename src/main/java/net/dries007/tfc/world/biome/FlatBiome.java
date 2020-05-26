/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.biome;

import net.minecraft.world.biome.Biome;

import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.world.noise.INoise2D;
import net.dries007.tfc.world.noise.SimplexNoise2D;

public class FlatBiome extends TFCBiome
{
    private final float minHeight;
    private final float maxHeight;

    protected FlatBiome(Biome.Builder builder, float minHeight, float maxHeight, BiomeTemperature temperature, BiomeRainfall rainfall)
    {
        super(builder, temperature, rainfall);
        this.minHeight = minHeight;
        this.maxHeight = maxHeight;
    }

    @Override
    public INoise2D createNoiseLayer(long seed)
    {
        return new SimplexNoise2D(seed).octaves(6).spread(0.17f).scaled(TFCConfig.COMMON.seaLevel.get() + minHeight, TFCConfig.COMMON.seaLevel.get() + maxHeight);
    }
}
