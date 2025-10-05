package net.dries007.tfc.common.blocks.devices;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
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

import net.dries007.tfc.common.blockentities.CalendarClockBlockEntity;
import net.dries007.tfc.common.blocks.EntityBlockExtension;
import net.dries007.tfc.common.blocks.ExtendedBlock;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.IForgeBlockExtension;
import net.dries007.tfc.common.blocks.TFCBlockStateProperties;

public class CalendarClockBlock extends ExtendedBlock implements EntityBlockExtension, IForgeBlockExtension
{
    public static BooleanProperty CLOCK_MONTH_MODE = TFCBlockStateProperties.CLOCK_MONTH_MODE;
    public static DirectionProperty FACING = BlockStateProperties.FACING;
    private static final VoxelShape SHAPE_UP = box(1D, 0D, 1D, 15D, 2.0D, 15D); // "up" is +Y
    private static final VoxelShape SHAPE_DOWN = SHAPE_UP.move(0D, 14D, 0D);
    private static final VoxelShape SHAPE_SOUTH = box(1D, 1D, 0D, 15D, 15D, 2D); // "up" is +Z
    private static final VoxelShape SHAPE_NORTH = SHAPE_SOUTH.move(0D, 0D, -14D);
    private static final VoxelShape SHAPE_WEST = box(0D, 1D, 1D, 2D, 15D, 15D); // "up" is +X
    private static final VoxelShape SHAPE_EAST = SHAPE_WEST.move(-14D, 0D, 0D);

    public CalendarClockBlock(ExtendedProperties properties)
    {
        super(properties);
        registerDefaultState(getStateDefinition().any().setValue(CLOCK_MONTH_MODE, false));
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving)
    {
        level.updateNeighborsAt(pos, this);
        level.updateNeighborsAt(pos.below(), this);
        level.removeBlockEntity(pos); // wasn't getting removed otherwise?
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving)
    {
        if (level.getBlockEntity(pos) instanceof CalendarClockBlockEntity clock)
        {
            clock.needsInstantUpdate();
        }
        level.updateNeighborsAt(pos, this);
        level.updateNeighborsAt(pos.below(), this);
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
                return InteractionResult.SUCCESS;
            }
            else
            {
                BlockState blockstate = (state.cycle(CLOCK_MONTH_MODE));
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
        builder.add(CLOCK_MONTH_MODE);
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
    public RenderShape getRenderShape(BlockState state)
    {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    protected boolean isSignalSource(BlockState state)
    {
        return true;
    }

    protected int getDirectSignal(BlockState blockState, BlockGetter blockAccess, BlockPos pos, Direction side)
    {
        return getSignal(blockState, blockAccess, pos, side);
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

    protected boolean canSurvive(BlockState state, LevelReader levelReader, BlockPos pos)
    {
        return true;
    }

}