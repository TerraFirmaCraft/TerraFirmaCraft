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

import net.dries007.tfc.api.capability.size.CapabilityItemSize;
import net.dries007.tfc.api.capability.size.IItemSize;
import net.dries007.tfc.api.capability.size.Size;
import net.dries007.tfc.objects.inventory.capability.ISlotCallback;
import net.dries007.tfc.objects.inventory.slot.SlotCallback;

public class ContainerSmallVessel extends ContainerItemStack implements ISlotCallback
{
    public ContainerSmallVessel(InventoryPlayer playerInv, ItemStack stack)
    {
        super(playerInv, stack);
        this.itemIndex += 4;
    }

    @Override
    public boolean isItemValid(int slot, @Nonnull ItemStack stack)
    {
        IItemSize size = CapabilityItemSize.getIItemSize(stack);
        if (size != null)
        {
            return size.getSize(stack).isSmallerThan(Size.LARGE);
        }
        return false;
    }

    @Override
    protected void addContainerSlots()
    {
        IItemHandler inventory = stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
        if (inventory != null)
        {
            addSlotToContainer(new SlotCallback(inventory, 0, 71, 23, this));
            addSlotToContainer(new SlotCallback(inventory, 1, 89, 23, this));
            addSlotToContainer(new SlotCallback(inventory, 2, 71, 41, this));
            addSlotToContainer(new SlotCallback(inventory, 3, 89, 41, this));
        }
    }
}
