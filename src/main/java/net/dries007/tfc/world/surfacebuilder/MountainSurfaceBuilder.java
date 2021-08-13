/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.surfacebuilder;


import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilderConfig;

import com.mojang.serialization.Codec;
import net.dries007.tfc.world.noise.Noise2D;
import net.dries007.tfc.world.noise.OpenSimplex2D;

public class MountainSurfaceBuilder extends SeededSurfaceBuilder<SurfaceBuilderConfig>
{
    private Noise2D surfaceMaterialNoise;

    public MountainSurfaceBuilder(Codec<SurfaceBuilderConfig> codec)
    {
        super(codec);
    }

    @Override
    public void apply(SurfaceBuilderContext context, Biome biome, int x, int z, int startHeight, double noise, double slope, float temperature, float rainfall, boolean saltWater, SurfaceBuilderConfig config)
    {
        final NormalSurfaceBuilder surfaceBuilder = TFCSurfaceBuilders.NORMAL.get();
        final double heightNoise = noise * 3f + startHeight;
        if (heightNoise > 130)
        {
            float surfaceMaterialValue = surfaceMaterialNoise.noise(x, z) + 0.1f * context.getRandom().nextFloat() - 0.05f;
            if (surfaceMaterialValue > 0.3f)
            {
                surfaceBuilder.apply(context, x, z, startHeight, slope, temperature, rainfall, saltWater, SurfaceStates.COBBLE, SurfaceStates.COBBLE, SurfaceStates.RAW);
            }
            else if (surfaceMaterialValue < -0.3f)
            {
                surfaceBuilder.apply(context, x, z, startHeight, slope, temperature, rainfall, saltWater, SurfaceStates.GRAVEL, SurfaceStates.GRAVEL, SurfaceStates.RAW);
            }
            else
            {
                surfaceBuilder.apply(context, x, z, startHeight, slope, temperature, rainfall, saltWater, SurfaceStates.RAW, SurfaceStates.RAW, SurfaceStates.RAW);
            }
        }
        else
        {
            surfaceBuilder.apply(context, biome, x, z, startHeight, noise, slope, temperature, rainfall, saltWater, config);
        }
    }

    @Override
    protected void initSeed(long seed)
    {
        surfaceMaterialNoise = new OpenSimplex2D(seed).octaves(2).spread(0.02f);
    }
}