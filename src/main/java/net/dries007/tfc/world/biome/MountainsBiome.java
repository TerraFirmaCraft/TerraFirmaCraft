/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.biome;

import net.minecraft.block.BlockState;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilder;

import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.world.noise.INoise2D;
import net.dries007.tfc.world.noise.SimplexNoise2D;
import net.dries007.tfc.world.surfacebuilder.TFCSurfaceBuilders;

import net.minecraft.world.biome.Biome.Builder;
import net.minecraft.world.biome.Biome.Category;

public class MountainsBiome extends TFCBiome
{
    private final float baseHeight;
    private final float scaleHeight;
    private final boolean isOceanMountains;

    public MountainsBiome(float baseHeight, float scaleHeight, boolean isOceanMountains, BiomeTemperature temperature, BiomeRainfall rainfall)
    {
        super(new Builder().biomeCategory(Category.EXTREME_HILLS), temperature, rainfall);

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

            setSurfaceBuilder(TFCSurfaceBuilders.MOUNTAINS.get(), SurfaceBuilder.CONFIG_OCEAN_SAND);
        });
    }

    @Override
    public INoise2D createNoiseLayer(long seed)
    {
        final int seaLevel = TFCConfig.COMMON.seaLevel.get();

        final INoise2D baseNoise = new SimplexNoise2D(seed) // A simplex noise forms the majority of the base
            .octaves(6) // High octaves to create highly fractal terrain
            .spread(0.14f)
            .add(new SimplexNoise2D(seed + 1) // Ridge noise is added to mimic real mountain ridges. It is scaled smaller than the base noise to not be overpowering
                .octaves(4)
                .ridged() // Ridges are applied after octaves as it creates less directional artifacts this way
                .spread(0.02f)
                .scaled(-0.7f, 0.7f))
            .map(x -> 0.125f * (x + 1) * (x + 1) * (x + 1)) // Power scaled, flattens most areas but maximizes peaks
            .map(x -> seaLevel + baseHeight + scaleHeight * x); // Scale the entire thing to mountain ranges

        // Cliff noise consists of noise that's been artificially clamped over half the domain, which is then selectively added above a base height level
        // This matches up with the distinction between dirt and stone
        final INoise2D cliffNoise = new SimplexNoise2D(seed + 2).octaves(2).map(x -> x > 0 ? x : 0).spread(0.01f).scaled(-25, 25);
        final INoise2D cliffHeightNoise = new SimplexNoise2D(seed + 3).octaves(2).spread(0.01f).scaled(-20, 20);

        return (x, z) -> {
            float height = baseNoise.noise(x, z);
            if (height > 120) // Only sample each cliff noise layer if the base noise could be influenced by it
            {
                float cliffHeight = cliffHeightNoise.noise(x, z);
                if (height > 140 + cliffHeight)
                {
                    float cliff = cliffNoise.noise(x, z);
                    return height + cliff;
                }
            }
            return height;
        };
    }

    @Override
    public BlockState getWaterState()
    {
        return isOceanMountains ? SALT_WATER : FRESH_WATER;
    }
}