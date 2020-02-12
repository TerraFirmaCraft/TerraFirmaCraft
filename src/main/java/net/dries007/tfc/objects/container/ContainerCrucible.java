/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.container;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import net.dries007.tfc.objects.inventory.slot.SlotCallback;
import net.dries007.tfc.objects.te.TECrucible;

import static net.dries007.tfc.objects.te.TECrucible.*;

public class ContainerCrucible extends ContainerTE<TECrucible>
{
    public ContainerCrucible(InventoryPlayer playerInv, TECrucible tile)
    {
        super(playerInv, tile, 55);
    }

    @Override
    protected void addContainerSlots()
    {
        IItemHandler inventory = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
        if (inventory != null)
        {
            for (int i = SLOT_INPUT_START; i <= SLOT_INPUT_END; i++)
            {
                int line = i / 3;
                int column = i % 3;
                int x = 26 + column * 18;
                int y = 82 + line * 18;
                addSlotToContainer(new SlotCallback(inventory, i, x, y, tile));
            }

            addSlotToContainer(new SlotCallback(inventory, SLOT_OUTPUT, 152, 100, tile));
        }
    }
}
