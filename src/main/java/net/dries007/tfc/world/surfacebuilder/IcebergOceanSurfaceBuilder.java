/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.surfacebuilder;

import java.util.Random;
import java.util.stream.IntStream;

import com.google.common.collect.ImmutableList;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.PerlinNoiseGenerator;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilderConfig;

import com.mojang.serialization.Codec;
import net.dries007.tfc.util.Climate;
import net.dries007.tfc.world.TFCChunkGenerator;

/**
 * Modified from {@link net.minecraft.world.gen.surfacebuilders.FrozenOceanSurfaceBuilder}
 */
public class IcebergOceanSurfaceBuilder extends SeededSurfaceBuilder<SurfaceBuilderConfig>
{
    private PerlinNoiseGenerator icebergNoise;
    private PerlinNoiseGenerator icebergRoofNoise;

    public IcebergOceanSurfaceBuilder(Codec<SurfaceBuilderConfig> codec)
    {
        super(codec);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void apply(SurfaceBuilderContext context, Biome biome, int x, int z, int startHeight, double noise, double slope, float temperature, float rainfall, boolean saltWater, SurfaceBuilderConfig config)
    {
        final BlockState packedIce = Blocks.PACKED_ICE.defaultBlockState();
        final BlockState snowBlock = Blocks.SNOW_BLOCK.defaultBlockState();

        final int seaLevel = context.getSeaLevel();
        final Random random = context.getRandom();

        double icebergMaxY = 0.0D;
        double icebergMinY = 0.0D;

        final BlockPos.Mutable mutablePos = new BlockPos.Mutable().set(x, startHeight, z);
        final float maxAnnualTemperature = Climate.calculateMonthlyAverageTemperature(z, TFCChunkGenerator.SEA_LEVEL, context.getChunkData().getAverageTemp(mutablePos), 1);

        double thresholdTemperature = -1f;
        double cutoffTemperature = 3f;
        double icebergValue = Math.min(Math.abs(noise), icebergNoise.getValue(x * 0.1D, z * 0.1D, false) * 15.0D);
        icebergValue += (thresholdTemperature - maxAnnualTemperature) * 0.2f;
        if (maxAnnualTemperature > thresholdTemperature)
        {
            icebergValue *= Math.max(0, (cutoffTemperature - maxAnnualTemperature) / (cutoffTemperature - thresholdTemperature));
        }
        if (icebergValue > 1.8D)
        {
            final double icebergRoofValue = Math.abs(icebergRoofNoise.getValue(x * 0.09765625D, z * 0.09765625D, false));
            final double maxIcebergRoofValue = Math.ceil(icebergRoofValue * 40.0D) + 14.0D;

            icebergMaxY = icebergValue * icebergValue * 1.2D;
            if (icebergMaxY > maxIcebergRoofValue)
            {
                icebergMaxY = maxIcebergRoofValue;
            }

            if (icebergMaxY > 2.0D)
            {
                icebergMinY = (double) seaLevel - icebergMaxY - 7.0D;
                icebergMaxY = icebergMaxY + (double) seaLevel;
            }
            else
            {
                icebergMaxY = 0.0D;
            }
        }

        final int localX = x & 15;
        final int localZ = z & 15;

        ISurfaceState underState = SurfaceStates.LOW_UNDERWATER;
        int surfaceDepth = -1;
        int currentSnowLayers = 0;
        int maximumSnowLayers = 2 + random.nextInt(4);
        int minimumSnowY = seaLevel + 18 + random.nextInt(10);

        int surfaceY = 0;
        boolean firstLayer = false;
        ISurfaceState surfaceState = SurfaceStates.RAW;

        for (int y = Math.max(startHeight, (int) icebergMaxY + 1); y >= 0; --y)
        {
            mutablePos.set(localX, y, localZ);

            // Place packed ice, both above and below water
            if (context.getBlockState(mutablePos).isAir() && y < (int) icebergMaxY && random.nextDouble() > 0.01D)
            {
                context.setBlockState(mutablePos, packedIce);
            }
            else if (context.getBlockState(mutablePos).getMaterial() == Material.WATER && y > (int) icebergMinY && y < seaLevel && icebergMinY != 0.0D && random.nextDouble() > 0.15D)
            {
                context.setBlockState(mutablePos, packedIce);
            }

            // After iceberg placement, continue with standard surface builder replacements
            BlockState stateAt = context.getBlockState(mutablePos);
            if (stateAt.isAir())
            {
                surfaceDepth = -1;
            }
            else if (stateAt.getBlock() == context.getDefaultBlock().getBlock())
            {
                if (surfaceDepth == -1)
                {
                    // Reached surface. Place top state and switch to subsurface layers
                    surfaceY = y;
                    firstLayer = true;
                    surfaceDepth = calculateAltitudeSlopeSurfaceDepth(surfaceY, slope, 3, 0.1, 0);
                    surfaceState = SurfaceStates.TOP_UNDERWATER;
                    if (surfaceDepth > 0)
                    {
                        context.setBlockState(mutablePos, surfaceState, temperature, rainfall, saltWater);
                    }
                }
                else if (surfaceDepth > 0)
                {
                    // Subsurface layers
                    surfaceDepth--;
                    context.setBlockState(mutablePos, surfaceState, temperature, rainfall, saltWater);
                    if (surfaceDepth == 0 && firstLayer)
                    {
                        // Next subsurface layer
                        firstLayer = false;
                        surfaceDepth = calculateAltitudeSlopeSurfaceDepth(surfaceY, slope, 7, 0.3, 0);
                        surfaceState = underState;
                    }
                }
                else if (surfaceDepth == 0)
                {
                    // Underground layers
                    context.setBlockState(mutablePos, SurfaceStates.RAW, temperature, rainfall, saltWater);
                }
            }
            else if (stateAt.getBlock() == context.getDefaultFluid().getBlock())
            {
                context.setBlockState(mutablePos, SurfaceStates.WATER, temperature, rainfall, saltWater);
            }
            else if (stateAt.is(Blocks.PACKED_ICE) && currentSnowLayers <= maximumSnowLayers && y > minimumSnowY)
            {
                context.setBlockState(mutablePos, snowBlock);
                ++currentSnowLayers;
            }
        }
    }

    @Override
    protected void initSeed(long seed)
    {
        SharedSeedRandom random = new SharedSeedRandom(seed);
        this.icebergNoise = new PerlinNoiseGenerator(random, IntStream.rangeClosed(-3, 0));
        this.icebergRoofNoise = new PerlinNoiseGenerator(random, ImmutableList.of(0));
    }
}
