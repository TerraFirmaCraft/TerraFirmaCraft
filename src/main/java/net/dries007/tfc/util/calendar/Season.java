/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
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
    public String getName()
    {
        return serializedName;
    }
}
