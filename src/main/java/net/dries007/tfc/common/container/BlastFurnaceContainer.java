/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.container;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

import net.dries007.tfc.common.blockentities.BlastFurnaceBlockEntity;
import net.dries007.tfc.common.container.slot.CallbackSlot;

public class BlastFurnaceContainer extends BlockEntityContainer<BlastFurnaceBlockEntity>
{
    public static BlastFurnaceContainer create(BlastFurnaceBlockEntity blastFurnace, Inventory playerInventory, int windowId)
    {
        return new BlastFurnaceContainer(windowId, blastFurnace).init(playerInventory, 20);
    }

    private BlastFurnaceContainer(int windowId, BlastFurnaceBlockEntity blastFurnace)
    {
        super(TFCContainerTypes.BLAST_FURNACE.get(), windowId, blastFurnace);

        addDataSlots(blastFurnace.getSyncedData());
    }

    @Override
    protected boolean moveStack(ItemStack stack, int slotIndex)
    {
        return switch (typeOf(slotIndex))
            {
                case CONTAINER -> !moveItemStackTo(stack, containerSlots, containerSlots + 36, false);
                case HOTBAR, MAIN_INVENTORY -> !moveItemStackTo(stack, 0, 1, false);
            };
    }

    @Override
    protected void addContainerSlots()
    {
        addSlot(new CallbackSlot(blockEntity, 0, 152, 17));
    }
}
