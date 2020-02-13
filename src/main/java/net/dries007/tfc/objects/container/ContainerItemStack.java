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

import net.dries007.tfc.api.capability.heat.CapabilityItemHeat;
import net.dries007.tfc.api.capability.heat.Heat;
import net.dries007.tfc.api.capability.heat.IItemHeat;

@ParametersAreNonnullByDefault
public abstract class ContainerItemStack extends Container
{
    protected final ItemStack stack;
    protected final EntityPlayer player;
    protected int itemIndex;
    protected int itemDragIndex;
    protected boolean isOffhand;

    ContainerItemStack(InventoryPlayer playerInv, ItemStack stack)
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
            //tile.setAndUpdateSlots(index);
        }
        // Transfer into the container
        else
        {
            if (!this.mergeItemStack(itemstack1, 0, containerSlots, false))
            {
                return ItemStack.EMPTY;
            }
        }

        // Required
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

    @Override
    public void detectAndSendChanges()
    {
        // Same as ContainerTE
        for (int i = 0; i < inventorySlots.size(); ++i)
        {
            ItemStack stack = inventorySlots.get(i).getStack();
            ItemStack newStack = inventoryItemStacks.get(i);

            if (!ItemStack.areItemStacksEqual(newStack, stack))
            {
                // Since heat temperatures are updated every tick, it can cause network issues (server sending too many update packets = overriding slots, ghost items, etc)
                // To alleviate that, we're gonna update the client on tooltip changes only
                boolean updateClient = true;
                IItemHeat cap1 = stack.getCapability(CapabilityItemHeat.ITEM_HEAT_CAPABILITY, null);
                IItemHeat cap2 = newStack.getCapability(CapabilityItemHeat.ITEM_HEAT_CAPABILITY, null);
                if (cap1 != null && cap2 != null && Heat.compareHeat(cap1.getTemperature(), cap2.getTemperature()))
                {
                    updateClient = false;
                }
                // May need to do the same for food decay?
                newStack = stack.isEmpty() ? ItemStack.EMPTY : stack.copy();
                inventoryItemStacks.set(i, newStack);
                if (updateClient)
                {
                    for (IContainerListener listener : listeners)
                    {
                        listener.sendSlotContents(this, i, newStack);
                    }
                }
            }
        }
    }
}
