/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.capabilities.size;

public enum Weight
{
    VERY_LIGHT("very_light", 64),
    LIGHT("light", 32),
    MEDIUM("medium", 16),
    HEAVY("heavy", 4),
    VERY_HEAVY("very_heavy", 1);

    public final int stackSize;
    public final String name;

    Weight(String name, int stackSize)
    {
        this.name = name;
        this.stackSize = stackSize;
    }

    public boolean isSmallerThan(Weight other)
    {
        return this.stackSize > other.stackSize;
    }
}
