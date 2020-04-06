/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.container;

import javax.annotation.Nonnull;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import net.dries007.tfc.api.capability.size.CapabilityItemSize;
import net.dries007.tfc.api.capability.size.IItemSize;
import net.dries007.tfc.api.capability.size.Size;

public class ContainerChestTFC extends Container
{
    private final IInventory lowerChestInventory;

    public ContainerChestTFC(IInventory playerInventory, IInventory chestInventory, EntityPlayer player)
    {
        this.lowerChestInventory = chestInventory;
        int numRows = chestInventory.getSizeInventory() / 9;
        chestInventory.openInventory(player);
        int i = (numRows - 4) * 18;

        //Adding chest slots
        for (int j = 0; j < numRows; ++j)
        {
            for (int k = 0; k < 9; ++k)
            {
                this.addSlotToContainer(new SlotChestTFC(chestInventory, k + j * 9, 8 + k * 18, 18 + j * 18));
            }
        }

        //Adding player slots
        for (int l = 0; l < 3; ++l)
        {
            for (int j = 0; j < 9; ++j)
            {
                this.addSlotToContainer(new Slot(playerInventory, j + l * 9 + 9, 8 + j * 18, 103 + l * 18 + i));
            }
        }

        for (int i1 = 0; i1 < 9; ++i1)
        {
            this.addSlotToContainer(new Slot(playerInventory, i1, 8 + i1 * 18, 161 + i));
        }
    }

    /**
     * Copied from {@link ContainerTE}
     */
    @Override
    @Nonnull
    public ItemStack transferStackInSlot(EntityPlayer player, int index)
    {
        // Slot that was clicked
        Slot slot = inventorySlots.get(index);
        if (slot != null && slot.getHasStack())
        {
            ItemStack stack = slot.getStack();
            ItemStack stackCopy = stack.copy();

            // Transfer out of the container
            int containerSlots = inventorySlots.size() - player.inventory.mainInventory.size();
            if (index < containerSlots)
            {
                if (!mergeItemStack(stack, containerSlots, inventorySlots.size(), true))
                {
                    return ItemStack.EMPTY;
                }
            }
            // Transfer into the container
            else if (!mergeItemStack(stack, 0, containerSlots, false))
            {
                return ItemStack.EMPTY;
            }

            if (stack.getCount() == 0)
            {
                slot.putStack(ItemStack.EMPTY);
            }
            else
            {
                slot.onSlotChanged();
            }
            if (stack.getCount() == stackCopy.getCount())
            {
                return ItemStack.EMPTY;
            }
            slot.onTake(player, stack);
            return stackCopy;
        }
        return ItemStack.EMPTY;
    }

    /**
     * Called when the container is closed.
     */
    @Override
    public void onContainerClosed(EntityPlayer playerIn)
    {
        super.onContainerClosed(playerIn);
        this.lowerChestInventory.closeInventory(playerIn);
    }

    @Override
    public boolean canInteractWith(@Nonnull EntityPlayer playerIn)
    {
        return this.lowerChestInventory.isUsableByPlayer(playerIn);
    }

    /**
     * Gets the inventory associated with this chest container.
     */
    public IInventory getLowerChestInventory()
    {
        return this.lowerChestInventory;
    }

    private static class SlotChestTFC extends Slot
    {

        SlotChestTFC(IInventory inventoryIn, int index, int xPosition, int yPosition)
        {
            super(inventoryIn, index, xPosition, yPosition);
        }

        @Override
        public boolean isItemValid(ItemStack stack)
        {
            IItemSize cap = CapabilityItemSize.getIItemSize(stack);
            if (cap != null)
            {
                return cap.getSize(stack).isSmallerThan(Size.VERY_LARGE);
            }
            return true;
        }
    }
}
