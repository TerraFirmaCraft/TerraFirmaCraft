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
    HEALTHY, FLOWERING, FRUITING, DORMANT;

    @Override
    public String getSerializedName()
    {
        return this.name().toLowerCase(Locale.ROOT);
    }
}
