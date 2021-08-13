/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes;

import javax.annotation.Nullable;

import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.container.KnappingContainer;

public class RockKnappingRecipe extends KnappingRecipe
{
    protected final Ingredient ingredient;

    public RockKnappingRecipe(ResourceLocation id, KnappingPattern matrix, ItemStack result, Ingredient ingredient)
    {
        super(id, matrix, result, TFCRecipeSerializers.ROCK_KNAPPING.get());
        this.ingredient = ingredient;
    }

    @Override
    public boolean matches(KnappingContainer container, World level)
    {
        return container.getMatrix().matches(this.matrix) && ingredient.test(container.getStackCopy());
    }

    public static class RockSerializer extends TypedRecipeSerializer<RockKnappingRecipe>
    {
        @Override
        public RockKnappingRecipe fromJson(ResourceLocation id, JsonObject json)
        {
            final ItemStack stack = ShapedRecipe.itemFromJson(JSONUtils.getAsJsonObject(json, "result"));
            final Ingredient ingredient = json.has("ingredient") ? Ingredient.fromJson(json.get("ingredient")) : Ingredient.of(TFCTags.Items.ROCK_KNAPPING);
            return new RockKnappingRecipe(id, KnappingPattern.fromJson(json.getAsJsonObject("matrix")), stack, ingredient);
        }

        @Nullable
        @Override
        public RockKnappingRecipe fromNetwork(ResourceLocation id, PacketBuffer buffer)
        {
            final boolean outsideSlotRequired = buffer.readBoolean();
            final KnappingPattern matrix = KnappingPattern.fromNetwork(buffer, outsideSlotRequired);
            final ItemStack stack = buffer.readItem();
            final Ingredient ingredient = Ingredient.fromNetwork(buffer);
            return new RockKnappingRecipe(id, matrix, stack, ingredient);
        }

        @Override
        public void toNetwork(PacketBuffer buffer, RockKnappingRecipe recipe)
        {
            buffer.writeBoolean(recipe.matrix.outsideSlot);
            recipe.matrix.toNetwork(buffer, recipe.matrix.getWidth(), recipe.matrix.getHeight());
            buffer.writeItem(recipe.getResultItem());
            recipe.ingredient.toNetwork(buffer);
        }

        @Override
        public IRecipeType<?> getRecipeType()
        {
            return TFCRecipeTypes.ROCK_KNAPPING;
        }
    }
}
