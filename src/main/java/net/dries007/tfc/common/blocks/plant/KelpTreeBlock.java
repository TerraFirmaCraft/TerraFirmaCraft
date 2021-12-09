/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.plant;

import java.util.Random;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.fluids.FluidProperty;
import net.dries007.tfc.common.fluids.IFluidLoggable;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public abstract class KelpTreeBlock extends PipeBlock implements IFluidLoggable
{
    public static KelpTreeBlock create(BlockBehaviour.Properties builder, FluidProperty fluid)
    {
        return new KelpTreeBlock(builder)
        {
            @Override
            public FluidProperty getFluidProperty()
            {
                return fluid;
            }
        };
    }

    protected KelpTreeBlock(BlockBehaviour.Properties builder)
    {
        super(0.3125F, builder);
        registerDefaultState(stateDefinition.any().setValue(NORTH, Boolean.FALSE).setValue(EAST, Boolean.FALSE).setValue(SOUTH, Boolean.FALSE).setValue(WEST, Boolean.FALSE).setValue(UP, Boolean.FALSE).setValue(DOWN, Boolean.FALSE).setValue(getFluidProperty(), getFluidProperty().keyFor(Fluids.EMPTY)));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        return getStateForPlacement(context.getLevel(), context.getClickedPos());
    }

    @Override
    public void playerWillDestroy(Level worldIn, BlockPos pos, BlockState state, Player player)
    {
        updateFluid(worldIn, state, pos);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(getFluidProperty());
        builder.add(NORTH, EAST, SOUTH, WEST, UP, DOWN);
    }

    public BlockState getStateForPlacement(BlockGetter world, BlockPos pos)
    {
        Block downBlock = world.getBlockState(pos.below()).getBlock();
        Block upBlock = world.getBlockState(pos.above()).getBlock();
        Block northBlock = world.getBlockState(pos.north()).getBlock();
        Block eastBlock = world.getBlockState(pos.east()).getBlock();
        Block southBlock = world.getBlockState(pos.south()).getBlock();
        Block westBlock = world.getBlockState(pos.west()).getBlock();
        return defaultBlockState()
            .setValue(DOWN, TFCTags.Blocks.KELP_TREE.contains(downBlock) || TFCTags.Blocks.SEA_BUSH_PLANTABLE_ON.contains(downBlock))
            .setValue(UP, TFCTags.Blocks.KELP_TREE.contains(upBlock))
            .setValue(NORTH, TFCTags.Blocks.KELP_TREE.contains(northBlock))
            .setValue(EAST, TFCTags.Blocks.KELP_TREE.contains(eastBlock))
            .setValue(SOUTH, TFCTags.Blocks.KELP_TREE.contains(southBlock))
            .setValue(WEST, TFCTags.Blocks.KELP_TREE.contains(westBlock));
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos)
    {
        if (!stateIn.canSurvive(level, currentPos))
        {
            level.scheduleTick(currentPos, this, 1);
            updateFluid(level, stateIn, currentPos);
            return stateIn;
        }
        else
        {
            updateFluid(level, stateIn, currentPos);
            boolean flag = facingState.is(TFCTags.Blocks.KELP_TREE) || (facing == Direction.DOWN && facingState.is(TFCTags.Blocks.SEA_BUSH_PLANTABLE_ON));
            return stateIn.setValue(PROPERTY_BY_DIRECTION.get(facing), flag);
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public FluidState getFluidState(BlockState state)
    {
        return IFluidLoggable.super.getFluidState(state);
    }

    /**
     * {@link net.minecraft.world.level.block.ChorusPlantBlock#canSurvive}
     */
    @Override
    @SuppressWarnings("deprecation")
    public boolean canSurvive(BlockState state, LevelReader worldIn, BlockPos pos)
    {
        BlockState belowState = worldIn.getBlockState(pos.below());
        for (Direction direction : Direction.Plane.HORIZONTAL)
        {
            BlockPos relativePos = pos.relative(direction);
            if (TFCTags.Blocks.KELP_BRANCH.contains(worldIn.getBlockState(relativePos).getBlock()))
            {

                Block below = worldIn.getBlockState(relativePos.below()).getBlock();
                if (TFCTags.Blocks.KELP_BRANCH.contains(below) || TFCTags.Blocks.SEA_BUSH_PLANTABLE_ON.contains(below))
                {
                    return true;
                }
            }
        }
        Block blockIn = belowState.getBlock();
        return TFCTags.Blocks.KELP_BRANCH.contains(blockIn) || TFCTags.Blocks.SEA_BUSH_PLANTABLE_ON.contains(blockIn);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void tick(BlockState state, ServerLevel worldIn, BlockPos pos, Random rand)
    {
        if (!state.canSurvive(worldIn, pos))
        {
            worldIn.destroyBlock(pos, true);
        }
    }

    private void updateFluid(LevelAccessor level, BlockState state, BlockPos pos)
    {
        final Fluid containedFluid = state.getValue(getFluidProperty()).getFluid();
        if (containedFluid != Fluids.EMPTY)
        {
            level.scheduleTick(pos, containedFluid, containedFluid.getTickDelay(level));
        }
    }
}
