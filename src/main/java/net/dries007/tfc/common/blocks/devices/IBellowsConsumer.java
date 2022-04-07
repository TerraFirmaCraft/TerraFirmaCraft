/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.devices;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public interface IBellowsConsumer
{
    /**
     * Determines if the block can intake air from a directionMapper.
     *
     * @param state
     * @param level
     * @param pos
     * @param facing
     * @return true if input is allowed
     */
    boolean canAcceptAir(BlockState state, Level level, BlockPos pos, Direction facing);

    /**
     * Called when the block gets air from a valid directionMapper.
     *
     * @param state
     * @param level
     * @param pos
     * @param facing
     * @param amount
     */
    void intakeAir(BlockState state, Level level, BlockPos pos, Direction facing, int amount);
}
