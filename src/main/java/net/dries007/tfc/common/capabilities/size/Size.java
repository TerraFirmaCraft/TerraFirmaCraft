/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.capabilities.size;

public enum Size
{
    TINY("tiny"), // Fits in anything
    VERY_SMALL("very_small"), // Fits in anything
    SMALL("small"), // Fits in small vessels
    NORMAL("normal"), // Fits in large vessels
    LARGE("large"), // Fits in chests, Pit kilns can hold four.
    VERY_LARGE("very_large"), // Pit kilns can only hold one.
    HUGE("huge"); // Pit kilns can only hold one. Counts towards overburdened when also very heavy.

    private static final Size[] VALUES = values();

    public static Size valueOf(int i)
    {
        return i >= 0 && i < VALUES.length ? VALUES[i] : NORMAL;
    }

    public final String name;

    Size(String name)
    {
        this.name = name;
    }

    public boolean isSmallerThan(Size other)
    {
        return this.ordinal() < other.ordinal();
    }

    public boolean isEqualOrSmallerThan(Size other)
    {
        return this.ordinal() <= other.ordinal();
    }
}
