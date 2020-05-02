/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.inventory.capability;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.items.IItemHandlerModifiable;

public class ItemHandlerSidedWrapper implements IItemHandlerModifiable
{
    private final IItemHandlerSidedCallback callback;
    private final IItemHandlerModifiable handler;
    private final EnumFacing side;

    public ItemHandlerSidedWrapper(IItemHandlerSidedCallback callback, IItemHandlerModifiable handler, EnumFacing side)
    {
        this.callback = callback;
        this.handler = handler;
        this.side = side;
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
        if (callback.canInsert(slot, stack, side))
        {
            return handler.insertItem(slot, stack, simulate);
        }
        return stack;
    }

    @Nonnull
    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate)
    {
        if (callback.canExtract(slot, side))
        {
            return handler.extractItem(slot, amount, simulate);
        }
        return ItemStack.EMPTY;
    }

    @Override
    public int getSlotLimit(int slot)
    {
        return handler.getSlotLimit(slot);
    }
}
