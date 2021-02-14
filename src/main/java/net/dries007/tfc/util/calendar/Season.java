/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util.calendar;

import net.minecraft.util.IStringSerializable;

public enum Season implements IStringSerializable
{
    SPRING,
    SUMMER,
    FALL,
    WINTER;

    private static final Season[] VALUES = values();

    private final String serializedName;

    Season()
    {
        this.serializedName = name().toLowerCase();
    }

    public Season next()
    {
        return this == WINTER ? SPRING : VALUES[this.ordinal() + 1];
    }


    @Override
    public String getString() {
        return serializedName;
    }
}