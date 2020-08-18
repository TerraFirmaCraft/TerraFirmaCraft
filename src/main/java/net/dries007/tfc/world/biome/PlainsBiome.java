/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.biome;

import net.dries007.tfc.world.surfacebuilder.TFCSurfaceBuilders;

public class PlainsBiome extends FlatBiome
{
    public PlainsBiome(BiomeTemperature temperature, BiomeRainfall rainfall)
    {
        super(new Builder().category(Category.PLAINS), 4, 10, temperature, rainfall);

        biomeFeatures.enqueue(() -> {
            TFCDefaultBiomeFeatures.addCarvers(this);
            setSurfaceBuilder(TFCSurfaceBuilders.DEEP.get(), TFCSurfaceBuilders.GRASS_DIRT_GRAVEL_GRAVEL_CONFIG);
        });
    }
}
