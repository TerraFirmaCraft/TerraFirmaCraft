/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.container;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

/**
 * A very simple container implementation.
 * Used for gui's that have no internal inventory, or no TE they need to access
 */
public class SimpleContainer extends AbstractContainerMenu
{
    public SimpleContainer(MenuType<?> type, int windowId)
    {
        super(type, windowId);
    }

    public SimpleContainer(MenuType<?> type, int windowId, Inventory playerInv)
    {
        super(type, windowId);
        addPlayerInventorySlots(playerInv);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index)
    {
        ItemStack stackCopy = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot != null && slot.hasItem())
        {
            ItemStack stack = slot.getItem();
            stackCopy = stack.copy();

            if (index < 27)
            {
                if (!this.moveItemStackTo(stack, 27, 36, false))
                {
                    return ItemStack.EMPTY;
                }
            }
            else
            {
                if (!this.moveItemStackTo(stack, 0, 27, false))
                {
                    return ItemStack.EMPTY;
                }
            }

            if (stack.isEmpty())
            {
                slot.set(ItemStack.EMPTY);
            }
            else
            {
                slot.setChanged();
            }

            if (stack.getCount() == stackCopy.getCount())
            {
                return ItemStack.EMPTY;
            }

            ItemStack stackTake = slot.onTake(player, stack);
            if (index == 0)
            {
                player.drop(stackTake, false);
            }
        }

        return stackCopy;
    }

    @Override
    public boolean stillValid(Player playerIn)
    {
        return true;
    }

    protected void addPlayerInventorySlots(Inventory playerInv)
    {
        addPlayerInventorySlots(playerInv, 0);
    }

    protected void addPlayerInventorySlots(Inventory playerInv, int yOffset)
    {
        // Add Player Inventory Slots
        for (int i = 0; i < 3; i++)
        {
            for (int j = 0; j < 9; j++)
            {
                addSlot(new Slot(playerInv, j + i * 9 + 9, 8 + j * 18, 84 + i * 18 + yOffset));
            }
        }

        for (int k = 0; k < 9; k++)
        {
            addSlot(new Slot(playerInv, k, 8 + k * 18, 142 + yOffset));
        }
    }
}