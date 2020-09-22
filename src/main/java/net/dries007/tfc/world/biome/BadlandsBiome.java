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

import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biome.Category;

public class BadlandsBiome extends TFCBiome
{
    public BadlandsBiome(BiomeTemperature temperature, BiomeRainfall rainfall)
    {
        super(new Biome.Builder().biomeCategory(Category.MESA), temperature, rainfall);

        biomeFeatures.enqueue(() -> {
            TFCDefaultBiomeFeatures.addCarvers(this);
            setSurfaceBuilder(TFCSurfaceBuilders.BADLANDS.get(), SurfaceBuilder.CONFIG_OCEAN_SAND);
        });
    }

    @Override
    public INoise2D createNoiseLayer(long seed)
    {
        // Normal flat noise, lowered by inverted power-ridge noise, looks like badlands
        int seaLevel = TFCConfig.COMMON.seaLevel.get();
        return new SimplexNoise2D(seed)
            .octaves(6)
            .spread(0.08f)
            .scaled(seaLevel + 22, seaLevel + 32)
            .add(new SimplexNoise2D(seed + 1)
                .octaves(4)
                .ridged()
                .spread(0.04f)
                .map(x -> 1.3f * -(x > 0 ? x * x * x : 0.5f * x))
                .scaled(-1f, 0.3f, -1f, 1f)
                .terraces(17)
                .scaled(-22, 0)
            )
            .map(x -> x < seaLevel ? seaLevel - 0.3f * (seaLevel - x) : x);
    }
}