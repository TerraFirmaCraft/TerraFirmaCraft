/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.wood;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.blockentities.TFCBlockEntities;
import net.dries007.tfc.common.blockentities.ToolRackBlockEntity;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.devices.DeviceBlock;

public class ToolRackBlock extends DeviceBlock implements SimpleWaterloggedBlock
{
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    public static final VoxelShape SHAPE_EAST = Block.box(0, 0, 0, 2, 16, 16);
    public static final VoxelShape SHAPE_WEST = Block.box(14, 0, 0, 16, 16, 16);
    public static final VoxelShape SHAPE_SOUTH = Block.box(0, 0, 0, 16, 16, 2);
    public static final VoxelShape SHAPE_NORTH = Block.box(0, 0, 14, 16, 16, 16);

    public ToolRackBlock(ExtendedProperties properties)
    {
        super(properties, InventoryRemoveBehavior.DROP);
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos)
    {
        if (facing.getOpposite() == state.getValue(FACING) && !state.canSurvive(level, currentPos))
        {
            return Blocks.AIR.defaultBlockState();
        }
        else if (state.getValue(WATERLOGGED))
        {
            level.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }
        return super.updateShape(state, facing, facingState, level, currentPos, facingPos);
    }

    @Override
    public FluidState getFluidState(BlockState state)
    {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos)
    {
        Direction direction = state.getValue(FACING);
        return canAttachTo(level, pos.relative(direction.getOpposite()), direction);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
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
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult)
    {
        ToolRackBlockEntity toolRack = level.getBlockEntity(pos, TFCBlockEntities.TOOL_RACK.get()).orElse(null);
        if (toolRack != null)
        {
            return toolRack.onRightClick(player, getSlotFromPos(state, hitResult.getLocation().subtract(pos.getX(), pos.getY(), pos.getZ())));
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    private boolean canAttachTo(BlockGetter level, BlockPos pos, Direction direction)
    {
        BlockState blockstate = level.getBlockState(pos);
        return !blockstate.isSignalSource() && blockstate.isFaceSturdy(level, pos, direction);
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

    @Override
    @SuppressWarnings("deprecation")
    public BlockState rotate(BlockState state, Rotation rot)
    {
        return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState mirror(BlockState state, Mirror mirror)
    {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }
}