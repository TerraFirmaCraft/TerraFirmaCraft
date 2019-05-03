/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.te;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.*;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;

import net.dries007.tfc.objects.blocks.wood.BlockBarrel;
import net.dries007.tfc.objects.fluids.capability.FluidHandlerSided;
import net.dries007.tfc.objects.fluids.capability.IFluidHandlerSidedCallback;
import net.dries007.tfc.objects.inventory.capability.IItemHandlerSidedCallback;
import net.dries007.tfc.objects.inventory.capability.ItemHandlerSided;
import net.dries007.tfc.util.FluidTransferHelper;

@ParametersAreNonnullByDefault
public class TEBarrel extends TEInventory implements ITickable, IItemHandlerSidedCallback, IFluidHandlerSidedCallback
{
    public static final int SLOT_FLUID_CONTAINER_IN = 0;
    public static final int SLOT_FLUID_CONTAINER_OUT = 1;
    public static final int SLOT_ITEM = 2;
    @SuppressWarnings("WeakerAccess")
    public static final int TANK_CAPACITY = 10000;

    private FluidTank tank = new FluidTank(TANK_CAPACITY);
    private boolean sealed;
    private int tickCounter;

    public TEBarrel()
    {
        super(3);
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

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState)
    {
        return oldState.getBlock() != newState.getBlock();
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

    @Override
    public boolean isItemValid(int slot, ItemStack stack)
    {
        //TODO: validate items that go in the item storage slot
        return slot == SLOT_ITEM || slot == SLOT_FLUID_CONTAINER_IN && FluidUtil.getFluidHandler(stack) != null;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        super.readFromNBT(compound);

        tank.readFromNBT(compound.getCompoundTag("tank"));
    }

    @Override
    @Nonnull
    public NBTTagCompound writeToNBT(NBTTagCompound compound)
    {
        compound.setTag("tank", tank.writeToNBT(new NBTTagCompound()));

        return super.writeToNBT(compound);
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
            return (T) new ItemHandlerSided(this, inventory, facing);
        }

        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
        {
            return (T) new FluidHandlerSided(this, tank, facing);
        }

        return super.getCapability(capability, facing);
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, EnumFacing side)
    {
        return !sealed && (isItemValid(slot, stack) || side == null && slot == SLOT_FLUID_CONTAINER_OUT);
    }

    @Override
    public boolean canExtract(int slot, EnumFacing side)
    {
        return !sealed && (side == null || slot != SLOT_FLUID_CONTAINER_IN);
    }

    @Override
    public boolean canFill(FluidStack resource, EnumFacing side)
    {
        return !sealed;
    }

    @Override
    public boolean canDrain(EnumFacing side)
    {
        return !sealed;
    }

    @Override
    public void update()
    {
        //TODO: recipes

        if (world.isRemote)
        {
            return;
        }

        tickCounter++;

        if (tickCounter != 10)
        {
            return;
        }

        tickCounter = 0;

        ItemStack fluidContainerIn = inventory.getStackInSlot(SLOT_FLUID_CONTAINER_IN);
        FluidActionResult result = FluidTransferHelper.emptyContainerIntoTank(fluidContainerIn, tank, inventory, SLOT_FLUID_CONTAINER_OUT, TANK_CAPACITY, world, pos);

        if (!result.isSuccess())
        {
            result = FluidTransferHelper.fillContainerFromTank(fluidContainerIn, tank, inventory, SLOT_FLUID_CONTAINER_OUT, TANK_CAPACITY, world, pos);
        }

        if (result.isSuccess())
        {
            inventory.setStackInSlot(SLOT_FLUID_CONTAINER_IN, result.getResult());

            IBlockState state = world.getBlockState(pos);
            world.notifyBlockUpdate(pos, state, state, 3);
        }

        Fluid freshWater = FluidRegistry.getFluid("fresh_water");

        if (!sealed && world.isRainingAt(pos.up()) && (tank.getFluid() == null || tank.getFluid().getFluid() == freshWater))
        {
            tank.fill(new FluidStack(freshWater, 10), true);
            IBlockState state = world.getBlockState(pos);
            world.notifyBlockUpdate(pos, state, state, 3);
        }
    }

    public boolean isSealed()
    {
        return sealed;
    }

    private void updateLockStatus()
    {
        sealed = world.getBlockState(pos).getValue(BlockBarrel.SEALED);
    }
}
