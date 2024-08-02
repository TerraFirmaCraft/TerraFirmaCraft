/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.container;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.blockentities.LargeVesselBlockEntity;
import net.dries007.tfc.common.blocks.LargeVesselBlock;
import net.dries007.tfc.common.blocks.devices.SealableDeviceBlock;
import net.dries007.tfc.common.container.slot.CallbackSlot;

public class LargeVesselContainer extends BlockEntityContainer<LargeVesselBlockEntity> implements ButtonHandlerContainer, PestContainer
{
    public static LargeVesselContainer create(LargeVesselBlockEntity vessel, Inventory playerInventory, int windowId)
    {
        return new LargeVesselContainer(vessel, windowId).init(playerInventory);
    }

    public LargeVesselContainer(LargeVesselBlockEntity vessel, int windowId)
    {
        super(TFCContainerTypes.LARGE_VESSEL.get(), windowId, vessel);
    }

    @Override
    public void clicked(int slot, int button, ClickType clickType, Player player)
    {
        if (slot >= 0 && slot < LargeVesselBlockEntity.SLOTS && blockEntity.getBlockState().getValue(SealableDeviceBlock.SEALED))
        {
            return;
        }
        super.clicked(slot, button, clickType, player);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onButtonPress(int buttonID, @Nullable CompoundTag extraNBT)
    {
        Level level = blockEntity.getLevel();
        if (level != null)
        {
            LargeVesselBlock.toggleSeal(level, blockEntity.getBlockPos(), blockEntity.getBlockState(), (BlockEntityType<? extends LargeVesselBlockEntity>) blockEntity.getType());
        }
    }

    @Override
    protected boolean moveStack(ItemStack stack, int slotIndex)
    {
        if (blockEntity.getBlockState().getValue(LargeVesselBlock.SEALED))
        {
            return true;
        }
        return switch (typeOf(slotIndex))
            {
                case MAIN_INVENTORY, HOTBAR -> !moveItemStackTo(stack, 0, LargeVesselBlockEntity.SLOTS, false);
                case CONTAINER -> !moveItemStackTo(stack, containerSlots, slots.size(), false);
            };
    }

    @Override
    protected void addContainerSlots()
    {
        addSlot(new CallbackSlot(blockEntity, 0, 62, 19));
        addSlot(new CallbackSlot(blockEntity, 1, 80, 19));
        addSlot(new CallbackSlot(blockEntity, 2, 98, 19));
        addSlot(new CallbackSlot(blockEntity, 3, 62, 37));
        addSlot(new CallbackSlot(blockEntity, 4, 80, 37));
        addSlot(new CallbackSlot(blockEntity, 5, 98, 37));
        addSlot(new CallbackSlot(blockEntity, 6, 62, 55));
        addSlot(new CallbackSlot(blockEntity, 7, 80, 55));
        addSlot(new CallbackSlot(blockEntity, 8, 98, 55));
    }

    @Override
    public boolean canBeInfested()
    {
        return !isSealed();
    }

    public boolean isSealed()
    {
        return blockEntity.getBlockState().hasProperty(LargeVesselBlock.SEALED) && blockEntity.getBlockState().getValue(LargeVesselBlock.SEALED);
    }
}
