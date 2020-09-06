/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.biome;

import net.minecraft.world.gen.surfacebuilders.SurfaceBuilder;

import net.dries007.tfc.world.surfacebuilder.TFCSurfaceBuilders;

public class PlainsBiome extends FlatBiome
{
    public PlainsBiome(BiomeTemperature temperature, BiomeRainfall rainfall)
    {
        super(new Builder().category(Category.PLAINS), 4, 10, temperature, rainfall);

        biomeFeatures.enqueue(() -> {
            TFCDefaultBiomeFeatures.addCarvers(this);
            setSurfaceBuilder(TFCSurfaceBuilders.DEEP.get(), SurfaceBuilder.GRASS_DIRT_GRAVEL_CONFIG);
        });
    }
}
