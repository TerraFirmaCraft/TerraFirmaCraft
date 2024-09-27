/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util.tooltip;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.client.ClientRotationNetworkHandler;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.network.RotationOwner;

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
        return Component.translatable(molten ? "tfc.tooltip.molten" : "tfc.tooltip.solid");
    }

    @Nullable
    public static MutableComponent meltsInto(FluidStack stack, float atTemperature)
    {
        final MutableComponent heat = TFCConfig.CLIENT.heatTooltipStyle.get().formatColored(atTemperature);
        return heat == null ? null : Component.translatable("tfc.tooltip.melts_into", fluidUnitsOf(stack), heat);
    }

    public static MutableComponent contents()
    {
        return Component.translatable("tfc.tooltip.contents").withStyle(ChatFormatting.DARK_GREEN);
    }

    public static MutableComponent countOfItem(ItemStack stack)
    {
        return countOfItem(stack, stack.getCount());
    }

    public static MutableComponent countOfItem(ItemStack stack, int count)
    {
        return Component.literal(String.valueOf(count))
            .append(" x ")
            .append(stack.getHoverName());
    }

    public static MutableComponent tier(int tier)
    {
        return Component.translatable("tfc.tooltip.tier_" + tier);
    }

    @Contract("null, null -> fail")
    public static MutableComponent require(@Nullable Component min, @Nullable Component max)
    {
        if (min != null && max != null) return Component.translatable("tfc.tooltip.required_range", min, max);
        if (min != null) return Component.translatable("tfc.tooltip.required_greater_than", min);
        if (max != null) return Component.translatable("tfc.tooltip.required_less_than", max);
        throw new IllegalArgumentException("One of min or max must be non-null");
    }

    public static MutableComponent rpm(RotationOwner owner)
    {
        // radians/tick x 20 ticks/second x 60 seconds/minute div 2pi radians/rotation
        return Component.translatable("tfc.tooltip.rpm", String.format("%.1f", (20 * 60 / Mth.TWO_PI) * ClientRotationNetworkHandler.getRotationSpeed(owner)));
    }
}
