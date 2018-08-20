/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.items.ceramics;

import java.util.EnumMap;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.FluidTankPropertiesWrapper;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import net.dries007.tfc.api.capability.ISmallVesselHandler;
import net.dries007.tfc.objects.MetalType;
import net.dries007.tfc.objects.fluids.FluidMetal;

public class ItemMold extends ItemFiredPottery
{
    private static final EnumMap<MetalType, ItemMold> MAP = new EnumMap<>(MetalType.class);

    public static ItemMold get(MetalType category)
    {
        return MAP.get(category);
    }

    public final MetalType type;

    public ItemMold(MetalType type)
    {
        this.type = type;
        if (MAP.put(type, this) != null) throw new IllegalStateException("There can only be one.");
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand)
    {

        ItemStack stack = player.getHeldItem(hand);
        if (!world.isRemote)
        {
            if (!player.isSneaking())
            {
            }
        }
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt)
    {
        return new FilledMoldCapability(nbt);
    }

    public static class FilledMoldCapability extends ItemStackHandler implements ICapabilityProvider, ISmallVesselHandler
    {
        private final FluidTank tank;
        private IFluidTankProperties fluidTankProperties[];

        public FilledMoldCapability(NBTTagCompound nbt)
        {
            super(2);
            tank = new FluidTank(100);
        }

        @Override
        public NBTTagCompound serializeNBT()
        {
            NBTTagCompound nbt = new NBTTagCompound();
            return tank.writeToNBT(nbt);
        }

        @Override
        public void deserializeNBT(NBTTagCompound nbt)
        {
            tank.readFromNBT(nbt);
        }

        @Override
        public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing)
        {
            return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY ||
                capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY;
        }

        @Nullable
        @Override
        public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing)
        {
            if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
                return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(this);
            if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
                return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(this);
            return null;
        }

        @Override
        public IFluidTankProperties[] getTankProperties()
        {
            if (fluidTankProperties == null)
            {
                fluidTankProperties = new IFluidTankProperties[] {new FluidTankPropertiesWrapper(tank)};
            }
            return fluidTankProperties;
        }

        @Override
        public int fill(FluidStack resource, boolean doFill)
        {
            if (resource.getFluid() instanceof FluidMetal)
                return tank.fill(resource, doFill);
            return 0;
        }

        @Nullable
        @Override
        public FluidStack drain(FluidStack resource, boolean doDrain)
        {
            if (resource.getFluid() instanceof FluidMetal)
                return tank.drain(resource, doDrain);
            return null;
        }

        @Nullable
        @Override
        public FluidStack drain(int maxDrain, boolean doDrain)
        {
            return tank.drain(maxDrain, doDrain);
        }
    }
}
