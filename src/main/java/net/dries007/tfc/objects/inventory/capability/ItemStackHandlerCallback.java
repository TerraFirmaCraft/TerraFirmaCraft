/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.inventory.capability;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

public class ItemStackHandlerCallback extends ItemStackHandler
{
    private final ISlotCallback callback;

    public ItemStackHandlerCallback(ISlotCallback callback, int slots)
    {
        super(slots);
        this.callback = callback;
    }

    @Override
    public int getSlotLimit(int slot)
    {
        return callback.getSlotLimit(slot);
    }

    @Override
    public boolean isItemValid(int slot, @Nonnull ItemStack stack)
    {
        return callback.isItemValid(slot, stack);
    }

    @Override
    protected void onContentsChanged(int slot)
    {
        callback.setAndUpdateSlots(slot);
    }
}
