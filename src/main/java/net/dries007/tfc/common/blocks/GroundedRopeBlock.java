/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.fluids.FluidHelpers;
import net.dries007.tfc.common.fluids.IFluidLoggable;
import net.dries007.tfc.util.Helpers;

public class GroundedRopeBlock extends AbstractRopeBlock implements IFluidLoggable, IForgeBlockExtension
{
    public static final BooleanProperty ASCENDING = TFCBlockStateProperties.ASCENDING;

    public static final VoxelShape[] SHAPES_ASCENDING = Helpers.computeHorizontalShapes(dir ->
        Shapes.or(
            Helpers.rotateShape(dir, 7, 0, 0, 9, 2, 16),
            Helpers.rotateShape(dir, 7, 2, 0, 9, 4, 14),
            Helpers.rotateShape(dir, 7, 4, 0, 9, 6, 12),
            Helpers.rotateShape(dir, 7, 6, 0, 9, 8, 10),
            Helpers.rotateShape(dir, 7, 8, 0, 9, 10, 8),
            Helpers.rotateShape(dir, 7, 10, 0, 9, 12, 6),
            Helpers.rotateShape(dir, 7, 12, 0, 9, 14, 4),
            Helpers.rotateShape(dir, 7, 14, 0, 9, 16, 2)
        ));

    public GroundedRopeBlock(ExtendedProperties properties)
    {
        super(properties);
        registerDefaultState(getStateDefinition().any().setValue(FLUID, FLUID.keyFor(Fluids.EMPTY)).setValue(ASCENDING, false));
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos)
    {
        return isAttached(level, pos, state);
    }

    public boolean isAttached(LevelReader level, BlockPos pos, BlockState state)
    {
        final Direction facing = state.getValue(FACING);
        BlockPos offsetPos = pos.relative(facing);
        if (state.getValue(ASCENDING))
        {
            final BlockState aboveState = level.getBlockState(pos.above());
            if (aboveState.getBlock() instanceof HangingRopeBlock && aboveState.getValue(FACING) == facing)
            {
                return isBlockBelowSturdy(level, pos);
            }
            offsetPos = offsetPos.above();
        }
        final BlockState offsetState = level.getBlockState(offsetPos);
        return isBlockBelowSturdy(level, pos) && (
            (offsetState.getBlock() instanceof GroundedRopeBlock && offsetState.getValue(FACING) == facing) ||
            (offsetState.getBlock() instanceof RopeAnchorBlock && offsetState.getValue(FACING) == facing.getOpposite())
        );
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston)
    {
        final BlockPos possibleRopePos = pos.below().relative(state.getValue(FACING).getOpposite());
        if (level.getBlockState(possibleRopePos).getBlock() instanceof AbstractRopeBlock)
        {
            level.destroyBlock(possibleRopePos, true);
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        if (state.getValue(ASCENDING))
        {
            return SHAPES_ASCENDING[state.getValue(FACING).get2DDataValue()];
        }
        else
        {
            return state.getValue(FACING).getAxis() == Direction.Axis.X ? SHAPE_Z : SHAPE_X;
        }
    }

    @Override
    protected VoxelShape getVisualShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return Shapes.empty();
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context)
    {
        final FluidState fluid = context.getLevel().getFluidState(context.getClickedPos());
        BlockState state = super.getStateForPlacement(context);
        if (state == null)
            return null;

        state = state.setValue(FACING, context.getHorizontalDirection());
        if (isAttached(context.getLevel(), context.getClickedPos(), state.setValue(ASCENDING, false)))
        {
            return FluidHelpers.fillWithFluid(state.setValue(ASCENDING, false), fluid.getType());
        }
        if (isAttached(context.getLevel(), context.getClickedPos(), state.setValue(ASCENDING, true)))
        {
            return FluidHelpers.fillWithFluid(state.setValue(ASCENDING, true), fluid.getType());
        }
        return null;
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos)
    {
        FluidHelpers.tickFluid(level, pos, state);
        if (!isAttached(level, pos, state))
        {
            return level.getFluidState(pos).createLegacyBlock();
        }
        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }


    @Override
    public void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder.add(ASCENDING));
    }

}
