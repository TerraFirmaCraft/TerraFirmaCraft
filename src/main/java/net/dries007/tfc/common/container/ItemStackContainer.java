/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.container;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public abstract class ItemStackContainer extends SimpleContainer
{
    protected final ItemStack stack;
    protected final Player player;
    protected int itemIndex;
    protected int itemDragIndex;
    protected boolean isOffhand;

    public ItemStackContainer(MenuType<?> type, int windowId, Inventory playerInv, ItemStack stack, int yOffset)
    {
        super(type, windowId);
        this.player = playerInv.player;
        this.stack = stack;
        this.itemDragIndex = playerInv.selected;

        if (stack == player.getMainHandItem())
        {
            this.itemIndex = playerInv.selected + 27; // Mainhand opened inventory
            this.isOffhand = false;
        }
        else
        {
            this.itemIndex = -100; // Offhand, so ignore this rule
            this.isOffhand = true;
        }

        addContainerSlots();
        addPlayerInventorySlots(playerInv, yOffset);
    }

    public ItemStackContainer(MenuType<?> type, int windowId, Inventory playerInv, ItemStack stack)
    {
        this(type, windowId, playerInv, stack, 0);
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

            // begin custom transfer code here
            int containerSlots = slots.size() - player.getInventory().items.size();
            if (index < containerSlots)
            {
                // Transfer out of the container
                if (!this.moveItemStackTo(stack, containerSlots, slots.size(), true))
                {
                    // Don't transfer anything
                    return ItemStack.EMPTY;
                }
            }
            else
            {
                if (!this.moveItemStackTo(stack, 0, containerSlots, false))
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

            // todo: what? slot.onTake() has no return value anymore
            //ItemStack stackTake = slot.onTake(player, stack);
            //if (index == 0)
            //{
            //    player.drop(stackTake, false);
            //}
        }

        return stackCopy;
    }

    @Override
    public void clicked(int slot, int dragType, ClickType clickType, Player player)
    {
        // todo: wot?
        if (slot == itemIndex && (clickType == ClickType.QUICK_MOVE || clickType == ClickType.PICKUP || clickType == ClickType.THROW || clickType == ClickType.SWAP))
        {
            // return ItemStack.EMPTY;
        }
        else if ((dragType == itemDragIndex) && clickType == ClickType.SWAP)
        {
            // return ItemStack.EMPTY;
        }
        else
        {
            // return super.clicked(slot, dragType, clickType, player);
        }
    }

    protected abstract void addContainerSlots();
}

