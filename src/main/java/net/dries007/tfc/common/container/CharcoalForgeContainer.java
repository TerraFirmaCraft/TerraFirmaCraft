/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.container;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

import net.dries007.tfc.common.blockentities.CharcoalForgeBlockEntity;

public class CharcoalForgeContainer extends BlockEntityContainer<CharcoalForgeBlockEntity>
{
    public static CharcoalForgeContainer create(CharcoalForgeBlockEntity forge, Inventory playerInventory, int windowId)
    {
        return new CharcoalForgeContainer(forge, windowId).init(playerInventory, 20);
    }

    private CharcoalForgeContainer(CharcoalForgeBlockEntity forge, int windowId)
    {
        super(TFCContainerTypes.CHARCOAL_FORGE.get(), windowId, forge);

        addDataSlots(forge.getSyncableData());
    }

    @Override
    protected boolean moveStack(ItemStack stack, int slotIndex)
    {
        return switch (typeOf(slotIndex))
            {
                case MAIN_INVENTORY, HOTBAR -> !moveItemStackTo(stack, CharcoalForgeBlockEntity.SLOT_EXTRA_MIN, CharcoalForgeBlockEntity.SLOT_EXTRA_MAX + 1, false) && !moveItemStackTo(stack, CharcoalForgeBlockEntity.SLOT_FUEL_MIN, CharcoalForgeBlockEntity.SLOT_INPUT_MAX + 1, false);
                case CONTAINER -> !moveItemStackTo(stack, containerSlots, slots.size(), false);
            };
    }

    @Override
    protected void addContainerSlots()
    {
        // Fuel slots
        // Note: the order of these statements is important
        int index = CharcoalForgeBlockEntity.SLOT_FUEL_MIN;
        addSlot(new CallbackSlot(blockEntity, index++, 80, 70));
        addSlot(new CallbackSlot(blockEntity, index++, 98, 52));
        addSlot(new CallbackSlot(blockEntity, index++, 62, 52));
        addSlot(new CallbackSlot(blockEntity, index++, 116, 34));
        addSlot(new CallbackSlot(blockEntity, index, 44, 34));

        // Input slots
        // Note: the order of these statements is important
        index = CharcoalForgeBlockEntity.SLOT_INPUT_MIN;
        addSlot(new CallbackSlot(blockEntity, index++, 80, 52));
        addSlot(new CallbackSlot(blockEntity, index++, 98, 34));
        addSlot(new CallbackSlot(blockEntity, index++, 62, 34));
        addSlot(new CallbackSlot(blockEntity, index++, 116, 16));
        addSlot(new CallbackSlot(blockEntity, index, 44, 16));

        // Extra slots (for ceramic molds)
        for (int i = CharcoalForgeBlockEntity.SLOT_EXTRA_MIN; i <= CharcoalForgeBlockEntity.SLOT_EXTRA_MAX; i++)
        {
            addSlot(new CallbackSlot(blockEntity, i, 152, 16 + 18 * (i - CharcoalForgeBlockEntity.SLOT_EXTRA_MIN)));
        }
    }
}
