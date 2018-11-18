/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.container;

import javax.annotation.Nonnull;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import net.dries007.tfc.objects.te.TEInventory;
import net.dries007.tfc.util.ITileFields;

public abstract class ContainerTE<TE extends TEInventory> extends Container
{
    protected TE tile;

    private int[] cachedFields;

    ContainerTE(InventoryPlayer playerInv, TE te)
    {
        this.tile = te;

        addContainerSlots();
        addPlayerInventorySlots(playerInv);
    }

    @Override
    @Nonnull
    public ItemStack transferStackInSlot(EntityPlayer player, int index)
    {
        // Slot that was clicked
        Slot slot = inventorySlots.get(index);
        if (slot == null || !slot.getHasStack())
            return ItemStack.EMPTY;

        ItemStack stack = slot.getStack();
        ItemStack stackCopy = stack.copy();

        // Transfer out of the container
        int containerSlots = inventorySlots.size() - player.inventory.mainInventory.size();
        if (index < containerSlots)
        {
            if (!this.mergeItemStack(stack, containerSlots, inventorySlots.size(), true))
            {
                return ItemStack.EMPTY;
            }
            tile.setAndUpdateSlots(index);
        }
        // Transfer into the container
        else
        {
            if (!this.mergeItemStack(stack, 0, containerSlots, false))
            {
                return ItemStack.EMPTY;
            }
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

    @Override
    public boolean canInteractWith(@Nonnull EntityPlayer player)
    {
        return true;
    }

    protected abstract void addContainerSlots();

    private void addPlayerInventorySlots(InventoryPlayer playerInv)
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
        super.detectAndSendChanges();

        if (tile instanceof ITileFields)
        {
            ITileFields tileFields = (ITileFields) tile;
            boolean allFieldsHaveChanged = false;
            boolean fieldHasChanged[] = new boolean[tileFields.getFieldCount()];

            if (cachedFields == null)
            {
                cachedFields = new int[tileFields.getFieldCount()];
                allFieldsHaveChanged = true;
            }

            for (int i = 0; i < cachedFields.length; ++i)
            {
                if (allFieldsHaveChanged || cachedFields[i] != tileFields.getField(i))
                {
                    cachedFields[i] = tileFields.getField(i);
                    fieldHasChanged[i] = true;
                }
            }

            // go through the list of listeners (players using this container) and update them if necessary
            for (IContainerListener listener : this.listeners)
            {
                for (int fieldID = 0; fieldID < tileFields.getFieldCount(); ++fieldID)
                {
                    if (fieldHasChanged[fieldID])
                    {
                        // Note that although sendWindowProperty takes 2 ints on a server these are truncated to shorts
                        listener.sendWindowProperty(this, fieldID, cachedFields[fieldID]);
                    }
                }
            }
        }
    }
}
