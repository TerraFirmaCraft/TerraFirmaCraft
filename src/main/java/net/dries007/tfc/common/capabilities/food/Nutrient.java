package net.dries007.tfc.common.capabilities.food;

import java.util.Locale;

import net.minecraft.util.IStringSerializable;
import net.minecraft.util.text.TextFormatting;

public enum Nutrient implements IStringSerializable
{
    GRAIN(TextFormatting.GOLD),
    FRUIT(TextFormatting.GREEN),
    VEGETABLES(TextFormatting.DARK_GREEN),
    PROTEIN(TextFormatting.RED),
    DAIRY(TextFormatting.DARK_PURPLE);

    public static final int TOTAL = values().length;
    public static final Nutrient[] VALUES = values();

    public static Nutrient valueOf(int i)
    {
        return i >= 0 && i < VALUES.length ? VALUES[i] : GRAIN;
    }

    private final String serializedName;
    private final TextFormatting color;

    Nutrient(TextFormatting color)
    {
        this.serializedName = name().toLowerCase(Locale.ROOT);
        this.color = color;
    }

    @Override
    public String getSerializedName()
    {
        return serializedName;
    }

    public TextFormatting getColor()
    {
        return color;
    }
}
