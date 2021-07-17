/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.inventory.Slot;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import net.dries007.tfc.api.capability.food.CapabilityFood;
import net.dries007.tfc.api.capability.food.FoodTrait;
import net.dries007.tfc.api.capability.food.IFood;
import net.dries007.tfc.objects.inventory.capability.ISlotCallback;
import net.dries007.tfc.objects.inventory.slot.SlotCallback;

import javax.annotation.Nonnull;

public class ContainerSmallVessel extends ContainerItemStack implements ISlotCallback
{
    public ContainerSmallVessel(InventoryPlayer playerInv, ItemStack stack)
    {
        super(playerInv, stack);
        this.itemIndex += 4;
    }

    @Override
    protected void addContainerSlots()
    {
        IItemHandler inventory = stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
        if (inventory instanceof ISlotCallback)
        {
            ISlotCallback callback = (ISlotCallback) inventory;
            addSlotToContainer(new SlotCallback(inventory, 0, 71, 23, callback));
            addSlotToContainer(new SlotCallback(inventory, 1, 89, 23, callback));
            addSlotToContainer(new SlotCallback(inventory, 2, 71, 41, callback));
            addSlotToContainer(new SlotCallback(inventory, 3, 89, 41, callback));
        }
    }

    /**
     * Copied from ContainerItemStack, but modified to change preserved trait
     */
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

            // Player shift-clicked item out of vessel. Shift-clicks ignore the
            // returned stack from ItemSmallVessel.extractItem, so the preserved
            // trait of food items need to be updated here. Unset the preserved
            // trait of this stack
            IFood cap = itemstack1.getCapability(CapabilityFood.CAPABILITY, null);
            if (cap != null)
            {
                CapabilityFood.removeTrait(cap, FoodTrait.PRESERVED);
            }

            if (!this.mergeItemStack(itemstack1, containerSlots, inventorySlots.size(), true))
            {
                // Set the preserved trait again; items failed to transfer
                IFood capFail = itemstack1.getCapability(CapabilityFood.CAPABILITY, null);
                if (capFail != null)
                {
                    CapabilityFood.applyTrait(capFail, FoodTrait.PRESERVED);
                }

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
}
