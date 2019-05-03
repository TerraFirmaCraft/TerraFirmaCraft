/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.container;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import net.dries007.tfc.objects.inventory.slot.SlotTEInput;
import net.dries007.tfc.objects.te.TEFirePit;

import static net.dries007.tfc.objects.te.TEFirePit.*;

public class ContainerFirePit extends ContainerTE<TEFirePit>
{
    private static final int[] SLOT_SHIFT_ORDER = {SLOT_FUEL_INPUT, SLOT_ITEM_INPUT};

    public ContainerFirePit(InventoryPlayer playerInv, TEFirePit te)
    {
        super(playerInv, te, true);
    }

    @Override
    protected void addContainerSlots()
    {
        IItemHandler inventory = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
        if (inventory != null)
        {
            // fuel slots
            for (int i = 0; i < 4; i++)
                addSlotToContainer(new SlotTEInput(inventory, i, 8, 62 - 18 * i, tile));
            // input slot
            addSlotToContainer(new SlotTEInput(inventory, SLOT_ITEM_INPUT, 80, 20, tile));
            // output slots
            addSlotToContainer(new SlotTEInput(inventory, SLOT_OUTPUT_1, 71, 48, tile));
            addSlotToContainer(new SlotTEInput(inventory, SLOT_OUTPUT_2, 89, 48, tile));
        }
    }

    @Override
    protected int[] getSlotShiftOrder(int containerSlots)
    {
        return SLOT_SHIFT_ORDER;
    }
}
