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

public final class ItemStackIngredient implements Predicate<ItemStack>
{
    public static ItemStackIngredient fromJson(JsonObject json)
    {
        return new ItemStackIngredient(json);
    }

    public static ItemStackIngredient fromNetwork(FriendlyByteBuf buffer)
    {
        return new ItemStackIngredient(buffer);
    }

    private final Ingredient item;
    private final int count;

    private ItemStackIngredient(JsonObject json)
    {
        this.item = Ingredient.fromJson(JsonHelpers.get(json, "item"));
        this.count = JsonHelpers.getAsInt(json, "count");
    }

    private ItemStackIngredient(FriendlyByteBuf buffer)
    {
        this.item = Ingredient.fromNetwork(buffer);
        this.count = buffer.readVarInt();
    }

    @Override
    public boolean test(ItemStack stack)
    {
        return stack.getCount() >= count && item.test(stack);
    }

    public void toNetwork(FriendlyByteBuf buffer)
    {
        item.toNetwork(buffer);
        buffer.writeVarInt(count);
    }

    public int getCount()
    {
        return count;
    }

    public Ingredient getItem()
    {
        return item;
    }
}
