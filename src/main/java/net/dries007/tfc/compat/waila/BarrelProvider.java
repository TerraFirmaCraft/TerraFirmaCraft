/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.compat.waila;

import java.util.List;
import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

import mcp.mobius.waila.api.*;
import net.dries007.tfc.api.recipes.barrel.BarrelRecipe;
import net.dries007.tfc.objects.blocks.wood.BlockBarrel;
import net.dries007.tfc.objects.te.TEBarrel;

@WailaPlugin
public class BarrelProvider implements IWailaDataProvider, IWailaPlugin
{
    @Nonnull
    @Override
    public List<String> getWailaHead(ItemStack itemStack, List<String> currentTooltip, IWailaDataAccessor accessor, IWailaConfigHandler config)
    {
        if (accessor.getBlock() instanceof BlockBarrel && accessor.getTileEntity() instanceof TEBarrel)
        {
            BlockBarrel b = (BlockBarrel) accessor.getBlock();
            TEBarrel te = (TEBarrel) accessor.getTileEntity();

            if (te.isSealed())
            {
                currentTooltip.set(0, TextFormatting.WHITE.toString() + new TextComponentTranslation(b.getTranslationKey() + ".sealed.name").getFormattedText());
                currentTooltip.add(new TextComponentTranslation("waila.tfc.sealed", te.getSealedDate()).getFormattedText());
            }
            else
            {
                currentTooltip.set(0, TextFormatting.WHITE.toString() + new TextComponentTranslation(b.getTranslationKey() + ".name").getFormattedText());
            }
        }
        return currentTooltip;
    }

    @Nonnull
    @Override
    public List<String> getWailaBody(ItemStack itemStack, List<String> currentTooltip, IWailaDataAccessor accessor, IWailaConfigHandler config)
    {
        if (accessor.getTileEntity() instanceof TEBarrel)
        {
            TEBarrel te = (TEBarrel) accessor.getTileEntity();
            IFluidHandler fluidHandler = te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);
            FluidStack fluid = fluidHandler != null ? fluidHandler.drain(Integer.MAX_VALUE, false) : null;

            if (te.isSealed())
            {
                BarrelRecipe recipe = te.getRecipe();
                if (recipe != null)
                {
                    currentTooltip.add(new TextComponentTranslation("waila.tfc.making", recipe.getResultName()).getFormattedText());
                }
                else
                {
                    currentTooltip.add(new TextComponentTranslation("waila.tfc.no_recipe").getFormattedText());
                }
            }
            if (fluid != null && fluid.amount > 0)
            {
                currentTooltip.add(new TextComponentTranslation("waila.tfc.barrel_contents", fluid.amount, fluid.getLocalizedName()).getFormattedText());
            }
        }
        return currentTooltip;
    }

    @Override
    public void register(IWailaRegistrar registrar)
    {
        registrar.registerHeadProvider(this, BlockBarrel.class);
        registrar.registerBodyProvider(this, BlockBarrel.class);
    }
}
