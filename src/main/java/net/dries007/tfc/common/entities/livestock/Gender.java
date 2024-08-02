/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities.livestock;

public enum Gender
{
    MALE, FEMALE;

    public static Gender valueOf(boolean value)
    {
        return value ? MALE : FEMALE;
    }

    public boolean toBool()
    {
        return this == MALE;
    }

    public boolean isMale() { return this == MALE; }
    public boolean isFemale() { return this == FEMALE; }
}
