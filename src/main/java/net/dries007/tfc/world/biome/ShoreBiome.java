/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.biome;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilder;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilderConfig;

import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.world.noise.INoise2D;
import net.dries007.tfc.world.noise.SimplexNoise2D;
import net.dries007.tfc.world.surfacebuilder.TFCSurfaceBuilders;

public class ShoreBiome extends TFCBiome
{
    public ShoreBiome(boolean isSandy, BiomeTemperature temperature, BiomeRainfall rainfall)
    {
        super(new Biome.Builder().category(Category.BEACH), temperature, rainfall);

        biomeFeatures.enqueue(() -> {
            TFCDefaultBiomeFeatures.addCarvers(this);

            SurfaceBuilderConfig config = isSandy ? TFCSurfaceBuilders.SANDSTONE_CONFIG : TFCSurfaceBuilders.RED_SANDSTONE_CONFIG;
            setSurfaceBuilder(SurfaceBuilder.DEFAULT, config);
        });
    }

    @Override
    public INoise2D createNoiseLayer(long seed)
    {
        int seaLevel = TFCConfig.COMMON.seaLevel.get();
        return new SimplexNoise2D(seed).octaves(2).spread(0.17f).scaled(seaLevel, seaLevel + 1.8f);
    }
}
