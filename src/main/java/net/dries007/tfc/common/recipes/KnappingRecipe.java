/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes;

import java.util.function.Supplier;
import com.google.gson.JsonObject;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.container.KnappingContainer;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.JsonHelpers;
import net.dries007.tfc.util.KnappingPattern;
import net.dries007.tfc.util.KnappingType;

public class KnappingRecipe implements ISimpleRecipe<KnappingContainer.Query>
{
    private final ResourceLocation id;
    private final KnappingPattern pattern;
    private final ItemStack result;
    private final @Nullable Ingredient ingredient;
    private final Supplier<KnappingType> knappingType;

    public KnappingRecipe(ResourceLocation id, KnappingPattern pattern, ItemStack result, @Nullable Ingredient ingredient, Supplier<KnappingType> knappingType)
    {
        this.id = id;
        this.pattern = pattern;
        this.result = result;
        this.ingredient = ingredient;
        this.knappingType = knappingType;
    }

    @Override
    public boolean matches(KnappingContainer.Query query, Level level)
    {
        return query.container().getKnappingType() == knappingType.get()
            && query.container().getPattern().matches(getPattern())
            && matchesItem(query.container().getOriginalStack());
    }

    public boolean matchesItem(ItemStack stack)
    {
        return ingredient == null || ingredient.test(stack);
    }

    @Override
    public ItemStack getResultItem(@Nullable RegistryAccess access)
    {
        return result;
    }

    @Override
    public ResourceLocation getId()
    {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer()
    {
        return TFCRecipeSerializers.KNAPPING.get();
    }

    @Override
    public RecipeType<?> getType()
    {
        return TFCRecipeTypes.KNAPPING.get();
    }

    public KnappingPattern getPattern()
    {
        return pattern;
    }

    @Nullable
    public Ingredient getIngredient()
    {
        return ingredient;
    }

    public KnappingType getKnappingType()
    {
        return knappingType.get();
    }

    public static class Serializer extends RecipeSerializerImpl<KnappingRecipe>
    {
        @Override
        public KnappingRecipe fromJson(ResourceLocation recipeId, JsonObject json)
        {
            final Supplier<KnappingType> knappingType = JsonHelpers.getReference(json, "knapping_type", KnappingType.MANAGER);
            final ItemStack result = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "result"));
            final @Nullable Ingredient ingredient = json.has("ingredient") ? Ingredient.fromJson(json.get("ingredient")) : null;
            final KnappingPattern pattern = KnappingPattern.fromJson(json);
            return new KnappingRecipe(recipeId, pattern, result, ingredient, knappingType);
        }

        @Nullable
        @Override
        public KnappingRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer)
        {
            final KnappingPattern pattern = KnappingPattern.fromNetwork(buffer);
            final ItemStack stack = buffer.readItem();
            final @Nullable Ingredient ingredient = Helpers.decodeNullable(buffer, Ingredient::fromNetwork);
            final Supplier<KnappingType> knappingType = KnappingType.MANAGER.getReference(buffer.readResourceLocation());
            return new KnappingRecipe(recipeId, pattern, stack, ingredient, knappingType);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, KnappingRecipe recipe)
        {
            recipe.getPattern().toNetwork(buffer);
            buffer.writeItem(recipe.result);
            Helpers.encodeNullable(recipe.ingredient, buffer, Ingredient::toNetwork);
            buffer.writeResourceLocation(recipe.knappingType.get().getId());
        }
    }
}
