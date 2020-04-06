/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.container;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

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
}
