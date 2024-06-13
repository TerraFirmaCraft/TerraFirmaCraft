/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.recipes.ingredients.ItemStackIngredient;
import net.dries007.tfc.common.recipes.inventory.ItemStackInventory;
import net.dries007.tfc.common.recipes.outputs.ItemStackProvider;
import net.dries007.tfc.util.JsonHelpers;
import net.dries007.tfc.util.collections.IndirectHashCollection;

/**
 * Note the non-counted ingredient is used for matching, so that we can initialize the recipe with even just a single item.
 * However, for consistency reasons we now use an {@link ItemStackIngredient} to demonstrate that the count and the ingredient are linked.
 */
public class LoomRecipe extends SimpleItemRecipe
{
    public static final IndirectHashCollection<Item, LoomRecipe> CACHE = IndirectHashCollection.createForRecipe(LoomRecipe::getValidItems, TFCRecipeTypes.LOOM);

    @Nullable
    public static LoomRecipe getRecipe(Level level, ItemStack stack)
    {
        return getRecipe(level, new ItemStackInventory(stack));
    }

    @Nullable
    public static LoomRecipe getRecipe(Level level, ItemStackInventory wrapper)
    {
        for (LoomRecipe recipe : CACHE.getAll(wrapper.getStack().getItem()))
        {
            if (recipe.matches(wrapper, level))
            {
                return recipe;
            }
        }
        return null;
    }

    private final ItemStackIngredient ingredient;
    private final int stepsRequired;
    private final ResourceLocation inProgressTexture;

    public LoomRecipe(ResourceLocation id, ItemStackIngredient ingredient, ItemStackProvider result, int stepsRequired, ResourceLocation inProgressTexture)
    {
        super(id, ingredient.ingredient(), result);
        this.ingredient = ingredient;
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

    public ItemStackIngredient getItemStackIngredient()
    {
        return ingredient;
    }

    public int getInputCount()
    {
        return ingredient.count();
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
            final ItemStackIngredient ingredient = ItemStackIngredient.fromJson(JsonHelpers.getAsJsonObject(json, "ingredient"));
            final ItemStackProvider stack = ItemStackProvider.fromJson(JsonHelpers.getAsJsonObject(json, "result"));
            final int stepsRequired = JsonHelpers.getAsInt(json, "steps_required");
            final ResourceLocation inProgressTexture = JsonHelpers.getResourceLocation(json, "in_progress_texture");
            return new LoomRecipe(recipeId, ingredient, stack, stepsRequired, inProgressTexture);
        }

        @Nullable
        @Override
        public LoomRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer)
        {
            final ItemStackIngredient ingredient = ItemStackIngredient.fromNetwork(buffer);
            final ItemStackProvider stack = ItemStackProvider.fromNetwork(buffer);
            final int steps = buffer.readVarInt();
            final ResourceLocation inProgressTexture = buffer.readResourceLocation();
            return new LoomRecipe(recipeId, ingredient, stack, steps, inProgressTexture);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, LoomRecipe recipe)
        {
            recipe.ingredient.toNetwork(buffer);
            recipe.result.toNetwork(buffer);
            buffer.writeVarInt(recipe.stepsRequired);
            buffer.writeUtf(recipe.inProgressTexture.toString());
        }
    }
}
