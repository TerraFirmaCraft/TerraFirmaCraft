/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.rotation;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.blocks.DirectionPropertyBlock;
import net.dries007.tfc.common.blocks.ExtendedBlock;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.TFCBlockStateProperties;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.fluids.FluidHelpers;
import net.dries007.tfc.common.fluids.FluidProperty;
import net.dries007.tfc.common.fluids.IFluidLoggable;
import net.dries007.tfc.util.Helpers;

public class FluidPipeBlock extends ExtendedBlock implements DirectionPropertyBlock, IFluidLoggable
{
    public static final FluidProperty FLUID = TFCBlockStateProperties.ALL_WATER;

    private static final VoxelShape[] SHAPES = new VoxelShape[64];

    static
    {
        final VoxelShape north = box(5, 5, 0, 11, 11, 5);
        final VoxelShape south = box(5, 5, 11, 11, 11, 16);
        final VoxelShape west = box(11, 5, 5, 16, 11, 11);
        final VoxelShape east = box(0, 5, 5, 5, 11, 11);
        final VoxelShape up = box(5, 11, 5, 11, 16, 11);
        final VoxelShape down = box(5, 0, 5, 11, 5, 11);

        // Must match Direction.ordinal order
        final VoxelShape[] directions = new VoxelShape[] {down, up, north, south, east, west};

        final VoxelShape center = box(5, 5, 5, 11, 11, 11);

        for (int i = 0; i < SHAPES.length; i++)
        {
            VoxelShape shape = center;
            for (Direction direction : Helpers.DIRECTIONS)
            {
                if (((i >> direction.ordinal()) & 1) == 1)
                {
                    shape = Shapes.or(shape, directions[direction.ordinal()]);
                }
            }
            SHAPES[i] = shape;
        }
    }

    public FluidPipeBlock(ExtendedProperties properties)
    {
        super(properties);
        registerDefaultState(DirectionPropertyBlock.setAllDirections(getStateDefinition().any(), false));
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos currentPos, BlockPos neighborPos)
    {
        FluidHelpers.tickFluid(level, currentPos, state);
        return updateConnectedSides(level, currentPos, state, null);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        final FluidState fluidState = context.getLevel().getFluidState(context.getClickedPos());
        final BlockState state = updateConnectedSides(context.getLevel(), context.getClickedPos(), defaultBlockState(), context.getNearestLookingDirection());
        if (getFluidProperty().canContain(fluidState.getType()))
        {
            return state.setValue(getFluidProperty(), getFluidProperty().keyFor(fluidState.getType()));
        }
        return state;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        int index = 0;
        for (Direction side : Helpers.DIRECTIONS)
        {
            if (state.getValue(DirectionPropertyBlock.getProperty(side)))
            {
                index |= 1 << side.ordinal();
            }
        }
        return SHAPES[index];
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        builder.add(PROPERTIES).add(getFluidProperty());
    }

    @Override
    public FluidState getFluidState(BlockState state)
    {
        return IFluidLoggable.super.getFluidState(state);
    }

    @Override
    public FluidProperty getFluidProperty()
    {
        return FLUID;
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState rotate(BlockState state, Rotation rotation)
    {
        return DirectionPropertyBlock.rotate(state, rotation);
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState mirror(BlockState state, Mirror mirror)
    {
        return DirectionPropertyBlock.mirror(state, mirror);
    }

    private BlockState updateConnectedSides(LevelAccessor level, BlockPos pos, BlockState state, @Nullable Direction defaultDirection)
    {
        int openSides = 0;
        @Nullable Direction openDirection = null;
        for (final Direction direction : Helpers.DIRECTIONS)
        {
            final BooleanProperty property = DirectionPropertyBlock.getProperty(direction);

            if (defaultDirection == null && state.getValue(property))
            {
                defaultDirection = direction;
            }

            final BlockPos adjacentPos = pos.relative(direction);
            final BlockState adjacentState = level.getBlockState(adjacentPos);
            final boolean adjacentConnection = connectsToPipeInDirection(adjacentState, direction);
            if (adjacentConnection)
            {
                openSides++;
                openDirection = direction;
            }

            state = state.setValue(property, adjacentConnection);
        }

        if (openSides == 0)
        {
            // Either we called this method with a non-null default direction, or
            // The state must have already been in-world, which must have had at least one direction previously, which we would have taken as the default
            assert defaultDirection != null;

            return state.setValue(DirectionPropertyBlock.getProperty(defaultDirection), true)
                .setValue(DirectionPropertyBlock.getProperty(defaultDirection.getOpposite()), true);
        }
        if (openSides == 1)
        {
            // If we only have a single open side, then we always treat this as a straight pipe.
            return state.setValue(DirectionPropertyBlock.getProperty(openDirection.getOpposite()), true);
        }

        return state;
    }

    private boolean connectsToPipeInDirection(BlockState state, Direction direction)
    {
        return state.getBlock() == this || (state.getBlock() == TFCBlocks.STEEL_PUMP.get() && direction == Direction.UP);
    }
}
