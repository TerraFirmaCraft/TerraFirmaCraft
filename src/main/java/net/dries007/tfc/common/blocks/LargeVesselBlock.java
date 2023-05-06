/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks;

import java.util.List;
import java.util.Optional;

import net.dries007.tfc.config.TFCConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.items.ItemStackHandler;

import net.dries007.tfc.common.blockentities.LargeVesselBlockEntity;
import net.dries007.tfc.common.blocks.devices.SealableDeviceBlock;
import net.dries007.tfc.util.Helpers;
import org.jetbrains.annotations.Nullable;

public class LargeVesselBlock extends SealableDeviceBlock
{
    public static final VoxelShape OPENED_SHAPE = box(3D, 0D, 3D, 13D, 10D, 13D);
    public static final VoxelShape CLOSED_SHAPE = Shapes.or(
        OPENED_SHAPE,
        box(2.5D, 9.5D, 2.5D, 13.5D, 11D, 13.5D),
        box(7D, 11D, 7D, 9D, 12D, 9D)
    );

    public static <T extends LargeVesselBlockEntity> void toggleSeal(Level level, BlockPos pos, BlockState state, BlockEntityType<T> type)
    {
        level.getBlockEntity(pos, type).ifPresent(barrel -> {
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

    public LargeVesselBlock(ExtendedProperties properties)
    {
        super(properties);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return state.getValue(SEALED) ? CLOSED_SHAPE : OPENED_SHAPE;
    }


    @Override
    @SuppressWarnings("deprecation")
    public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos)
    {
        return canSurvive(state, level, currentPos) ? super.updateShape(state, facing, facingState, level, currentPos, facingPos) : Blocks.AIR.defaultBlockState();
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos)
    {
        BlockPos belowPos = pos.below();
        BlockState belowState = level.getBlockState(belowPos);
        return !belowState.isAir() && belowState.isFaceSturdy(level, belowPos, Direction.UP) && super.canSurvive(state, level, pos);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter level, List<Component> tooltip, TooltipFlag flag)
    {
        final CompoundTag tag = stack.getTagElement(Helpers.BLOCK_ENTITY_TAG);
        if (tag != null)
        {
            final CompoundTag inventoryTag = tag.getCompound("inventory");
            final ItemStackHandler inventory = new ItemStackHandler();

            inventory.deserializeNBT(inventoryTag);

            if (!Helpers.isEmpty(inventory) && !TFCConfig.CLIENT.displayItemContentsAsImages.get())
            {
                tooltip.add(Helpers.translatable("tfc.tooltip.contents").withStyle(ChatFormatting.DARK_GREEN));
                Helpers.addInventoryTooltipInfo(inventory, tooltip);
            }
            addExtraInfo(tooltip, inventoryTag);
        }
    }

    @Override
    public Optional<TooltipComponent> getTooltipImage(ItemStack stack)
    {
        if (TFCConfig.CLIENT.displayItemContentsAsImages.get())
        {
            final CompoundTag tag = stack.getTagElement(Helpers.BLOCK_ENTITY_TAG);
            if (tag != null)
            {
                final CompoundTag inventoryTag = tag.getCompound("inventory");
                final ItemStackHandler inventory = new ItemStackHandler();

                inventory.deserializeNBT(inventoryTag);

                if (!Helpers.isEmpty(inventory))
                {
                    return Helpers.getTooltipImage(inventory, 3, 3, 0, LargeVesselBlockEntity.SLOTS - 1);
                }
            }
        }
        return Optional.empty();
    }

    @Override
    @SuppressWarnings("deprecation")
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit)
    {
        if (player.isShiftKeyDown())
        {
            toggleSeal(level, pos, state, getExtendedProperties().blockEntity());
        }
        else
        {
            level.getBlockEntity(pos, getExtendedProperties().<LargeVesselBlockEntity>blockEntity()).ifPresent(vessel -> {
                if (player instanceof ServerPlayer serverPlayer)
                {
                    Helpers.openScreen(serverPlayer, vessel, pos);
                }
            });
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving)
    {
        if (TFCConfig.SERVER.largeVesselEnableRedstoneSeal.get() && level.getBlockEntity(pos) instanceof LargeVesselBlockEntity vessel)
            handleNeighborChanged(state, level, pos, vessel::onSeal, vessel::onUnseal);
    }
}
