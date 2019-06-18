/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.te;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.block.state.IBlockState;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import mcp.MethodsReturnNonnullByDefault;
import net.dries007.tfc.objects.inventory.capability.ItemStackHandlerTE;

/**
 * This is a helper class for TE's with a simple inventory that will respect automation
 * To provide side based automation, you must expose a IItemHandler wrapper based on the input side
 * Without overriding the getCapability methods, this will not accept items from external automation
 */
@ParametersAreNonnullByDefault
public abstract class TEInventory extends TEBase
{
    protected final ItemStackHandler inventory;

    TEInventory(int inventorySize)
    {
        super();
        inventory = new ItemStackHandlerTE(this, inventorySize);
        this.markDirty();
    }

    public void setAndUpdateSlots(int slot)
    {
        this.markDirty();
    }

    public int getSlotLimit(int slot)
    {
        return 64;
    }

    public boolean isItemValid(int slot, ItemStack stack)
    {
        return true;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        inventory.deserializeNBT(nbt.getCompoundTag("inventory"));
        super.readFromNBT(nbt);
    }

    @Override
    @Nonnull
    public NBTTagCompound writeToNBT(NBTTagCompound nbt)
    {
        nbt.setTag("inventory", inventory.serializeNBT());
        return super.writeToNBT(nbt);
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing)
    {
        return (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && facing == null) || super.hasCapability(capability, facing);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing)
    {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && facing == null)
        {
            return (T) inventory;
        }
        return super.getCapability(capability, facing);
    }

    public void onBreakBlock(World world, BlockPos pos)
    {
        for (int i = 0; i < inventory.getSlots(); i++)
        {
            InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), inventory.getStackInSlot(i));
        }
    }
}
