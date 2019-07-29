/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.container;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import net.dries007.tfc.objects.inventory.slot.SlotCallback;
import net.dries007.tfc.objects.te.TEBlastFurnace;

import static net.dries007.tfc.objects.te.TEBlastFurnace.SLOT_TUYERE;

public class ContainerBlastFurnace extends ContainerTE<TEBlastFurnace>
{
    public ContainerBlastFurnace(InventoryPlayer playerInv, TEBlastFurnace tile)
    {
        super(playerInv, tile);
    }

    @Override
    protected void addContainerSlots()
    {
        IItemHandler inventory = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
        if (inventory != null)
        {
            addSlotToContainer(new SlotCallback(inventory, SLOT_TUYERE, 153, 7, tile));
        }
    }
}
