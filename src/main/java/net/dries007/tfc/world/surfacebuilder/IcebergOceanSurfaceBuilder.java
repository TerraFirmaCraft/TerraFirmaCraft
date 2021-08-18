/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.surfacebuilder;

import java.util.Random;
import java.util.stream.IntStream;

import com.google.common.collect.ImmutableList;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.surfacebuilders.SurfaceBuilderBaseConfiguration;
import net.minecraft.world.level.levelgen.synth.PerlinSimplexNoise;
import net.minecraft.world.level.material.Material;

import com.mojang.serialization.Codec;
import net.dries007.tfc.util.Climate;

/**
 * Modified from {@link net.minecraft.world.level.levelgen.surfacebuilders.FrozenOceanSurfaceBuilder}
 */
public class IcebergOceanSurfaceBuilder extends SeededSurfaceBuilder<SurfaceBuilderBaseConfiguration>
{
    private PerlinSimplexNoise icebergNoise;
    private PerlinSimplexNoise icebergRoofNoise;

    public IcebergOceanSurfaceBuilder(Codec<SurfaceBuilderBaseConfiguration> codec)
    {
        super(codec);
    }

    @Override
    public void apply(SurfaceBuilderContext context, Biome biome, int x, int z, int startHeight, int minSurfaceHeight, double noise, double slope, float temperature, float rainfall, boolean saltWater, SurfaceBuilderBaseConfiguration config)
    {
        final BlockState packedIce = Blocks.PACKED_ICE.defaultBlockState();
        final BlockState snowBlock = Blocks.SNOW_BLOCK.defaultBlockState();

        final int seaLevel = context.getSeaLevel();
        final Random random = context.getRandom();

        double icebergMaxY = 0.0D;
        double icebergMinY = 0.0D;

        final BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos().set(x, startHeight, z);
        final float maxAnnualTemperature = Climate.calculateMonthlyAverageTemperature(z, seaLevel, context.getChunkData().getAverageTemp(mutablePos), 1);

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

        SurfaceState underState = SurfaceStates.LOW_UNDERWATER;
        int surfaceDepth = -1;
        int currentSnowLayers = 0;
        int maximumSnowLayers = 2 + random.nextInt(4);
        int minimumSnowY = seaLevel + 18 + random.nextInt(10);

        int surfaceY = 0;
        boolean firstLayer = false;
        SurfaceState surfaceState = SurfaceStates.RAW;

        mutablePos.set(localX, startHeight, localZ);
        for (int y = Math.max(startHeight, (int) icebergMaxY + 1); y >= 0; --y)
        {
            mutablePos.setY(y);

            BlockState stateAt = context.getBlockState(mutablePos);

            // Place packed ice, both above and below water
            if (stateAt.isAir() && y < (int) icebergMaxY && random.nextDouble() > 0.01D)
            {
                context.setBlockState(mutablePos, packedIce);
                stateAt = packedIce;
            }
            else if (stateAt.getMaterial() == Material.WATER && y > (int) icebergMinY && y < seaLevel && icebergMinY != 0.0D && random.nextDouble() > 0.15D)
            {
                context.setBlockState(mutablePos, packedIce);
                stateAt = packedIce;
            }

            // After iceberg placement, continue with standard surface builder replacements
            if (stateAt.isAir())
            {
                surfaceDepth = -1;
            }
            else if (context.isDefaultBlock(stateAt))
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
        WorldgenRandom random = new WorldgenRandom(seed);
        this.icebergNoise = new PerlinSimplexNoise(random, IntStream.rangeClosed(-3, 0));
        this.icebergRoofNoise = new PerlinSimplexNoise(random, ImmutableList.of(0));
    }
}
