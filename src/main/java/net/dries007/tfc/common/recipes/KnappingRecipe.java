/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes;

import javax.annotation.Nullable;

import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

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
    public boolean matches(KnappingContainer container, World level)
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
    public IRecipeSerializer<?> getSerializer()
    {
        return serializer;
    }

    @Override
    public IRecipeType<?> getType()
    {
        return serializer.getRecipeType();
    }

    public static class Serializer extends TypedRecipeSerializer<KnappingRecipe>
    {
        private final IRecipeType<?> type;

        public Serializer(IRecipeType<?> type)
        {
            this.type = type;
        }

        @Override
        public KnappingRecipe fromJson(ResourceLocation id, JsonObject json)
        {
            final ItemStack stack = ShapedRecipe.itemFromJson(JSONUtils.getAsJsonObject(json, "result"));
            return new KnappingRecipe(id, KnappingPattern.fromJson(json.getAsJsonObject("matrix")), stack, this);
        }

        @Nullable
        @Override
        public KnappingRecipe fromNetwork(ResourceLocation id, PacketBuffer buffer)
        {
            final boolean outsideSlotRequired = buffer.readBoolean();
            final KnappingPattern matrix = KnappingPattern.fromNetwork(buffer, outsideSlotRequired);
            final ItemStack stack = buffer.readItem();
            return new KnappingRecipe(id, matrix, stack, this);
        }

        @Override
        public void toNetwork(PacketBuffer buffer, KnappingRecipe recipe)
        {
            buffer.writeBoolean(recipe.matrix.outsideSlot);
            recipe.matrix.toNetwork(buffer, recipe.matrix.getWidth(), recipe.matrix.getHeight());
            buffer.writeItem(recipe.getResultItem());
        }

        @Override
        public IRecipeType<?> getRecipeType()
        {
            return type;
        }
    }
}
