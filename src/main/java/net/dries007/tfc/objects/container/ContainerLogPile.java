/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 *
 */

package net.dries007.tfc.objects.container;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import mcp.MethodsReturnNonnullByDefault;
import net.dries007.tfc.objects.inventory.SlotTEInput;
import net.dries007.tfc.objects.te.TELogPile;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ContainerLogPile extends ContainerTFC<TELogPile>
{

    public ContainerLogPile(InventoryPlayer playerInv, TELogPile te)
    {
        super(playerInv, te);
    }

    @Override
    @Nonnull
    public ItemStack transferStackInSlot(EntityPlayer player, int index)
    {
        // Slot that was clicked
        Slot slot = inventorySlots.get(index);

        ItemStack itemstack;

        if (slot == null || !slot.getHasStack()) { return ItemStack.EMPTY; }

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
            tile.setAndUpdateSlots(index);
        }
        // Transfer into the container
        else
        {
            if (!this.mergeItemStack(itemstack1, 0, 4, false))
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
    protected void addContainerSlots(TELogPile tile)
    {
        IItemHandler inventory = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
        if (inventory != null)
        {
            addSlotToContainer(new SlotTEInput(inventory, 0, 71, 23, tile, TELogPile::isStackValid));
            addSlotToContainer(new SlotTEInput(inventory, 1, 89, 23, tile, TELogPile::isStackValid));
            addSlotToContainer(new SlotTEInput(inventory, 2, 71, 41, tile, TELogPile::isStackValid));
            addSlotToContainer(new SlotTEInput(inventory, 3, 89, 41, tile, TELogPile::isStackValid));
        }
    }

    @Override
    public boolean canInteractWith(@Nonnull EntityPlayer player)
    {
        if (this.tile.burning) { return false; }
        return this.tile.countLogs() > 0;
    }
}
