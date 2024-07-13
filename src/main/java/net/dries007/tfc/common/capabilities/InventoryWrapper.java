/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.capabilities;

import javax.annotation.Nonnull;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.NotNull;

import net.dries007.tfc.common.container.ISlotCallback;



public record InventoryWrapper(Container container, ISlotCallback callback) implements IItemHandlerModifiable
{

    @Override
    public void setStackInSlot(int slot, ItemStack stack)
    {
        container.setItem(slot, stack);
        callback.setAndUpdateSlots(slot);
    }

    @Override
    public int getSlots()
    {
        return container.getContainerSize();
    }

    @NotNull
    @Override
    public ItemStack getStackInSlot(int slot)
    {
        validateSlotIndex(slot);
        return container.getItem(slot);
    }

    @NotNull
    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate)
    {
        if (stack.isEmpty())
        {
            return ItemStack.EMPTY;
        }

        if (!isItemValid(slot, stack))
        {
            return stack;
        }

        final ItemStack existing = getStackInSlot(slot);
        int limit = getStackLimit(slot, stack);

        if (!existing.isEmpty())
        {
            if (!ItemStack.isSameItemSameComponents(stack, existing))
            {
                return stack;
            }
            limit -= existing.getCount();
        }

        if (limit <= 0)
        {
            return stack;
        }

        final boolean reachedLimit = stack.getCount() > limit;
        if (!simulate)
        {
            if (existing.isEmpty())
            {
                setStackInSlot(slot, reachedLimit ? stack.copyWithCount(limit) : stack);
            }
            else
            {
                existing.grow(reachedLimit ? limit : stack.getCount());
                callback.setAndUpdateSlots(slot);
            }
        }
        return reachedLimit ? stack.copyWithCount(stack.getCount() - limit) : ItemStack.EMPTY;
    }

    @NotNull
    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate)
    {
        if (amount == 0)
        {
            return ItemStack.EMPTY;
        }

        final ItemStack existing = getStackInSlot(slot);
        if (existing.isEmpty())
        {
            return ItemStack.EMPTY;
        }

        final int toExtract = Math.min(amount, existing.getMaxStackSize());
        if (existing.getCount() <= toExtract)
        {
            if (!simulate)
            {
                setStackInSlot(slot, ItemStack.EMPTY);
                return existing;
            }
            else
            {
                return existing.copy();
            }
        }
        else
        {
            if (!simulate)
            {
                setStackInSlot(slot, existing.copyWithCount(existing.getCount() - toExtract));
            }
            return existing.copyWithCount(toExtract);
        }
    }

    @Override
    public int getSlotLimit(int slot)
    {
        return callback.getSlotStackLimit(slot);
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack)
    {
        return callback.isItemValid(slot, stack);
    }

    private int getStackLimit(int slot, @Nonnull ItemStack stack)
    {
        return Math.min(getSlotLimit(slot), stack.getMaxStackSize());
    }

    private void validateSlotIndex(int slot)
    {
        if (slot < 0 || slot >= getSlots())
        {
            throw new RuntimeException("Slot " + slot + " not in valid range - [0," + getSlots() + ")");
        }
    }
}
