/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes;

import java.util.function.Supplier;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.Level;

import net.dries007.tfc.common.container.KnappingContainer;
import net.dries007.tfc.util.KnappingPattern;
import org.jetbrains.annotations.Nullable;

public class KnappingRecipe implements ISimpleRecipe<KnappingContainer.Query>
{
    protected final ResourceLocation id;
    protected final KnappingPattern pattern;
    protected final ItemStack result;
    protected final TypedRecipeSerializer<?> serializer;

    public KnappingRecipe(ResourceLocation id, KnappingPattern pattern, ItemStack result, TypedRecipeSerializer<?> serializer)
    {
        this.id = id;
        this.pattern = pattern;
        this.result = result;
        this.serializer = serializer;
    }

    @Override
    public boolean matches(KnappingContainer.Query query, Level level)
    {
        return query.container().getPattern().matches(getPattern());
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
        return serializer;
    }

    @Override
    public RecipeType<?> getType()
    {
        return serializer.getRecipeType();
    }

    public KnappingPattern getPattern()
    {
        return pattern;
    }

    public static class Serializer extends TypedRecipeSerializer<KnappingRecipe>
    {
        private final Supplier<RecipeType<KnappingRecipe>> type;

        public Serializer(Supplier<RecipeType<KnappingRecipe>> type)
        {
            this.type = type;
        }

        @Override
        public KnappingRecipe fromJson(ResourceLocation id, JsonObject json)
        {
            final ItemStack stack = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "result"));
            return new KnappingRecipe(id, KnappingPattern.fromJson(json), stack, this);
        }

        @Nullable
        @Override
        public KnappingRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buffer)
        {
            final KnappingPattern pattern = KnappingPattern.fromNetwork(buffer);
            final ItemStack stack = buffer.readItem();
            return new KnappingRecipe(id, pattern, stack, this);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, KnappingRecipe recipe)
        {
            recipe.getPattern().toNetwork(buffer);
            buffer.writeItem(recipe.getResultItem());
        }

        @Override
        public RecipeType<?> getRecipeType()
        {
            return type.get();
        }
    }
}
