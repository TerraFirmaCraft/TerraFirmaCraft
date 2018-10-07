/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.container;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import net.dries007.tfc.objects.inventory.SlotOutput;

public class ContainerKnapping extends ContainerItemStack
{
    public ContainerKnapping(InventoryPlayer playerInv, ItemStack stack)
    {
        super(playerInv, stack);
        this.itemIndex += 1;
    }

    @Override
    protected void addContainerSlots()
    {
        // todo: this needs to not come from an item capability; the item in question doesn't have that capability
        IItemHandler inventory = stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
        if (inventory != null)
        {
            addSlotToContainer(new SlotOutput(inventory, 0, 128, 44));
        }
    }

    @Override
    protected void addPlayerInventorySlots(InventoryPlayer playerInv)
    {
        // Add Player Inventory Slots (lower down)
        for (int i = 0; i < 3; i++)
        {
            for (int j = 0; j < 9; j++)
            {
                addSlotToContainer(new Slot(playerInv, j + i * 9 + 9, 8 + j * 18, 84 + i * 18 + 18));
            }
        }

        for (int k = 0; k < 9; k++)
        {
            addSlotToContainer(new Slot(playerInv, k, 8 + k * 18, 142 + 18));
        }
    }
}
