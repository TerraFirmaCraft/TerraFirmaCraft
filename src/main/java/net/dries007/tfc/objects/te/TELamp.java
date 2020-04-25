package net.dries007.tfc.objects.te;

import javax.annotation.Nullable;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;

import net.dries007.tfc.objects.fluids.capability.FluidHandlerSided;
import net.dries007.tfc.objects.fluids.capability.FluidTankCallback;
import net.dries007.tfc.objects.fluids.capability.IFluidHandlerSidedCallback;
import net.dries007.tfc.objects.fluids.capability.IFluidTankCallback;

public class TELamp extends TETickCounter implements IFluidTankCallback, IFluidHandlerSidedCallback
{
    public final static int CAPACITY = 250;

    private final FluidTank tank = new FluidTankCallback(this, 0, 250);

    @Override
    public void setAndUpdateFluidTank(int fluidTankID)
    {
        IBlockState state = world.getBlockState(pos);
        world.notifyBlockUpdate(pos, state, state, 3);
    }

    /**
     * Called when this TileEntity was created by placing an item
     * Loads its data from the Item's NBTTagCompound without loading xyz coordinates.
     *
     * @param nbt The NBTTagCompound to load from.
     */
    public void readFromItemTag(NBTTagCompound nbt)
    {
        tank.readFromNBT(nbt.getCompoundTag("tank"));
        if (tank.getFluidAmount() > tank.getCapacity())
        {
            // Fix config changes
            FluidStack fluidStack = tank.getFluid();
            //noinspection ConstantConditions
            fluidStack.amount = tank.getCapacity();
            tank.setFluid(fluidStack);
        }
        markDirty();
    }

    /**
     * Called to get the NBTTagCompound that is put on Lamps.
     *
     * Public access needed from getPickBlock
     *
     * @return An NBTTagCompound containing tank data.
     */
    public NBTTagCompound getItemTag()
    {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setTag("tank", tank.writeToNBT(new NBTTagCompound()));

        return nbt;
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing)
    {
        return capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing)
    {
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
        {
            return (T) new FluidHandlerSided(this, tank, facing);
        }
        return super.getCapability(capability, facing);
    }

    @Override
    public boolean canFill(FluidStack resource, EnumFacing side)
    {
        return true;
    }

    @Override
    public boolean canDrain(EnumFacing side)
    {
        return true;
    }
}
