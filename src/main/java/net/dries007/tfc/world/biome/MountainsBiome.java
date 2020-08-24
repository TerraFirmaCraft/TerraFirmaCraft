/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.biome;

import net.minecraft.block.BlockState;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilder;

import net.dries007.tfc.world.noise.INoise2D;
import net.dries007.tfc.world.noise.SimplexNoise2D;
import net.dries007.tfc.world.surfacebuilder.TFCSurfaceBuilders;

public class MountainsBiome extends TFCBiome
{
    private final float baseHeight;
    private final float scaleHeight;
    private final boolean isOceanMountains;

    public MountainsBiome(float baseHeight, float scaleHeight, boolean isOceanMountains, BiomeTemperature temperature, BiomeRainfall rainfall)
    {
        super(new Builder().category(Category.EXTREME_HILLS), temperature, rainfall);

        this.baseHeight = baseHeight;
        this.scaleHeight = scaleHeight;
        this.isOceanMountains = isOceanMountains;

        biomeFeatures.enqueue(() -> {
            if (isOceanMountains)
            {
                TFCDefaultBiomeFeatures.addOceanCarvers(this);
            }
            else
            {
                TFCDefaultBiomeFeatures.addCarvers(this);
            }

            setSurfaceBuilder(TFCSurfaceBuilders.MOUNTAINS.get(), SurfaceBuilder.GRASS_DIRT_SAND_CONFIG);
        });
    }

    @Override
    public INoise2D createNoiseLayer(long seed)
    {
        // Power scaled noise, looks like mountains over large area
        //final INoise2D mountainNoise = new SimplexNoise2D(seed).octaves(6).spread(0.14f).map(x -> 2.67f * (float) Math.pow(0.5f * (x + 1), 3.2f) - 0.8f);
        //return (x, z) -> TFCConfig.COMMON.seaLevel.get() + baseHeight + scaleHeight * mountainNoise.noise(x, z);


        // High octave simplex noise forms a base height map for the mountains
        final INoise2D baseNoise = new SimplexNoise2D(seed).octaves(6).spread(0.14f);

        // Ridge noise is added to create mountain ridges. This both functions as more octaves (thus higher range) and as variation
        final INoise2D ridgeNoise = new SimplexNoise2D(seed + 1).octaves(4).ridged().spread(0.02f).scaled(-0.7f, 0.7f);

        // Add the base and ridge noise, and power scale it. This flattens lower areas, and creates higher peaks in select areas
        final INoise2D composited = baseNoise.add(ridgeNoise).map(in -> 0.125f * (in + 1) * (in + 1) * (in + 1));

        // Cliff noise consists of noise that's been artificially clamped over half the domain, which is then selectively added above a base height level
        final INoise2D cliffNoise = new SimplexNoise2D(seed + 2).octaves(2).map(x -> x > 0 ? x : 0).spread(0.01f).scaled(-0.6f, 0.6f);
        final INoise2D cliffHeightNoise = new SimplexNoise2D(seed + 3).octaves(2).spread(0.01f).scaled(-0.1f, 0.1f);

        // Add the cliff noise based on the current height, and an additional cliff start height noise
        final INoise2D result = (x, z) -> {
            // Only sample noise if necessary
            float height = composited.noise(x, z);
            if (height > 0.5)
            {
                float cliffHeight = cliffHeightNoise.noise(x, z);
                if (height > 0.6 + cliffHeight)
                {
                    float cliff = cliffNoise.noise(x, z);
                    return height + cliff;
                }
            }
            return height;
        };

        final INoise2D scaled = result.scaled(40, 200);

        return (x, y) -> scaled.noise(x, y) - 30;
    }

    @Override
    public BlockState getWaterState()
    {
        return isOceanMountains ? SALT_WATER : FRESH_WATER;
    }
}
