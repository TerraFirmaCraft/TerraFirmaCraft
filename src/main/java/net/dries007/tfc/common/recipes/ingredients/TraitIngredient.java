/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes.ingredients;

import java.util.function.BiFunction;
import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.IIngredientSerializer;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.capabilities.food.FoodTrait;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.JsonHelpers;

public abstract class TraitIngredient extends DelegateIngredient
{
    protected final FoodTrait trait;

    public TraitIngredient(@Nullable Ingredient delegate, FoodTrait trait)
    {
        super(delegate);
        this.trait = trait;
    }

    public FoodTrait getTrait()
    {
        return trait;
    }

    @Override
    public JsonObject toJson()
    {
        final JsonObject json = super.toJson();
        json.addProperty("trait", FoodTrait.getId(trait).toString());
        return json;
    }

    public static class TraitSerializer<T extends TraitIngredient> implements IIngredientSerializer<T>
    {
        public static final TraitSerializer<HasTraitIngredient> HAS_TRAIT = new HasTraitIngredient.TraitSerializer<>(HasTraitIngredient::new);
        public static final TraitSerializer<LacksTraitIngredient> LACKS_TRAIT = new HasTraitIngredient.TraitSerializer<>(LacksTraitIngredient::new);

        private final BiFunction<Ingredient, FoodTrait, T> factory;

        public TraitSerializer(BiFunction<Ingredient, FoodTrait, T> factory)
        {
            this.factory = factory;
        }

        @Override
        public T parse(JsonObject json)
        {
            final Ingredient internal = json.has("ingredient") ? Ingredient.fromJson(JsonHelpers.get(json, "ingredient")) : null;
            final FoodTrait trait = FoodTrait.getTraitOrThrow(JsonHelpers.getResourceLocation(json, "trait"));
            return factory.apply(internal, trait);
        }

        @Override
        public T parse(FriendlyByteBuf buffer)
        {
            final Ingredient internal = Helpers.decodeNullable(buffer, Ingredient::fromNetwork);
            final FoodTrait trait = FoodTrait.getTraitOrThrow(buffer.readResourceLocation());
            return factory.apply(internal, trait);
        }

        @Override
        public void write(FriendlyByteBuf buffer, T ingredient)
        {
            Helpers.encodeNullable(ingredient.delegate, buffer, Ingredient::toNetwork);
            buffer.writeResourceLocation(FoodTrait.getId(ingredient.getTrait()));
        }
    }
}
