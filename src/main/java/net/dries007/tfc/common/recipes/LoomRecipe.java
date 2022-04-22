/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes;

import java.util.Objects;

import net.dries007.tfc.common.recipes.outputs.ItemStackProvider;
import org.jetbrains.annotations.Nullable;

import com.google.gson.JsonObject;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import net.dries007.tfc.common.recipes.inventory.ItemStackInventory;
import net.dries007.tfc.util.JsonHelpers;
import net.dries007.tfc.util.collections.IndirectHashCollection;

public class LoomRecipe extends SimpleItemRecipe
{
    public static final IndirectHashCollection<Item, LoomRecipe> CACHE = IndirectHashCollection.createForRecipe(LoomRecipe::getValidItems, TFCRecipeTypes.LOOM);

    @Nullable
    public static LoomRecipe getRecipe(Level world, ItemStackInventory wrapper)
    {
        for (LoomRecipe recipe : CACHE.getAll(wrapper.getStack().getItem()))
        {
            if (recipe.matches(wrapper, world))
            {
                return recipe;
            }
        }
        return null;
    }

    private final int inputCount;
    private final int stepsRequired;
    private final ResourceLocation inProgressTexture;


    public LoomRecipe(ResourceLocation id, Ingredient ingredient, ItemStackProvider result, int inputCount, int stepsRequired, ResourceLocation inProgressTexture)
    {
        super(id, ingredient, result);
        this.inputCount = inputCount;
        this.stepsRequired = stepsRequired;
        this.inProgressTexture = inProgressTexture;
    }

    @Override
    public RecipeSerializer<?> getSerializer()
    {
        return TFCRecipeSerializers.LOOM.get();
    }

    @Override
    public RecipeType<?> getType()
    {
        return TFCRecipeTypes.LOOM.get();
    }

    @Override
    public boolean isIncomplete()
    {
        return super.isIncomplete();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height)
    {
        return super.canCraftInDimensions(width, height);
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(ItemStackInventory inv)
    {
        return super.getRemainingItems(inv);
    }

    @Override
    public boolean isSpecial()
    {
        return super.isSpecial();
    }

    @Override
    public String getGroup()
    {
        return super.getGroup();
    }

    @Override
    public ItemStack getToastSymbol()
    {
        return super.getToastSymbol();
    }

    public int getInputCount()
    {
        return inputCount;
    }

    public ResourceLocation getInProgressTexture()
    {
        return inProgressTexture;
    }

    public int getStepCount()
    {
        return stepsRequired;
    }

    public static class Serializer extends RecipeSerializerImpl<LoomRecipe>
    {
        @Override
        public LoomRecipe fromJson(ResourceLocation recipeId, JsonObject json)
        {
            final Ingredient ingredient = Ingredient.fromJson(Objects.requireNonNull(json.get("ingredient"), "Missing required field 'ingredient'"));
            final ItemStackProvider stack = ItemStackProvider.fromJson(JsonHelpers.getAsJsonObject(json, "result"));
            final int inputCount = json.get("input_count").getAsInt();
            final int stepsRequired = json.get("steps_required").getAsInt();
            final ResourceLocation inProgressTexture = new ResourceLocation(JsonHelpers.convertToString(json.get("in_progress_texture"), "in_progress_texture"));
            return new LoomRecipe(recipeId, ingredient, stack, inputCount, stepsRequired, inProgressTexture);
        }

        @Nullable
        @Override
        public LoomRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer)
        {
            final Ingredient ingredient = Ingredient.fromNetwork(buffer);
            final ItemStackProvider stack = ItemStackProvider.fromNetwork(buffer);
            final int inputCount = buffer.readVarInt();
            final int steps = buffer.readVarInt();
            final ResourceLocation inProgressTexture = new ResourceLocation(buffer.readUtf());
            return new LoomRecipe(recipeId, ingredient, stack, inputCount, steps, inProgressTexture);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, LoomRecipe recipe)
        {
            recipe.getIngredient().toNetwork(buffer);
            recipe.result.toNetwork(buffer);
            buffer.writeVarInt(recipe.inputCount);
            buffer.writeVarInt(recipe.stepsRequired);
            buffer.writeUtf(recipe.inProgressTexture.toString());
        }
    }
}
