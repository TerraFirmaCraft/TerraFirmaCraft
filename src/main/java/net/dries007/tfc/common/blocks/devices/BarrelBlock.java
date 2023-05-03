/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.devices;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SupportType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.blockentities.BarrelBlockEntity;
import net.dries007.tfc.common.blockentities.TFCBlockEntities;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.TFCBlockStateProperties;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.fluids.FluidHelpers;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.Tooltips;

public class BarrelBlock extends SealableDeviceBlock
{
    public static void toggleSeal(Level level, BlockPos pos, BlockState state)
    {
        level.getBlockEntity(pos, TFCBlockEntities.BARREL.get()).ifPresent(barrel -> {
            final boolean previousSealed = state.getValue(SEALED);
            level.setBlockAndUpdate(pos, state.setValue(SEALED, !previousSealed));

            if (previousSealed)
            {
                barrel.onUnseal();
            }
            else
            {
                barrel.onSeal();
            }
        });
    }

    public static final VoxelShape SHAPE_Z = box(2, 0, 0, 14, 12, 16);
    public static final VoxelShape SHAPE_X = box(0, 0, 2, 16, 12, 14);
    public static final VoxelShape RACK_SHAPE = Shapes.or(
        box(0, 0, 0, 2, 16, 2),
        box(14, 0, 14, 16, 16, 16),
        box(14, 0, 0, 16, 16, 2),
        box(0, 0, 14, 2, 16, 16),
        box(0, 14, 0, 16, 16, 16)
    );
    public static final VoxelShape SHAPE_Z_RACK = Shapes.or(SHAPE_Z, RACK_SHAPE);
    public static final VoxelShape SHAPE_X_RACK = Shapes.or(SHAPE_X, RACK_SHAPE);

    // not down
    public static final EnumProperty<Direction> FACING = TFCBlockStateProperties.FACING_NOT_DOWN;
    public static final BooleanProperty RACK = TFCBlockStateProperties.RACK;

    public BarrelBlock(ExtendedProperties properties)
    {
        super(properties);
        registerDefaultState(getStateDefinition().any().setValue(SEALED, false).setValue(FACING, Direction.UP).setValue(RACK, false));
    }

    @Override
    @SuppressWarnings("deprecation")
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit)
    {
        final BarrelBlockEntity barrel = level.getBlockEntity(pos, TFCBlockEntities.BARREL.get()).orElse(null);
        if (barrel != null)
        {
            final ItemStack stack = player.getItemInHand(hand);
            if (stack.isEmpty() && player.isShiftKeyDown())
            {
                if (state.getValue(RACK) && level.getBlockState(pos.above()).isAir() && hit.getLocation().y - pos.getY() > 0.875f)
                {
                    ItemHandlerHelper.giveItemToPlayer(player, new ItemStack(TFCBlocks.BARREL_RACK.get().asItem()));
                    level.setBlockAndUpdate(pos, state.setValue(RACK, false));
                }
                else
                {
                    toggleSeal(level, pos, state);
                }
                level.playSound(null, pos, SoundEvents.WOOD_PLACE, SoundSource.BLOCKS, 1.0f, 0.85f);
                return InteractionResult.SUCCESS;
            }
            else if (Helpers.isItem(stack, TFCBlocks.BARREL_RACK.get().asItem()) && state.getValue(FACING) != Direction.UP && !state.getValue(RACK))
            {
                if (!player.isCreative()) stack.shrink(1);
                level.setBlockAndUpdate(pos, state.setValue(RACK, true));
                Helpers.playPlaceSound(level, pos, state);
                return InteractionResult.sidedSuccess(level.isClientSide);
            }
            else if (FluidHelpers.transferBetweenBlockEntityAndItem(stack, barrel, player, hand))
            {
                return InteractionResult.SUCCESS;
            }
            else if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer)
            {
                Helpers.openScreen(serverPlayer, barrel, barrel.getBlockPos());
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @Override
    protected void addExtraInfo(List<Component> tooltip, CompoundTag inventoryTag)
    {
        final FluidTank tank = new FluidTank(TFCConfig.SERVER.barrelCapacity.get());
        tank.readFromNBT(inventoryTag.getCompound("tank"));
        if (!tank.isEmpty())
        {
            tooltip.add(Tooltips.fluidUnitsOf(tank.getFluid()));
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos pos, BlockPos facingPos)
    {
        if (state.getValue(FACING).getAxis().isHorizontal() && facing == Direction.DOWN && !level.getBlockState(facingPos).isFaceSturdy(level, facingPos, Direction.UP, SupportType.CENTER))
        {
            return Blocks.AIR.defaultBlockState();
        }
        return state;
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        BlockState state = super.getStateForPlacement(context);
        if (state != null)
        {
            Direction dir = context.getClickedFace();
            if (dir == Direction.DOWN) dir = Direction.UP;
            state = state.setValue(FACING, dir);

            final Level level = context.getLevel();
            final BlockPos pos = context.getClickedPos();
            // case of replacing a barrel rack block
            if (Helpers.isBlock(level.getBlockState(pos), TFCBlocks.BARREL_RACK.get()))
            {
                return state.setValue(FACING, context.getHorizontalDirection().getOpposite()).setValue(RACK, true);
            }
            // require racks or any kind of block for horizontal placement
            // we won't pop the barrels off directly though, in order to be a little forgiving.
            if (dir.getAxis().isHorizontal() && !level.getBlockState(pos.below()).isFaceSturdy(level, pos.below(), Direction.UP, SupportType.CENTER))
            {
                return null;
            }
        }
        return state;
    }

    @Override
    public BlockState rotate(BlockState state, LevelAccessor level, BlockPos pos, Rotation direction)
    {
        return state.setValue(FACING, direction.rotate(state.getValue(FACING)));
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState mirror(BlockState state, Mirror mirror)
    {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        final boolean rack = state.getValue(RACK);
        return switch (state.getValue(FACING).getAxis())
            {
                case X -> rack ? SHAPE_X_RACK : SHAPE_X;
                case Z -> rack ? SHAPE_Z_RACK : SHAPE_Z;
                case Y -> super.getShape(state, level, pos, context);
            };
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving)
    {
        if (!(Helpers.isBlock(state, newState.getBlock())) && state.getValue(RACK))
        {
            Helpers.spawnItem(level, pos, new ItemStack(TFCBlocks.BARREL_RACK.get()));
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder.add(FACING, RACK));
    }

    @Override
    @SuppressWarnings("deprecation")
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving)
    {
        if (TFCConfig.SERVER.barrelEnableRedstoneSeal.get() && level.getBlockEntity(pos) instanceof BarrelBlockEntity barrel)
            handleNeighborChanged(state, level, pos, barrel::onSeal, barrel::onUnseal);
    }
}
