/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.container;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import net.dries007.tfc.objects.inventory.slot.SlotTEInput;
import net.dries007.tfc.objects.te.TEAnvilTFC;

import static net.dries007.tfc.objects.te.TEAnvilTFC.*;

public class ContainerAnvilTFC extends ContainerTE<TEAnvilTFC>
{
    public ContainerAnvilTFC(InventoryPlayer playerInv, TEAnvilTFC te)
    {
        super(playerInv, te, true, 25);
    }

    public void onReceivePacket(int buttonID)
    {
        switch (buttonID)
        {
            // todo: send stuff to the tile entity when a button is pressed
            // case for a recipe button needs to
            default:
                // Step button pressed
                // do something here
        }
    }

    @Override
    protected void addContainerSlots()
    {
        IItemHandler inventory = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
        if (inventory != null)
        {
            addSlotToContainer(new SlotTEInput(inventory, SLOT_INPUT_1, 80, 50, tile));
            addSlotToContainer(new SlotTEInput(inventory, SLOT_INPUT_2, 62, 50, tile));
            addSlotToContainer(new SlotTEInput(inventory, SLOT_HAMMER, 16, 73, tile));
            addSlotToContainer(new SlotTEInput(inventory, SLOT_FLUX, 145, 73, tile));
        }
    }

}
