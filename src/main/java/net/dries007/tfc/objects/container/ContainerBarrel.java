/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.container;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import net.dries007.tfc.objects.inventory.SlotOutput;
import net.dries007.tfc.objects.inventory.SlotTEInput;
import net.dries007.tfc.objects.te.TEBarrel;

import static net.dries007.tfc.objects.te.TEBarrel.*;

public class ContainerBarrel extends ContainerTE<TEBarrel>
{
    public ContainerBarrel(InventoryPlayer playerInv, TEBarrel teBarrel)
    {
        super(playerInv, teBarrel);
    }

    @Override
    protected void addContainerSlots()
    {
        IItemHandler inventory = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);

        if (inventory != null)
        {
            this.addSlotToContainer(new SlotTEInput(inventory, SLOT_FLUID_CONTAINER_IN, 35, 20, tile));
            this.addSlotToContainer(new SlotOutput(inventory, SLOT_FLUID_CONTAINER_OUT, 35, 54));
            this.addSlotToContainer(new SlotTEInput(inventory, SLOT_ITEM, 89, 37, tile));
        }
    }

    public IFluidHandler getFluidTank()
    {
        return tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);
    }
}
