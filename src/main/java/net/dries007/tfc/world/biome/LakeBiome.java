/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.biome;

import net.minecraft.world.gen.surfacebuilders.SurfaceBuilder;

import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.world.noise.INoise2D;
import net.dries007.tfc.world.noise.SimplexNoise2D;

public class LakeBiome extends TFCBiome
{
    public LakeBiome(BiomeTemperature temperature, BiomeRainfall rainfall)
    {
        super(new Builder().category(Category.RIVER), temperature, rainfall);

        biomeFeatures.enqueue(() -> {
            TFCDefaultBiomeFeatures.addCarvers(this);
            setSurfaceBuilder(SurfaceBuilder.DEFAULT, TFCDefaultBiomeFeatures.getUnderwaterSurfaceConfig(this));
        });
    }

    @Override
    public INoise2D createNoiseLayer(long seed)
    {
        return new SimplexNoise2D(seed).octaves(4).spread(0.15f).scaled(TFCConfig.COMMON.seaLevel.get() - 12, TFCConfig.COMMON.seaLevel.get() - 2);
    }

    @Override
    public LargeGroup getLargeGroup()
    {
        return LargeGroup.LAKE;
    }
}
