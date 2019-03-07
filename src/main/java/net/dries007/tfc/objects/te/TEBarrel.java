/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.te;

import javax.annotation.Nullable;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;

import net.dries007.tfc.objects.blocks.wood.BlockBarrel;
import net.dries007.tfc.objects.fluids.LockableFluidHandler;
import net.dries007.tfc.objects.inventory.LockableItemHandler;
import net.dries007.tfc.util.Helpers;

public class TEBarrel extends TESidedInventory implements ITickable
{
    public static final int SLOT_FLUID_CONTAINER_IN = 0;
    public static final int SLOT_FLUID_CONTAINER_OUT = 1;
    public static final int SLOT_ITEM = 2;
    @SuppressWarnings("WeakerAccess")
    public static final int TANK_CAPACITY = 10000;

    private FluidTank tank = new FluidTank(TANK_CAPACITY);
    private LockableFluidHandler fluidHandler;
    private LockableItemHandler itemHandler;

    public TEBarrel()
    {
        super(3);

        fluidHandler = new LockableFluidHandler(tank);
        itemHandler = new LockableItemHandler(inventory);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        super.readFromNBT(compound);

        tank.readFromNBT(compound.getCompoundTag("tank"));
    }

    /**
     * Called when this TileEntity was created by placing a sealed Barrel Item.
     * Loads its data from the Item's NBTTagCompound without loading xyz coordinates.
     *
     * @param compound The NBTTagCompound to load from.
     */
    public void readFromItemTag(NBTTagCompound compound)
    {
        tank.readFromNBT(compound.getCompoundTag("tank"));
        inventory.deserializeNBT(compound.getCompoundTag("inventory"));

        this.markDirty();
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound)
    {
        compound.setTag("tank", tank.writeToNBT(new NBTTagCompound()));

        return super.writeToNBT(compound);
    }

    /**
     * Called to get the NBTTagCompound that is put on Barrel Items.
     * This happens when a sealed Barrel was broken.
     *
     * @return An NBTTagCompound containing inventory and tank data.
     */
    public NBTTagCompound getItemTag()
    {
        NBTTagCompound compound = new NBTTagCompound();
        compound.setTag("tank", tank.writeToNBT(new NBTTagCompound()));
        compound.setTag("inventory", inventory.serializeNBT());

        return compound;
    }

    /**
     * Called once per side when the TileEntity has finished loading.
     * On servers, this is the earliest point in time to safely access the TE's World object.
     */
    @Override
    public void onLoad()
    {
        if (!world.isRemote)
        {
            updateLockStatus();
        }
    }

    /**
     * Retrieves the packet to send to clients whenever this TileEntity is updated via World.notifyBlockUpdate.
     * We are using this method to update the lock status on our ItemHandler and FluidHandler, since a Block update occurred.
     * This method is only called server-side.
     *
     * @return The Packet that will be sent to clients in range.
     */
    @Override
    @Nullable
    public SPacketUpdateTileEntity getUpdatePacket()
    {
        updateLockStatus();

        return super.getUpdatePacket();
    }

    /**
     * Called on clients whenever this TileEntity received an update from the server.
     *
     * @param tag An NBTTagCompound containing the TE's data.
     */
    @Override
    public void handleUpdateTag(NBTTagCompound tag)
    {
        readFromNBT(tag);

        updateLockStatus();
    }

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState)
    {
        return oldState.getBlock() != newState.getBlock();
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack)
    {
        //TODO: validate items that go in the item storage slot
        return slot == SLOT_ITEM || slot == SLOT_FLUID_CONTAINER_IN && FluidUtil.getFluidHandler(stack) != null;
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing)
    {
        return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing)
    {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
        {
            return (T) itemHandler;
        }

        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
        {
            return (T) fluidHandler;
        }

        return super.getCapability(capability, facing);
    }

    @Override
    public void update()
    {
        //TODO: tick only when needed
        //TODO: recipes

        if (world.isRemote)
        {
            return;
        }

        ItemStack fluidContainerIn = inventory.getStackInSlot(SLOT_FLUID_CONTAINER_IN);
        FluidActionResult result = Helpers.emptyContainerIntoTank(fluidContainerIn, tank, inventory, SLOT_FLUID_CONTAINER_OUT, TANK_CAPACITY);

        if (!result.isSuccess())
        {
            result = Helpers.fillContainerFromTank(fluidContainerIn, tank, inventory, SLOT_FLUID_CONTAINER_OUT, TANK_CAPACITY);
        }

        if (result.isSuccess())
        {
            inventory.setStackInSlot(SLOT_FLUID_CONTAINER_IN, result.getResult());

            IBlockState state = world.getBlockState(pos);
            world.notifyBlockUpdate(pos, state, state, 3);
        }
    }

    private void updateLockStatus()
    {
        boolean sealed = world.getBlockState(pos).getValue(BlockBarrel.SEALED);

        fluidHandler.setLockStatus(sealed);
        itemHandler.setLockStatus(sealed);
    }
}
