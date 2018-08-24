/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.items.ceramics;

import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.FluidTankPropertiesWrapper;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import mcp.MethodsReturnNonnullByDefault;
import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.api.capability.ISmallVesselHandler;
import net.dries007.tfc.api.capability.heat.CapabilityItemHeat;
import net.dries007.tfc.api.capability.size.Size;
import net.dries007.tfc.api.capability.size.Weight;
import net.dries007.tfc.api.types.Metal;
import net.dries007.tfc.client.TFCGuiHandler;
import net.dries007.tfc.objects.fluids.FluidMetal;
import net.dries007.tfc.objects.fluids.FluidsTFC;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.IMetalObject;
import net.dries007.tfc.world.classic.CalenderTFC;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class ItemSmallVessel extends ItemFiredPottery
{
    public final boolean glazed;

    public ItemSmallVessel(boolean glazed)
    {
        this.glazed = glazed;
        setHasSubtypes(glazed);
    }

    @Override
    public String getTranslationKey(ItemStack stack)
    {
        if (!glazed)
            return super.getTranslationKey(stack);
        return super.getTranslationKey(stack) + "." + EnumDyeColor.byDyeDamage(stack.getItemDamage()).getName();
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand)
    {

        ItemStack stack = player.getHeldItem(hand);
        if (!world.isRemote && !player.isSneaking())
        {
            IFluidHandler cap = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);
            if (cap instanceof ISmallVesselHandler)
            {
                ISmallVesselHandler.Mode mode = ((ISmallVesselHandler) cap).getFluidMode();
                switch (mode)
                {
                    case INVENTORY:
                        player.openGui(TerraFirmaCraft.getInstance(), TFCGuiHandler.SMALL_VESSEL, world, 0, 0, 0);
                        break;
                    case LIQUID_MOLTEN:
                        player.openGui(TerraFirmaCraft.getInstance(), TFCGuiHandler.SMALL_VESSEL_LIQUID, world, 0, 0, 0);
                        break;
                    case LIQUID_SOLID:
                        break;
                }
            }
        }
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items)
    {
        if (!isInCreativeTab(tab)) return;

        if (!glazed)
            items.add(new ItemStack(this));
        else
            for (EnumDyeColor color : EnumDyeColor.values())
                items.add(new ItemStack(this, 1, color.getDyeDamage()));
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt)
    {
        return new SmallVesselCapability(stack, nbt);
    }

    @Override
    public boolean canStack(ItemStack stack)
    {
        return false;
    }

    @Override
    public Size getSize(ItemStack stack)
    {
        return Size.LARGE;
    }

    @Override
    public Weight getWeight(ItemStack stack)
    {
        return Weight.HEAVY;
    }

    @Override
    public ItemStack getFiringResult(ItemStack input, Metal.Tier tier)
    {
        NBTTagCompound nbt = input.getTagCompound();
        // Case 1: The input is a filled vessel
        IItemHandler capItemHandler = input.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
        if (capItemHandler instanceof ISmallVesselHandler && capItemHandler != null)
        {
            ISmallVesselHandler cap = (ISmallVesselHandler) capItemHandler;

            // Check if it can transform into liquid metal
            // todo: improve, add alloy recipes
            Metal metal = null;
            int amount = 0;
            boolean canMelt = true;
            for (int i = 0; i < cap.getSlots(); i++)
            {
                ItemStack stack = cap.getStackInSlot(i);
                if (stack.isEmpty())
                    continue;
                if (stack.getItem() instanceof IMetalObject)
                {
                    IMetalObject metalObj = (IMetalObject) stack.getItem();
                    if (metal == null)
                    {
                        metal = metalObj.getMetal(stack);
                        amount += metalObj.getSmeltAmount(stack) * stack.getCount();
                    }
                    else if (metal == metalObj.getMetal(stack))
                    {
                        amount += metalObj.getSmeltAmount(stack) * stack.getCount();
                    }
                    else
                    {
                        canMelt = false;
                        break;
                    }
                }
                else
                {
                    canMelt = false;
                    break;
                }
            }
            if (canMelt)
            {
                // Fill with the liquid metal
                cap.setFluidMode(true);
                cap.fill(new FluidStack(FluidsTFC.getMetalFluid(metal), amount), true);
                nbt = cap.serializeNBT();
            }

        }
        input.setTagCompound(nbt);
        return input;
    }

    public class SmallVesselCapability extends ItemStackHandler implements ICapabilityProvider, ISmallVesselHandler
    {
        private final ItemStack container;
        private final FluidTank tank;

        private float heatCapacity;
        private float meltingTemp;
        private float temperature;
        private long ticksSinceUpdate;

        private boolean fluidMode; // Does the stack contain molten metal?
        private IFluidTankProperties fluidTankProperties[];

        public SmallVesselCapability(ItemStack stack, @Nullable NBTTagCompound nbt)
        {
            super(4);
            this.container = stack;
            this.heatCapacity = heatCapacity;
            this.meltingTemp = meltingTemp;

            tank = new FluidTank(4000);
            fluidMode = false;
            if (nbt != null)
            {
                deserializeNBT(nbt);
            }
        }

        @Override
        public void setFluidMode(boolean fluidMode)
        {
            this.fluidMode = fluidMode;
        }

        @Override
        public Mode getFluidMode()
        {
            if (fluidMode)
            {
                return temperature < meltingTemp ? Mode.LIQUID_SOLID : Mode.LIQUID_MOLTEN;
            }
            return Mode.INVENTORY;
        }

        @Nullable
        @Override
        public Metal getMetal()
        {
            return fluidMode ? ((FluidMetal) tank.getFluid().getFluid()).getMetal() : null;
        }

        @Override
        public int getAmount()
        {
            return fluidMode ? tank.getFluidAmount() : 0;
        }

        @Override
        public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing)
        {
            return capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY
                || capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY
                || capability == CapabilityItemHeat.ITEM_HEAT_CAPABILITY;
        }

        @Nullable
        @Override
        public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing)
        {
            if (hasCapability(capability, facing))
            {
                updateTemperature(0, CalenderTFC.getTotalTime() - ticksSinceUpdate);
                return (T) this;
            }
            return null;
        }

        @Override
        public NBTTagCompound serializeNBT()
        {
            NBTTagCompound nbt = new NBTTagCompound();
            fluidMode = tank.getFluidAmount() > 0;
            nbt.setBoolean("liquid", fluidMode);

            nbt.setFloat("item_heat", temperature);
            nbt.setLong("ticks", CalenderTFC.getTotalTime());

            if (fluidMode)
            {
                // Save fluid data
                NBTTagCompound fluidData = new NBTTagCompound();
                tank.writeToNBT(fluidData);
                nbt.setTag("fluids", fluidData);
                updateFluidData(tank.getFluid().getFluid());
            }
            else
            {
                // Save item data
                nbt.setTag("items", super.serializeNBT());
            }
            return nbt;
        }

        @Override
        public void deserializeNBT(NBTTagCompound nbt)
        {
            temperature = nbt.getFloat("item_heat");
            ticksSinceUpdate = nbt.getLong("ticks");

            if (nbt.hasKey("liquid"))
            {
                fluidMode = nbt.getBoolean("liquid");
            }
            if (fluidMode && nbt.hasKey("fluids", Constants.NBT.TAG_COMPOUND))
            {
                // Read fluid contents
                tank.readFromNBT(nbt.getCompoundTag("fluids"));
            }
            else if (!fluidMode && nbt.hasKey("items", Constants.NBT.TAG_COMPOUND))
            {
                // Read item contents
                super.deserializeNBT(nbt.getCompoundTag("items"));
            }

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
            {
                updateFluidData(resource.getFluid());
                return tank.fill(resource, doFill);
            }
            return 0;
        }

        @Nullable
        @Override
        public FluidStack drain(FluidStack resource, boolean doDrain)
        {
            if (getFluidMode() == Mode.LIQUID_MOLTEN)
                return tank.drain(resource, doDrain);
            return null;
        }

        @Nullable
        @Override
        public FluidStack drain(int maxDrain, boolean doDrain)
        {
            if (getFluidMode() == Mode.LIQUID_MOLTEN)
                return tank.drain(maxDrain, doDrain);
            return null;
        }

        @Override
        public float getTemperature()
        {
            return temperature;
        }

        @Override
        public void setTemperature(float temperature)
        {
            this.temperature = temperature;
        }

        @Override
        public void updateTemperature(float enviromentTemperature, long ticks)
        {
            this.temperature = CapabilityItemHeat.getTempChange(temperature, heatCapacity, enviromentTemperature, ticks);
        }

        @SideOnly(Side.CLIENT)
        @Override
        public void addHeatInfo(ItemStack stack, List<String> text)
        {
            Metal metal = getMetal();
            if (metal != null)
                text.add(TextFormatting.DARK_GREEN + I18n.format(Helpers.getTypeName(metal)) + ": " + I18n.format("tfc.tooltip.units", getAmount()));
            ISmallVesselHandler.super.addHeatInfo(stack, text);
        }

        private void updateFluidData(Fluid fluid)
        {
            if (fluid instanceof FluidMetal)
            {
                Metal metal = ((FluidMetal) fluid).getMetal();
                this.meltingTemp = metal.meltTemp;
                this.heatCapacity = metal.specificHeat;
            }
            else
            {
                this.meltingTemp = 1500;
                this.heatCapacity = 1;
            }

        }
    }

}
