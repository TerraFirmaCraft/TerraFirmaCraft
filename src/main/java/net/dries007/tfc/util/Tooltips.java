/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraftforge.fluids.FluidStack;

public final class Tooltips
{
    public static MutableComponent fluidUnits(double mB)
    {
        return mB < 1.0 ? lessThanOneFluidUnits() : fluidUnits((int) Math.round(mB));
    }

    public static MutableComponent fluidUnits(int mB)
    {
        return Helpers.translatable("tfc.tooltip.fluid_units", mB);
    }

    public static MutableComponent fluidUnitsOf(FluidStack fluid)
    {
        return Helpers.translatable("tfc.tooltip.fluid_units_of", fluid.getAmount(), fluid.getDisplayName());
    }

    public static MutableComponent fluidUnitsAndCapacityOf(FluidStack fluid, int capacity)
    {
        return fluidUnitsAndCapacityOf(fluid.getDisplayName(), fluid.getAmount(), capacity);
    }

    public static MutableComponent fluidUnitsAndCapacityOf(Component fluid, int amount, int capacity)
    {
        return Helpers.translatable("tfc.tooltip.fluid_units_and_capacity_of", amount, capacity, fluid);
    }

    public static MutableComponent lessThanOneFluidUnits()
    {
        return Helpers.translatable("tfc.tooltip.less_than_one_fluid_units");
    }

    public static MutableComponent moltenOrSolid(boolean molten)
    {
        return Helpers.translatable(molten ? "tfc.tooltip.small_vessel.molten" : "tfc.tooltip.small_vessel.solid");
    }
}
