/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes.ingredients;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.common.crafting.ICustomIngredient;
import net.neoforged.neoforge.common.crafting.IngredientType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.util.registry.RegistryHolder;

public final class TFCIngredients
{
    public static final DeferredRegister<IngredientType<?>> TYPES = DeferredRegister.create(NeoForgeRegistries.INGREDIENT_TYPES, TerraFirmaCraft.MOD_ID);

    public static final Id<RottenIngredient> ROTTEN = register("rotten", RottenIngredient.CODEC, RottenIngredient.STREAM_CODEC);
    public static final Id<NotRottenIngredient> NOT_ROTTEN = register("not_rotten", NotRottenIngredient.CODEC, NotRottenIngredient.STREAM_CODEC);
    public static final Id<HasTraitIngredient> HAS_TRAIT = register("has_trait", HasTraitIngredient.CODEC, HasTraitIngredient.STREAM_CODEC);
    public static final Id<LacksTraitIngredient> LACKS_TRAIT = register("lacks_trait", LacksTraitIngredient.CODEC, LacksTraitIngredient.STREAM_CODEC);
    public static final Id<HeatIngredient> HEAT = register("heat", HeatIngredient.CODEC, HeatIngredient.STREAM_CODEC);
    public static final Id<FluidContentIngredient> FLUID_CONTENT = register("fluid_content", FluidContentIngredient.CODEC, FluidContentIngredient.STREAM_CODEC);

    public static final Id<AndIngredient> AND = register("and", AndIngredient.CODEC, AndIngredient.STREAM_CODEC);

    private static <T extends ICustomIngredient> Id<T> register(String name, MapCodec<T> codec, StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec)
    {
        return new Id<>(TYPES.register(name, () -> new IngredientType<>(codec, streamCodec)));
    }

    record Id<T extends ICustomIngredient>(DeferredHolder<IngredientType<?>, IngredientType<T>> holder)
        implements RegistryHolder<IngredientType<?>, IngredientType<T>> {}
}
