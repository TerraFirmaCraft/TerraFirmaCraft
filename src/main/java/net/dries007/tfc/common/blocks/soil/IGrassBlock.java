/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.soil;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.lighting.LightEngine;

/**
 * Grass blocks, which MUST
 * 1. react to connected texture based properties (see {  ConnectedGrassBlock}
 * 2. can be converted to dirt
 */
public interface IGrassBlock extends ISoilBlock
{
    /**
     * Like {  net.minecraft.block.SpreadableSnowyDirtBlock#canBeGrass(BlockState, IWorldReader, BlockPos)}, but omits the requirement that snow layers only be one thick.
     * Represents if the current block state can be grass
     */
    default boolean canBeGrass(BlockState state, IWorldReader world, BlockPos pos)
    {
        BlockPos posUp = pos.up();
        BlockState stateUp = world.getBlockState(posUp);
        if (stateUp.isIn(Blocks.SNOW))
        {
            return true;
        }
        else if (stateUp.getFluidState().getLevel() == 8)
        {
            return false;
        }
        else
        {//get light block into
            return LightEngine.func_215613_a(world, state, pos, stateUp, posUp, Direction.UP, stateUp.getLightValue(world, posUp)) < world.getMaxLightLevel();
        }
    }

    /**
     * Like {  net.minecraft.block.SpreadableSnowyDirtBlock#canPropagate(BlockState, IWorldReader, BlockPos)}
     * Represents if the current grass can spread to the given location.
     *
     * @param state The grass state to place
     */
    default boolean canPropagate(BlockState state, IWorldReader world, BlockPos pos)
    {
        BlockPos posUp = pos.up();
        return canBeGrass(state, world, pos) && !world.getFluidState(posUp).isTagged(FluidTags.WATER);
    }
}
