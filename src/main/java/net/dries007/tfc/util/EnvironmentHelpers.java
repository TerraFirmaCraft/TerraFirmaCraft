/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.fluids.FluidHelpers;

import static net.dries007.tfc.util.climate.OverworldClimateModel.*;

public final class EnvironmentHelpers
{
    public static float adjustAvgTempForElev(int y, float averageTemp)
    {
        return adjustAvgTempForElev(y, averageTemp, SEA_LEVEL);
    }

    public static float adjustAvgTempForElev(int y, float averageTemp, float seaLevel)
    {
        if (y > seaLevel)
        {
            // -1.6 C / 10 blocks above sea level
            float elevationTemperature = Mth.clamp((y - seaLevel) * 0.16225f, 0, 17.822f);
            return averageTemp - elevationTemperature;
        }
        else
        {
            //Average temp doesn't vary below sea level
            return averageTemp;
        }
    }

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
