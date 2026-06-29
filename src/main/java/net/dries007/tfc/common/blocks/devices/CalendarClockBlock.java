/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.devices;

import java.util.Locale;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blockentities.CalendarClockBlockEntity;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.TFCBlockStateProperties;
import net.dries007.tfc.util.Helpers;

public class CalendarClockBlock extends DeviceBlock
{
    public static EnumProperty<Mode> MODE = TFCBlockStateProperties.CLOCK_MODE;
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
        registerDefaultState(getStateDefinition().any().setValue(MODE, Mode.HOUR).setValue(FACING, Direction.UP));
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
        BlockState state = Helpers.getSupportedDirectionalStateForPlacement(this, context, false);

        if (state == null)
        {
            return null;
        }
        final Direction facing = state.getValue(FACING);
        final LevelReader level = context.getLevel();
        final BlockPos pos = context.getClickedPos();
        final BlockState facingState = level.getBlockState(pos.relative(facing.getOpposite()));

        if (Helpers.isBlock(facingState, TFCTags.Blocks.CLOCK_READABLE))
        {
            state = state.setValue(FACING, facing);
        }
        return state;
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
                BlockState blockState;
                Direction direction = state.getValue(FACING);
                switch (state.getValue(MODE))
                {
                    case MONTH:
                        final Direction oppositeDirection = direction.getOpposite();
                        final BlockState blockState1 = level.getBlockState(pos.relative(oppositeDirection));
                        final BlockState blockState2 = level.getBlockState(pos.relative(oppositeDirection).relative(oppositeDirection));
                        //Since it now checks for an interface, addon devices can support this too (justified tag)
                        if (Helpers.isBlock(blockState1, TFCTags.Blocks.CLOCK_READABLE))
                        {
                            blockState = state.setValue(MODE, Mode.TIMER);
                        }
                        else if (Helpers.isBlock(blockState2, TFCTags.Blocks.CLOCK_READABLE))
                        {
                            blockState = state.setValue(MODE, Mode.TIMER);
                        }
                        else
                        {
                            blockState = state.setValue(MODE, Mode.HOUR);
                        }
                        break;
                    case TIMER:
                        blockState = state.setValue(MODE, Mode.HOUR);
                        break;
                    default:
                        blockState = state.setValue(MODE, Mode.MONTH);
                }
                level.setBlockAndUpdate(pos, blockState);
                level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(player, blockState));
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
        builder.add(MODE).add(FACING);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return switch (state.getValue(FACING))
        {
            case NORTH -> SHAPE_NORTH;
            case SOUTH -> SHAPE_SOUTH;
            case EAST -> SHAPE_EAST;
            case WEST -> SHAPE_WEST;
            case UP -> SHAPE_UP;
            case DOWN -> SHAPE_DOWN;
        };
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos blockPos, BlockPos facingPos)
    {
        if (facing == state.getValue(FACING).getOpposite() && !this.canSurvive(state, level, blockPos))
        {
            return Blocks.AIR.defaultBlockState();
        }
        return super.updateShape(state, facing, facingState, level, blockPos, facingPos);
    }

    @Override
    protected boolean isSignalSource(BlockState state)
    {
        return true;
    }

    @Override
    protected int getDirectSignal(BlockState blockState, BlockGetter blockAccess, BlockPos pos, Direction side)
    {
        if (blockState.getValue(FACING) == side && !blockState.getValue(MODE).equals(Mode.TIMER))
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
        if (Helpers.isBlock(blockstate, TFCTags.Blocks.CLOCK_READABLE))
        {
            return true;
        }
        return blockstate.isFaceSturdy(level, blockpos, facing);
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rot)
    {
        return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror)
    {
        return rotate(state, mirror.getRotation(state.getValue(FACING)));
    }

    public enum Mode implements StringRepresentable
    {
        HOUR, MONTH, TIMER;

        private final String serializedName;

        Mode()
        {
            this.serializedName = name().toLowerCase(Locale.ROOT);
        }

        @Override
        public String getSerializedName()
        {
            return serializedName;
        }
    }
}