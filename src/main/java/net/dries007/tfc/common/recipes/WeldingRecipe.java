/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

import net.dries007.tfc.common.capabilities.heat.HeatCapability;
import net.dries007.tfc.common.recipes.inventory.EmptyInventory;
import net.dries007.tfc.common.recipes.outputs.ItemStackProvider;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.JsonHelpers;
import org.jetbrains.annotations.Nullable;

public class WeldingRecipe implements ISimpleRecipe<WeldingRecipe.Inventory>
{
    @Nullable
    public static WeldingRecipe getFirstMatching(Level level, ItemStack stack)
    {
        for (WeldingRecipe recipe : Helpers.getRecipes(level, TFCRecipeTypes.WELDING)
            .values())
        {
            if (recipe.firstInput.test(stack) || recipe.secondInput.test(stack))
            {
                return recipe;
            }
        }
        return null;
    }

    private final ResourceLocation id;
    private final Ingredient firstInput, secondInput;
    private final Ingredient catalystInput;
    private final int tier;
    private final int minTemperature;
    private final ItemStackProvider output;

    public WeldingRecipe(ResourceLocation id, Ingredient firstInput, Ingredient secondInput, Ingredient catalystInput, int tier, int minTemperature, ItemStackProvider output)
    {
        this.id = id;
        this.firstInput = firstInput;
        this.secondInput = secondInput;
        this.catalystInput = catalystInput;
        this.tier = tier;
        this.minTemperature = minTemperature;
        this.output = output;
    }

    /**
     * This is used when querying the matching recipe for a stack.
     * As such it doesn't check if the recipe is complete, but only if the recipe could be completed.
     */
    @Override
    public boolean matches(Inventory inventory, Level level)
    {
        final ItemStack left = inventory.getLeft(), right = inventory.getRight();
        return inventory.getTier() >= tier
            && ((firstInput.test(left) && secondInput.test(right)) || (firstInput.test(right) && secondInput.test(left)))
            && catalystInput.test(inventory.getCatalyst())
            && (minTemperature == -1 || (HeatCapability.getTemperature(left) > minTemperature && HeatCapability.getTemperature(right) > minTemperature));
    }

    @Override
    public ItemStack assemble(Inventory inventory)
    {
        return output.getStack(inventory.getLeft());
    }

    @Override
    public ItemStack getResultItem()
    {
        return output.getStack(ItemStack.EMPTY);
    }

    @Override
    public ResourceLocation getId()
    {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer()
    {
        return TFCRecipeSerializers.WELDING.get();
    }

    @Override
    public RecipeType<?> getType()
    {
        return TFCRecipeTypes.WELDING.get();
    }

    public interface Inventory extends EmptyInventory
    {
        ItemStack getLeft();

        ItemStack getRight();

        ItemStack getCatalyst();

        int getTier();
    }

    public static class Serializer extends RecipeSerializerImpl<WeldingRecipe>
    {
        @Override
        public WeldingRecipe fromJson(ResourceLocation recipeId, JsonObject json)
        {
            final Ingredient firstInput = Ingredient.fromJson(JsonHelpers.get(json, "first_input"));
            final Ingredient secondInput = Ingredient.fromJson(JsonHelpers.get(json, "second_input"));
            final Ingredient catalyst = Ingredient.fromJson(JsonHelpers.get(json, "catalyst"));
            final int tier = JsonHelpers.getAsInt(json, "tier", -1);
            final int minTemperature = JsonHelpers.getAsInt(json, "min_temperature", -1);
            final ItemStackProvider output = ItemStackProvider.fromJson(JsonHelpers.getAsJsonObject(json, "result"));
            return new WeldingRecipe(recipeId, firstInput, secondInput, catalyst, tier, minTemperature, output);
        }

        @Nullable
        @Override
        public WeldingRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer)
        {
            final Ingredient firstInput = Ingredient.fromNetwork(buffer);
            final Ingredient secondInput = Ingredient.fromNetwork(buffer);
            final Ingredient catalystInput = Ingredient.fromNetwork(buffer);
            final int tier = buffer.readVarInt();
            final int minTemperature = buffer.readVarInt();
            final ItemStackProvider output = ItemStackProvider.fromNetwork(buffer);
            return new WeldingRecipe(recipeId, firstInput, secondInput, catalystInput, tier, minTemperature, output);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, WeldingRecipe recipe)
        {
            recipe.firstInput.toNetwork(buffer);
            recipe.secondInput.toNetwork(buffer);
            recipe.catalystInput.toNetwork(buffer);
            buffer.writeVarInt(recipe.tier);
            buffer.writeVarInt(recipe.minTemperature);
            recipe.output.toNetwork(buffer);
        }
    }
}
