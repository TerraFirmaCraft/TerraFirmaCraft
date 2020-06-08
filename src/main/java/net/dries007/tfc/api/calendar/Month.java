/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.api.calendar;

import java.util.Arrays;

public enum Month
{
    JANUARY(-1f),
    FEBRUARY(-0.866f),
    MARCH(-0.5f),
    APRIL(0f),
    MAY(0.5f),
    JUNE(0.866f),
    JULY(1f),
    AUGUST(0.866f),
    SEPTEMBER(0.5f),
    OCTOBER(0f),
    NOVEMBER(-0.5f),
    DECEMBER(-0.866f);

    private static final Month[] VALUES = values();
    public static final float AVERAGE_TEMPERATURE_MODIFIER = (float) Arrays.stream(VALUES).mapToDouble(Month::getTemperatureModifier).average().orElse(0);

    public static Month valueOf(int id)
    {
        return id >= 0 && id < VALUES.length ? VALUES[id] : JANUARY;
    }

    private final float temperatureModifier;

    Month(float temperatureModifier)
    {
        this.temperatureModifier = temperatureModifier;
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
}
