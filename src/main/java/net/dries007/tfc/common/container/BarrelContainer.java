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
import net.neoforged.neoforge.capabilities.Capabilities;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.blockentities.BarrelBlockEntity;
import net.dries007.tfc.common.blocks.devices.BarrelBlock;
import net.dries007.tfc.common.blocks.devices.SealableDeviceBlock;
import net.dries007.tfc.common.component.heat.HeatCapability;
import net.dries007.tfc.common.component.heat.IHeat;

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
    public void clicked(int slot, int button, ClickType clickType, Player player)
    {
        if (slot >= 0 && slot < BarrelBlockEntity.SLOTS && blockEntity.getBlockState().getValue(SealableDeviceBlock.SEALED))
        {
            return;
        }
        super.clicked(slot, button, clickType, player);
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
        addSlot(new CallbackSlot(blockEntity, BarrelBlockEntity.SLOT_FLUID_CONTAINER_IN, 35, 20));
        addSlot(new CallbackSlot(blockEntity, BarrelBlockEntity.SLOT_FLUID_CONTAINER_OUT, 35, 54));
        addSlot(new CallbackSlot(blockEntity, BarrelBlockEntity.SLOT_ITEM, 89, 37));
    }

    @Override
    protected boolean moveStack(ItemStack stack, int slotIndex)
    {
        if (blockEntity.getBlockState().getValue(BarrelBlock.SEALED))
        {
            return true;
        }

        final @Nullable IHeat heat = HeatCapability.get(stack);
        final int containerSlot = stack.getCapability(Capabilities.FluidHandler.ITEM) != null
            && heat != null
            && heat.getTemperature() == 0 ? BarrelBlockEntity.SLOT_FLUID_CONTAINER_IN : BarrelBlockEntity.SLOT_ITEM;

        return switch (typeOf(slotIndex))
            {
                case MAIN_INVENTORY, HOTBAR -> !moveItemStackTo(stack, containerSlot, containerSlot + 1, false);
                case CONTAINER -> !moveItemStackTo(stack, containerSlots, slots.size(), false);
            };
    }
}
