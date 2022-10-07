/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes.ingredients;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.IIngredientSerializer;

import net.dries007.tfc.common.capabilities.food.FoodCapability;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.JsonHelpers;
import org.jetbrains.annotations.Nullable;

/**
 * An ingredient which respects non-rotten foods
 */
public class NotRottenIngredient extends DelegateIngredient
{
    public static NotRottenIngredient of(Ingredient ingredient)
    {
        return new NotRottenIngredient(ingredient);
    }

    protected NotRottenIngredient(@Nullable Ingredient delegate)
    {
        super(delegate);
    }

    @Override
    public boolean test(@Nullable ItemStack stack)
    {
        return super.test(stack) && stack != null && stack.getCapability(FoodCapability.CAPABILITY).map(cap -> !cap.isRotten()).orElse(false);
    }

    @Override
    public IIngredientSerializer<? extends DelegateIngredient> getSerializer()
    {
        return Serializer.INSTANCE;
    }

    @Nullable
    @Override
    protected ItemStack testDefaultItem(ItemStack stack)
    {
        return stack.getCapability(FoodCapability.CAPABILITY).map(food -> {
            food.setNonDecaying();
            return stack;
        }).orElse(null);
    }

    public enum Serializer implements IIngredientSerializer<NotRottenIngredient>
    {
        INSTANCE;

        @Override
        public NotRottenIngredient parse(JsonObject json)
        {
            final Ingredient internal = json.has("ingredient") ? Ingredient.fromJson(JsonHelpers.get(json, "ingredient")) : null;
            return new NotRottenIngredient(internal);
        }

        @Override
        public NotRottenIngredient parse(FriendlyByteBuf buffer)
        {
            return new NotRottenIngredient(Helpers.decodeNullable(buffer, Ingredient::fromNetwork));
        }

        @Override
        public void write(FriendlyByteBuf buffer, NotRottenIngredient ingredient)
        {
            Helpers.encodeNullable(ingredient.delegate, buffer, Ingredient::toNetwork);
        }
    }
}
