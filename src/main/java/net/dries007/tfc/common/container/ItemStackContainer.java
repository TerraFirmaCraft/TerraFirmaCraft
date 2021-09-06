/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.container;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;

/**
 * A default container implementation for containers from item stacks.
 */
public class ItemStackContainer extends SimpleContainer
{
    protected final ItemStack stack;
    protected final Player player;

    protected final int itemIndex; // Index into the slot for the hotbar slot. Hotbar is at the end of the inventory.
    protected final int itemDragIndex; // Index in the hotbar. Between [0, 9)
    protected final boolean isOffhand;

    protected ItemStackContainer(MenuType<?> type, int windowId, Inventory playerInv, ItemStack stack, int yOffset)
    {
        super(type, windowId, playerInv, yOffset);

        this.player = playerInv.player;
        this.stack = stack;
        this.itemDragIndex = playerInv.selected;

        if (stack == player.getMainHandItem())
        {
            this.itemIndex = containerSlots + playerInv.selected + 27; // Main hand opened inventory
            this.isOffhand = false;
        }
        else
        {
            this.itemIndex = -100; // Offhand, so ignore this rule
            this.isOffhand = true;
        }
    }

    /**
     * Prevent any movement of the item stack from which this container was opened.
     */
    @Override
    public void clicked(int slot, int dragType, ClickType clickType, Player player)
    {
        if (slot == itemIndex || (dragType == itemDragIndex && clickType == ClickType.SWAP))
        {
            return;
        }
        super.clicked(slot, dragType, clickType, player);
    }
}

