/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.inventory;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandlerModifiable;

public class LockableItemHandler implements IItemHandlerModifiable
{
    private boolean locked;
    private IItemHandlerModifiable handler;

    public LockableItemHandler(IItemHandlerModifiable handler)
    {
        this.handler = handler;
    }

    public void setLockStatus(boolean locked)
    {
        this.locked = locked;
    }

    @Override
    public void setStackInSlot(int slot, @Nonnull ItemStack stack)
    {
        handler.setStackInSlot(slot, stack);
    }

    @Override
    public int getSlots()
    {
        return handler.getSlots();
    }

    @Nonnull
    @Override
    public ItemStack getStackInSlot(int slot)
    {
        return handler.getStackInSlot(slot);
    }

    @Nonnull
    @Override
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate)
    {
        if (locked)
        {
            return stack;
        }

        return handler.insertItem(slot, stack, simulate);
    }

    @Nonnull
    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate)
    {
        if (locked)
        {
            return ItemStack.EMPTY;
        }

        return handler.extractItem(slot, amount, simulate);
    }

    @Override
    public int getSlotLimit(int slot)
    {
        return handler.getSlotLimit(slot);
    }
}
