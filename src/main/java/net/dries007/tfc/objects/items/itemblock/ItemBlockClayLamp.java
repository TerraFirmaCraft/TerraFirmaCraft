/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.items.itemblock;

import java.util.HashSet;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;

import net.dries007.tfc.ConfigTFC;
import net.dries007.tfc.api.capability.size.Size;
import net.dries007.tfc.api.capability.size.Weight;
import net.dries007.tfc.objects.fluids.FluidsTFC;
import net.dries007.tfc.objects.fluids.capability.FluidWhitelistHandler;
import net.dries007.tfc.objects.items.ItemTFC;

/**
 * todo: this
 */
public class ItemBlockClayLamp extends ItemTFC
{
    public final static int CAPACITY = 25;

    @Override
    @Nonnull
    public String getItemStackDisplayName(@Nonnull ItemStack stack)
    {
        IFluidHandler fluidCap = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
        if (fluidCap != null)
        {
            FluidStack fluidStack = fluidCap.drain(CAPACITY, false);
            if (fluidStack != null)
            {
                String fluidName = fluidStack.getLocalizedName();
                return new TextComponentTranslation(getTranslationKey() + ".filled.name", fluidName).getFormattedText();
            }
        }
        return super.getItemStackDisplayName(stack);
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items)
    {
        if (isInCreativeTab(tab))
        {
            items.add(new ItemStack(this));
            for (Fluid fluid : getValidFluids())
            {
                ItemStack stack = new ItemStack(this);
                IFluidHandlerItem cap = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
                if (cap != null)
                {
                    cap.fill(new FluidStack(fluid, CAPACITY), true);
                }
                items.add(stack);
            }
        }
    }

    public Set<Fluid> getValidFluids()
    {
        String[] fluidNames = {};//= ConfigTFC.GENERAL.clayLampFuels;
        Set<Fluid> validFluids = new HashSet<>();
        for (String fluidName : fluidNames)
        {
            validFluids.add(FluidRegistry.getFluid(fluidName));
        }
        return validFluids;
    }

    @Override
    public ICapabilityProvider initCapabilities(@Nonnull ItemStack stack, @Nullable NBTTagCompound nbt)
    {
        return new FluidWhitelistHandler(stack, CAPACITY,getValidFluids());
    }

    @Nonnull
    @Override
    public Size getSize(@Nonnull ItemStack stack)
    {
        return Size.SMALL;
    }

    @Nonnull
    @Override
    public Weight getWeight(@Nonnull ItemStack stack)
    {
        return Weight.LIGHT;
    }

    @Override
    public boolean canStack(@Nonnull ItemStack stack)
    {
        return false;
    }
}
