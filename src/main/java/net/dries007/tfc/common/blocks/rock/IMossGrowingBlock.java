/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.rock;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

import net.dries007.tfc.common.fluids.FluidHelpers;
import net.dries007.tfc.util.Helpers;

/**
 * A {@link Block} which can be converted to a mossy variant. Existing mossy blocks, on random ticks, will attempt to call
 * {@link #convertToMossy(Level, BlockPos, BlockState, boolean)} on nearby blocks. The destination block is responsible for
 * checking if it can be converted into a mossy block (typically by requiring the presence of nearby water)
 */
public interface IMossGrowingBlock
{
    /**
     * Converts the block at {@code pos} to a mossy block, if it is able.
     * @param needsWater If {@code false}, the destination block should ignore any checks for nearby water.
     */
    void convertToMossy(Level level, BlockPos pos, BlockState state, boolean needsWater);

    /**
     * @return {@code true} if the block at {@code pos} is adjacent to any water or waterlogged block.
     */
    default boolean isAdjacentToWater(Level level, BlockPos pos)
    {
        final BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        for (Direction direction : Helpers.DIRECTIONS)
        {
            final FluidState state = level.getFluidState(cursor.setWithOffset(pos, direction));
            if (FluidHelpers.isSame(state, Fluids.WATER))
            {
                return true;
            }
        }
        return false;
    }
}
