package net.dries007.tfc.objects.container;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import net.dries007.tfc.objects.inventory.slot.SlotTEInput;
import net.dries007.tfc.objects.te.TECrucible;

import static net.dries007.tfc.objects.te.TECrucible.SLOT_INPUT;
import static net.dries007.tfc.objects.te.TECrucible.SLOT_OUTPUT;

public class ContainerCrucible extends ContainerTE<TECrucible>
{
    public ContainerCrucible(InventoryPlayer playerInv, TECrucible tile)
    {
        super(playerInv, tile, true);
    }

    @Override
    protected void addContainerSlots()
    {
        IItemHandler inventory = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
        if (inventory != null)
        {
            addSlotToContainer(new SlotTEInput(inventory, SLOT_INPUT, 144, 12, tile));
            addSlotToContainer(new SlotTEInput(inventory, SLOT_OUTPUT, 144, 56, tile));
        }
    }
}
