/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.api.capability.size;

public enum Weight
{
    LIGHT("light", 4),
    MEDIUM("medium", 2),
    HEAVY("heavy", 1);

    public final String name;
    public final int multiplier;

    Weight(String name, int multiplier)
    {
        this.name = name;
        this.multiplier = multiplier;
    }

    public boolean isSmallerThan(Weight other)
    {
        return this.multiplier > other.multiplier;
    }
}
