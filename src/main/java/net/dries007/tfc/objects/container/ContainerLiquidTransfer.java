/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 *
 */

package net.dries007.tfc.objects.container;

import javax.annotation.Nonnull;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;

import net.dries007.tfc.api.capability.IMoldHandler;
import net.dries007.tfc.api.capability.heat.CapabilityItemHeat;
import net.dries007.tfc.api.capability.heat.IItemHeat;
import net.dries007.tfc.objects.inventory.SlotFluidTransfer;
import net.dries007.tfc.util.Helpers;

public class ContainerLiquidTransfer extends ContainerItemStack
{
    private IItemHandlerModifiable inventory;

    public ContainerLiquidTransfer(InventoryPlayer playerInv, ItemStack stack)
    {
        super(playerInv, stack);
        this.itemIndex += 1;
    }

    @Override
    public void detectAndSendChanges()
    {
        // This is where we transfer liquid metal into a mold
        IFluidHandler capFluidHandler = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);
        if (capFluidHandler instanceof IMoldHandler)
        {
            ItemStack outputStack = inventory.getStackInSlot(0);
            if (!outputStack.isEmpty())
            {
                IFluidHandler outFluidHandler = outputStack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);
                if (outFluidHandler instanceof IMoldHandler)
                {
                    FluidStack fStack = capFluidHandler.drain(1, false);
                    if (fStack != null && outFluidHandler.fill(fStack, false) == 1)
                    {
                        outFluidHandler.fill(capFluidHandler.drain(1, true), true);
                    }

                    // Copy the input temperature onto the output temperature
                    ((IMoldHandler) outFluidHandler).setTemperature(((IMoldHandler) capFluidHandler).getTemperature());

                    // Save the NBT on both stacks
                    stack.setTagCompound(((IMoldHandler) capFluidHandler).serializeNBT());
                    outputStack.setTagCompound(((IMoldHandler) outFluidHandler).serializeNBT());
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
    public boolean canInteractWith(@Nonnull EntityPlayer player)
    {
        IItemHeat heat = stack.getCapability(CapabilityItemHeat.ITEM_HEAT_CAPABILITY, null);
        return heat != null && heat.isMolten();
    }

    @Override
    protected void addContainerSlots()
    {
        this.inventory = new ItemStackHandler(1);
        addSlotToContainer(new SlotFluidTransfer(inventory, 0, 80, 34));
    }
}
