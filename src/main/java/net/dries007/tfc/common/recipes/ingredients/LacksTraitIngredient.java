/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes.ingredients;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.crafting.IngredientType;

import net.dries007.tfc.common.component.food.FoodCapability;
import net.dries007.tfc.common.component.food.FoodTrait;

public record LacksTraitIngredient(FoodTrait trait) implements PreciseIngredient
{
    public static final MapCodec<LacksTraitIngredient> CODEC = FoodTrait.CODEC.fieldOf("trait").xmap(LacksTraitIngredient::new, LacksTraitIngredient::trait);
    public static final StreamCodec<RegistryFriendlyByteBuf, LacksTraitIngredient> STREAM_CODEC = FoodTrait.STREAM_CODEC.map(LacksTraitIngredient::new, LacksTraitIngredient::trait);

    public static LacksTraitIngredient of(Holder<FoodTrait> trait)
    {
        return new LacksTraitIngredient(trait.value());
    }

    @Override
    public boolean test(ItemStack stack)
    {
        return !FoodCapability.hasTrait(stack, trait);
    }

    @Override
    public IngredientType<?> getType()
    {
        return TFCIngredients.LACKS_TRAIT.get();
    }
}
