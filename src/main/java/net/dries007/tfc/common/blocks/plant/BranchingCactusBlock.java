/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.plant;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;

import net.dries007.tfc.common.blocks.ExtendedProperties;

public class BranchingCactusBlock extends PipePlantBlock
{
    public static BranchingCactusBlock createBody(ExtendedProperties properties)
    {
        return new BranchingCactusBlock(0.3125f, properties);
    }

    public BranchingCactusBlock(float size, ExtendedProperties properties)
    {
        super(size, properties);
        registerDefaultState(stateDefinition.any().setValue(NORTH, false).setValue(EAST, false).setValue(SOUTH, false).setValue(WEST, false).setValue(UP, false).setValue(DOWN, false));
    }

    @Override
    protected boolean testDown(BlockState state)
    {
        return PlantBlock.isDryBlockPlantable(state) || state.getBlock() instanceof BranchingCactusBlock;
    }

    @Override
    protected boolean testUp(BlockState state)
    {
        return state.getBlock() instanceof BranchingCactusBlock || state.getBlock() instanceof PlantBlock;
    }

    @Override
    protected boolean testHorizontal(BlockState state)
    {
        return state.getBlock() instanceof BranchingCactusBlock;
    }

    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity)
    {
        entity.hurt(entity.damageSources().cactus(), 1f);
    }

    @Override
    protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType)
    {
        return false;
    }

    @Override
    protected boolean canGrowLongSideways()
    {
        return true;
    }
}
