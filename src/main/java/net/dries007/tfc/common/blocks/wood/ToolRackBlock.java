/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.wood;

import org.jetbrains.annotations.Nullable;

import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import net.dries007.tfc.common.blockentities.TFCBlockEntities;
import net.dries007.tfc.common.blockentities.ToolRackBlockEntity;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.devices.DeviceBlock;

public class ToolRackBlock extends DeviceBlock implements SimpleWaterloggedBlock
{
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    public static final VoxelShape SHAPE_EAST = Block.box(0.0D, 0.0D, 0.0D, 2.0D, 16.0D, 16.0D);
    public static final VoxelShape SHAPE_WEST = Block.box(14.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
    public static final VoxelShape SHAPE_SOUTH = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 2.0D);
    public static final VoxelShape SHAPE_NORTH = Block.box(0.0D, 0.0D, 14.0D, 16.0D, 16.0D, 16.0D);

    public ToolRackBlock(ExtendedProperties properties)
    {
        super(properties, InventoryRemoveBehavior.DROP);
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos)
    {
        if (facing.getOpposite() == stateIn.getValue(FACING) && !stateIn.canSurvive(level, currentPos))
        {
            return Blocks.AIR.defaultBlockState();
        }
        else if (stateIn.getValue(WATERLOGGED))
        {
            level.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }
        return super.updateShape(stateIn, facing, facingState, level, currentPos, facingPos);
    }

    @Override
    @SuppressWarnings("deprecation")
    public FluidState getFluidState(BlockState state)
    {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean canSurvive(BlockState state, LevelReader worldIn, BlockPos pos)
    {
        Direction direction = state.getValue(FACING);
        return canAttachTo(worldIn, pos.relative(direction.getOpposite()), direction);
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context)
    {
        return switch (state.getValue(FACING))
            {
                case NORTH -> SHAPE_NORTH;
                case SOUTH -> SHAPE_SOUTH;
                case WEST -> SHAPE_WEST;
                default -> SHAPE_EAST;
            };
    }

    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context)
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
        LevelReader world = context.getLevel();
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
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        builder.add(FACING, WATERLOGGED);
    }

    @Override
    @SuppressWarnings("deprecation")
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit)
    {
        ToolRackBlockEntity toolRack = level.getBlockEntity(pos, TFCBlockEntities.TOOL_RACK.get()).orElse(null);
        if (toolRack != null)
        {
            return toolRack.onRightClick(player, getSlotFromPos(state, hit.getLocation().subtract(pos.getX(), pos.getY(), pos.getZ())));
        }
        return InteractionResult.PASS;
    }

    private boolean canAttachTo(BlockGetter blockReader, BlockPos pos, Direction directionIn)
    {
        BlockState blockstate = blockReader.getBlockState(pos);
        return !blockstate.isSignalSource() && blockstate.isFaceSturdy(blockReader, pos, directionIn);
    }

    public int getSlotFromPos(BlockState state, Vec3 pos)
    {
        int slot = 0;
        if ((state.getValue(FACING).getAxis().equals(Direction.Axis.Z) ? pos.x : pos.z) > .5f)
        {
            slot += 1;
        }
        if (pos.y < 0.5f)
        {
            slot += 2;
        }
        return slot;
    }
}