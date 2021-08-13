/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.capabilities;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandlerModifiable;

/**
 * A partially exposed item handler, implementing the actual handler part of {@link SidedHandler} for {@link net.minecraftforge.items.IItemHandler}s
 * This allows selective slots to be set as valid to insert or extract. Any other operations are denied.
 */
public class PartialItemHandler implements DelegateItemHandler
{
    private final IItemHandlerModifiable internal;
    private final boolean[] insertSlots;
    private final boolean[] extractSlots;

    public PartialItemHandler(IItemHandlerModifiable internal)
    {
        this.internal = internal;
        this.insertSlots = new boolean[internal.getSlots()];
        this.extractSlots = new boolean[internal.getSlots()];
    }

    public PartialItemHandler insert(int... slots)
    {
        for (int slot : slots)
        {
            insertSlots[slot] = true;
        }
        return this;
    }

    public PartialItemHandler extract(int... slots)
    {
        for (int slot : slots)
        {
            extractSlots[slot] = true;
        }
        return this;
    }

    @Override
    public IItemHandlerModifiable getItemHandler()
    {
        return internal;
    }

    @Nonnull
    @Override
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate)
    {
        return insertSlots[slot] ? internal.insertItem(slot, stack, simulate) : stack;
    }

    @Nonnull
    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate)
    {
        return extractSlots[slot] ? internal.extractItem(slot, amount, simulate) : ItemStack.EMPTY;
    }
}
