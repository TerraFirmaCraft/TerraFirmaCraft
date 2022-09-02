/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.config;

import net.dries007.tfc.util.Helpers;
import org.jetbrains.annotations.Nullable;

import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;

import net.dries007.tfc.common.capabilities.heat.Heat;

public enum HeatTooltipStyle
{
    COLOR(temperature -> {
        final Heat heat = Heat.getHeat(temperature);
        if (heat != null)
        {
            final MutableComponent base = heat.getDisplayName();
            if (heat != Heat.BRILLIANT_WHITE)
            {
                for (int i = 1; i <= 4; i++)
                {
                    if (temperature <= heat.getMin() + ((float) i * 0.2f) * (heat.getMax() - heat.getMin()))
                        continue;
                    base.append("\u066D");
                }
            }
            return base;
        }
        return null;
    }),
    CELSIUS(temperature -> Helpers.translatable("tfc.tooltip.temperature_celsius", String.format("%.0f", temperature))),
    FAHRENHEIT(temperature -> Helpers.translatable("tfc.tooltip.temperature_fahrenheit", String.format("%.0f", temperature * (9f / 5f) + 32f)));

    private final Function formatter;

    HeatTooltipStyle(Function formatter)
    {
        this.formatter = formatter;
    }

    @Nullable
    public MutableComponent format(float temperature)
    {
        if (temperature > 1)
        {
            return formatter.format(temperature);
        }
        return null;
    }

    @Nullable
    public MutableComponent formatColored(float temperature)
    {
        Heat heat = Heat.getHeat(temperature);
        MutableComponent tooltip = format(temperature);
        if (tooltip != null && heat != null)
        {
            tooltip.withStyle(heat.getColor());
        }
        return tooltip;
    }

    @FunctionalInterface
    interface Function
    {
        @Nullable
        MutableComponent format(float temperature);
    }
}
