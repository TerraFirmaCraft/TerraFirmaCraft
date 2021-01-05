package net.dries007.tfc.common.container;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

/**
 * Helper interface for interacting with ItemStackHandler
 */
public interface ISlotCallback
{
    /**
     * Gets the slot stack size
     */
    default int getSlotStackLimit(int slot)
    {
        return 64;
    }

    /**
     * Checks if an item is valid for a slot
     *
     * @return true if the item can be inserted
     */
    default boolean isItemValid(int slot, ItemStack stack)
    {
        return true;
    }

    /**
     * Called when a slot changed
     *
     * @param slot the slot index, or -1 if the call method had no specific slot
     */
    default void setAndUpdateSlots(int slot) {}

    /**
     * Called when a slot is taken from
     */
    default void onSlotTake(PlayerEntity player, int slot, ItemStack stack) {}
}
