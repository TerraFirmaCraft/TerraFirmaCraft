/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.inventory.capability;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;

import net.dries007.tfc.objects.inventory.slot.SlotCallback;

/**
 * This is a callback for various methods on an ItemStackHandler.
 * Methods are default to support overriding as many or as little as nessecary
 *
 * {@link ItemStackHandlerCallback}
 */
public interface ISlotCallback
{
    /**
     * Gets the slot stack size
     *
     * @param slot the slot index
     */
    default int getSlotLimit(int slot)
    {
        return 64;
    }

    /**
     * Checks if an item is valid for a slot
     *
     * @param slot  the slot index
     * @param stack the stack to be inserted
     * @return true if the item can be inserted
     */
    default boolean isItemValid(int slot, @Nonnull ItemStack stack)
    {
        return true;
    }

    /**
     * Called when a slot changed
     *
     * @param slot the slot index, or -1 if the call method had no specific slot
     */
    default void setAndUpdateSlots(int slot) {}

    default void beforePutStack(@Nonnull SlotCallback slot, @Nonnull ItemStack stack) {}
}
