/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.wood;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.client.TFCSounds;
import net.dries007.tfc.common.blockentities.TFCBlockEntities;
import net.dries007.tfc.common.blockentities.ToolRackBlockEntity;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.TFCBlockStateProperties;
import net.dries007.tfc.common.blocks.devices.DeviceBlock;
import net.dries007.tfc.common.fluids.FluidHelpers;
import net.dries007.tfc.common.fluids.FluidProperty;
import net.dries007.tfc.common.fluids.IFluidLoggable;
import net.dries007.tfc.util.Helpers;

public class ToolRackBlock extends DeviceBlock implements IFluidLoggable
{
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final FluidProperty FLUID = TFCBlockStateProperties.WATER;

    public static final VoxelShape SHAPE_EAST = Block.box(0, 0, 0, 2, 16, 16);
    public static final VoxelShape SHAPE_WEST = Block.box(14, 0, 0, 16, 16, 16);
    public static final VoxelShape SHAPE_SOUTH = Block.box(0, 0, 0, 16, 16, 2);
    public static final VoxelShape SHAPE_NORTH = Block.box(0, 0, 14, 16, 16, 16);

    public ToolRackBlock(ExtendedProperties properties)
    {
        super(properties, InventoryRemoveBehavior.DROP);
        registerDefaultState(getStateDefinition().any().setValue(FACING, Direction.NORTH).setValue(FLUID, FLUID.keyFor(Fluids.EMPTY)));
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos)
    {
        if (facing.getOpposite() == state.getValue(FACING) && !state.canSurvive(level, currentPos))
        {
            return Blocks.AIR.defaultBlockState();
        }
        FluidHelpers.tickFluid(level, currentPos, state);
        return super.updateShape(state, facing, facingState, level, currentPos, facingPos);
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
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos)
    {
        final Direction facing = state.getValue(FACING);
        final BlockPos attachedPos = pos.relative(facing.getOpposite());
        return level.getBlockState(attachedPos).isFaceSturdy(level, attachedPos, facing);
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

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        final Level level = context.getLevel();
        final BlockPos pos = context.getClickedPos();
        if (!context.replacingClickedOnBlock())
        {
            final BlockState attachedState = level.getBlockState(pos.relative(context.getClickedFace().getOpposite()));
            if (Helpers.isBlock(attachedState, this) && attachedState.getValue(FACING) == context.getClickedFace())
            {
                return null;
            }
        }

        final @Nullable BlockState filledState = FluidHelpers.fillWithFluid(defaultBlockState(), level.getFluidState(pos).getType());
        final BlockState defaultState = filledState != null ? filledState : defaultBlockState();
        for (Direction direction : context.getNearestLookingDirections())
        {
            if (direction.getAxis().isHorizontal())
            {
                final BlockState state = defaultState.setValue(FACING, direction.getOpposite());
                if (state.canSurvive(level, pos))
                {
                    return state;
                }
            }
        }
        return null;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        builder.add(FACING, FLUID);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult)
    {
        final @Nullable ToolRackBlockEntity toolRack = level.getBlockEntity(pos, TFCBlockEntities.TOOL_RACK.get()).orElse(null);
        if (toolRack != null)
        {
            final IItemHandler inventory = toolRack.getInventory();
            final int slot = getSlotFromPos(state, hitResult.getLocation().subtract(Vec3.atLowerCornerOf(pos)));
            final boolean canInsert = inventory.isItemValid(slot, stack);
            if (!canInsert && inventory.getStackInSlot(slot).isEmpty())
            {
                return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            }
            if (!level.isClientSide)
            {
                final ItemStack extracted = inventory.extractItem(slot, 1, false);
                if (canInsert)
                {
                    inventory.insertItem(slot, stack.split(1), false);
                    Helpers.playSound(level, pos, TFCSounds.TOOL_RACK_PLACE.get());
                }
                if (stack.isEmpty())
                {
                    player.setItemInHand(hand, extracted);
                }
                else
                {
                    ItemHandlerHelper.giveItemToPlayer(player, extracted);
                }
            }
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    public int getSlotFromPos(BlockState state, Vec3 pos)
    {
        final double horizontal = state.getValue(FACING).getAxis() == Direction.Axis.Z ? pos.x : pos.z;
        return (horizontal > 0.5 ? 1 : 0) + (pos.y < 0.5 ? 2 : 0);
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
}