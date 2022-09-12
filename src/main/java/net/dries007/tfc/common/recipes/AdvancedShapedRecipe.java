/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes;

import java.util.Map;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;

import net.dries007.tfc.common.recipes.outputs.ItemStackProvider;
import net.dries007.tfc.util.JsonHelpers;

/**
 * A shaped recipe type which uses {@link ItemStackProvider} as it's output mechanism
 * It also requires that the recipe specify which (of the crafting grid) inputs is responsible for the item stack provider's "input" stack.
 */
public class AdvancedShapedRecipe extends ShapedRecipe
{
    private final ItemStackProvider providerResult;
    private final int inputSlot;

    public AdvancedShapedRecipe(ResourceLocation id, String group, int width, int height, NonNullList<Ingredient> recipeItems, ItemStackProvider result, int inputSlot)
    {
        super(id, group, width, height, recipeItems, ItemStack.EMPTY);

        this.providerResult = result;
        this.inputSlot = inputSlot;
    }

    @Override
    public ItemStack getResultItem()
    {
        return providerResult.getEmptyStack();
    }

    @Override
    public ItemStack assemble(CraftingContainer inventory)
    {
        RecipeHelpers.setCraftingContainer(inventory);
        final int matchSlot = RecipeHelpers.translateMatch(this, inputSlot, inventory);
        final ItemStack inputStack = matchSlot != -1 ? inventory.getItem(matchSlot).copy() : ItemStack.EMPTY;
        final ItemStack result = providerResult.getSingleStack(inputStack);
        RecipeHelpers.setCraftingContainer(null);
        return result;
    }

    @Override
    public boolean isSpecial()
    {
        return providerResult.dependsOnInput();
    }

    @Override
    public RecipeSerializer<?> getSerializer()
    {
        return TFCRecipeSerializers.ADVANCED_SHAPED_CRAFTING.get();
    }

    public static class Serializer extends RecipeSerializerImpl<AdvancedShapedRecipe>
    {
        @Override
        public AdvancedShapedRecipe fromJson(ResourceLocation recipeId, JsonObject json)
        {
            final String group = GsonHelper.getAsString(json, "group", "");
            final Map<String, Ingredient> keys = RecipeHelpers.keyFromJson(GsonHelper.getAsJsonObject(json, "key"));
            final String[] pattern = RecipeHelpers.shrink(RecipeHelpers.patternFromJson(GsonHelper.getAsJsonArray(json, "pattern")));
            final int width = pattern[0].length();
            final int height = pattern.length;
            final NonNullList<Ingredient> recipeItems = RecipeHelpers.dissolvePattern(pattern, keys, width, height);
            final ItemStackProvider providerResult = ItemStackProvider.fromJson(JsonHelpers.getAsJsonObject(json, "result"));
            final int inputRow = JsonHelpers.getAsInt(json, "input_row");
            final int inputCol = JsonHelpers.getAsInt(json, "input_column");
            if (inputRow < 0 || inputRow >= width)
            {
                throw new JsonParseException("input_row must be in the range [0, width)");
            }
            if (inputCol < 0 || inputCol >= height)
            {
                throw new JsonParseException("input_column must be in the range [0, height)");
            }
            final int inputSlot = RecipeHelpers.dissolveRowColumn(inputRow, inputCol, width);
            return new AdvancedShapedRecipe(recipeId, group, width, height, recipeItems, providerResult, inputSlot);
        }

        @Override
        public AdvancedShapedRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer)
        {
            final int width = buffer.readVarInt();
            final int height = buffer.readVarInt();
            final String group = buffer.readUtf();
            final NonNullList<Ingredient> recipeItems = NonNullList.withSize(width * height, Ingredient.EMPTY);

            for (int k = 0; k < recipeItems.size(); ++k)
            {
                recipeItems.set(k, Ingredient.fromNetwork(buffer));
            }

            final ItemStackProvider provider = ItemStackProvider.fromNetwork(buffer);
            final int inputSlot = buffer.readVarInt();
            return new AdvancedShapedRecipe(recipeId, group, width, height, recipeItems, provider, inputSlot);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, AdvancedShapedRecipe recipe)
        {
            buffer.writeVarInt(recipe.getWidth());
            buffer.writeVarInt(recipe.getHeight());
            buffer.writeUtf(recipe.getGroup());

            for (Ingredient ingredient : recipe.getIngredients())
            {
                ingredient.toNetwork(buffer);
            }

            recipe.providerResult.toNetwork(buffer);
            buffer.writeVarInt(recipe.inputSlot);
        }
    }
}
