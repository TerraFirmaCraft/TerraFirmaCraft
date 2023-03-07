/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.placement;

import net.minecraft.core.Registry;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;


@SuppressWarnings("unused")
public final class TFCPlacements
{
    public static final DeferredRegister<PlacementModifierType<?>> PLACEMENT_MODIFIERS = DeferredRegister.create(Registry.PLACEMENT_MODIFIER_REGISTRY, MOD_ID);

    public static final RegistryObject<PlacementModifierType<FlatEnoughPlacement>> FLAT_ENOUGH = register("flat_enough", () -> FlatEnoughPlacement.CODEC);
    public static final RegistryObject<PlacementModifierType<UndergroundPlacement>> UNDERGROUND = register("underground", () -> UndergroundPlacement.CODEC);
    public static final RegistryObject<PlacementModifierType<BoundedCarvingMaskPlacement>> CARVING_MASK = register("carving_mask", () -> BoundedCarvingMaskPlacement.CODEC);
    public static final RegistryObject<PlacementModifierType<ClimatePlacement>> CLIMATE = register("climate", () -> ClimatePlacement.PLACEMENT_CODEC);
    public static final RegistryObject<PlacementModifierType<VolcanoPlacement>> VOLCANO = register("volcano", () -> VolcanoPlacement.CODEC);
    public static final RegistryObject<PlacementModifierType<NearWaterPlacement>> NEAR_WATER = register("near_water", () -> NearWaterPlacement.CODEC);
    public static final RegistryObject<PlacementModifierType<ShallowWaterPlacement>> SHALLOW_WATER = register("shallow_water", () -> ShallowWaterPlacement.CODEC);
    public static final RegistryObject<PlacementModifierType<OnTopPlacement>> ON_TOP = register("on_top", () -> OnTopPlacement.CODEC);
    public static final RegistryObject<PlacementModifierType<BiomePlacement>> BIOME = register("biome", () -> BiomePlacement.CODEC);

    private static <C extends PlacementModifier> RegistryObject<PlacementModifierType<C>> register(String name, PlacementModifierType<C> codec)
    {
        return PLACEMENT_MODIFIERS.register(name, () -> codec);
    }
}
