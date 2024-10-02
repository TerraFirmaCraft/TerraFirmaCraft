/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util.calendar;

import java.util.Locale;
import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;

import net.dries007.tfc.util.Helpers;

import static net.dries007.tfc.util.calendar.Season.*;

public enum Month implements StringRepresentable
{
    JANUARY(-1f, WINTER),
    FEBRUARY(-0.866f, WINTER),
    MARCH(-0.5f, SPRING),
    APRIL(0f, SPRING),
    MAY(0.5f, SPRING),
    JUNE(0.866f, SUMMER),
    JULY(1f, SUMMER),
    AUGUST(0.866f, SUMMER),
    SEPTEMBER(0.5f, FALL),
    OCTOBER(0f, FALL),
    NOVEMBER(-0.5f, FALL),
    DECEMBER(-0.866f, WINTER);

    public static final Codec<Month> CODEC = StringRepresentable.fromEnum(Month::values);
    private static final Month[] VALUES = values();

    public static Month valueOf(int id)
    {
        return id >= 0 && id < VALUES.length ? VALUES[id] : JANUARY;
    }


    private final float temperatureModifier;
    private final Season season;
    private final String serializedName;

    Month(float temperatureModifier, Season season)
    {
        this.temperatureModifier = temperatureModifier;
        this.season = season;
        this.serializedName = this.name().toLowerCase(Locale.ROOT);
    }

    public float getTemperatureModifier()
    {
        return temperatureModifier;
    }

    public Month next()
    {
        return this == DECEMBER ? JANUARY : VALUES[ordinal() + 1];
    }

    public String getTranslationKey(Style style)
    {
        return switch (style)
            {
                case LONG_MONTH -> Helpers.getEnumTranslationKey(this);
                case SEASON -> Helpers.getEnumTranslationKey(this, "season");
            };
    }

    public Season getSeason()
    {
        return season;
    }

    @Override
    public String getSerializedName()
    {
        return serializedName;
    }

    public enum Style
    {
        LONG_MONTH, SEASON
    }
}