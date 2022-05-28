/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.container;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;

import net.dries007.tfc.common.blockentities.BarrelBlockEntity;
import net.dries007.tfc.common.blocks.devices.BarrelBlock;
import org.jetbrains.annotations.Nullable;

public class BarrelContainer extends BlockEntityContainer<BarrelBlockEntity> implements ButtonHandlerContainer
{
    public static BarrelContainer create(BarrelBlockEntity barrel, Inventory playerInv, int windowId)
    {
        return new BarrelContainer(windowId, barrel).init(playerInv, 12);
    }

    private BarrelContainer(int windowId, BarrelBlockEntity barrel)
    {
        super(TFCContainerTypes.BARREL.get(), windowId, barrel);
    }

    @Override
    public void onButtonPress(int buttonID, @Nullable CompoundTag extraNBT)
    {
        Level level = blockEntity.getLevel();
        if (level != null)
        {
            BarrelBlock.toggleSeal(level, blockEntity.getBlockPos(), blockEntity.getBlockState());
        }
    }

    @Override
    protected void addContainerSlots()
    {
        blockEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(inventory -> {
            addSlot(new CallbackSlot(blockEntity, inventory, BarrelBlockEntity.SLOT_FLUID_CONTAINER_IN, 35, 20));
            addSlot(new CallbackSlot(blockEntity, inventory, BarrelBlockEntity.SLOT_FLUID_CONTAINER_OUT, 35, 54));
            addSlot(new CallbackSlot(blockEntity, inventory, BarrelBlockEntity.SLOT_ITEM, 89, 37));
        });
    }

    @Override
    protected boolean moveStack(ItemStack stack, int slotIndex)
    {
        if (blockEntity.getBlockState().getValue(BarrelBlock.SEALED)) return true;
        final int containerSlot = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).isPresent() ? BarrelBlockEntity.SLOT_FLUID_CONTAINER_IN : BarrelBlockEntity.SLOT_ITEM;
        return switch (typeOf(slotIndex))
            {
                case MAIN_INVENTORY, HOTBAR -> !moveItemStackTo(stack, containerSlot, containerSlot + 1, false);
                case CONTAINER -> !moveItemStackTo(stack, containerSlots, slots.size(), false);
            };
    }
}
