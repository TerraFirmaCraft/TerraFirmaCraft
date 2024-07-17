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

import net.dries007.tfc.common.capabilities.food.FoodCapability;
import net.dries007.tfc.common.capabilities.food.FoodTrait;

public record HasTraitIngredient(FoodTrait trait) implements PreciseIngredient
{
    public static final MapCodec<HasTraitIngredient> CODEC = FoodTrait.CODEC.fieldOf("trait").xmap(HasTraitIngredient::new, HasTraitIngredient::trait);
    public static final StreamCodec<RegistryFriendlyByteBuf, HasTraitIngredient> STREAM_CODEC = FoodTrait.STREAM_CODEC.map(HasTraitIngredient::new, HasTraitIngredient::trait);

    public static HasTraitIngredient of(Holder<FoodTrait> trait)
    {
        return new HasTraitIngredient(trait.value());
    }

    @Override
    public boolean test(ItemStack stack)
    {
        return FoodCapability.hasTrait(stack, trait);
    }

    @Override
    public ItemStack modifyStackForDisplay(ItemStack stack)
    {
        return FoodCapability.applyTrait(stack, trait);
    }

    @Override
    public IngredientType<?> getType()
    {
        return TFCIngredients.HAS_TRAIT.get();
    }
}
