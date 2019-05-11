/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.container;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import mcp.MethodsReturnNonnullByDefault;
import net.dries007.tfc.objects.inventory.slot.SlotTEInput;
import net.dries007.tfc.objects.te.TELogPile;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ContainerLogPile extends ContainerTE<TELogPile>
{
    private static final int[] SLOT_SHIFT_ORDER = new int[] {0, 1, 2, 3};

    public ContainerLogPile(InventoryPlayer playerInv, TELogPile te)
    {
        super(playerInv, te);
    }

    @Override
    public boolean canInteractWith(@Nonnull EntityPlayer player)
    {
        if (this.tile.isBurning())
        {
            return false;
        }
        return this.tile.countLogs() > 0;
    }

    @Override
    protected void addContainerSlots()
    {
        IItemHandler inventory = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
        if (inventory != null)
        {
            addSlotToContainer(new SlotTEInput(inventory, 0, 71, 23, tile));
            addSlotToContainer(new SlotTEInput(inventory, 1, 89, 23, tile));
            addSlotToContainer(new SlotTEInput(inventory, 2, 71, 41, tile));
            addSlotToContainer(new SlotTEInput(inventory, 3, 89, 41, tile));
        }
    }

    @Override
    protected int[] getSlotShiftOrder(int containerSlots)
    {
        return SLOT_SHIFT_ORDER;
    }
}
