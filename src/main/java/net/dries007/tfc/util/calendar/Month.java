/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.util.calendar;

import java.util.Arrays;
import javax.annotation.Nonnull;

public enum Month
{
    JANUARY(66.5f),
    FEBRUARY(65.5f),
    MARCH(56f),
    APRIL(47.5f),
    MAY(38f),
    JUNE(29.5f),
    JULY(27f),
    AUGUST(29.5f),
    SEPTEMBER(38f),
    OCTOBER(47.5f),
    NOVEMBER(56f),
    DECEMBER(65.5f);

    private static final Month[] VALUES = values();
    public static final float AVERAGE_TEMPERATURE_MODIFIER = (float) Arrays.stream(VALUES).mapToDouble(Month::getTemperatureModifier).average().orElse(0);

    @Nonnull
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
