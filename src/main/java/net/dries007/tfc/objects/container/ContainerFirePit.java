/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.container;

import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import net.dries007.tfc.objects.inventory.slot.SlotCallback;
import net.dries007.tfc.objects.te.TEFirePit;

import static net.dries007.tfc.objects.te.TEFirePit.*;

@ParametersAreNonnullByDefault
public class ContainerFirePit extends ContainerTE<TEFirePit>
{
    public ContainerFirePit(InventoryPlayer playerInv, TEFirePit te)
    {
        super(playerInv, te);
    }

    @Override
    protected void addContainerSlots()
    {
        IItemHandler inventory = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
        if (inventory != null)
        {
            // fuel slots
            for (int i = 0; i < 4; i++)
                addSlotToContainer(new SlotCallback(inventory, i, 8, 62 - 18 * i, tile));
            // input slot
            addSlotToContainer(new SlotCallback(inventory, SLOT_ITEM_INPUT, 80, 20, tile));
            // output slots
            addSlotToContainer(new SlotCallback(inventory, SLOT_OUTPUT_1, 71, 48, tile));
            addSlotToContainer(new SlotCallback(inventory, SLOT_OUTPUT_2, 89, 48, tile));
        }
    }

    @Override
    protected boolean transferStackIntoContainer(ItemStack stack, int containerSlots)
    {
        return !mergeItemStack(stack, SLOT_FUEL_INPUT, SLOT_ITEM_INPUT + 1, false);
    }
}
