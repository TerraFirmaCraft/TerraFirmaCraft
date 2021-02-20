/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.plant;

import java.util.Random;

import net.dries007.tfc.common.blocks.TFCBlockStateProperties;
import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.fluids.FluidProperty;
import net.dries007.tfc.common.fluids.IFluidLoggable;

public abstract class KelpTreeBlock extends SixWayBlock implements IFluidLoggable
{    public static final BooleanProperty NORTH = BlockStateProperties.NORTH;
    public static final BooleanProperty EAST = BlockStateProperties.EAST;
    public static final BooleanProperty SOUTH = BlockStateProperties.SOUTH;
    public static final BooleanProperty WEST = BlockStateProperties.WEST;
    public static final BooleanProperty UP = BlockStateProperties.UP;
    public static final BooleanProperty DOWN = BlockStateProperties.DOWN;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static KelpTreeBlock create(AbstractBlock.Properties builder, FluidProperty fluid)
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

    protected KelpTreeBlock(AbstractBlock.Properties builder)
    {
        super(0.3125F, builder);
        setDefaultState(getDefaultState().with(NORTH, Boolean.FALSE)
            .with(EAST, Boolean.FALSE)
            .with(SOUTH, Boolean.FALSE)
            .with(WEST, Boolean.FALSE)
            .with(UP, Boolean.FALSE)
            .with(DOWN, Boolean.FALSE)
            .with(WATERLOGGED,Boolean.FALSE)
            .with(getFluidProperty(), getFluidProperty().keyFor(Fluids.EMPTY)));
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        return getStateForPlacement(context.getWorld(), context.getPos());
    }

    public BlockState getStateForPlacement(IBlockReader world, BlockPos pos)
    {
        Block downBlock = world.getBlockState(pos.down()).getBlock();
        Block upBlock = world.getBlockState(pos.up()).getBlock();
        Block northBlock = world.getBlockState(pos.north()).getBlock();
        Block eastBlock = world.getBlockState(pos.east()).getBlock();
        Block southBlock = world.getBlockState(pos.south()).getBlock();
        Block westBlock = world.getBlockState(pos.west()).getBlock();
        return getDefaultState()
            .with(DOWN, downBlock.isIn(TFCTags.Blocks.KELP_TREE) || downBlock.isIn(TFCTags.Blocks.SEA_BUSH_PLANTABLE_ON))
            .with(UP, upBlock.isIn(TFCTags.Blocks.KELP_TREE))
            .with(NORTH, northBlock.isIn(TFCTags.Blocks.KELP_TREE))
            .with(EAST, eastBlock.isIn(TFCTags.Blocks.KELP_TREE))
            .with(SOUTH, southBlock.isIn(TFCTags.Blocks.KELP_TREE))
            .with(WATERLOGGED,Boolean.FALSE)
            .with(WEST, westBlock.isIn(TFCTags.Blocks.KELP_TREE));
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos)
    {
        if (!stateIn.blockNeedsPostProcessing(worldIn, currentPos))
        {
            worldIn.getPendingBlockTicks().scheduleTick(currentPos, this, 1);
            updateFluid(worldIn, stateIn, currentPos);
            return stateIn;
        }
        else
        {
            updateFluid(worldIn, stateIn, currentPos);
            boolean flag = facingState.isIn(TFCTags.Blocks.KELP_TREE) || (facing == Direction.DOWN && facingState.isIn(TFCTags.Blocks.SEA_BUSH_PLANTABLE_ON));
            return stateIn.with(FACING_TO_PROPERTY_MAP.get(facing), flag);
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public void tick(BlockState state, ServerWorld worldIn, BlockPos pos, Random rand)
    {
        if (!state.blockNeedsPostProcessing(worldIn, pos))
        {
            worldIn.destroyBlock(pos, true);
        }
    }

    @Override
    public void onBlockHarvested(World worldIn, BlockPos pos, BlockState state, PlayerEntity player)
    {
        updateFluid(worldIn, state, pos);
    }

    /**
     * {  ChorusPlantBlock#isValidPosition}
     */
    @Override
    @SuppressWarnings("deprecation")
    public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos)
    {
        BlockState belowState = worldIn.getBlockState(pos.down());
        for (Direction direction : Direction.Plane.HORIZONTAL)
        {
            BlockPos relativePos = pos.offset(direction);
            if (worldIn.getBlockState(relativePos).getBlock().isIn(TFCTags.Blocks.KELP_BRANCH))
            {

                Block below = worldIn.getBlockState(relativePos.down()).getBlock();
                if (below.isIn(TFCTags.Blocks.KELP_BRANCH) || below.isIn(TFCTags.Blocks.SEA_BUSH_PLANTABLE_ON))
                {
                    return true;
                }
            }
        }
        Block blockIn = belowState.getBlock();
        return blockIn.isIn(TFCTags.Blocks.KELP_BRANCH) || blockIn.isIn(TFCTags.Blocks.SEA_BUSH_PLANTABLE_ON);
    }

    @Override
    @SuppressWarnings("deprecation")
    public FluidState getFluidState(BlockState state)
    {
        return IFluidLoggable.super.getFluidState(state);
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
    {
        super.fillStateContainer(builder);
        builder.add(getFluidProperty());
        builder.add(NORTH, EAST, SOUTH, WEST, UP, DOWN,WATERLOGGED);
    }

    private void updateFluid(IWorld world, BlockState state, BlockPos pos)
    {
        final Fluid containedFluid = state.get(getFluidProperty()).getFluid();
        if (containedFluid != Fluids.EMPTY)
        {
            world.getPendingFluidTicks().scheduleTick(pos, containedFluid, containedFluid.getTickRate(world));
        }
    }
}
