/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.container;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

import net.minecraftforge.items.CapabilityItemHandler;

import net.dries007.tfc.common.blockentities.NestBoxBlockEntity;

public class NestBoxContainer extends BlockEntityContainer<NestBoxBlockEntity>
{
    public static NestBoxContainer create(NestBoxBlockEntity nest, Inventory playerInventory, int windowId)
    {
        return new NestBoxContainer(nest, playerInventory, windowId).init(playerInventory);
    }

    public NestBoxContainer(NestBoxBlockEntity blockEntity, Inventory playerInv, int windowId)
    {
        super(TFCMenuTypes.NEST_BOX.get(), windowId, blockEntity);
    }

    @Override
    protected boolean moveStack(ItemStack stack, int slotIndex)
    {
        return switch (typeOf(slotIndex))
            {
                case MAIN_INVENTORY, HOTBAR -> !moveItemStackTo(stack, 0, NestBoxBlockEntity.SLOTS, false);
                case CONTAINER -> !moveItemStackTo(stack, containerSlots, slots.size(), false);
            };
    }

    @Override
    protected void addContainerSlots()
    {
        blockEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(handler -> {
            addSlot(new CallbackSlot(blockEntity, handler, 0, 71, 23));
            addSlot(new CallbackSlot(blockEntity, handler, 1, 89, 23));
            addSlot(new CallbackSlot(blockEntity, handler, 2, 71, 41));
            addSlot(new CallbackSlot(blockEntity, handler, 3, 89, 41));
        });
    }
}
