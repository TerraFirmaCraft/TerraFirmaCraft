/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes;

import javax.annotation.Nullable;

import com.google.gson.JsonObject;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

import net.dries007.tfc.common.container.KnappingContainer;

public class KnappingRecipe implements ISimpleRecipe<KnappingContainer>
{
    protected final ResourceLocation id;
    protected final KnappingPattern matrix;
    protected final ItemStack result;
    protected final TypedRecipeSerializer<?> serializer;

    public KnappingRecipe(ResourceLocation id, KnappingPattern matrix, ItemStack result, TypedRecipeSerializer<?> serializer)
    {
        this.id = id;
        this.matrix = matrix;
        this.result = result;
        this.serializer = serializer;
    }

    @Override
    public boolean matches(KnappingContainer container, Level level)
    {
        return container.getMatrix().matches(this.matrix);
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

    public static class Serializer extends TypedRecipeSerializer<KnappingRecipe>
    {
        private final RecipeType<?> type;

        public Serializer(RecipeType<?> type)
        {
            this.type = type;
        }

        @Override
        public KnappingRecipe fromJson(ResourceLocation id, JsonObject json)
        {
            final ItemStack stack = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "result"));
            return new KnappingRecipe(id, KnappingPattern.fromJson(json.getAsJsonObject("matrix")), stack, this);
        }

        @Nullable
        @Override
        public KnappingRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buffer)
        {
            final boolean outsideSlotRequired = buffer.readBoolean();
            final KnappingPattern matrix = KnappingPattern.fromNetwork(buffer, outsideSlotRequired);
            final ItemStack stack = buffer.readItem();
            return new KnappingRecipe(id, matrix, stack, this);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, KnappingRecipe recipe)
        {
            buffer.writeBoolean(recipe.matrix.outsideSlot);
            recipe.matrix.toNetwork(buffer, recipe.matrix.getWidth(), recipe.matrix.getHeight());
            buffer.writeItem(recipe.getResultItem());
        }

        @Override
        public RecipeType<?> getRecipeType()
        {
            return type;
        }
    }
}
