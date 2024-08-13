/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.surface.builder;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.BlockColumn;
import net.minecraft.world.level.levelgen.SurfaceSystem;
import net.minecraft.world.level.levelgen.XoroshiroRandomSource;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.util.climate.OverworldClimateModel;
import net.dries007.tfc.world.surface.SurfaceBuilderContext;
import net.dries007.tfc.world.surface.SurfaceState;
import net.dries007.tfc.world.surface.SurfaceStates;

public class OceanSurfaceBuilder implements SurfaceBuilder
{
    public static final SurfaceBuilderFactory INSTANCE = OceanSurfaceBuilder::new;

    private final NormalNoise icebergPillarNoise;
    private final NormalNoise icebergPillarRoofNoise;
    private final NormalNoise icebergSurfaceNoise;

    /**
     * {@link net.minecraft.data.worldgen.NoiseData} for values
     * {@link SurfaceSystem}'s constructor for the specific noises used
     */
    public OceanSurfaceBuilder(long seed)
    {
        final RandomSource generator = new XoroshiroRandomSource(seed);

        this.icebergPillarNoise = NormalNoise.create(generator.fork(), new NormalNoise.NoiseParameters(-6, 1.0D, 1.0D, 1.0D, 1.0D));
        this.icebergPillarRoofNoise = NormalNoise.create(generator.fork(), new NormalNoise.NoiseParameters(-3, 1.0D));
        this.icebergSurfaceNoise = NormalNoise.create(generator.fork(), new NormalNoise.NoiseParameters(-6, 1.0D, 1.0D, 1.0D));
    }

    @Override
    public void buildSurface(SurfaceBuilderContext context, int startY, int endY)
    {
        final SurfaceState topState = context.averageTemperature() > 0f ? SurfaceStates.SANDY_WHEN_NEAR_SEA_LEVEL : SurfaceStates.GRAVEL;
        NormalSurfaceBuilder.INSTANCE.buildSurface(context, startY, endY, context.getSlope() < 5 ? SurfaceStates.GRASS : topState, SurfaceStates.DIRT, SurfaceStates.SANDSTONE_OR_GRAVEL);
        frozenOceanExtension(context, startY, endY);
    }

    /**
     * Modified from {@link net.minecraft.world.level.levelgen.SurfaceSystem#frozenOceanExtension(int, Biome, BlockColumn, BlockPos.MutableBlockPos, int, int, int)}
     */
    private void frozenOceanExtension(SurfaceBuilderContext context, int startY, int endY)
    {
        final BlockState packedIce = Blocks.PACKED_ICE.defaultBlockState();
        final BlockState snow = Blocks.SNOW_BLOCK.defaultBlockState();

        final int seaLevel = context.getSeaLevel();
        final int x = context.pos().getX();
        final int z = context.pos().getZ();
        final RandomSource random = context.random();

        final OverworldClimateModel model = OverworldClimateModel.getIfPresent(context.level());
        if (model == null)
        {
            return;
        }

        final float maxAnnualTemperature = model.getAverageMonthlyTemperature(z, seaLevel, context.averageTemperature(), 1);
        if (maxAnnualTemperature > 2)
        {
            // This is run for all climates, and needs to exist early if we possibly can.
            return;
        }

        final float temperatureFactor = Mth.clampedMap(maxAnnualTemperature, -4, 2, 0, 6);

        final double baseNoise = Math.min(
            Math.abs(icebergSurfaceNoise.getValue(x, 0, z) * 8.25),
            icebergPillarNoise.getValue(x * 1.28, 0, z * 1.28) * 15
        );

        if (baseNoise > 1.8)
        {
            final double pillarNoise = Math.abs(icebergPillarRoofNoise.getValue(x * 1.17, 0, z * 1.17) * 1.5);
            double icebergMaxY = Math.min(
                baseNoise * baseNoise * 1.2,
                Math.ceil(pillarNoise * 40) + 14
            );

            icebergMaxY -= temperatureFactor;

            final double icebergMinY;
            if (icebergMaxY > 2)
            {
                icebergMinY = seaLevel - icebergMaxY - 7;
                icebergMaxY += seaLevel;
            }
            else
            {
                icebergMaxY = 0;
                icebergMinY = 0;
            }

            final int snowDepth = 2 + random.nextInt(4);
            final int snowBoundaryY = seaLevel + 18 + random.nextInt(10);

            int placedSnow = 0;
            for (int y = Math.max(startY, (int) icebergMaxY + 1); y >= endY; --y)
            {
                final BlockState state = context.getBlockState(y);
                if ((state.isAir() && y < icebergMaxY && random.nextDouble() > 0.01)
                    || ((state.getBlock() == TFCBlocks.SALT_WATER.get() || state.getBlock() == Blocks.WATER) && y > (int) icebergMinY && y < seaLevel && icebergMinY != 0 && random.nextDouble() > 0.15))
                {
                    if (placedSnow <= snowDepth && y > snowBoundaryY)
                    {
                        context.setBlockState(y, snow);
                        ++placedSnow;
                    }
                    else
                    {
                        context.setBlockState(y, packedIce);
                    }
                }
            }
        }
    }
}
