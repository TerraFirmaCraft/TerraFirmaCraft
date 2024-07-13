/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.container;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blockentities.GrillBlockEntity;
import net.dries007.tfc.util.Helpers;

import static net.dries007.tfc.common.blockentities.GrillBlockEntity.*;

public class GrillContainer extends BlockEntityContainer<GrillBlockEntity>
{
    public static GrillContainer create(GrillBlockEntity grill, Inventory playerInv, int windowId)
    {
        return new GrillContainer(grill, windowId).init(playerInv, 20);
    }

    private GrillContainer(GrillBlockEntity grill, int windowId)
    {
        super(TFCContainerTypes.GRILL.get(), windowId, grill);

        addDataSlots(grill.getSyncableData());
    }

    @Override
    protected boolean moveStack(ItemStack stack, int slotIndex)
    {
        return switch (typeOf(slotIndex))
            {
                case MAIN_INVENTORY, HOTBAR -> {
                    if (Helpers.isItem(stack, TFCTags.Items.FIREPIT_FUEL))
                    {
                        yield !moveItemStackTo(stack, SLOT_FUEL_CONSUME, SLOT_FUEL_INPUT + 1, false);
                    }
                    else
                    {
                        yield !moveItemStackTo(stack, SLOT_EXTRA_INPUT_START, SLOT_EXTRA_INPUT_END + 1, false);
                    }
                }
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
        for (int i = SLOT_EXTRA_INPUT_START; i <= SLOT_EXTRA_INPUT_END; i++) // Grill input
        {
            addSlot(new CallbackSlot(blockEntity, i, 62 + (i - SLOT_EXTRA_INPUT_START) * 18, 20));
        }
    }
}
