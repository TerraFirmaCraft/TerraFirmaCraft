/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util.climate;

import java.util.function.Supplier;

import net.minecraftforge.common.util.Lazy;

import net.dries007.tfc.util.Helpers;

public final class ClimateModels
{
    public static final Supplier<ClimateModelType> OVERWORLD = register("overworld", OverworldClimateModel::new);
    public static final Supplier<ClimateModelType> BIOME_BASED = register("biome_based", BiomeBasedClimateModel::new);

    public static void registerClimateModels()
    {
        OVERWORLD.get();
        BIOME_BASED.get();
    }

    private static Supplier<ClimateModelType> register(String id, Supplier<ClimateModel> model)
    {
        return Lazy.of(() -> Climate.register(Helpers.identifier(id), model));
    }
}
