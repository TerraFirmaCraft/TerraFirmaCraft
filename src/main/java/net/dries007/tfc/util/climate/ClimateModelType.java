/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util.climate;

import java.util.function.Supplier;

import net.minecraft.resources.ResourceLocation;

public record ClimateModelType(Supplier<ClimateModel> factory, ResourceLocation id)
{
    public ClimateModel create()
    {
        return factory.get();
    }
}
