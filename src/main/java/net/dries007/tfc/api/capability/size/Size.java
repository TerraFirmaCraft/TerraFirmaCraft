/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.api.capability.size;

public enum Size
{
    TINY("tiny"), // Fits in anything
    VERY_SMALL("very_small"), // Fits in anything
    SMALL("small"), // Fits in small vessels
    NORMAL("normal"), // Fits in large vessels
    LARGE("large"), // Fits in chests, Pit kilns can hold four
    VERY_LARGE("very_large"), // Pit kilns can only hold one
    HUGE("huge"); // Counts towards overburdened, fits in nothing

    public final String name;

    Size(String name)
    {
        this.name = name;
    }

    public boolean isSmallerThan(Size other)
    {
        return this.ordinal() < other.ordinal();
    }
}
