/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.devices;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ObserverBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.blockentities.CalendarClockBlockEntity;
import net.dries007.tfc.common.blocks.EntityBlockExtension;
import net.dries007.tfc.common.blocks.ExtendedBlock;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.IForgeBlockExtension;
import net.dries007.tfc.common.blocks.TFCBlockStateProperties;

public class CalendarClockBlock extends DeviceBlock
{
    public static BooleanProperty CLOCK_MONTH_MODE = TFCBlockStateProperties.CLOCK_MONTH_MODE;
    public static DirectionProperty FACING = BlockStateProperties.FACING;
    private static final VoxelShape SHAPE_UP = box(1D, 0D, 1D, 15D, 2.0D, 15D);
    private static final VoxelShape SHAPE_DOWN = box(1D, 14D, 1D, 15D, 16.0D, 15D);
    private static final VoxelShape SHAPE_NORTH = box(1D, 1D, 14D, 15D, 15D, 16D);
    private static final VoxelShape SHAPE_SOUTH = box(1D, 1D, 0D, 15D, 15D, 2D);
    private static final VoxelShape SHAPE_EAST = box(0D, 1D, 1D, 2D, 15D, 15D);
    private static final VoxelShape SHAPE_WEST = box(14D, 1D, 1D, 16D, 15D, 15D);

    public CalendarClockBlock(ExtendedProperties properties)
    {
        super(properties, InventoryRemoveBehavior.NOOP);
        registerDefaultState(getStateDefinition().any().setValue(CLOCK_MONTH_MODE, false).setValue(FACING, Direction.UP));
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving)
    {
        super.onRemove(state, level, pos, newState, isMoving);
        level.updateNeighborsAt(pos.relative(state.getValue(FACING).getOpposite()), this); // needed for strong power update
    }

    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        BlockState blockstate = this.defaultBlockState();
        final LevelReader levelreader = context.getLevel();
        final BlockPos blockpos = context.getClickedPos();
        final Direction[] adirection = context.getNearestLookingDirections();
        final Direction[] var6 = adirection;
        int var7 = adirection.length;

        for (int var8 = 0; var8 < var7; ++var8)
        {
            Direction direction = var6[var8];
            Direction direction1 = direction.getOpposite();
            blockstate = blockstate.setValue(FACING, direction1);
            if (blockstate.canSurvive(levelreader, blockpos))
            {
                return blockstate;
            }
        }

        return null;
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving)
    {
        if (level.getBlockEntity(pos) instanceof CalendarClockBlockEntity clock)
        {
            clock.needsInstantUpdate();
        }
        level.updateNeighborsAt(pos.relative(state.getValue(FACING).getOpposite()), this);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult)
    {
        if (player.mayBuild())
        {
            if (level.isClientSide)
            {
                if (level.getBlockEntity(pos) instanceof CalendarClockBlockEntity clock)
                {
                    clock.needsInstantUpdate();
                }
                return InteractionResult.sidedSuccess(level.isClientSide());
            }
            else
            {
                final BlockState blockstate = state.cycle(CLOCK_MONTH_MODE);
                level.setBlock(pos, blockstate, 2);
                level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(player, blockstate));
                if (level.getBlockEntity(pos) instanceof CalendarClockBlockEntity clock)
                {
                    clock.needsInstantUpdate();
                }
                return InteractionResult.CONSUME;
            }
        }
        else
        {
            return super.useWithoutItem(state, level, pos, player, hitResult);
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        builder.add(CLOCK_MONTH_MODE).add(FACING);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        switch (state.getValue(FACING))
        {
            case NORTH -> {return SHAPE_NORTH;}
            case SOUTH -> {return SHAPE_SOUTH;}
            case EAST -> {return SHAPE_EAST;}
            case WEST -> {return SHAPE_WEST;}
            case UP -> {return SHAPE_UP;}
            case DOWN -> {return SHAPE_DOWN;}
        }
        return SHAPE_DOWN;
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos blockpos, BlockPos facingPos)
    {
        if (facing == state.getValue(FACING).getOpposite() && !this.canSurvive(state, level, blockpos))
        {
            return Blocks.AIR.defaultBlockState();
        }
        return super.updateShape(state, facing, facingState, level, blockpos, facingPos);
    }

    @Override
    protected boolean isSignalSource(BlockState state)
    {
        return true;
    }

    @Override
    protected int getDirectSignal(BlockState blockState, BlockGetter blockAccess, BlockPos pos, Direction side)
    {
        if (blockState.getValue(FACING) == side)
        {
            return getSignal(blockState, blockAccess, pos, side);
        }
        return 0;
    }

    @Override
    protected int getSignal(BlockState blockState, BlockGetter blockAccess, BlockPos pos, Direction side)
    {
        if (blockAccess.getBlockEntity(pos) instanceof CalendarClockBlockEntity clock)
        {
            return clock.getRedstoneSignal();
        }
        return 0;
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos)
    {
        return canSurvive(level, pos, state.getValue(FACING));
    }

    public static boolean canSurvive(LevelReader level, BlockPos pos, Direction facing)
    {
        BlockPos blockpos = pos.relative(facing.getOpposite());
        BlockState blockstate = level.getBlockState(blockpos);
        return blockstate.isFaceSturdy(level, blockpos, facing);
    }
}