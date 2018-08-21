/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.items.ceramics;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.FluidTankPropertiesWrapper;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import mcp.MethodsReturnNonnullByDefault;
import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.api.capability.ISmallVesselHandler;
import net.dries007.tfc.api.capability.size.Size;
import net.dries007.tfc.api.capability.size.Weight;
import net.dries007.tfc.api.types.Metal;
import net.dries007.tfc.client.TFCGuiHandler;
import net.dries007.tfc.objects.fluids.FluidMetal;
import net.dries007.tfc.objects.fluids.FluidsTFC;
import net.dries007.tfc.util.IMetalObject;

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
        if (!world.isRemote)
        {
            int flag = 0;
            NBTTagCompound nbt = stack.getTagCompound();
            if (nbt != null && nbt.hasKey("liquid"))
            {
                if (nbt.getBoolean("liquid"))
                    flag = 1;
            }

            if (!player.isSneaking())
            {
                switch (flag)
                {
                    case 0:
                        player.openGui(TerraFirmaCraft.getInstance(), TFCGuiHandler.SMALL_VESSEL, world, 0, 0, 0);
                        break;
                    case 1:
                        player.openGui(TerraFirmaCraft.getInstance(), TFCGuiHandler.SMALL_VESSEL_LIQUID, world, 0, 0, 0);
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
        return new SmallVesselCapability(stack, nbt, 1, Float.MAX_VALUE);
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

    public static class SmallVesselCapability extends ItemStackHandler implements ICapabilityProvider, ISmallVesselHandler
    {
        private final ItemStack container;
        private final FluidTank tank;
        private final float heatCapacity;
        private final float meltingTemp;

        private boolean fluidMode; // Does the stack contain molten metal?
        private IFluidTankProperties fluidTankProperties[];
        private float temperature;

        public SmallVesselCapability(ItemStack stack, @Nullable NBTTagCompound nbt, float heatCapacity, float meltingTemp)
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
        public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing)
        {
            return capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY || capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY;
        }

        @Nullable
        @Override
        public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing)
        {
            if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
                return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(this);
            if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
                return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(this);
            return null;
        }

        @Override
        public NBTTagCompound serializeNBT()
        {
            NBTTagCompound nbt = new NBTTagCompound();
            fluidMode = tank.getFluidAmount() > 0;
            nbt.setBoolean("liquid", fluidMode);
            nbt.setFloat("item_heat", temperature);
            if (fluidMode)
            {
                // Save fluid data
                NBTTagCompound fluidData = new NBTTagCompound();
                tank.writeToNBT(fluidData);
                nbt.setTag("fluids", fluidData);
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
            if (fluidMode && resource.getFluid() instanceof FluidMetal)
                return tank.fill(resource, doFill);
            return 0;
        }

        @Nullable
        @Override
        public FluidStack drain(FluidStack resource, boolean doDrain)
        {
            if (fluidMode && resource.getFluid() instanceof FluidMetal)
                return tank.drain(resource, doDrain);
            return null;
        }

        @Nullable
        @Override
        public FluidStack drain(int maxDrain, boolean doDrain)
        {
            if (fluidMode)
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
        public float getMeltingPoint()
        {
            return meltingTemp;
        }

        @Override
        public float getHeatCapacity()
        {
            return heatCapacity;
        }

        @Override
        public void addTemperature(float temperature)
        {
            this.temperature += temperature;
        }
    }

}
