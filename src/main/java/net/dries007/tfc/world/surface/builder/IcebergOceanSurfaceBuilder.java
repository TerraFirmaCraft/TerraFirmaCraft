/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.surface.builder;

import com.google.common.collect.ImmutableList;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.RandomSource;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.synth.PerlinSimplexNoise;
import net.minecraft.world.level.material.Material;

import net.dries007.tfc.util.climate.OverworldClimateModel;
import net.dries007.tfc.world.surface.SurfaceBuilderContext;
import net.dries007.tfc.world.surface.SurfaceState;
import net.dries007.tfc.world.surface.SurfaceStates;

public class IcebergOceanSurfaceBuilder implements SurfaceBuilder
{
    public static final SurfaceBuilderFactory INSTANCE = IcebergOceanSurfaceBuilder::new;

    private final PerlinSimplexNoise icebergNoise;
    private final PerlinSimplexNoise icebergRoofNoise;

    public IcebergOceanSurfaceBuilder(long seed)
    {
        WorldgenRandom random = new WorldgenRandom(new LegacyRandomSource(seed));

        this.icebergNoise = new PerlinSimplexNoise(random, ImmutableList.of(-3, -2, -1, 0));
        this.icebergRoofNoise = new PerlinSimplexNoise(random, ImmutableList.of(0));
    }

    @Override
    public void buildSurface(SurfaceBuilderContext context, int startY, int endY)
    {
        final int x = context.pos().getX(), z = context.pos().getZ();
        final double noise = 0; // todo: better noise?

        final BlockState packedIce = Blocks.PACKED_ICE.defaultBlockState();
        final BlockState snowBlock = Blocks.SNOW_BLOCK.defaultBlockState();

        final int seaLevel = context.getSeaLevel();
        final RandomSource random = context.random();

        double icebergMaxY = 0.0D;
        double icebergMinY = 0.0D;

        final float maxAnnualTemperature = OverworldClimateModel.getAverageMonthlyTemperature(new BlockPos(x, seaLevel, z), context.averageTemperature(), 1);

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

        SurfaceState underState = SurfaceStates.LOW_UNDERWATER;
        int surfaceDepth = -1;
        int currentSnowLayers = 0;
        int maximumSnowLayers = 2 + random.nextInt(4);
        int minimumSnowY = seaLevel + 18 + random.nextInt(10);

        int surfaceY = 0;
        boolean firstLayer = false;
        SurfaceState surfaceState = SurfaceStates.RAW;

        for (int y = Math.max(startY, (int) icebergMaxY + 1); y >= 0; --y)
        {
            BlockState stateAt = context.getBlockState(y);

            // Place packed ice, both above and below water
            if (stateAt.isAir() && y < (int) icebergMaxY && random.nextDouble() > 0.01D)
            {
                context.setBlockState(y, packedIce);
                stateAt = packedIce;
            }
            else if (stateAt.getMaterial() == Material.WATER && y > (int) icebergMinY && y < seaLevel && icebergMinY != 0.0D && random.nextDouble() > 0.15D)
            {
                context.setBlockState(y, packedIce);
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
                    surfaceDepth = context.calculateAltitudeSlopeSurfaceDepth(surfaceY, 3, 0.1, 0);
                    surfaceState = SurfaceStates.TOP_UNDERWATER;
                    if (surfaceDepth > 0)
                    {
                        context.setBlockState(y, surfaceState);
                    }
                }
                else if (surfaceDepth > 0)
                {
                    // Subsurface layers
                    surfaceDepth--;
                    context.setBlockState(y, surfaceState);
                    if (surfaceDepth == 0 && firstLayer)
                    {
                        // Next subsurface layer
                        firstLayer = false;
                        surfaceDepth = context.calculateAltitudeSlopeSurfaceDepth(surfaceY, 7, 0.3, 0);
                        surfaceState = underState;
                    }
                }
            }
            else if (stateAt.is(Blocks.PACKED_ICE) && currentSnowLayers <= maximumSnowLayers && y > minimumSnowY)
            {
                context.setBlockState(y, snowBlock);
                ++currentSnowLayers;
            }
        }
    }
}
