/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.fluids.FluidHelpers;


public final class EnvironmentHelpers
{
    public static boolean isAdjacentToNotWater(LevelAccessor level, BlockPos pos)
    {
        final BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
        for (Direction direction : Direction.Plane.HORIZONTAL)
        {
            if (!level.isWaterAt(mutablePos.setWithOffset(pos, direction)))
            {
                return true;
            }
        }
        return false;
    }

    public static boolean isWorldgenReplaceable(WorldGenLevel level, BlockPos pos)
    {
        return isWorldgenReplaceable(level.getBlockState(pos));
    }

    public static boolean isWorldgenReplaceable(BlockState state)
    {
        return FluidHelpers.isAirOrEmptyFluid(state) || Helpers.isBlock(state, TFCTags.Blocks.SINGLE_BLOCK_REPLACEABLE);
    }

    public static boolean canPlaceBushOn(WorldGenLevel level, BlockPos pos)
    {
        return isWorldgenReplaceable(level, pos) && Helpers.isBlock(level.getBlockState(pos.below()), TFCTags.Blocks.BUSH_PLANTABLE_ON);
    }

    public static boolean isOnSturdyFace(WorldGenLevel level, BlockPos pos)
    {
        pos = pos.below();
        return level.getBlockState(pos).isFaceSturdy(level, pos, Direction.UP);
    }
}
