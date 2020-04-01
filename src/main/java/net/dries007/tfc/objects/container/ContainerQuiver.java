/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.container;

import javax.annotation.Nonnull;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import net.dries007.tfc.objects.inventory.capability.ISlotCallback;
import net.dries007.tfc.objects.inventory.slot.SlotCallback;
import net.dries007.tfc.objects.items.ItemQuiver;

public class ContainerQuiver extends ContainerItemStack implements ISlotCallback
{
    public ContainerQuiver(InventoryPlayer playerInv, ItemStack stack)
    {
        super(playerInv, stack);
        this.itemIndex += 8;
    }

    @Override
    public boolean isItemValid(int slot, @Nonnull ItemStack stack)
    {
        IItemHandler quiverCapability = this.stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
        if (quiverCapability instanceof ItemQuiver.QuiverCapability)
        {
            return quiverCapability.isItemValid(slot, stack);
        }
        return false;
    }

    @Override
    protected void addContainerSlots()
    {
        IItemHandler inventory = stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
        if (inventory != null)
        {
            addSlotToContainer(new SlotCallback(inventory, 0, 53, 23, this));
            addSlotToContainer(new SlotCallback(inventory, 1, 71, 23, this));
            addSlotToContainer(new SlotCallback(inventory, 2, 89, 23, this));
            addSlotToContainer(new SlotCallback(inventory, 3, 107, 23, this));
            addSlotToContainer(new SlotCallback(inventory, 4, 53, 41, this));
            addSlotToContainer(new SlotCallback(inventory, 5, 71, 41, this));
            addSlotToContainer(new SlotCallback(inventory, 6, 89, 41, this));
            addSlotToContainer(new SlotCallback(inventory, 7, 107, 41, this));
        }
    }
}
