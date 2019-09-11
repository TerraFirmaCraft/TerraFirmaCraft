/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.container;

import javax.annotation.Nullable;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import net.dries007.tfc.objects.blocks.wood.BlockBarrel;
import net.dries007.tfc.objects.inventory.slot.SlotCallback;
import net.dries007.tfc.objects.te.TEBarrel;

import static net.dries007.tfc.objects.te.TEBarrel.*;

public class ContainerBarrel extends ContainerTE<TEBarrel> implements IButtonHandler
{
    public ContainerBarrel(InventoryPlayer playerInv, TEBarrel teBarrel)
    {
        super(playerInv, teBarrel);
    }

    @Nullable
    public IFluidHandler getBarrelTank()
    {
        return tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);
    }

    @Override
    public void onButtonPress(int buttonID, @Nullable NBTTagCompound extraNBT)
    {
        // Slot will always be 0, extraNBT will be empty
        if (!tile.getWorld().isRemote)
        {
            BlockBarrel.toggleBarrelSeal(tile.getWorld(), tile.getPos());
        }
    }

    @Override
    protected void addContainerSlots()
    {
        IItemHandler inventory = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);

        if (inventory != null)
        {
            addSlotToContainer(new SlotCallback(inventory, SLOT_FLUID_CONTAINER_IN, 35, 20, tile));
            addSlotToContainer(new SlotCallback(inventory, SLOT_FLUID_CONTAINER_OUT, 35, 54, tile));
            addSlotToContainer(new SlotCallback(inventory, SLOT_ITEM, 89, 37, tile));
        }
    }
}
