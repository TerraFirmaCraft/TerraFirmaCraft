/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.api.capability.heat;

import javax.annotation.Nullable;

import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextFormatting;

import net.dries007.tfc.util.Helpers;

public enum Heat
{
    WARMING(1f, 80f, TextFormatting.GRAY, TextFormatting.DARK_GRAY),
    HOT(80f, 210f, TextFormatting.GRAY, TextFormatting.DARK_GRAY),
    VERY_HOT(210f, 480f, TextFormatting.GRAY, TextFormatting.DARK_GRAY),
    FAINT_RED(480f, 580f, TextFormatting.DARK_RED),
    DARK_RED(580f, 730f, TextFormatting.DARK_RED),
    BRIGHT_RED(730f, 930f, TextFormatting.RED),
    ORANGE(930f, 1100f, TextFormatting.GOLD),
    YELLOW(1100f, 1300f, TextFormatting.YELLOW),
    YELLOW_WHITE(1300f, 1400f, TextFormatting.YELLOW),
    WHITE(1400f, 1500f, TextFormatting.WHITE),
    BRILLIANT_WHITE(1500f, 1601f, TextFormatting.WHITE);

    private static final Heat[] VALUES = values();

    public static float maxVisibleTemperature()
    {
        return BRILLIANT_WHITE.max;
    }

    @Nullable
    public static Heat getHeat(float temperature)
    {
        for (Heat heat : VALUES)
        {
            if (heat.min <= temperature && temperature < heat.max)
            {
                return heat;
            }
        }
        if (temperature > BRILLIANT_WHITE.max)
        {
            // Default to "hotter than brilliant white" for max
            return BRILLIANT_WHITE;
        }
        return null;
    }

    @Nullable
    public static String getTooltipColorless(float temperature)
    {
        Heat heat = Heat.getHeat(temperature);
        if (heat != null)
        {
            StringBuilder b = new StringBuilder();
            b.append(I18n.format(Helpers.getEnumName(heat)));
            if (heat != Heat.BRILLIANT_WHITE)
            {
                for (int i = 1; i <= 4; i++)
                {
                    if (temperature <= heat.min + ((float) i * 0.2f) * (heat.max - heat.min))
                        continue;
                    b.append("\u2605");
                }
            }
            return b.toString();
        }
        return null;
    }

    /**
     * Compare the equality of two temperatures.
     * Used to sync IItemHeat instances on client
     *
     * @return true if Heat are equally the same, false if should be sync
     */
    public static boolean compareHeat(float temperature1, float temperature2)
    {
        Heat heat1 = Heat.getHeat(temperature1);
        Heat heat2 = Heat.getHeat(temperature2);
        if (heat1 == heat2 && heat1 != null)
        {
            float value = ((heat1.max - heat1.min) / 0.2f); // A "*" value
            float heat1Value = (int) ((temperature1 - heat1.min) / value);
            float heat2Value = (int) ((temperature2 - heat2.min) / value);
            return Math.abs(heat1Value - heat2Value) > 0.5D; // Half the amount so we can catch when client is about to decline one "*"
        }
        return false;
    }

    @Nullable
    public static String getTooltip(float temperature)
    {
        Heat heat = Heat.getHeat(temperature);
        String tooltip = getTooltipColorless(temperature);
        if (tooltip != null && heat != null)
        {
            tooltip = heat.format + tooltip;
        }
        return tooltip;
    }

    @Nullable
    public static String getTooltipAlternate(float temperature)
    {
        Heat heat = Heat.getHeat(temperature);
        String tooltip = getTooltipColorless(temperature);
        if (tooltip != null && heat != null)
        {
            tooltip = heat.alternate + tooltip;
        }
        return tooltip;
    }

    final float min;
    final float max;
    final TextFormatting format, alternate;

    Heat(float min, float max, TextFormatting format, TextFormatting alternate)
    {
        this.min = min;
        this.max = max;
        this.format = format;
        this.alternate = alternate;
    }

    Heat(float min, float max, TextFormatting format)
    {
        this(min, max, format, format);
    }
}
