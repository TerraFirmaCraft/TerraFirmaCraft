/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.surfacebuilder;

import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.surfacebuilders.SurfaceBuilderBaseConfiguration;

import com.mojang.serialization.Codec;
import net.dries007.tfc.world.noise.Noise2D;
import net.dries007.tfc.world.noise.OpenSimplex2D;

public class ShoreSurfaceBuilder extends SeededSurfaceBuilder<SurfaceBuilderBaseConfiguration>
{
    private Noise2D variantNoise;

    public ShoreSurfaceBuilder(Codec<SurfaceBuilderBaseConfiguration> codec)
    {
        super(codec);
    }

    @Override
    public void apply(SurfaceBuilderContext context, Biome biome, int x, int z, int startHeight, double noise, double slope, float temperature, float rainfall, boolean saltWater, SurfaceBuilderBaseConfiguration config)
    {
        float variantNoiseValue = variantNoise.noise(x, z);
        if (variantNoiseValue > 0.6f)
        {
            TFCSurfaceBuilders.NORMAL.get().apply(context, x, z, startHeight, slope, temperature, rainfall, saltWater, SurfaceStates.RARE_SHORE_SAND, SurfaceStates.RARE_SHORE_SAND, SurfaceStates.RARE_SHORE_SANDSTONE);
        }
        else
        {
            TFCSurfaceBuilders.NORMAL.get().apply(context, x, z, startHeight, slope, temperature, rainfall, saltWater, SurfaceStates.SHORE_SAND, SurfaceStates.SHORE_SAND, SurfaceStates.SHORE_SANDSTONE);
        }
    }

    @Override
    protected void initSeed(long seed)
    {
        variantNoise = new OpenSimplex2D(seed).octaves(2).spread(0.003f).abs();
    }
}