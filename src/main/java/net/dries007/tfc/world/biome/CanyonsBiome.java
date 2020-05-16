/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.biome;

import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.world.noise.INoise2D;
import net.dries007.tfc.world.noise.SimplexNoise2D;

public class CanyonsBiome extends TFCBiome
{
    private final float minHeight;
    private final float maxHeight;

    public CanyonsBiome(float minHeight, float maxHeight, BiomeTemperature temperature, BiomeRainfall rainfall)
    {
        super(new Builder().category(Category.EXTREME_HILLS), temperature, rainfall);

        this.minHeight = minHeight;
        this.maxHeight = maxHeight;
    }

    @Override
    public INoise2D createNoiseLayer(long seed)
    {
        final INoise2D warpX = new SimplexNoise2D(seed).octaves(4).spread(0.1f).scaled(-30, 30);
        final INoise2D warpZ = new SimplexNoise2D(seed + 1).octaves(4).spread(0.1f).scaled(-30, 30);
        return new SimplexNoise2D(seed).octaves(4).spread(0.2f).warped(warpX, warpZ).map(x -> x > 0.4 ? x - 0.8f : -x).scaled(-0.4f, 0.8f, TFCConfig.COMMON.seaLevel.get() + minHeight, TFCConfig.COMMON.seaLevel.get() + maxHeight).spread(0.3f);
    }
}
