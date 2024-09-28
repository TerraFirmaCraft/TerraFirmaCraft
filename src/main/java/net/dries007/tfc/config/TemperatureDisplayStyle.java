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
import net.dries007.tfc.util.Helpers;

public enum TemperatureDisplayStyle
{
    COLOR(temperature -> {
        final Heat heat = Heat.getHeat(temperature);
        if (heat != null)
        {
            final MutableComponent base = Helpers.translateEnum(heat);
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

    /**
     * Format a temperature, if <strong>positive (hot)</strong>
     */
    @Nullable
    public MutableComponent format(float temperature)
    {
        return temperature > 0 ? formatter.format(temperature) : null;
    }

    /**
     * Format a temperature, if <strong>positive (hot)</strong>, and with a color dependent on the heat.
     */
    @Nullable
    public MutableComponent formatColored(float temperature)
    {
        final Heat heat = Heat.getHeat(temperature);
        if (heat != null)
        {
            final MutableComponent tooltip = formatter.format(temperature);
            if (tooltip != null) tooltip.withStyle(heat.getColor());
            return tooltip;
        }
        return null;
    }

    /**
     * Format a temperature, including the whole display range possible of the temperature.
     */
    @Nullable
    public MutableComponent formatRange(float temperature)
    {
        return formatter.format(temperature);
    }

    @FunctionalInterface
    interface Function
    {
        @Nullable
        MutableComponent format(float temperature);
    }
}
