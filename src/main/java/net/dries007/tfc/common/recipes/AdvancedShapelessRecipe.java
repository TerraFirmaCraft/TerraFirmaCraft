/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.world.level.Level;

import com.mojang.datafixers.util.Function5;
import net.dries007.tfc.common.recipes.outputs.ItemStackProvider;
import net.dries007.tfc.util.JsonHelpers;
import org.jetbrains.annotations.Nullable;

/**
 * A shaped recipe type which uses {@link ItemStackProvider} as it's output mechanism
 * The seed ingredient determines which ItemStack from the inventory will be used to compute the output
 * There is a requirement that an ingredient in the inventory matches the seed ingredient
 * Any number of items may match the seed ingredient, but only the first is used.
 */
public class AdvancedShapelessRecipe extends ShapelessRecipe
{
    protected final ItemStackProvider result;
    protected final Ingredient primaryIngredient;

    public AdvancedShapelessRecipe(ResourceLocation id, String group, ItemStackProvider result, NonNullList<Ingredient> ingredients, Ingredient primaryIngredient)
    {
        super(id, group, result.getEmptyStack(), ingredients);
        this.result = result;
        this.primaryIngredient = primaryIngredient;
    }

    @Override
    public boolean matches(CraftingContainer inv, Level level)
    {
        return super.matches(inv, level) && !getSeed(inv).isEmpty();
    }

    @Override
    public ItemStack assemble(CraftingContainer inv)
    {
        RecipeHelpers.setCraftingContainer(inv);
        return result.getSingleStack(getSeed(inv).copy());
    }

    public ItemStackProvider getResult()
    {
        return result;
    }

    public Ingredient getPrimaryIngredient()
    {
        return primaryIngredient;
    }

    private ItemStack getSeed(CraftingContainer inv)
    {
        for (int i = 0; i < inv.getContainerSize(); i++)
        {
            ItemStack item = inv.getItem(i);
            if (primaryIngredient.test(item))
            {
                return item;
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public RecipeSerializer<?> getSerializer()
    {
        return TFCRecipeSerializers.ADVANCED_SHAPELESS_CRAFTING.get();
    }

    public static class Serializer extends RecipeSerializerImpl<AdvancedShapelessRecipe>
    {
        @Override
        public AdvancedShapelessRecipe fromJson(ResourceLocation id, JsonObject json)
        {
            final String group = JsonHelpers.getAsString(json, "group", "");
            final NonNullList<Ingredient> ingredients = RecipeHelpers.itemsFromJson(JsonHelpers.getAsJsonArray(json, "ingredients"));
            if (ingredients.isEmpty() || ingredients.size() > 3 * 3)
            {
                throw new JsonParseException("ingredients should be 1 to 9 ingredients long, it was: " + ingredients.size());
            }
            final ItemStackProvider result = ItemStackProvider.fromJson(JsonHelpers.getAsJsonObject(json, "result"));
            final Ingredient primaryIngredient = Ingredient.fromJson(json.get("primary_ingredient"));
            return new AdvancedShapelessRecipe(id, group, result, ingredients, primaryIngredient);
        }

        @Nullable
        @Override
        public AdvancedShapelessRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buffer)
        {
            final String group = buffer.readUtf();
            final int size = buffer.readVarInt();
            final NonNullList<Ingredient> ingredients = NonNullList.withSize(size, Ingredient.EMPTY);
            for (int j = 0; j < ingredients.size(); ++j)
            {
                ingredients.set(j, Ingredient.fromNetwork(buffer));
            }
            final ItemStackProvider result = ItemStackProvider.fromNetwork(buffer);
            final Ingredient primaryIngredient = Ingredient.fromNetwork(buffer);
            return new AdvancedShapelessRecipe(id, group, result, ingredients, primaryIngredient);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, AdvancedShapelessRecipe recipe)
        {
            buffer.writeUtf(recipe.getGroup());
            buffer.writeVarInt(recipe.getIngredients().size());
            for (Ingredient ingredient : recipe.getIngredients())
            {
                ingredient.toNetwork(buffer);
            }
            recipe.result.toNetwork(buffer);
            recipe.primaryIngredient.toNetwork(buffer);
        }
    }
}
