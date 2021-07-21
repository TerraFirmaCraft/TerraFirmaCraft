package net.dries007.tfc.common.container;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;

public abstract class ItemStackContainer extends SimpleContainer
{
    protected final ItemStack stack;
    protected final PlayerEntity player;
    protected int itemIndex;
    protected int itemDragIndex;
    protected boolean isOffhand;

    public ItemStackContainer(ContainerType<?> type, int windowId, PlayerInventory playerInv, ItemStack stack, int yOffset)
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

    public ItemStackContainer(ContainerType<?> type, int windowId, PlayerInventory playerInv, ItemStack stack)
    {
        this(type, windowId, playerInv, stack, 0);
    }

    @Override
    public ItemStack quickMoveStack(PlayerEntity player, int index)
    {
        ItemStack stackCopy = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot != null && slot.hasItem())
        {
            ItemStack stack = slot.getItem();
            stackCopy = stack.copy();

            // begin custom transfer code here
            int containerSlots = slots.size() - player.inventory.items.size();
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

            ItemStack stackTake = slot.onTake(player, stack);
            if (index == 0)
            {
                player.drop(stackTake, false);
            }
        }

        return stackCopy;
    }

    @Override
    public ItemStack clicked(int slot, int dragType, ClickType clickType, PlayerEntity player)
    {
        if (slot == itemIndex && (clickType == ClickType.QUICK_MOVE || clickType == ClickType.PICKUP || clickType == ClickType.THROW || clickType == ClickType.SWAP))
        {
            return ItemStack.EMPTY;
        }
        else if ((dragType == itemDragIndex) && clickType == ClickType.SWAP)
        {
            return ItemStack.EMPTY;
        }
        else
        {
            return super.clicked(slot, dragType, clickType, player);
        }
    }

    protected abstract void addContainerSlots();
}

