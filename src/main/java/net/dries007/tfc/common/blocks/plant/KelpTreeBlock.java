/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.plant;

import java.util.Random;

import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.StateContainer;
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
{
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
        registerDefaultState(stateDefinition.any().setValue(NORTH, Boolean.FALSE).setValue(EAST, Boolean.FALSE).setValue(SOUTH, Boolean.FALSE).setValue(WEST, Boolean.FALSE).setValue(UP, Boolean.FALSE).setValue(DOWN, Boolean.FALSE).setValue(getFluidProperty(), getFluidProperty().keyFor(Fluids.EMPTY)));
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        return getStateForPlacement(context.getLevel(), context.getClickedPos());
    }

    @Override
    public void playerWillDestroy(World worldIn, BlockPos pos, BlockState state, PlayerEntity player)
    {
        updateFluid(worldIn, state, pos);
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(getFluidProperty());
        builder.add(NORTH, EAST, SOUTH, WEST, UP, DOWN);
    }

    public BlockState getStateForPlacement(IBlockReader world, BlockPos pos)
    {
        Block downBlock = world.getBlockState(pos.below()).getBlock();
        Block upBlock = world.getBlockState(pos.above()).getBlock();
        Block northBlock = world.getBlockState(pos.north()).getBlock();
        Block eastBlock = world.getBlockState(pos.east()).getBlock();
        Block southBlock = world.getBlockState(pos.south()).getBlock();
        Block westBlock = world.getBlockState(pos.west()).getBlock();
        return defaultBlockState()
            .setValue(DOWN, downBlock.is(TFCTags.Blocks.KELP_TREE) || downBlock.is(TFCTags.Blocks.SEA_BUSH_PLANTABLE_ON))
            .setValue(UP, upBlock.is(TFCTags.Blocks.KELP_TREE))
            .setValue(NORTH, northBlock.is(TFCTags.Blocks.KELP_TREE))
            .setValue(EAST, eastBlock.is(TFCTags.Blocks.KELP_TREE))
            .setValue(SOUTH, southBlock.is(TFCTags.Blocks.KELP_TREE))
            .setValue(WEST, westBlock.is(TFCTags.Blocks.KELP_TREE));
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos)
    {
        if (!stateIn.canSurvive(worldIn, currentPos))
        {
            worldIn.getBlockTicks().scheduleTick(currentPos, this, 1);
            updateFluid(worldIn, stateIn, currentPos);
            return stateIn;
        }
        else
        {
            updateFluid(worldIn, stateIn, currentPos);
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
     * {@link ChorusPlantBlock#canSurvive}
     */
    @Override
    @SuppressWarnings("deprecation")
    public boolean canSurvive(BlockState state, IWorldReader worldIn, BlockPos pos)
    {
        BlockState belowState = worldIn.getBlockState(pos.below());
        for (Direction direction : Direction.Plane.HORIZONTAL)
        {
            BlockPos relativePos = pos.relative(direction);
            if (worldIn.getBlockState(relativePos).getBlock().is(TFCTags.Blocks.KELP_BRANCH))
            {

                Block below = worldIn.getBlockState(relativePos.below()).getBlock();
                if (below.is(TFCTags.Blocks.KELP_BRANCH) || below.is(TFCTags.Blocks.SEA_BUSH_PLANTABLE_ON))
                {
                    return true;
                }
            }
        }
        Block blockIn = belowState.getBlock();
        return blockIn.is(TFCTags.Blocks.KELP_BRANCH) || blockIn.is(TFCTags.Blocks.SEA_BUSH_PLANTABLE_ON);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void tick(BlockState state, ServerWorld worldIn, BlockPos pos, Random rand)
    {
        if (!state.canSurvive(worldIn, pos))
        {
            worldIn.destroyBlock(pos, true);
        }
    }

    private void updateFluid(IWorld world, BlockState state, BlockPos pos)
    {
        final Fluid containedFluid = state.getValue(getFluidProperty()).getFluid();
        if (containedFluid != Fluids.EMPTY)
        {
            world.getLiquidTicks().scheduleTick(pos, containedFluid, containedFluid.getTickDelay(world));
        }
    }
}
