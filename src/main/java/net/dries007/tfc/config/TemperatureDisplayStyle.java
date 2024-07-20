/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.config;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.component.heat.Heat;

public enum TemperatureDisplayStyle
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
    CELSIUS(temperature -> Component.translatable("tfc.tooltip.temperature_celsius", String.format("%.0f", temperature))),
    FAHRENHEIT(temperature -> Component.translatable("tfc.tooltip.temperature_fahrenheit", String.format("%.0f", temperature * (9f / 5f) + 32f))),
    RANKINE(temperature -> Component.translatable("tfc.tooltip.temperature_rankine", String.format("%.0f", temperature * (9f / 5f) + 32f - 459.67f))),
    KELVIN(temperature -> Component.translatable("tfc.tooltip.temperature_kelvin", String.format("%.0f", temperature + 273.15f)));

    private final Function formatter;

    TemperatureDisplayStyle(Function formatter)
    {
        this.formatter = formatter;
    }

    @Nullable
    public MutableComponent format(float temperature)
    {
        return format(temperature, false);
    }

    @Nullable
    public MutableComponent format(float temperature, boolean fullRange)
    {
        if (temperature > 1 || fullRange)
        {
            return formatter.format(temperature);
        }
        return null;
    }

    @Nullable
    public MutableComponent formatColored(float temperature)
    {
        Heat heat = Heat.getHeat(temperature);
        MutableComponent tooltip = format(temperature, false);
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
