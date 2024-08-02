/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.container;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

import net.dries007.tfc.common.blockentities.NestBoxBlockEntity;
import net.dries007.tfc.common.container.slot.CallbackSlot;

public class NestBoxContainer extends BlockEntityContainer<NestBoxBlockEntity> implements PestContainer
{
    public static NestBoxContainer create(NestBoxBlockEntity nest, Inventory playerInventory, int windowId)
    {
        return new NestBoxContainer(nest, playerInventory, windowId).init(playerInventory);
    }

    public NestBoxContainer(NestBoxBlockEntity blockEntity, Inventory playerInv, int windowId)
    {
        super(TFCContainerTypes.NEST_BOX.get(), windowId, blockEntity);
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
        addSlot(new CallbackSlot(blockEntity, 0, 71, 23));
        addSlot(new CallbackSlot(blockEntity, 1, 89, 23));
        addSlot(new CallbackSlot(blockEntity, 2, 71, 41));
        addSlot(new CallbackSlot(blockEntity, 3, 89, 41));
    }
}
