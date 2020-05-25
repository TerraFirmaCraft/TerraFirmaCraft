/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.container;

import javax.annotation.Nullable;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import net.dries007.tfc.objects.blocks.BlockPowderKeg;
import net.dries007.tfc.objects.inventory.slot.SlotCallback;
import net.dries007.tfc.objects.te.TEPowderKeg;

public class ContainerPowderKeg extends ContainerTE<TEPowderKeg> implements IButtonHandler
{
    public ContainerPowderKeg(InventoryPlayer playerInv, TEPowderKeg tile)
    {
        super(playerInv, tile);
    }

    @Override
    public void onButtonPress(int buttonID, @Nullable NBTTagCompound extraNBT)
    {
        // Slot will always be 0, extraNBT will be empty
        if (!tile.getWorld().isRemote)
        {
            BlockPowderKeg.togglePowderKegSeal(tile.getWorld(), tile.getPos());
        }
    }

    @Override
    protected void addContainerSlots()
    {
        IItemHandler inventory = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);

        if (inventory != null)
        {
            for (int y = 0; y < 3; y++)
            {
                for (int x = 0; x < 4; x++)
                {
                    addSlotToContainer(new SlotCallback(inventory, x * 3 + y, 25 + x * 18, 19 + y * 18, tile));
                }
            }
        }
    }
}
