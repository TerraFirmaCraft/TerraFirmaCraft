/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 *
 */

package net.dries007.tfc.objects.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;

import net.dries007.tfc.objects.inventory.SlotFluidTransfer;
import net.dries007.tfc.util.Helpers;

public class ContainerSmallVesselLiquid extends ContainerItemStack
{
    private final IItemHandlerModifiable inventory;

    public ContainerSmallVesselLiquid(InventoryPlayer playerInv, ItemStack stack)
    {
        super(playerInv, stack);
        this.inventory = new ItemStackHandler(1);

        addContainerSlots();
        addPlayerInventorySlots(playerInv);
        this.itemIndex += 1;
    }

    @Override
    public void detectAndSendChanges()
    {
        // This is where we transfer liquid metal into a mold
        IFluidHandler capFluidHandler = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);
        if (capFluidHandler != null)
        {
            ItemStack outputStack = inventory.getStackInSlot(0);
            if (!outputStack.isEmpty())
            {
                IFluidHandler outFluidHandler = outputStack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);
                if (outFluidHandler != null)
                {
                    FluidStack fStack = capFluidHandler.drain(1, false);
                    if (fStack != null && outFluidHandler.fill(fStack, false) == 1)
                    {
                        outFluidHandler.fill(capFluidHandler.drain(1, true), true);
                    }

                    stack.setTagCompound(((INBTSerializable<NBTTagCompound>) capFluidHandler).serializeNBT());
                    outputStack.setTagCompound(((INBTSerializable<NBTTagCompound>) outFluidHandler).serializeNBT());
                }
            }
        }
        super.detectAndSendChanges();
    }

    @Override
    public void onContainerClosed(EntityPlayer player)
    {
        if (!player.getEntityWorld().isRemote)
        {
            ItemStack stack = inventory.getStackInSlot(0);
            if (!stack.isEmpty())
            {
                Helpers.spawnItemStack(player.getEntityWorld(), player.getPosition(), stack);
            }
        }
        super.onContainerClosed(player);
    }

    @Override
    protected void addContainerSlots()
    {
        addSlotToContainer(new SlotFluidTransfer(inventory, 0, 80, 34));
    }

}
