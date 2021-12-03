/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.decorator;

import java.util.function.Function;
import java.util.function.Supplier;

import net.minecraft.core.Registry;
import net.minecraft.world.level.levelgen.feature.configurations.DecoratorConfiguration;
import net.minecraft.world.level.levelgen.placement.FeatureDecorator;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import com.mojang.serialization.Codec;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

@SuppressWarnings("unused")
public final class TFCDecorators
{
    // public static final DeferredRegister<PlacementModifierType<?>> PLACEMENT_MODIFIERS = DeferredRegister.create(ForgeRegistries.PLACEMENT_MODIFIERS, MOD_ID);

    public static final Supplier<PlacementModifierType<FlatEnoughDecorator>> FLAT_ENOUGH = register("flat_enough", FlatEnoughDecorator.CODEC);
    public static final Supplier<PlacementModifierType<ClimateDecorator>> CLIMATE = register("climate", ClimateDecorator.CODEC);
    public static final Supplier<PlacementModifierType<NearWaterDecorator>> NEAR_WATER = register("near_water", NearWaterDecorator.CODEC);
    public static final Supplier<PlacementModifierType<TFCCarvingMaskDecorator>> CARVING_MASK = register("carving_mask", TFCCarvingMaskDecorator.CODEC);
    public static final Supplier<PlacementModifierType<VolcanoDecorator>> VOLCANO = register("volcano", VolcanoDecorator.CODEC);

    private static <C extends PlacementModifier> Supplier<PlacementModifierType<C>> register(String name, Codec<C> codec)
    {
        // todo: move to DR when forge converts placement modifiers to a registry
        return Lazy.of(() -> Registry.register(Registry.PLACEMENT_MODIFIERS, name, () -> codec));
    }
}
