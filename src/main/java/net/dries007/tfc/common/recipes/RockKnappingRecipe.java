/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.Level;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.container.KnappingContainer;
import net.dries007.tfc.util.KnappingPattern;
import org.jetbrains.annotations.Nullable;

public class RockKnappingRecipe extends KnappingRecipe
{
    protected final Ingredient ingredient;

    public RockKnappingRecipe(ResourceLocation id, KnappingPattern matrix, ItemStack result, Ingredient ingredient)
    {
        super(id, matrix, result, TFCRecipeSerializers.ROCK_KNAPPING.get());
        this.ingredient = ingredient;
    }

    public Ingredient getIngredient()
    {
        return ingredient;
    }

    @Override
    public boolean matches(KnappingContainer.Query query, Level level)
    {
        return query.container().getPattern().matches(this.getPattern()) && ingredient.test(query.container().getOriginalStack());
    }

    public boolean matchesItem(ItemStack stack)
    {
        return ingredient.test(stack);
    }

    public static class RockSerializer extends TypedRecipeSerializer<RockKnappingRecipe>
    {
        @Override
        public RockKnappingRecipe fromJson(ResourceLocation id, JsonObject json)
        {
            final ItemStack stack = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "result"));
            final Ingredient ingredient = json.has("ingredient") ? Ingredient.fromJson(json.get("ingredient")) : Ingredient.of(TFCTags.Items.ROCK_KNAPPING);
            return new RockKnappingRecipe(id, KnappingPattern.fromJson(json), stack, ingredient);
        }

        @Nullable
        @Override
        public RockKnappingRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buffer)
        {
            final KnappingPattern pattern = KnappingPattern.fromNetwork(buffer);
            final ItemStack stack = buffer.readItem();
            final Ingredient ingredient = Ingredient.fromNetwork(buffer);
            return new RockKnappingRecipe(id, pattern, stack, ingredient);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, RockKnappingRecipe recipe)
        {
            recipe.getPattern().toNetwork(buffer);
            buffer.writeItem(recipe.getResultItem());
            recipe.ingredient.toNetwork(buffer);
        }

        @Override
        public RecipeType<?> getRecipeType()
        {
            return TFCRecipeTypes.ROCK_KNAPPING.get();
        }
    }
}
