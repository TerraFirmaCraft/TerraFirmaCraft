/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.plant;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;

import net.dries007.tfc.common.blocks.DirectionPropertyBlock;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.IForgeBlockExtension;

public abstract class PipePlantBlock extends PipeBlock implements IForgeBlockExtension
{
    private final ExtendedProperties properties;

    public PipePlantBlock(float size, ExtendedProperties properties)
    {
        super(size, properties.properties());
        this.properties = properties;
    }

    @Override
    public ExtendedProperties getExtendedProperties()
    {
        return properties;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder.add(NORTH, EAST, SOUTH, WEST, UP, DOWN));
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos)
    {
        if (!state.canSurvive(level, currentPos))
        {
            level.scheduleTick(currentPos, this, 1);
            return state;
        }
        else
        {
            final boolean exists = (facing.getAxis().isHorizontal() && testHorizontal(facingState)) || (facing == Direction.DOWN && testDown(facingState)) || (facing == Direction.UP && testUp(facingState));
            return state.setValue(PROPERTY_BY_DIRECTION.get(facing), exists);
        }
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        return this.getStateForPlacement(context.getLevel(), context.getClickedPos());
    }

    public BlockState getStateForPlacement(BlockGetter level, BlockPos pos)
    {
        final BlockState down = level.getBlockState(pos.below());
        final BlockState up = level.getBlockState(pos.above());
        final BlockState north = level.getBlockState(pos.north());
        final BlockState east = level.getBlockState(pos.east());
        final BlockState south = level.getBlockState(pos.south());
        final BlockState west = level.getBlockState(pos.west());
        return defaultBlockState()
            .setValue(DOWN, testDown(down))
            .setValue(UP, testUp(up))
            .setValue(NORTH, testHorizontal(north))
            .setValue(WEST, testHorizontal(west))
            .setValue(EAST, testHorizontal(east))
            .setValue(SOUTH, testHorizontal(south));
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos)
    {
        final BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos().set(pos);
        for (Direction direction : Direction.Plane.HORIZONTAL)
        {
            cursor.setWithOffset(pos, direction);
            // if we have a horizontal branch
            if (testHorizontal(level.getBlockState(cursor)))
            {
                // check for a block below
                cursor.move(0, -1, 0);
                if (testDown(level.getBlockState(cursor)))
                {
                    return true;
                }
                if (canGrowLongSideways())
                {
                    cursor.move(0, 1, 0);
                    cursor.move(direction);
                    if (testHorizontal(level.getBlockState(cursor)))
                    {
                        return true;
                    }
                }
            }
        }
        cursor.setWithOffset(pos, 0, -1, 0);
        return testDown(level.getBlockState(cursor));
    }

    protected abstract boolean testDown(BlockState state);

    protected abstract boolean testUp(BlockState state);

    protected abstract boolean testHorizontal(BlockState state);

    protected boolean canGrowLongSideways()
    {
        return false;
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState rotate(BlockState state, Rotation rot)
    {
        return DirectionPropertyBlock.rotate(state, rot);
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState mirror(BlockState state, Mirror mirror)
    {
        return DirectionPropertyBlock.mirror(state, mirror);
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource rand)
    {
        if (!state.canSurvive(level, pos))
        {
            level.destroyBlock(pos, true);
        }
    }

    @Override
    protected MapCodec<? extends PipeBlock> codec()
    {
        return fakeBlockCodec();
    }
}