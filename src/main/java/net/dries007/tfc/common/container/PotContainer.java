/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.container;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blockentities.PotBlockEntity;
import net.dries007.tfc.util.Helpers;

import static net.dries007.tfc.common.blockentities.AbstractFirepitBlockEntity.*;
import static net.dries007.tfc.common.blockentities.GrillBlockEntity.*;

public class PotContainer extends BlockEntityContainer<PotBlockEntity>
{
    public static PotContainer create(PotBlockEntity pot, Inventory playerInv, int windowId)
    {
        return new PotContainer(pot, windowId).init(playerInv, 20);
    }

    private PotContainer(PotBlockEntity pot, int windowId)
    {
        super(TFCContainerTypes.POT.get(), windowId, pot);

        addDataSlots(pot.getSyncableData());
    }

    @Override
    protected boolean moveStack(ItemStack stack, int slotIndex)
    {
        return switch (typeOf(slotIndex))
            {
                case MAIN_INVENTORY, HOTBAR -> Helpers.isItem(stack, TFCTags.Items.FIREPIT_FUEL)
                    // Fuel is moved directly to the fuel inventory, always
                    ? !moveItemStackTo(stack, SLOT_FUEL_CONSUME, SLOT_FUEL_INPUT + 1, false)
                    // Non-fuel tries to move to the input slots, but only when it is not boiling
                    : blockEntity.hasRecipeStarted() || !moveItemStackTo(stack, SLOT_EXTRA_INPUT_START, SLOT_EXTRA_INPUT_END + 1, false);
                case CONTAINER -> !moveItemStackTo(stack, containerSlots, slots.size(), false);
            };
    }

    @Override
    protected void addContainerSlots()
    {
        for (int i = 0; i < 4; i++) // Fuel
        {
            addSlot(new CallbackSlot(blockEntity, i, 8, 70 - 18 * i));
        }
        addSlot(new CallbackSlot(blockEntity, SLOT_EXTRA_INPUT_START, 65, 23));
        addSlot(new CallbackSlot(blockEntity, SLOT_EXTRA_INPUT_START + 1, 83, 23));
        addSlot(new CallbackSlot(blockEntity, SLOT_EXTRA_INPUT_START + 2, 56, 41));
        addSlot(new CallbackSlot(blockEntity, SLOT_EXTRA_INPUT_START + 3, 74, 41));
        addSlot(new CallbackSlot(blockEntity, SLOT_EXTRA_INPUT_END, 92, 41));
    }
}
