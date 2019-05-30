/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.inventory.slot;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

import net.dries007.tfc.objects.te.TEInventory;

public class SlotTEInput extends SlotItemHandler
{
    private final TEInventory te;

    public SlotTEInput(@Nonnull IItemHandler inventory, int idx, int x, int y, @Nonnull TEInventory te)
    {
        super(inventory, idx, x, y);
        this.te = te;
    }

    @Override
    public void onSlotChanged()
    {
        // Calling this only happens here
        // If called in the container / item handler it can call during the middle of slot transfers, resulting in strange behavior
        te.setAndUpdateSlots(getSlotIndex());
        super.onSlotChanged();
    }

    @Override
    public boolean isItemValid(@Nonnull ItemStack stack)
    {
        return te.isItemValid(this.slotNumber, stack) && super.isItemValid(stack);
    }

    @Override
    public int getSlotStackLimit()
    {
        return Math.min(te.getSlotLimit(getSlotIndex()), super.getSlotStackLimit());
    }
}
