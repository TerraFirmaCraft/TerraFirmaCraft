/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.biome;

import net.minecraft.world.gen.surfacebuilders.SurfaceBuilder;

import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.world.noise.INoise2D;
import net.dries007.tfc.world.noise.SimplexNoise2D;
import net.dries007.tfc.world.surfacebuilder.TFCSurfaceBuilders;

import net.dries007.tfc.world.biome.ITFCBiome.LargeGroup;
import net.dries007.tfc.world.biome.ITFCBiome.SmallGroup;
import net.minecraft.world.biome.Biome.Builder;
import net.minecraft.world.biome.Biome.Category;

public class RiverBiome extends TFCBiome
{
    public RiverBiome(BiomeTemperature temperature, BiomeRainfall rainfall)
    {
        super(new Builder().biomeCategory(Category.RIVER), temperature, rainfall);

        biomeFeatures.enqueue(() -> {
            TFCDefaultBiomeFeatures.addCarvers(this);
            setSurfaceBuilder(TFCSurfaceBuilders.UNDERWATER.get(), SurfaceBuilder.CONFIG_EMPTY);
        });
    }

    @Override
    public INoise2D createNoiseLayer(long seed)
    {
        return new SimplexNoise2D(seed).octaves(6).spread(0.17f).scaled(TFCConfig.COMMON.seaLevel.get() - 6, TFCConfig.COMMON.seaLevel.get() - 1);
    }

    @Override
    public LargeGroup getLargeGroup()
    {
        return LargeGroup.RIVER;
    }

    @Override
    public SmallGroup getMediumGroup()
    {
        return SmallGroup.RIVER;
    }
}