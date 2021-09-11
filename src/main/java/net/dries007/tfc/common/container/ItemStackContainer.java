/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.container;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;

/**
 * A default container implementation for containers from item stacks.
 */
public class ItemStackContainer extends Container
{
    protected final ItemStack stack;
    protected final Player player;
    protected final InteractionHand hand;

    protected final int hotbarIndex; // Index in the hotbar. Between [0, 9)
    protected int itemIndex; // Index into the slot for the hotbar slot. Hotbar is at the end of the inventory.

    protected ItemStackContainer(MenuType<?> type, int windowId, Inventory playerInv, ItemStack stack, InteractionHand hand)
    {
        super(type, windowId);

        this.player = playerInv.player;
        this.stack = stack;
        this.hand = hand;

        this.hotbarIndex = playerInv.selected;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <C extends Container> C init(Inventory playerInventory, int yOffset)
    {
        super.init(playerInventory, yOffset);

        // Must run after slots are initialized
        if (hand == InteractionHand.MAIN_HAND)
        {
            this.itemIndex = containerSlots + playerInventory.selected + 27; // Main hand opened inventory
        }
        else
        {
            this.itemIndex = -100; // Offhand, so ignore this rule
        }

        return (C) this;
    }

    /**
     * @return the target {@link ItemStack} of this container.
     */
    public ItemStack getTargetStack()
    {
        return stack;
    }

    /**
     * Prevent any movement of the item stack from which this container was opened.
     */
    @Override
    public void clicked(int slot, int dragType, ClickType clickType, Player player)
    {
        if (slot == itemIndex || (dragType == hotbarIndex && clickType == ClickType.SWAP))
        {
            return;
        }
        super.clicked(slot, dragType, clickType, player);
    }

    @FunctionalInterface
    public interface Factory<C extends ItemStackContainer>
    {
        C create(ItemStack stack, InteractionHand hand, Inventory playerInventory, int windowId);
    }
}

