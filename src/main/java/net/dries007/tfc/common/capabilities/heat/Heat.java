/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.capabilities.heat;

import net.dries007.tfc.util.Helpers;
import org.jetbrains.annotations.Nullable;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;

import net.dries007.tfc.TerraFirmaCraft;

public enum Heat
{
    WARMING("warming", 1f, 80f, ChatFormatting.GRAY),
    HOT("hot", 80f, 210f, ChatFormatting.GRAY),
    VERY_HOT("very_hot", 210f, 480f, ChatFormatting.GRAY),
    FAINT_RED("faint_red", 480f, 580f, ChatFormatting.DARK_RED),
    DARK_RED("dark_red", 580f, 730f, ChatFormatting.DARK_RED),
    BRIGHT_RED("bright_red", 730f, 930f, ChatFormatting.RED),
    ORANGE("orange", 930f, 1100f, ChatFormatting.GOLD),
    YELLOW("yellow", 1100f, 1300f, ChatFormatting.YELLOW),
    YELLOW_WHITE("yellow_white", 1300f, 1400f, ChatFormatting.YELLOW),
    WHITE("white", 1400f, 1500f, ChatFormatting.WHITE),
    BRILLIANT_WHITE("brilliant_white", 1500f, 1600f, ChatFormatting.WHITE);

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

    private final ChatFormatting color;
    private final String translationKey;
    private final float min;
    private final float max;

    Heat(String name, float min, float max, ChatFormatting color)
    {
        this.min = min;
        this.max = max;
        this.translationKey = TerraFirmaCraft.MOD_ID + ".enum.heat." + name;
        this.color = color;
    }

    public ChatFormatting getColor()
    {
        return color;
    }

    public float getMin()
    {
        return min;
    }

    public float getMax()
    {
        return max;
    }

    public MutableComponent getDisplayName()
    {
        return Helpers.translatable(translationKey);
    }
}