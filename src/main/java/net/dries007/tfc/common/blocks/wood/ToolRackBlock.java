/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.common.blocks.wood;

import javax.annotation.Nullable;

import net.minecraft.block.*;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
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
    public static final DirectionProperty FACING = HorizontalBlock.FACING;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    public static final VoxelShape RACK_EAST_AABB = Block.box(0.0D, 3.0D, 0.0D, 2.0D, 12.0D, 16.0D);
    public static final VoxelShape RACK_WEST_AABB = Block.box(14.0D, 3.0D, 0.0D, 16.0D, 12.0D, 16.0D);
    public static final VoxelShape RACK_SOUTH_AABB = Block.box(0.0D, 3.0D, 0.0D, 16.0D, 12.0D, 2.0D);
    public static final VoxelShape RACK_NORTH_AABB = Block.box(0.0D, 3.0D, 14.0D, 16.0D, 12.0D, 16.0D);

    public ToolRackBlock(Properties properties)
    {
        super(properties);
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos)
    {
        if (facing.getOpposite() == stateIn.getValue(FACING) && !stateIn.canSurvive(worldIn, currentPos))
        {
            return Blocks.AIR.defaultBlockState();
        }
        else if (stateIn.getValue(WATERLOGGED))
        {
            worldIn.getLiquidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(worldIn));
        }
        return super.updateShape(stateIn, facing, facingState, worldIn, currentPos, facingPos);
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
    {
        switch (state.getValue(FACING))
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
    public boolean canSurvive(BlockState state, IWorldReader worldIn, BlockPos pos)
    {
        Direction direction = state.getValue(FACING);
        return canAttachTo(worldIn, pos.relative(direction.getOpposite()), direction);
    }

    @Nullable
    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        BlockState contextualState;
        if (!context.replacingClickedOnBlock())
        {
            contextualState = context.getLevel().getBlockState(context.getClickedPos().relative(context.getClickedFace().getOpposite()));
            if (contextualState.getBlock() == this && contextualState.getValue(FACING) == context.getClickedFace())
            {
                return null;
            }
        }

        contextualState = defaultBlockState();
        IWorldReader world = context.getLevel();
        BlockPos pos = context.getClickedPos();
        FluidState fluidState = world.getFluidState(context.getClickedPos());
        Direction[] directionList = context.getNearestLookingDirections();

        for (Direction direction : directionList)
        {
            if (direction.getAxis().isHorizontal())
            {
                contextualState = contextualState.setValue(FACING, direction.getOpposite());
                if (contextualState.canSurvive(world, pos))
                {
                    return contextualState.setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER);
                }
            }
        }

        return null;
    }

    @Override
    @SuppressWarnings("deprecation")
    public FluidState getFluidState(BlockState state)
    {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(FACING, WATERLOGGED);
    }

    private boolean canAttachTo(IBlockReader blockReader, BlockPos pos, Direction directionIn)
    {
        BlockState blockstate = blockReader.getBlockState(pos);
        return !blockstate.isSignalSource() && blockstate.isFaceSturdy(blockReader, pos, directionIn);
    }
}