/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.rotation;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blockentities.TFCBlockEntities;
import net.dries007.tfc.common.blockentities.rotation.HandWheelBlockEntity;
import net.dries007.tfc.common.blockentities.rotation.RotatingBlockEntity;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.TFCBlockStateProperties;
import net.dries007.tfc.common.blocks.devices.DeviceBlock;
import net.dries007.tfc.util.Helpers;


/**
 * todo: This is currently not survival obtainable, and not sure if it should be. It is, however, useful for creative testing in it's current form
 * Known issues: it can be activated without adding a wheel in certain circumstances
 */
public class HandWheelBlock extends DeviceBlock
{
    public static final BooleanProperty HAS_WHEEL = TFCBlockStateProperties.HAS_WHEEL;
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

    private static final VoxelShape[] SHAPE = Helpers.computeHorizontalShapes(dir -> Helpers.rotateShape(dir, 0, 0, 0, 16, 16, 4));

    private static final VoxelShape[] SHAPE_WITH_WHEEL = Helpers.computeHorizontalShapes(dir -> Shapes.or(
        Helpers.rotateShape(dir, 0, 0, 0, 16, 16, 4),
        Helpers.rotateShape(dir, 3, 3, 3, 13, 13, 13)
    ));

    public HandWheelBlock(ExtendedProperties properties)
    {
        super(properties, InventoryRemoveBehavior.DROP);
        registerDefaultState(getStateDefinition().any().setValue(HAS_WHEEL, false).setValue(FACING, Direction.NORTH));
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random)
    {
        if (level.getBlockEntity(pos) instanceof RotatingBlockEntity entity)
        {
            entity.destroyIfInvalid(level, pos);
        }
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult)
    {
        final HandWheelBlockEntity wheel = level.getBlockEntity(pos, TFCBlockEntities.HAND_WHEEL.get()).orElse(null);
        if (wheel != null)
        {
            final ItemStack held = player.getItemInHand(hand);
            if (Helpers.isItem(held, TFCTags.Items.HAND_WHEEL))
            {
                final ItemStackHandler inventory = wheel.getInventory();
                final ItemStack current = inventory.getStackInSlot(0);
                if (current.isEmpty() && inventory.isItemValid(0, held))
                {
                    ItemHandlerHelper.giveItemToPlayer(player, inventory.insertItem(0, held.split(1), false));
                    return ItemInteractionResult.sidedSuccess(level.isClientSide);
                }
                else if (!current.isEmpty() && player.isShiftKeyDown() && held.isEmpty())
                {
                    ItemHandlerHelper.giveItemToPlayer(player, inventory.extractItem(0, 1, false));
                    return ItemInteractionResult.sidedSuccess(level.isClientSide);
                }
            }
            else
            {
                if (!player.isShiftKeyDown())
                {
                    wheel.rotate();
                    return ItemInteractionResult.sidedSuccess(level.isClientSide);
                }
            }
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        return defaultBlockState().setValue(FACING, context.getHorizontalDirection());
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        final int idx = state.getValue(FACING).get2DDataValue();
        return state.getValue(HAS_WHEEL) ? SHAPE_WITH_WHEEL[idx] : SHAPE[idx];
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder.add(HAS_WHEEL, FACING));
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
