/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.items.ceramics;

import java.util.EnumMap;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.FluidTankPropertiesWrapper;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.dries007.tfc.api.capability.IMoldHandler;
import net.dries007.tfc.api.capability.heat.CapabilityItemHeat;
import net.dries007.tfc.api.capability.heat.IItemHeat;
import net.dries007.tfc.api.capability.heat.ItemHeatHandler;
import net.dries007.tfc.api.types.Metal;
import net.dries007.tfc.client.TFCGuiHandler;
import net.dries007.tfc.objects.fluids.FluidMetal;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.world.classic.CalenderTFC;

public class ItemMold extends ItemFiredPottery
{
    private static final EnumMap<Metal.ItemType, ItemMold> MAP = new EnumMap<>(Metal.ItemType.class);

    public static ItemMold get(Metal.ItemType category)
    {
        return MAP.get(category);
    }

    public final Metal.ItemType type;

    public ItemMold(Metal.ItemType type)
    {
        this.type = type;
        if (MAP.put(type, this) != null) throw new IllegalStateException("There can only be one.");
    }

    @Override
    @Nonnull
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, @Nonnull EnumHand hand)
    {

        ItemStack stack = player.getHeldItem(hand);
        if (!world.isRemote && !player.isSneaking())
        {
            IItemHeat cap = stack.getCapability(CapabilityItemHeat.ITEM_HEAT_CAPABILITY, null);
            if (cap != null && cap.isMolten())
            {
                TFCGuiHandler.openGui(world, player, TFCGuiHandler.Type.MOLD);
            }
        }
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }

    @Override
    @Nonnull
    public String getTranslationKey(ItemStack stack)
    {
        IFluidHandler capFluidHandler = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);
        if (capFluidHandler instanceof IMoldHandler)
        {
            Metal metal = ((IMoldHandler) capFluidHandler).getMetal();
            if (metal != null)
            {
                return super.getTranslationKey(stack) + "." + metal.getRegistryName().getPath();
            }
        }
        return super.getTranslationKey(stack);
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt)
    {
        return new FilledMoldCapability(nbt);
    }

    @Override
    @Nonnull
    public ItemStack getContainerItem(@Nonnull ItemStack itemStack)
    {
        if (type.getMoldReturnRate() >= 1)
            return new ItemStack(itemStack.getItem(), itemStack.getCount(), itemStack.getMetadata());
        return ItemStack.EMPTY;
    }

    @Override
    public boolean hasContainerItem(ItemStack stack)
    {
        return type.getMoldReturnRate() >= 1;
    }

    // Extends ItemHeatHandler for ease of use
    private class FilledMoldCapability extends ItemHeatHandler implements ICapabilityProvider, IMoldHandler
    {
        private final FluidTank tank;
        private IFluidTankProperties fluidTankProperties[];

        public FilledMoldCapability(@Nullable NBTTagCompound nbt)
        {
            super();
            tank = new FluidTank(100);

            if (nbt != null)
                deserializeNBT(nbt);

            updateFluidData(tank.getFluid());
        }

        @Nullable
        @Override
        public Metal getMetal()
        {
            return tank.getFluid() != null ? ((FluidMetal) tank.getFluid().getFluid()).getMetal() : null;
        }

        @Override
        public int getAmount()
        {
            return tank.getFluidAmount();
        }

        @Nullable
        @Override
        @SuppressWarnings("unchecked")
        public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing)
        {
            return hasCapability(capability, facing) ? (T) this : null;
        }

        @Override
        public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing)
        {
            return capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY
                || capability == CapabilityItemHeat.ITEM_HEAT_CAPABILITY;
        }

        @Override
        public NBTTagCompound serializeNBT()
        {
            NBTTagCompound nbt = new NBTTagCompound();
            nbt.setFloat("heat", getTemperature());
            nbt.setLong("ticks", CalenderTFC.getTotalTime());
            return tank.writeToNBT(nbt);
        }

        @Override
        public void deserializeNBT(NBTTagCompound nbt)
        {
            temperature = nbt.getFloat("heat");
            lastUpdateTick = nbt.getLong("ticks");
            tank.readFromNBT(nbt);
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
            if (resource.getFluid() instanceof FluidMetal && type.hasMold(((FluidMetal) resource.getFluid()).getMetal()))
            {
                updateFluidData(resource);
                return tank.fill(resource, doFill);
            }
            return 0;
        }

        @Nullable
        @Override
        public FluidStack drain(FluidStack resource, boolean doDrain)
        {
            return getTemperature() >= meltingPoint ? tank.drain(resource, doDrain) : null;
        }

        @Nullable
        @Override
        public FluidStack drain(int maxDrain, boolean doDrain)
        {
            return getTemperature() >= meltingPoint ? tank.drain(maxDrain, doDrain) : null;
        }

        @SideOnly(Side.CLIENT)
        @Override
        public void addHeatInfo(ItemStack stack, List<String> text, boolean clearStackNBT)
        {
            Metal metal = getMetal();
            if (metal != null)
            {
                String desc = TextFormatting.DARK_GREEN + I18n.format(Helpers.getTypeName(metal)) + ": " + I18n.format("tfc.tooltip.units", getAmount());
                if (isMolten())
                    desc += " - " + I18n.format("tfc.tooltip.liquid");
                text.add(desc);
            }
            IMoldHandler.super.addHeatInfo(stack, text, false); // Never clear the NBT based on heat alone
        }

        @Override
        public float getHeatCapacity()
        {
            return heatCapacity;
        }

        @Override
        public float getMeltingPoint()
        {
            return meltingPoint;
        }

        private void updateFluidData(FluidStack fluid)
        {
            if (fluid != null && fluid.getFluid() instanceof FluidMetal)
            {
                Metal metal = ((FluidMetal) fluid.getFluid()).getMetal();
                this.meltingPoint = metal.getMeltTemp();
                this.heatCapacity = metal.getSpecificHeat();
            }
            else
            {
                this.meltingPoint = 1000;
                this.heatCapacity = 1;
            }
        }
    }
}
