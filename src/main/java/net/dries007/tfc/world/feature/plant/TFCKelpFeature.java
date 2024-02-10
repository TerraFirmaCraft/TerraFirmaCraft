/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature.plant;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.GrowingPlantHeadBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.material.Fluid;

import net.dries007.tfc.common.fluids.FluidHelpers;
import net.dries007.tfc.util.Helpers;

public class TFCKelpFeature extends Feature<ColumnPlantConfig>
{
    public TFCKelpFeature(Codec<ColumnPlantConfig> codec)
    {
        super(codec);
    }

    public boolean place(FeaturePlaceContext<ColumnPlantConfig> context)
    {
        final WorldGenLevel level = context.level();
        final BlockPos pos = context.origin();
        final RandomSource rand = context.random();
        final ColumnPlantConfig config = context.config();

        final BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
        final int radius = config.radius();

        boolean placedAny = false;

        for (int i = 0; i < config.tries(); i++)
        {
            mutablePos.setWithOffset(pos, rand.nextInt(radius) - rand.nextInt(radius), 0, rand.nextInt(radius) - rand.nextInt(radius));
            mutablePos.set(level.getHeightmapPos(Heightmap.Types.OCEAN_FLOOR, mutablePos));

            final BlockState state = level.getBlockState(mutablePos);
            final Fluid fluid = state.getFluidState().getType();
            if (fluid.getFluidType().isAir())
            {
                continue;
            }
            final BlockState bodyState = FluidHelpers.fillWithFluid(config.bodyState(), fluid);
            final BlockState headState = FluidHelpers.fillWithFluid(config.headState(), fluid);

            if (bodyState != null && headState != null && canPlaceBlockAt(level, mutablePos, state))
            {
                placeColumn(level, rand, mutablePos, Mth.nextInt(rand, config.minHeight(), config.maxHeight()), 17, 25, bodyState, headState);
                placedAny = true;
            }
        }
        return placedAny;
    }

    private void placeColumn(LevelAccessor level, RandomSource rand, BlockPos.MutableBlockPos mutablePos, int height, int minAge, int maxAge, BlockState body, BlockState head)
    {
        for (int i = 1; i <= height; ++i)
        {
            if (canPlaceBlockAt(level, mutablePos, body))
            {
                if (i == height || !FluidHelpers.isEmptyFluid(level.getBlockState(mutablePos.above())))
                {
                    if (!Helpers.isBlock(level.getBlockState(mutablePos.below()), head.getBlock()))
                    {
                        level.setBlock(mutablePos, head.setValue(GrowingPlantHeadBlock.AGE, Mth.nextInt(rand, minAge, maxAge)), 16);
                    }
                    return;
                }
                level.setBlock(mutablePos, body, 16);
            }
            mutablePos.move(Direction.UP);
        }
    }

    private boolean canPlaceBlockAt(LevelAccessor level, BlockPos pos, BlockState state)
    {
        return state.canSurvive(level, pos) && FluidHelpers.isEmptyFluid(level.getBlockState(pos));
    }
}
