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
import net.dries007.tfc.api.capability.metal.IMetalItem;
import net.dries007.tfc.api.types.Metal;
import net.dries007.tfc.objects.blocks.metal.BlockMetalLamp;
import net.dries007.tfc.objects.fluids.capability.FluidWhitelistHandler;

/**
 * todo: this
 */
public class ItemBlockMetalLamp extends ItemBlockTFC implements IMetalItem
{
    public final static int CAPACITY = 250;

    public final ToolMaterial material;

    public ItemBlockMetalLamp(Metal metal)
    {
        super(BlockMetalLamp.get(metal));

        material = metal.getToolMetal();
    }

    @Override
    public boolean canStack(@Nonnull ItemStack stack)
    {
        IFluidHandler lampCap = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
        if (lampCap != null)
        {
            return lampCap.drain(CAPACITY, false) == null;
        }
        return true;
    }

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
        String[] fluidNames = ConfigTFC.GENERAL.metalLampFuels;
        Set<Fluid> validFluids = new HashSet<>();
        for (String fluidName : fluidNames)
        {
            validFluids.add(FluidRegistry.getFluid(fluidName));
        }
        return validFluids;
    }

    //no need for @Override itemRightClick to fill or place since fluidhandler interactions and placement are handled before it is called

    @Override
    public ICapabilityProvider initCapabilities(@Nonnull ItemStack stack, @Nullable NBTTagCompound nbt)
    {
        return new FluidWhitelistHandler(stack, CAPACITY, getValidFluids());
    }

    /**
     * @param stack the item stack. This can assume that it is of the right item type and do casts without checking
     * @return the metal of the stack
     */
    @Nullable
    @Override
    public Metal getMetal(ItemStack stack)
    {
        return ((BlockMetalLamp)(super.block)).getMetal();
    }

    /**
     * @param stack The item stack
     * @return the amount of liquid metal that this item will create (in TFC units or mB: 1 unit = 1 mB)
     */
    @Override
    public int getSmeltAmount(ItemStack stack)
    {
        return 100;
    }
}
