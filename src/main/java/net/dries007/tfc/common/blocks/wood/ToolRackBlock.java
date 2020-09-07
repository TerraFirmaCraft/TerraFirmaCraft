/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.common.blocks.wood;

import javax.annotation.Nullable;

import net.minecraft.block.*;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;

public class ToolRackBlock extends Block implements IWaterLoggable
{
    public static final DirectionProperty FACING;
    public static final BooleanProperty WATERLOGGED;
    protected static final VoxelShape RACK_EAST_AABB;
    protected static final VoxelShape RACK_WEST_AABB;
    protected static final VoxelShape RACK_SOUTH_AABB;
    protected static final VoxelShape RACK_NORTH_AABB;

    static
    {
        FACING = HorizontalBlock.HORIZONTAL_FACING;
        WATERLOGGED = BlockStateProperties.WATERLOGGED;
        RACK_EAST_AABB = Block.makeCuboidShape(0.0D, 3.0D, 0.0D, 2.0D, 12.0D, 16.0D);
        RACK_WEST_AABB = Block.makeCuboidShape(14.0D, 3.0D, 0.0D, 16.0D, 12.0D, 16.0D);
        RACK_SOUTH_AABB = Block.makeCuboidShape(0.0D, 3.0D, 0.0D, 16.0D, 12.0D, 2.0D);
        RACK_NORTH_AABB = Block.makeCuboidShape(0.0D, 3.0D, 14.0D, 16.0D, 12.0D, 16.0D);
    }

    public ToolRackBlock(Properties properties)
    {
        super(properties);
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos)
    {
        if (facing.getOpposite() == stateIn.get(FACING) && !stateIn.isValidPosition(worldIn, currentPos))
        {
            return Blocks.AIR.getDefaultState();
        }
        else if (stateIn.get(WATERLOGGED))
        {
            worldIn.getPendingFluidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickRate(worldIn));
        }
        return super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
    {
        switch (state.get(FACING))
        {
            case NORTH:
                return RACK_NORTH_AABB;
            case SOUTH:
                return RACK_SOUTH_AABB;
            case WEST:
                return RACK_WEST_AABB;
            case EAST:
            default:
                return RACK_EAST_AABB;
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos)
    {
        Direction direction = state.get(FACING);
        return this.canAttachTo(worldIn, pos.offset(direction.getOpposite()), direction);
    }

    @Nullable
    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        BlockState contextualState;
        if (!context.replacingClickedOnBlock())
        {
            contextualState = context.getWorld().getBlockState(context.getPos().offset(context.getFace().getOpposite()));
            if (contextualState.getBlock() == this && contextualState.get(FACING) == context.getFace())
            {
                return null;
            }
        }

        contextualState = this.getDefaultState();
        IWorldReader iworldreader = context.getWorld();
        BlockPos blockpos = context.getPos();
        IFluidState ifluidstate = iworldreader.getFluidState(context.getPos());
        Direction[] directionList = context.getNearestLookingDirections();

        for (Direction direction : directionList)
        {
            if (direction.getAxis().isHorizontal())
            {
                contextualState = contextualState.with(FACING, direction.getOpposite());
                if (contextualState.isValidPosition(iworldreader, blockpos))
                {
                    return contextualState.with(WATERLOGGED, ifluidstate.getFluid() == Fluids.WATER);
                }
            }
        }

        return null;
    }

    @Override
    @SuppressWarnings("deprecation")
    public IFluidState getFluidState(BlockState state)
    {
        return state.get(WATERLOGGED) ? Fluids.WATER.getStillFluidState(false) : super.getFluidState(state);
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) { builder.add(FACING, WATERLOGGED); }

    private boolean canAttachTo(IBlockReader blockReader, BlockPos pos, Direction directionIn)
    {
        BlockState blockstate = blockReader.getBlockState(pos);
        return !blockstate.canProvidePower() && blockstate.isSolidSide(blockReader, pos, directionIn);
    }
}
