/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 *
 */

package net.dries007.tfc.objects;

public enum Size
{
    TINY("tiny", 64),
    VERY_SMALL("very_small", 32),
    SMALL("small", 16),
    NORMAL("normal", 8),
    LARGE("large", 4),
    VERY_LARGE("very_large", 2),
    HUGE("huge", 1);

    public final int stackSize;
    public final String name;

    Size(String name, int stackSize)
    {
        this.name = name;
        this.stackSize = stackSize;
    }

}
