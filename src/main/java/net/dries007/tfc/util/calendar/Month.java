/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.util.calendar;

import net.dries007.tfc.util.Helpers;

import static net.dries007.tfc.util.calendar.Season.*;

public enum Month
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

    private static final Month[] VALUES = values();

    public static Month valueOf(int id)
    {
        return id >= 0 && id < VALUES.length ? VALUES[id] : JANUARY;
    }

    private final float temperatureModifier;
    private final Season season;

    Month(float temperatureModifier, Season season)
    {
        this.temperatureModifier = temperatureModifier;
        this.season = season;
    }

    public float getTemperatureModifier()
    {
        return temperatureModifier;
    }

    public Month next()
    {
        return VALUES[(ordinal() + 1) % VALUES.length];
    }

    public boolean isWithin(Month lowerBoundInclusive, Month upperBoundInclusive)
    {
        if (lowerBoundInclusive.ordinal() <= upperBoundInclusive.ordinal())
        {
            return this.ordinal() >= lowerBoundInclusive.ordinal() && this.ordinal() <= upperBoundInclusive.ordinal();
        }
        // If comparing the range NOV - FEB (for example), then both above and below count
        return this.ordinal() >= lowerBoundInclusive.ordinal() || this.ordinal() <= upperBoundInclusive.ordinal();
    }

    public String getTranslationKey(Style style)
    {
        switch (style)
        {
            case LONG_MONTH:
                return Helpers.getEnumTranslationKey(this);
            case SEASON:
                return Helpers.getEnumTranslationKey(this, "season");
            default:
                throw new IllegalArgumentException("Unknown text style? " + style);
        }
    }

    public Season getSeason()
    {
        return season;
    }

    public enum Style
    {
        LONG_MONTH, SEASON
    }
}