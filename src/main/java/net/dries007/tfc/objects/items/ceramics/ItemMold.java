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
import net.dries007.tfc.util.calendar.CalendarTFC;

import static net.minecraftforge.fluids.capability.CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY;

public class ItemMold extends ItemPottery
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
                //noinspection ConstantConditions
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
    public int getItemStackLimit(ItemStack stack)
    {
        IMoldHandler moldHandler = (IMoldHandler) stack.getCapability(FLUID_HANDLER_CAPABILITY, null);
        if (moldHandler != null && moldHandler.getMetal() != null)
        {
            return 1;
        }
        return super.getItemStackLimit(stack);
    }

    // Extends ItemHeatHandler for ease of use
    private class FilledMoldCapability extends ItemHeatHandler implements ICapabilityProvider, IMoldHandler
    {
        private final FluidTank tank;
        private IFluidTankProperties[] fluidTankProperties;

        FilledMoldCapability(@Nullable NBTTagCompound nbt)
        {
            tank = new FluidTank(100);

            if (nbt != null)
                deserializeNBT(nbt);
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
                int fillAmount = tank.fill(resource, doFill);
                if (fillAmount == tank.getFluidAmount())
                {
                    updateFluidData();
                }
                return fillAmount;
            }
            return 0;
        }

        @Nullable
        @Override
        public FluidStack drain(FluidStack resource, boolean doDrain)
        {
            return getTemperature() >= meltTemp ? tank.drain(resource, doDrain) : null;
        }

        @Nullable
        @Override
        public FluidStack drain(int maxDrain, boolean doDrain)
        {
            if (getTemperature() > meltTemp)
            {
                FluidStack stack = tank.drain(maxDrain, doDrain);
                if (tank.getFluidAmount() == 0)
                {
                    updateFluidData();
                }
                return stack;
            }
            return null;
        }

        @SideOnly(Side.CLIENT)
        @Override
        public void addHeatInfo(@Nonnull ItemStack stack, @Nonnull List<String> text)
        {
            Metal metal = getMetal();
            if (metal != null)
            {
                String desc = TextFormatting.DARK_GREEN + I18n.format(Helpers.getTypeName(metal)) + ": " + I18n.format("tfc.tooltip.units", getAmount());
                if (isMolten())
                {
                    desc += " - " + I18n.format("tfc.tooltip.liquid");
                }
                text.add(desc);
            }
            IMoldHandler.super.addHeatInfo(stack, text);
        }

        @Override
        public float getHeatCapacity()
        {
            return heatCapacity;
        }

        @Override
        public float getMeltTemp()
        {
            return meltTemp;
        }

        @Override
        public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing)
        {
            return capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY
                || capability == CapabilityItemHeat.ITEM_HEAT_CAPABILITY;
        }

        @Nullable
        @Override
        @SuppressWarnings("unchecked")
        public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing)
        {
            return hasCapability(capability, facing) ? (T) this : null;
        }

        @Override
        @Nonnull
        public NBTTagCompound serializeNBT()
        {
            NBTTagCompound nbt = new NBTTagCompound();
            float temp = getTemperature();
            nbt.setFloat("heat", temp);
            if (temp <= 0)
            {
                nbt.setLong("ticks", -1);
            }
            else
            {
                nbt.setLong("ticks", CalendarTFC.TOTAL_TIME.getTicks());
            }
            return tank.writeToNBT(nbt);
        }

        @Override
        public void deserializeNBT(NBTTagCompound nbt)
        {
            if (nbt != null)
            {
                temperature = nbt.getFloat("heat");
                lastUpdateTick = nbt.getLong("ticks");
                tank.readFromNBT(nbt);
            }
            updateFluidData();
        }

        private void updateFluidData()
        {
            updateFluidData(tank.getFluid());
        }

        private void updateFluidData(FluidStack fluid)
        {
            if (fluid != null && fluid.getFluid() instanceof FluidMetal)
            {
                Metal metal = ((FluidMetal) fluid.getFluid()).getMetal();
                this.meltTemp = metal.getMeltTemp();
                this.heatCapacity = metal.getSpecificHeat();
            }
            else
            {
                this.meltTemp = CapabilityItemHeat.MAX_TEMPERATURE;
                this.heatCapacity = 1;
            }
        }
    }
}
