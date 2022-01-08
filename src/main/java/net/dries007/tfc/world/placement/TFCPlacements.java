/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.placement;

import java.util.function.Supplier;

import net.minecraft.core.Registry;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import net.minecraftforge.common.util.Lazy;

import com.mojang.serialization.Codec;
import net.dries007.tfc.util.Helpers;


@SuppressWarnings("unused")
public final class TFCPlacements
{
    // public static final DeferredRegister<PlacementModifierType<?>> PLACEMENT_MODIFIERS = DeferredRegister.create(ForgeRegistries.PLACEMENT_MODIFIERS, MOD_ID);

    public static final Supplier<PlacementModifierType<FlatEnoughPlacement>> FLAT_ENOUGH = register("flat_enough", FlatEnoughPlacement.CODEC);
    public static final Supplier<PlacementModifierType<UndergroundPlacement>> UNDERGROUND = register("underground", UndergroundPlacement.CODEC);
    public static final Supplier<PlacementModifierType<BoundedCarvingMaskPlacement>> CARVING_MASK = register("carving_mask", BoundedCarvingMaskPlacement.CODEC);

    public static final Supplier<PlacementModifierType<ClimatePlacement>> CLIMATE = register("climate", ClimatePlacement.CODEC);
    public static final Supplier<PlacementModifierType<VolcanoPlacement>> VOLCANO = register("volcano", VolcanoPlacement.CODEC);

    public static final Supplier<PlacementModifierType<NearWaterPlacement>> NEAR_WATER = register("near_water", NearWaterPlacement.CODEC);
    public static final Supplier<PlacementModifierType<ShallowWaterPlacement>> SHALLOW_WATER = register("shallow_water", ShallowWaterPlacement.CODEC);

    public static void registerPlacements()
    {
        FLAT_ENOUGH.get();
        UNDERGROUND.get();
        CARVING_MASK.get();
        CLIMATE.get();
        VOLCANO.get();
        NEAR_WATER.get();
        SHALLOW_WATER.get();
    }

    private static <C extends PlacementModifier> Supplier<PlacementModifierType<C>> register(String name, Codec<C> codec)
    {
        // todo: move to DR if/when forge converts placement modifiers to a registry
        return Lazy.of(() -> Registry.register(Registry.PLACEMENT_MODIFIERS, Helpers.identifier(name), () -> codec));
    }
}
