/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.placement;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import static net.dries007.tfc.TerraFirmaCraft.*;


/**
 * <strong>Note</strong>: the reason placement modifier codecs are named {@code PLACEMENT_CODEC} is to avoid a shadowing issue with obfuscation.
 * Naming them {@code CODEC} may shadow {@link PlacementModifier#CODEC}, which can lead to questionable obfuscation situations where the field gets renamed.
 * <p>
 * todo 1.21: This goes away with runtime mojmap, aka Neo
 */
@SuppressWarnings("unused")
public final class TFCPlacements
{
    public static final DeferredRegister<PlacementModifierType<?>> PLACEMENT_MODIFIERS = DeferredRegister.create(Registries.PLACEMENT_MODIFIER_TYPE, MOD_ID);

    public static final RegistryObject<PlacementModifierType<FlatEnoughPlacement>> FLAT_ENOUGH = register("flat_enough", () -> FlatEnoughPlacement.PLACEMENT_CODEC);
    public static final RegistryObject<PlacementModifierType<UndergroundPlacement>> UNDERGROUND = register("underground", () -> UndergroundPlacement.PLACEMENT_CODEC);
    public static final RegistryObject<PlacementModifierType<BoundedCarvingMaskPlacement>> CARVING_MASK = register("carving_mask", () -> BoundedCarvingMaskPlacement.PLACEMENT_CODEC);
    public static final RegistryObject<PlacementModifierType<ClimatePlacement>> CLIMATE = register("climate", () -> ClimatePlacement.PLACEMENT_CODEC);
    public static final RegistryObject<PlacementModifierType<VolcanoPlacement>> VOLCANO = register("volcano", () -> VolcanoPlacement.PLACEMENT_CODEC);
    public static final RegistryObject<PlacementModifierType<NearFluidPlacement>> NEAR_FLUID = register("near_fluid", () -> NearFluidPlacement.PLACEMENT_CODEC);
    public static final RegistryObject<PlacementModifierType<ShallowWaterPlacement>> SHALLOW_WATER = register("shallow_water", () -> ShallowWaterPlacement.PLACEMENT_CODEC);
    public static final RegistryObject<PlacementModifierType<OnTopPlacement>> ON_TOP = register("on_top", () -> OnTopPlacement.PLACEMENT_CODEC);
    public static final RegistryObject<PlacementModifierType<BiomePlacement>> BIOME = register("biome", () -> BiomePlacement.PLACEMENT_CODEC);
    public static final RegistryObject<PlacementModifierType<NoSolidNeighborsPlacement>> NO_SOLID_NEIGHBORS = register("no_solid_neighbors", ()-> NoSolidNeighborsPlacement.PLACEMENT_CODEC);

    private static <C extends PlacementModifier> RegistryObject<PlacementModifierType<C>> register(String name, PlacementModifierType<C> codec)
    {
        return PLACEMENT_MODIFIERS.register(name, () -> codec);
    }
}
