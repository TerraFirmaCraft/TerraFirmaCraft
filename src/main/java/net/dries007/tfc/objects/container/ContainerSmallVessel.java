/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.container;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import net.dries007.tfc.api.capability.size.Size;
import net.dries007.tfc.objects.inventory.SlotSized;

public class ContainerSmallVessel extends ContainerItemStack
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
        if (inventory != null)
        {
            addSlotToContainer(new SlotSized(inventory, 0, 71, 23, Size.NORMAL));
            addSlotToContainer(new SlotSized(inventory, 1, 89, 23, Size.NORMAL));
            addSlotToContainer(new SlotSized(inventory, 2, 71, 41, Size.NORMAL));
            addSlotToContainer(new SlotSized(inventory, 3, 89, 41, Size.NORMAL));
        }
    }
}
