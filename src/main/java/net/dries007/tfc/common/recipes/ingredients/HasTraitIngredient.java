/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes.ingredients;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.IIngredientSerializer;

import net.dries007.tfc.common.capabilities.food.FoodCapability;
import net.dries007.tfc.common.capabilities.food.FoodTrait;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.JsonHelpers;
import org.jetbrains.annotations.Nullable;

public class HasTraitIngredient extends DelegateIngredient
{
    private final FoodTrait trait;

    public HasTraitIngredient(@Nullable Ingredient delegate, FoodTrait trait)
    {
        super(delegate);
        this.trait = trait;
    }

    @Override
    public boolean test(@Nullable ItemStack stack)
    {
        return super.test(stack) && stack != null && stack.getCapability(FoodCapability.CAPABILITY).map(f -> f.getTraits().contains(trait)).orElse(false);
    }

    @Override
    public IIngredientSerializer<? extends Ingredient> getSerializer()
    {
        return Serializer.INSTANCE;
    }

    public enum Serializer implements IIngredientSerializer<HasTraitIngredient>
    {
        INSTANCE;

        @Override
        public HasTraitIngredient parse(JsonObject json)
        {
            final Ingredient internal = json.has("ingredient") ? Ingredient.fromJson(JsonHelpers.get(json, "ingredient")) : null;
            final FoodTrait trait = FoodTrait.getTraitOrThrow(new ResourceLocation(JsonHelpers.getAsString(json, "trait")));
            return new HasTraitIngredient(internal, trait);
        }

        @Override
        public HasTraitIngredient parse(FriendlyByteBuf buffer)
        {
            final Ingredient internal = Helpers.decodeNullable(buffer, Ingredient::fromNetwork);
            final FoodTrait trait = FoodTrait.getTraitOrThrow(new ResourceLocation(buffer.readUtf()));
            return new HasTraitIngredient(internal, trait);
        }

        @Override
        public void write(FriendlyByteBuf buffer, HasTraitIngredient ingredient)
        {
            Helpers.encodeNullable(ingredient.delegate, buffer, Ingredient::toNetwork);
            buffer.writeResourceLocation(FoodTrait.getId(ingredient.trait));
        }
    }
}
