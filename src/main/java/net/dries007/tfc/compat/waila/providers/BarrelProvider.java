/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.compat.waila.providers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

import net.dries007.tfc.api.recipes.barrel.BarrelRecipe;
import net.dries007.tfc.compat.waila.interfaces.IWailaBlock;
import net.dries007.tfc.objects.te.TEBarrel;
import net.dries007.tfc.util.Helpers;

public class BarrelProvider implements IWailaBlock
{
    @Nonnull
    @Override
    public List<String> getHeadTooltip(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull NBTTagCompound nbt)
    {
        List<String> tooltip = new ArrayList<>();
        IBlockState state = world.getBlockState(pos);
        TEBarrel te = Helpers.getTE(world, pos, TEBarrel.class);
        if (te != null)
        {
            if (te.isSealed())
            {
                tooltip.add(TextFormatting.WHITE.toString() + new TextComponentTranslation(state.getBlock().getTranslationKey() + ".sealed.name").getFormattedText());
            }
            else
            {
                tooltip.add(TextFormatting.WHITE.toString() + new TextComponentTranslation(state.getBlock().getTranslationKey() + ".name").getFormattedText());
            }
        }
        return tooltip;
    }

    @Nonnull
    @Override
    public List<String> getBodyTooltip(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull List<String> currentTooltip, @Nonnull NBTTagCompound nbt)
    {
        TEBarrel te = Helpers.getTE(world, pos, TEBarrel.class);
        if (te != null)
        {
            IFluidHandler fluidHandler = te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);
            FluidStack fluid = fluidHandler != null ? fluidHandler.drain(Integer.MAX_VALUE, false) : null;

            if (te.isSealed())
            {
                currentTooltip.add(new TextComponentTranslation("waila.tfc.barrel.sealed", te.getSealedDate()).getFormattedText());
                BarrelRecipe recipe = te.getRecipe();
                if (recipe != null)
                {
                    currentTooltip.add(new TextComponentTranslation("waila.tfc.barrel.recipe", recipe.getResultName()).getFormattedText());
                }
                else
                {
                    currentTooltip.add(new TextComponentTranslation("waila.tfc.barrel.no_recipe").getFormattedText());
                }
            }
            if (fluid != null && fluid.amount > 0)
            {
                currentTooltip.add(new TextComponentTranslation("waila.tfc.barrel.contents", fluid.amount, fluid.getLocalizedName()).getFormattedText());
            }
        }
        return currentTooltip;
    }

    @Nonnull
    @Override
    public List<Class<?>> getHeadClassList()
    {
        return Collections.singletonList(TEBarrel.class);
    }

    @Nonnull
    @Override
    public List<Class<?>> getBodyClassList()
    {
        return Collections.singletonList(TEBarrel.class);
    }
}
