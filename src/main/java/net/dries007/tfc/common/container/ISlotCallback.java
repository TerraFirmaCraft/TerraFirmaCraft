/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.container;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.ItemStack;

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
     * Called when a slot is asked for its item inside, BEFORE the read actually goes through.
     * This is done to cause loot tables to unpack themselves first.
     *
     * Check where this is actually implemented. Not all ISlotCallback impls need or want this!
     */
    default void slotChecked() { }

    /**
     * Called when a slot is taken from
     */
    default void onSlotTake(Player player, int slot, ItemStack stack) {}

    /**
     * Called when an item stack is carried in the inventory. Useful for cases where {@link #onSlotTake(Player, int, ItemStack)} won't be called due to vanilla weirdness in {@link net.minecraft.world.inventory.AbstractContainerMenu#doClick(int, int, ClickType, Player)}.
     */
    default void onCarried(ItemStack stack) {}
}
