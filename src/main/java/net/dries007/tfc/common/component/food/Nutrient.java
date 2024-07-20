/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.component.food;

import java.util.Locale;
import net.minecraft.ChatFormatting;
import net.minecraft.util.StringRepresentable;

public enum Nutrient implements StringRepresentable
{
    GRAIN(ChatFormatting.GOLD),
    FRUIT(ChatFormatting.GREEN),
    VEGETABLES(ChatFormatting.DARK_GREEN),
    PROTEIN(ChatFormatting.RED),
    DAIRY(ChatFormatting.DARK_PURPLE);

    public static final int TOTAL = values().length;
    public static final Nutrient[] VALUES = values();

    public static Nutrient valueOf(int i)
    {
        return i >= 0 && i < VALUES.length ? VALUES[i] : GRAIN;
    }

    private final String serializedName;
    private final ChatFormatting color;

    Nutrient(ChatFormatting color)
    {
        this.serializedName = name().toLowerCase(Locale.ROOT);
        this.color = color;
    }

    @Override
    public String getSerializedName()
    {
        return serializedName;
    }

    public ChatFormatting getColor()
    {
        return color;
    }
}
