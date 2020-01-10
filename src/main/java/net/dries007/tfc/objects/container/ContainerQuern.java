/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.container;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import net.dries007.tfc.objects.inventory.slot.SlotCallback;
import net.dries007.tfc.objects.te.TEQuern;

import static net.dries007.tfc.objects.te.TEQuern.*;

public class ContainerQuern extends ContainerTE<TEQuern>
{
    public ContainerQuern(InventoryPlayer playerInv, TEQuern te)
    {
        super(playerInv, te);
    }

    @Override
    protected void addContainerSlots()
    {
        IItemHandler inventory = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
        if (inventory != null)
        {
            addSlotToContainer(new SlotCallback(inventory, SLOT_HANDSTONE, 93, 20, tile));
            addSlotToContainer(new SlotCallback(inventory, SLOT_INPUT, 66, 47, tile));
            addSlotToContainer(new SlotCallback(inventory, SLOT_OUTPUT, 93, 47, tile));
        }
    }
}
