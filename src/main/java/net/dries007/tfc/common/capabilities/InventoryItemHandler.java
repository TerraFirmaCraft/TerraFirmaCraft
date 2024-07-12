/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.capabilities;

import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ItemStackHandler;

import net.dries007.tfc.common.container.ISlotCallback;

public class InventoryItemHandler extends ItemStackHandler implements ISlotCallback
{
    private final ISlotCallback callback;

    public InventoryItemHandler(ISlotCallback callback, int slots)
    {
        super(slots);
        this.callback = callback;
    }

    /**
     * Provide access to the internal stacks, when direct mutation is acceptable.
     */
    public NonNullList<ItemStack> getInternalStacks()
    {
        return stacks;
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

    @Override
    protected void onContentsChanged(int slot)
    {
        callback.setAndUpdateSlots(slot);
    }

    @Override
    public int getSlotStackLimit(int slot)
    {
        return callback.getSlotStackLimit(slot);
    }

    @Override
    public void setAndUpdateSlots(int slot)
    {
        callback.setAndUpdateSlots(slot);
    }

    @Override
    public void onSlotTake(Player player, int slot, ItemStack stack)
    {
        callback.onSlotTake(player, slot, stack);
    }
}
