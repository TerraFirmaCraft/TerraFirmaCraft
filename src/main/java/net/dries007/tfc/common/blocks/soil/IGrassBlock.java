/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.soil;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.lighting.LightEngine;

import net.dries007.tfc.util.Helpers;

/**
 * Grass blocks, which MUST
 * 1. react to connected texture based properties (see {@link ConnectedGrassBlock}
 * 2. can be converted to dirt
 */
public interface IGrassBlock extends ISoilBlock
{
    /**
     * Like {@link net.minecraft.world.level.block.SpreadingSnowyDirtBlock#canBeGrass(BlockState, LevelReader, BlockPos)}, but omits the requirement that snow layers only be one thick.
     * Represents if the current block state can be grass
     */
    default boolean canBeGrass(BlockState state, LevelReader world, BlockPos pos)
    {
        BlockPos posUp = new BlockPos(pos.getX(), pos.getY() + 1, pos.getZ());
        BlockState stateUp = world.getBlockState(posUp);
        if (Helpers.isBlock(stateUp, BlockTags.SNOW))
        {
            return true;
        }
        else if (stateUp.getFluidState().getAmount() == 8)
        {
            return false;
        }
        else
        {
            return LightEngine.getLightBlockInto(world, state, pos, stateUp, posUp, Direction.UP, stateUp.getLightBlock(world, posUp)) < world.getMaxLightLevel();
        }
    }

    /**
     * Like {@link net.minecraft.world.level.block.SpreadingSnowyDirtBlock#canPropagate(BlockState, LevelReader, BlockPos)}
     * Represents if the current grass can spread to the given location.
     *
     * @param state The grass state to place
     */
    default boolean canPropagate(BlockState state, LevelReader level, BlockPos pos)
    {
        final BlockPos posUp = new BlockPos(pos.getX(), pos.getY() + 1, pos.getZ());
        return canBeGrass(state, level, pos) && level.getFluidState(posUp).isEmpty();
    }
}
