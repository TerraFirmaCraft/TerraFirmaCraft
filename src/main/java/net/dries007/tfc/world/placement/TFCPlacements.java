/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.placement;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import net.dries007.tfc.util.registry.RegistryHolder;

import static net.dries007.tfc.TerraFirmaCraft.*;


@SuppressWarnings("unused")
public final class TFCPlacements
{
    public static final DeferredRegister<PlacementModifierType<?>> PLACEMENT_MODIFIERS = DeferredRegister.create(Registries.PLACEMENT_MODIFIER_TYPE, MOD_ID);

    public static final RegistryId<FlatEnoughPlacement> FLAT_ENOUGH = register("flat_enough", () -> FlatEnoughPlacement.CODEC);
    public static final RegistryId<UndergroundPlacement> UNDERGROUND = register("underground", () -> UndergroundPlacement.CODEC);
    public static final RegistryId<BoundedCarvingMaskPlacement> CARVING_MASK = register("carving_mask", () -> BoundedCarvingMaskPlacement.CODEC);
    public static final RegistryId<ClimatePlacement> CLIMATE = register("climate", () -> ClimatePlacement.CODEC);
    public static final RegistryId<VolcanoPlacement> VOLCANO = register("volcano", () -> VolcanoPlacement.CODEC);
    public static final RegistryId<NearFluidPlacement> NEAR_FLUID = register("near_fluid", () -> NearFluidPlacement.CODEC);
    public static final RegistryId<ShallowWaterPlacement> SHALLOW_WATER = register("shallow_water", () -> ShallowWaterPlacement.CODEC);
    public static final RegistryId<OnTopPlacement> ON_TOP = register("on_top", () -> OnTopPlacement.CODEC);
    public static final RegistryId<BiomePlacement> BIOME = register("biome", () -> BiomePlacement.CODEC);
    public static final RegistryId<NoSolidNeighborsPlacement> NO_SOLID_NEIGHBORS = register("no_solid_neighbors", ()-> NoSolidNeighborsPlacement.CODEC);

    private static <C extends PlacementModifier> RegistryId<C> register(String name, PlacementModifierType<C> codec)
    {
        return new RegistryId<>(PLACEMENT_MODIFIERS.register(name, () -> codec));
    }

    public record RegistryId<T extends PlacementModifier>(DeferredHolder<PlacementModifierType<?>, PlacementModifierType<T>> holder)
        implements RegistryHolder<PlacementModifierType<?>, PlacementModifierType<T>> {}
}
