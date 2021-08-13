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
 * A delegate interface for {@link IItemHandlerModifiable}
 */
public interface DelegateItemHandler extends IItemHandlerModifiable
{
    IItemHandlerModifiable getItemHandler();

    @Override
    default void setStackInSlot(int slot, @Nonnull ItemStack stack)
    {
        getItemHandler().setStackInSlot(slot, stack);
    }

    @Override
    default int getSlots()
    {
        return getItemHandler().getSlots();
    }

    @Nonnull
    @Override
    default ItemStack getStackInSlot(int slot)
    {
        return getItemHandler().getStackInSlot(slot);
    }

    @Nonnull
    @Override
    default ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate)
    {
        return getItemHandler().insertItem(slot, stack, simulate);
    }

    @Nonnull
    @Override
    default ItemStack extractItem(int slot, int amount, boolean simulate)
    {
        return getItemHandler().extractItem(slot, amount, simulate);
    }

    @Override
    default int getSlotLimit(int slot)
    {
        return getItemHandler().getSlotLimit(slot);
    }

    @Override
    default boolean isItemValid(int slot, @Nonnull ItemStack stack)
    {
        return getItemHandler().isItemValid(slot, stack);
    }
}
