/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities.livestock;

public enum Age
{
    CHILD, ADULT, OLD;

    public static Age valueOf(int value)
    {
        return value == 0 ? CHILD : value == 1 ? ADULT : OLD;
    }
}
