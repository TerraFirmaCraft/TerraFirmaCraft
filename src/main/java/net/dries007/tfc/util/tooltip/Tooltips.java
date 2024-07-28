/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util.tooltip;

import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;

/**
 * Text based tooltips for common situations, such as displaying standardized quantities of a fluid.
 */
public final class Tooltips
{
    public static MutableComponent fluidUnits(double mB)
    {
        return mB < 1.0 ? lessThanOneFluidUnits() : fluidUnits((int) Math.round(mB));
    }

    public static MutableComponent fluidUnits(int mB)
    {
        return Component.translatable("tfc.tooltip.fluid_units", mB);
    }

    public static MutableComponent fluidUnitsOf(FluidStack fluid)
    {
        return Component.translatable("tfc.tooltip.fluid_units_of", fluid.getAmount(), fluid.getHoverName());
    }

    public static MutableComponent fluidUnitsAndCapacityOf(FluidStack fluid, int capacity)
    {
        return fluidUnitsAndCapacityOf(fluid.getHoverName(), fluid.getAmount(), capacity);
    }

    public static MutableComponent fluidUnitsAndCapacityOf(Component fluid, int amount, int capacity)
    {
        return Component.translatable("tfc.tooltip.fluid_units_and_capacity_of", amount, capacity, fluid);
    }

    public static MutableComponent lessThanOneFluidUnits()
    {
        return Component.translatable("tfc.tooltip.less_than_one_fluid_units");
    }

    public static MutableComponent moltenOrSolid(boolean molten)
    {
        String key = molten ? "tfc.tooltip.small_vessel.molten" : "tfc.tooltip.small_vessel.solid";
        return Component.translatable(key);
    }

    public static MutableComponent countOfItem(ItemStack stack)
    {
        return Component.literal(String.valueOf(stack.getCount()))
            .append(" x ")
            .append(stack.getHoverName());
    }

    public static MutableComponent tier(int tier)
    {
        return Component.translatable("tfc.tooltip.tier_" + tier);
    }

    public record DeviceImageTooltip(List<ItemStack> items, int width, int height) implements TooltipComponent {}
}
