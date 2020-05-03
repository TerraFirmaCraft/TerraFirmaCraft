package net.dries007.tfc.objects.te;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;

import net.dries007.tfc.ConfigTFC;
import net.dries007.tfc.objects.fluids.capability.FluidHandlerSided;
import net.dries007.tfc.objects.fluids.capability.FluidTankCallback;
import net.dries007.tfc.objects.fluids.capability.IFluidHandlerSidedCallback;
import net.dries007.tfc.objects.fluids.capability.IFluidTankCallback;
import net.dries007.tfc.objects.items.itemblock.ItemBlockMetalLamp;

@ParametersAreNonnullByDefault
public class TELamp extends TETickCounter implements IFluidTankCallback, IFluidHandlerSidedCallback
{
    public static int CAPACITY;

    public int getFuel()
    {
        return tank.getFluidAmount();
    }

    private final FluidTank tank = new FluidTankCallback(this, 0, CAPACITY);
    private boolean powered = false;

    public TELamp()
    {
        CAPACITY = ConfigTFC.GENERAL.metalLampCapacity;
        this.tank.setTileEntity(this);
    }

    @Override
    public void setAndUpdateFluidTank(int fluidTankID)
    {
        IBlockState state = world.getBlockState(pos);
        world.notifyBlockUpdate(pos, state, state, 3);
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
        if (resource == null)
        {
            return false;
        }
        return ItemBlockMetalLamp.getValidFluids().contains(resource.getFluid());
    }

    @Override
    public boolean canDrain(EnumFacing side)
    {
        return true;
    }

    public ItemStack getItemStack(TELamp tel, IBlockState state)
    {
        ItemStack stack = new ItemStack(state.getBlock());
        IFluidHandlerItem itemCap = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
        IFluidHandler teCap = tel.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);
        if (itemCap != null && teCap != null)
        {
            itemCap.fill(teCap.drain(CAPACITY, false), true); //don't drain creative item
        }
        return stack;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);
        tank.readFromNBT(nbt.getCompoundTag("tank"));
        if (tank.getFluidAmount() > tank.getCapacity())
        {
            // Fix config changes
            FluidStack fluidStack = tank.getFluid();
            if (fluidStack != null)
            {
                fluidStack.amount = tank.getCapacity();
            }
            tank.setFluid(fluidStack);
        }
        powered = nbt.getBoolean("powered");
    }

    @Nonnull
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt)
    {
        nbt.setTag("tank", tank.writeToNBT(new NBTTagCompound()));
        nbt.setBoolean("powered", powered);
        return super.writeToNBT(nbt);
    }

    public boolean isPowered()
    {
        return powered;
    }

    public void setPowered(boolean pow)
    {
        powered = pow;
    }
}
