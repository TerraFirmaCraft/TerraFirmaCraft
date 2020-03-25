/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.api.capability.size;

public enum Size
{
    TINY("tiny"),
    VERY_SMALL("very_small"),
    SMALL("small"),
    NORMAL("normal"),
    LARGE("large"),
    VERY_LARGE("very_large"),
    HUGE("huge");

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
