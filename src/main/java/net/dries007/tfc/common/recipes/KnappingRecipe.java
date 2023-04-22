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

public class KnappingRecipe implements ISimpleRecipe<KnappingContainer>
{
    private final ResourceLocation id;
    private final KnappingPattern pattern;
    private final ItemStack result;
    private final @Nullable Ingredient ingredient;
    private final KnappingType knappingType;

    public KnappingRecipe(ResourceLocation id, KnappingPattern pattern, ItemStack result, @Nullable Ingredient ingredient, KnappingType knappingType)
    {
        this.id = id;
        this.pattern = pattern;
        this.result = result;
        this.ingredient = ingredient;
        this.knappingType = knappingType;
    }

    @Override
    public boolean matches(KnappingContainer container, Level level)
    {
        return container.getKnappingType() == knappingType
            && container.getPattern().matches(getPattern())
            && matchesItem(container.getOriginalStack());
    }

    public boolean matchesItem(ItemStack stack)
    {
        return ingredient == null || ingredient.test(stack);
    }

    @Override
    public ItemStack getResultItem()
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

    public KnappingType getKnappingType()
    {
        return knappingType;
    }

    public KnappingPattern getPattern()
    {
        return pattern;
    }

    public static class Serializer extends RecipeSerializerImpl<KnappingRecipe>
    {
        @Override
        public KnappingRecipe fromJson(ResourceLocation recipeId, JsonObject json)
        {
            final KnappingType knappingType = JsonHelpers.getFrom(json, "knapping_type", KnappingType.MANAGER);
            return fromJson(recipeId, json, knappingType);
        }

        private KnappingRecipe fromJson(ResourceLocation recipeId, JsonObject json, KnappingType knappingType)
        {
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
            final KnappingType knappingType = KnappingType.MANAGER.getOrThrow(buffer.readResourceLocation());
            return new KnappingRecipe(recipeId, pattern, stack, ingredient, knappingType);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, KnappingRecipe recipe)
        {
            recipe.getPattern().toNetwork(buffer);
            buffer.writeItem(recipe.getResultItem());
            Helpers.encodeNullable(recipe.ingredient, buffer, Ingredient::toNetwork);
            buffer.writeResourceLocation(recipe.knappingType.getId());
        }
    }

    /**
     * @deprecated Usages should be moved to {@link KnappingRecipe.Serializer}, and recipes should use the {@code tfc:knapping} type instead.
     */
    @Deprecated(forRemoval = true)
    public static class LegacySerializer extends RecipeSerializerImpl<KnappingRecipe>
    {
        private final ResourceLocation knappingTypeId;

        public LegacySerializer(ResourceLocation knappingTypeId)
        {
            this.knappingTypeId = knappingTypeId;
        }

        @Override
        public KnappingRecipe fromJson(ResourceLocation id, JsonObject json)
        {
            return TFCRecipeSerializers.KNAPPING.get().fromJson(id, json, KnappingType.MANAGER.getOrThrow(knappingTypeId));
        }

        @Nullable
        @Override
        public KnappingRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buffer)
        {
            throw new IllegalStateException("Should not use legacy serializer for network read");
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, KnappingRecipe recipe)
        {
            throw new IllegalStateException("Should not use legacy serializer for network write");
        }
    }
}
