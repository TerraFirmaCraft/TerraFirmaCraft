/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.plant.fruit;

import java.util.Locale;

import net.minecraft.util.StringRepresentable;

public enum Lifecycle implements StringRepresentable
{
    DORMANT, HEALTHY, FLOWERING, FRUITING;

    private static final Lifecycle[] VALUES = values();

    public boolean active()
    {
        return this != DORMANT;
    }

    /**
     * Advances one 'stage' towards the target lifecycle, if it is greater. Otherwise, defaults down to other.
     */
    public Lifecycle advanceTowards(Lifecycle other)
    {
        if (other.ordinal() < this.ordinal())
        {
            return other;
        }
        if (other.ordinal() > this.ordinal() && this != FRUITING)
        {
            return VALUES[this.ordinal() + 1];
        }
        return this;
    }

    @Override
    public String getSerializedName()
    {
        return this.name().toLowerCase(Locale.ROOT);
    }
}
