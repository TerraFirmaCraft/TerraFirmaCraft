/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.container;

import javax.annotation.Nullable;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import net.dries007.tfc.objects.inventory.slot.SlotOutput;
import net.dries007.tfc.objects.inventory.slot.SlotTEInput;
import net.dries007.tfc.objects.te.TEBarrel;
import net.dries007.tfc.util.IButtonHandler;

import static net.dries007.tfc.objects.blocks.wood.BlockBarrel.SEALED;
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

    @Nullable
    public IItemHandler getBarrelInventory()
    {
        return tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
    }

    public boolean isBarrelSealed()
    {
        return tile.isSealed();
    }

    @Override
    public void onButtonPress(int buttonID, @Nullable NBTTagCompound extraNBT)
    {
        // Slot will always be 0, extraNBT will be empty
        if (!tile.getWorld().isRemote)
        {
            IBlockState state = tile.getWorld().getBlockState(tile.getPos());
            tile.getWorld().setBlockState(tile.getPos(), state.withProperty(SEALED, !state.getValue(SEALED)));
            tile.onSealed();
        }
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
}
