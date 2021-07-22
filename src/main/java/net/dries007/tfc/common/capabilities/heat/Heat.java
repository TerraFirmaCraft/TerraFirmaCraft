/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.capabilities.heat;

import javax.annotation.Nullable;

import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

import net.dries007.tfc.TerraFirmaCraft;

public enum Heat
{
    WARMING("warming", 1f, 80f, TextFormatting.GRAY),
    HOT("hot", 80f, 210f, TextFormatting.GRAY),
    VERY_HOT("very_hot", 210f, 480f, TextFormatting.GRAY),
    FAINT_RED("faint_red", 480f, 580f, TextFormatting.DARK_RED),
    DARK_RED("dark_red", 580f, 730f, TextFormatting.DARK_RED),
    BRIGHT_RED("bright_red", 730f, 930f, TextFormatting.RED),
    ORANGE("orange", 930f, 1100f, TextFormatting.GOLD),
    YELLOW("yellow", 1100f, 1300f, TextFormatting.YELLOW),
    YELLOW_WHITE("yellow_white", 1300f, 1400f, TextFormatting.YELLOW),
    WHITE("white", 1400f, 1500f, TextFormatting.WHITE),
    BRILLIANT_WHITE("brilliant_white", 1500f, 1601f, TextFormatting.WHITE);

    private static final Heat[] VALUES = values();

    public static float maxVisibleTemperature()
    {
        return BRILLIANT_WHITE.getMax();
    }

    @Nullable
    public static Heat getHeat(float temperature)
    {
        if (temperature >= WARMING.min)
        {
            for (Heat heat : VALUES)
            {
                if (temperature < heat.max)
                {
                    return heat;
                }
            }
            return BRILLIANT_WHITE;
        }
        return null;
    }

    @Nullable
    public static IFormattableTextComponent getTooltipColorless(float temperature)
    {
        Heat heat = Heat.getHeat(temperature);
        if (heat != null)
        {
            IFormattableTextComponent base = heat.getDisplayName();
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
    }

    @Nullable
    public static IFormattableTextComponent getTooltip(float temperature)
    {
        Heat heat = Heat.getHeat(temperature);
        IFormattableTextComponent tooltip = getTooltipColorless(temperature);
        if (tooltip != null && heat != null)
        {
            tooltip.withStyle(heat.color);
        }
        return tooltip;
    }

    private final TextFormatting color;
    private final String translationKey;
    private final float min;
    private final float max;

    Heat(String name, float min, float max, TextFormatting color)
    {
        this.min = min;
        this.max = max;
        this.translationKey = TerraFirmaCraft.MOD_ID + ".enum.heat." + name;
        this.color = color;
    }

    public float getMin()
    {
        return min;
    }

    public float getMax()
    {
        return max;
    }

    public IFormattableTextComponent getDisplayName()
    {
        return new TranslationTextComponent(translationKey);
    }
}