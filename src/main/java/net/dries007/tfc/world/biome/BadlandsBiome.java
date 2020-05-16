/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.biome;

import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.world.noise.INoise2D;
import net.dries007.tfc.world.noise.SimplexNoise2D;

public class BadlandsBiome extends TFCBiome
{
    public BadlandsBiome(BiomeTemperature temperature, BiomeRainfall rainfall)
    {
        super(new TFCBiome.Builder().category(Category.MESA), temperature, rainfall);
    }

    @Override
    public INoise2D createNoiseLayer(long seed)
    {
        // Normal flat noise, lowered by inverted power-ridge noise, looks like badlands
        final INoise2D ridgeNoise = new SimplexNoise2D(seed).octaves(4).ridged().spread(0.04f).map(x -> 1.3f * -(x > 0 ? (float) Math.pow(x, 3.2f) : 0.5f * x)).scaled(-1f, 0.3f, -1f, 1f).terraces(16).scaled(-20, 0);
        return new SimplexNoise2D(seed).octaves(6).spread(0.08f).scaled(TFCConfig.COMMON.seaLevel.get() + 22, TFCConfig.COMMON.seaLevel.get() + 32).add(ridgeNoise);
    }
}
