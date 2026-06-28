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
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.util.Helpers;

public class HangingRopeBlock extends AbstractRopeBlock
{
    public static final VoxelShape[] SHAPES = Helpers.computeHorizontalShapes(dir -> Helpers.rotateShape(dir, 7, 0, 0, 9, 16, 2));

    public HangingRopeBlock(ExtendedProperties properties)
    {
        super(properties);
    }

    public boolean isAttached(LevelReader level, BlockPos pos, BlockState state)
    {
        if (level.getBlockState(pos.above()).getBlock() == this)
        {
            return true;
        }
        final BlockState relativeState = level.getBlockState(pos.above().relative(state.getValue(FACING)));
        return (relativeState.getBlock() instanceof GroundedRopeBlock && state.getValue(FACING) == relativeState.getValue(FACING)) ||
            (relativeState.getBlock() instanceof RopeAnchorBlock && state.getValue(FACING) == relativeState.getValue(FACING).getOpposite());
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos)
    {
        return isAttached(level, pos, state) ? state : Blocks.AIR.defaultBlockState();
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos)
    {
        return isAttached(level, pos, state);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context)
    {
        for (Direction dir : Direction.Plane.HORIZONTAL)
        {
            final BlockState state = defaultBlockState().setValue(FACING, dir);
            if (isAttached(context.getLevel(), context.getClickedPos(), state))
            {
                return state;
            }
        }
        return null;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return SHAPES[state.getValue(FACING).get2DDataValue()];
    }
}
