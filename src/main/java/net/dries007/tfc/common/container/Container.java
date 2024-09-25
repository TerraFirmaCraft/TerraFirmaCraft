/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.container;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

/**
 * Base container implementation, which just adds the player inventory.
 * Can be used as-is, although most commonly used with an implementation extending either {@link ItemStackContainer} or {@link BlockEntityContainer}
 */
public class Container extends AbstractContainerMenu
{
    public static Container create(MenuType<?> type, int windowId, Inventory playerInv)
    {
        return new Container(type, windowId).init(playerInv);
    }

    protected int containerSlots; // The number of slots in the container (not including the player inventory)
    @Nullable protected Player player;
    @Nullable protected ISlotCallback callback;

    protected Container(@Nullable ISlotCallback callback, MenuType<?> type, int windowId)
    {
        super(type, windowId);
        this.callback = callback;
    }

    protected Container(MenuType<?> type, int windowId)
    {
        this(null, type, windowId);
    }

    /**
     * Problem: calling add slots from the container superclass, means that we cannot access subclass parameters, such as an item fluid or tile entity, which are necessary in order to do some things such as setup container slots.
     * Solutions for running this at the right time are very difficult.
     * So, we have an explicit post-constructor-initialization method, which needs to be ran externally, but will always run after final fields have been initialized.
     *
     * @return The current container, casted down as required.
     */
    @SuppressWarnings("unchecked")
    public <C extends Container> C init(Inventory playerInventory, int yOffset)
    {
        addContainerSlots();
        containerSlots = slots.size();
        addPlayerInventorySlots(playerInventory, yOffset);
        player = playerInventory.player;
        return (C) this;
    }

    public <C extends Container> C init(Inventory playerInventory)
    {
        return init(playerInventory, 0);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index)
    {
        final Slot slot = slots.get(index);
        if (slot.hasItem()) // Only move an item when the index clicked has any contents
        {
            final ItemStack stack = slot.getItem(); // The item in the current slot
            final ItemStack original = stack.copy(); // The original amount in the slot
            if (moveStack(stack, index))
            {
                return ItemStack.EMPTY;
            }

            if (stack.getCount() == original.getCount())
            {
                return ItemStack.EMPTY;
            }

            // Handle updates
            if (stack.isEmpty())
            {
                slot.set(ItemStack.EMPTY);
            }
            else
            {
                slot.setChanged();
            }

            slot.onTake(player, stack);
            return original;
        }
        return ItemStack.EMPTY;
    }

    /**
     * In {@link AbstractContainerMenu#doClick} there is a call path through which {@link Slot#onTake} is not called. It just directly sets the slot, and the carried in the container.
     * We call the callback's slotless version here, as it's all we can realistically do.
     *
     * @param stack The stack that is set to be carried.
     */
    @Override
    public void setCarried(ItemStack stack)
    {
        if (callback != null)
        {
            callback.onCarried(stack);
        }
        super.setCarried(stack);
    }

    @Override
    public boolean stillValid(Player playerIn)
    {
        return true;
    }

    /**
     * Handles the actual movement of stacks in {@link #quickMoveStack(Player, int)} with as little boilerplate as possible.
     * The default implementation only moves stacks between the main inventory and the hotbar.
     *
     * @return {@code true} if no movement is possible, or the result of {@code !moveItemStackTo(...) || ...}
     */
    protected boolean moveStack(ItemStack stack, int slotIndex)
    {
        return switch (typeOf(slotIndex))
            {
                case CONTAINER -> true;
                case HOTBAR -> !moveItemStackTo(stack, containerSlots, containerSlots + 27, false);
                case MAIN_INVENTORY -> !moveItemStackTo(stack, containerSlots + 27, containerSlots + 36, false);
            };
    }

    /**
     * Adds container slots.
     * These are added before the player inventory (and as such, the player inventory will be shifted upwards by the number of slots added here.
     */
    protected void addContainerSlots() {}

    /**
     * Adds the player inventory slots to the container.
     */
    protected final void addPlayerInventorySlots(Inventory playerInv, int yOffset)
    {
        // Main Inventory. Indexes [0, 27)
        for (int i = 0; i < 3; i++)
        {
            for (int j = 0; j < 9; j++)
            {
                addSlot(new Slot(playerInv, j + i * 9 + 9, 8 + j * 18, 84 + i * 18 + yOffset));
            }
        }

        // Hotbar. Indexes [27, 36)
        for (int k = 0; k < 9; k++)
        {
            addSlot(new Slot(playerInv, k, 8 + k * 18, 142 + yOffset));
        }
    }

    /**
     * Container specific implementation, mimicking {@link AbstractContainerMenu#removed(Player)}'s logic, which handles dead and disconnected players properly. See TerraFirmaCraft#2407
     */
    protected final void giveItemStackToPlayerOrDrop(Player player, ItemStack stack)
    {
        if (player instanceof ServerPlayer serverPlayer)
        {
            if (player.isAlive() && !serverPlayer.hasDisconnected())
            {
                player.getInventory().placeItemBackInInventory(stack);
            }
            else
            {
                player.drop(stack, false);
            }
        }
    }

    public final IndexType typeOf(int index)
    {
        if (index < containerSlots)
        {
            return IndexType.CONTAINER;
        }
        else if (index < containerSlots + 27)
        {
            return IndexType.MAIN_INVENTORY;
        }
        return IndexType.HOTBAR;
    }

    public enum IndexType
    {
        CONTAINER,
        MAIN_INVENTORY,
        HOTBAR
    }
}