/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.container;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

@ParametersAreNonnullByDefault
public abstract class ContainerItemStack extends Container implements ICapabilityUpdateContainer
{
    protected final ItemStack stack;
    protected final EntityPlayer player;
    protected int itemIndex;
    protected int itemDragIndex;
    protected boolean isOffhand;
    protected IContainerListener capabilityListener;

    protected ContainerItemStack(InventoryPlayer playerInv, ItemStack stack)
    {
        this.player = playerInv.player;
        this.stack = stack;
        this.itemDragIndex = playerInv.currentItem;

        if (stack == player.getHeldItemMainhand())
        {
            this.itemIndex = playerInv.currentItem + 27; // Mainhand opened inventory
            this.isOffhand = false;
        }
        else
        {
            this.itemIndex = -100; // Offhand, so ignore this rule
            this.isOffhand = true;
        }

        addContainerSlots();
        addPlayerInventorySlots(playerInv);
    }

    /**
     * This functionality duplicated from {@link ContainerTE#detectAndSendChanges()}
     */
    @Override
    public void detectAndSendChanges()
    {
        for (int i = 0; i < inventorySlots.size(); ++i)
        {
            ItemStack newStack = inventorySlots.get(i).getStack();
            ItemStack cachedStack = inventoryItemStacks.get(i);

            if (!ItemStack.areItemStacksEqual(cachedStack, newStack))
            {
                // Duplicated from Container#detectAndSendChanges
                boolean clientStackChanged = !ItemStack.areItemStacksEqualUsingNBTShareTag(cachedStack, newStack);
                cachedStack = newStack.isEmpty() ? ItemStack.EMPTY : newStack.copy();
                this.inventoryItemStacks.set(i, cachedStack);

                if (clientStackChanged)
                {
                    for (IContainerListener listener : this.listeners)
                    {
                        listener.sendSlotContents(this, i, cachedStack);
                    }
                }
                else if (capabilityListener != null)
                {
                    // There's a capability difference ONLY that needs to be synced, so we use our own handler here, as to not conflict with vanilla's sync, because this won't overwrite the client side item stack
                    // The listener will check if the item actually needs a sync based on capabilities we know we need to sync
                    capabilityListener.sendSlotContents(this, i, cachedStack);
                }
            }
        }
    }

    @Override
    @Nonnull
    public ItemStack transferStackInSlot(EntityPlayer player, int index)
    {
        // Slot that was clicked
        Slot slot = inventorySlots.get(index);

        ItemStack itemstack;

        if (slot == null || !slot.getHasStack())
            return ItemStack.EMPTY;

        if (index == itemIndex)
            return ItemStack.EMPTY;

        ItemStack itemstack1 = slot.getStack();
        itemstack = itemstack1.copy();

        // Begin custom transfer code here
        int containerSlots = inventorySlots.size() - player.inventory.mainInventory.size(); // number of slots in the container
        if (index < containerSlots)
        {
            // Transfer out of the container
            if (!this.mergeItemStack(itemstack1, containerSlots, inventorySlots.size(), true))
            {
                // Don't transfer anything
                return ItemStack.EMPTY;
            }
        }
        // Transfer into the container
        else
        {
            if (!this.mergeItemStack(itemstack1, 0, containerSlots, false))
            {
                return ItemStack.EMPTY;
            }
        }

        if (itemstack1.getCount() == 0)
        {
            slot.putStack(ItemStack.EMPTY);
        }
        else
        {
            slot.onSlotChanged();
        }
        if (itemstack1.getCount() == itemstack.getCount())
        {
            return ItemStack.EMPTY;
        }
        slot.onTake(player, itemstack1);
        return itemstack;
    }

    @Override
    @Nonnull
    public ItemStack slotClick(int slotID, int dragType, ClickType clickType, EntityPlayer player)
    {
        // Prevent moving of the item stack that is currently open
        if (slotID == itemIndex && (clickType == ClickType.QUICK_MOVE || clickType == ClickType.PICKUP || clickType == ClickType.THROW || clickType == ClickType.SWAP))
        {
            return ItemStack.EMPTY;
        }
        else if ((dragType == itemDragIndex) && clickType == ClickType.SWAP)
        {
            return ItemStack.EMPTY;
        }
        else
        {
            return super.slotClick(slotID, dragType, clickType, player);
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn)
    {
        return true;
    }

    @Override
    public void setCapabilityListener(IContainerListener capabilityListener)
    {
        this.capabilityListener = capabilityListener;
    }

    protected abstract void addContainerSlots();

    protected void addPlayerInventorySlots(InventoryPlayer playerInv)
    {
        // Add Player Inventory Slots
        for (int i = 0; i < 3; i++)
        {
            for (int j = 0; j < 9; j++)
            {
                addSlotToContainer(new Slot(playerInv, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for (int k = 0; k < 9; k++)
        {
            addSlotToContainer(new Slot(playerInv, k, 8 + k * 18, 142));
        }
    }
}
