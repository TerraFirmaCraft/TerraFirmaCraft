/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.te;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import net.dries007.tfc.objects.inventory.capability.ISlotCallback;
import net.dries007.tfc.objects.inventory.capability.ItemStackHandlerCallback;

/**
 * This is a helper class for TE's with a simple inventory that will respect automation
 * To provide side based automation, you must expose a IItemHandler wrapper based on the input side
 * Without overriding the getCapability methods, this will not accept items from external automation
 */
@ParametersAreNonnullByDefault
public abstract class TEInventory extends TEBase implements ISlotCallback
{
    protected final ItemStackHandler inventory;

    protected TEInventory(int inventorySize)
    {
        inventory = new ItemStackHandlerCallback(this, inventorySize);
    }

    protected TEInventory(ItemStackHandler inventory)
    {
        this.inventory = inventory;
    }

    @Override
    public void setAndUpdateSlots(int slot)
    {
        this.markDirty();
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

    public void onBreakBlock(World world, BlockPos pos, IBlockState state)
    {
        for (int i = 0; i < inventory.getSlots(); i++)
        {
            InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), inventory.getStackInSlot(i));
        }
    }

    /**
     * Delegated from {@link net.minecraft.inventory.Container#canInteractWith(EntityPlayer)}
     */
    public boolean canInteractWith(EntityPlayer player)
    {
        return this.world.getTileEntity(pos) == this && player.getDistanceSq(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D) <= 64.0D;
    }
}
