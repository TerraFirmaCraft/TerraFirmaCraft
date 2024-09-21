/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.container;

import java.util.EnumSet;
import java.util.Set;
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
    private static final Set<ClickType> ILLEGAL_ITEM_CLICKS = EnumSet.of(ClickType.QUICK_MOVE, ClickType.PICKUP, ClickType.THROW, ClickType.SWAP);

    protected final ItemStack stack;
    protected final Player player;
    protected final InteractionHand hand;

    protected final int hotbarIndex; // Index in the hotbar. Between [0, 9), or -1 if this is the offhand
    protected int itemIndex; // Index into the slot for the hotbar slot. Hotbar is at the end of the inventory.

    protected ItemStackContainer(MenuType<?> type, int windowId, Inventory playerInv, ItemStack stack, InteractionHand hand, int slot)
    {
        super(type, windowId);

        this.player = playerInv.player;
        this.stack = stack;
        this.hand = hand;

        this.hotbarIndex = slot;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <C extends Container> C init(Inventory playerInventory, int yOffset)
    {
        super.init(playerInventory, yOffset);

        // Must run after slots are initialized
        if (hand == InteractionHand.MAIN_HAND)
        {
            this.itemIndex = containerSlots + hotbarIndex + 27; // Main hand opened inventory
        }
        else
        {
            this.itemIndex = -100; // Offhand, so ignore this rule
        }

        return (C) this;
    }

    /**
     * Note: on a server side container, the target unsealedStack is never overwritten (in general).
     * However, on a client side container, the target unsealedStack can be overwritten due to synchronization, for example, as triggered from a NBT change, which was caused by a capability instance. As a result, we cannot cache the unsealedStack in a way visible to the client side container, or screen. So we need to re-query it.
     * <p>
     * <strong>Do not cache the result of this function!</strong> It may not be valid!
     *
     * @return the target {@link ItemStack} of this container.
     */
    public ItemStack getTargetStack()
    {
        return hand == InteractionHand.MAIN_HAND ? slots.get(itemIndex).getItem() : player.getOffhandItem();
    }

    @Override
    public boolean stillValid(Player player)
    {
        return !getTargetStack().isEmpty();
    }

    /**
     * Prevent any movement of the item unsealedStack from which this container was opened.
     */
    @Override
    public void clicked(int slot, int dragType, ClickType clickType, Player player)
    {
        // We can't move if:
        // the slot is the item index, and it's an illegal action (like, swapping the items)
        // the hotbar item is being swapped out
        // the action is "pickup all" (this ignores every slot, so we cannot allow it)
        if ((slot == itemIndex && ILLEGAL_ITEM_CLICKS.contains(clickType)) ||
            (dragType == hotbarIndex && clickType == ClickType.SWAP) ||
            (dragType == 40 && clickType == ClickType.SWAP && hand == InteractionHand.OFF_HAND) ||
            clickType == ClickType.PICKUP_ALL)
        {
            return;
        }
        super.clicked(slot, dragType, clickType, player);
    }

    @FunctionalInterface
    public interface Factory<C extends ItemStackContainer>
    {
        C create(ItemStack stack, InteractionHand hand, int slot, Inventory playerInventory, int windowId);
    }
}

