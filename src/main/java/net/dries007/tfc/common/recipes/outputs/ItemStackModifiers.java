/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes.outputs;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.RegistryBuilder;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.registry.RegistryHolder;

public class ItemStackModifiers
{
    public static final ResourceKey<Registry<ItemStackModifierType<?>>> KEY = ResourceKey.createRegistryKey(Helpers.identifier("item_stack_modifiers"));
    public static final Registry<ItemStackModifierType<?>> REGISTRY = new RegistryBuilder<>(KEY).sync(true).create();

    public static final DeferredRegister<ItemStackModifierType<?>> TYPES = DeferredRegister.create(KEY, TerraFirmaCraft.MOD_ID);

    public static final Id<CopyInputModifier> COPY_INPUT = register("copy_input", CopyInputModifier.INSTANCE);
    public static final Id<CopyFoodModifier> COPY_FOOD = register("copy_food", CopyFoodModifier.INSTANCE);
    public static final Id<CopyOldestFoodModifier> COPY_OLDEST_FOOD = register("copy_oldest_food", CopyOldestFoodModifier.INSTANCE);
    public static final Id<CopyHeatModifier> COPY_HEAT = register("copy_heat", CopyHeatModifier.INSTANCE);
    public static final Id<CopyForgingBonusModifier> COPY_FORGING_BONUS = register("copy_forging_bonus", CopyForgingBonusModifier.INSTANCE);
    public static final Id<ResetFoodModifier> RESET_FOOD = register("reset_food", ResetFoodModifier.INSTANCE);
    public static final Id<EmptyBowlModifier> EMPTY_BOWL = register("empty_bowl", EmptyBowlModifier.INSTANCE);
    public static final Id<AddBaitToRodModifier> ADD_BAIT_TO_ROD = register("add_bait_to_rod", AddBaitToRodModifier.INSTANCE);
    public static final Id<AddGlassModifier> ADD_GLASS = register("add_glass", AddGlassModifier.INSTANCE);
    public static final Id<AddPowderModifier> ADD_POWDER = register("add_powder", AddPowderModifier.INSTANCE);
    public static final Id<CraftingRemainderModifier> CRAFTING_REMAINDER = register("crafting_remainder", CraftingRemainderModifier.INSTANCE);
    public static final Id<DamageCraftingRemainderModifier> DAMAGE_CRAFTING_REMAINDER = register("damage_crafting_remainder", DamageCraftingRemainderModifier.INSTANCE);

    public static final Id<AddTraitModifier> ADD_TRAIT = register("add_trait", AddTraitModifier.CODEC, AddTraitModifier.STREAM_CODEC);
    public static final Id<RemoveTraitModifier> REMOVE_TRAIT = register("remove_trait", RemoveTraitModifier.CODEC, RemoveTraitModifier.STREAM_CODEC);
    public static final Id<AddHeatModifier> ADD_HEAT = register("add_heat", AddHeatModifier.CODEC, AddHeatModifier.STREAM_CODEC);
    public static final Id<DyeLeatherModifier> DYE_LEATHER = register("dye_leather", DyeLeatherModifier.CODEC, DyeLeatherModifier.STREAM_CODEC);
    public static final Id<MealModifier> MEAL = register("meal", MealModifier.CODEC, MealModifier.STREAM_CODEC);
    public static final Id<ExtraProductModifier> EXTRA_PRODUCT = register("extra_products", ExtraProductModifier.CODEC, ExtraProductModifier.STREAM_CODEC);
    public static final Id<ChanceModifier> CHANCE = register("chance", ChanceModifier.CHANCE, ChanceModifier.STREAM_CODEC);

    private static <T extends ItemStackModifier> Id<T> register(String name, T singleInstance)
    {
        return new Id<>(TYPES.register(name, () -> new ItemStackModifierType<>(MapCodec.unit(singleInstance), StreamCodec.unit(singleInstance))));
    }

    private static <T extends ItemStackModifier> Id<T> register(String name, MapCodec<T> codec, StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec)
    {
        return new Id<>(TYPES.register(name, () -> new ItemStackModifierType<>(codec, streamCodec.cast())));
    }

    record Id<T extends ItemStackModifier>(DeferredHolder<ItemStackModifierType<?>, ItemStackModifierType<T>> holder)
        implements RegistryHolder<ItemStackModifierType<?>, ItemStackModifierType<T>> {}
}
