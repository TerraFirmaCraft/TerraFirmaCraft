/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes.ingredients;

import java.util.function.Predicate;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import net.dries007.tfc.util.JsonHelpers;

public record ItemStackIngredient(Ingredient ingredient, int count) implements Predicate<ItemStack>
{
    public static final ItemStackIngredient EMPTY = new ItemStackIngredient(Ingredient.EMPTY, 0);

    public static ItemStackIngredient fromJson(JsonObject json)
    {
        final Ingredient ingredient = Ingredient.fromJson(JsonHelpers.get(json, "ingredient"));
        final int count = JsonHelpers.getAsInt(json, "count", 1);
        return new ItemStackIngredient(ingredient, count);
    }

    public static ItemStackIngredient fromNetwork(FriendlyByteBuf buffer)
    {
        final Ingredient ingredient = Ingredient.fromNetwork(buffer);
        final int count = buffer.readVarInt();
        return new ItemStackIngredient(ingredient, count);
    }

    @Override
    public boolean test(ItemStack stack)
    {
        return ingredient.test(stack) && stack.getCount() >= count;
    }

    public void toNetwork(FriendlyByteBuf buffer)
    {
        ingredient.toNetwork(buffer);
        buffer.writeVarInt(count);
    }
}
