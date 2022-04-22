/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes;

import javax.annotation.Nullable;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import net.minecraftforge.fluids.FluidStack;

import net.dries007.tfc.common.recipes.ingredients.FluidStackIngredient;
import net.dries007.tfc.common.recipes.inventory.BloomeryInventory;
import net.dries007.tfc.common.recipes.inventory.ItemStackInventory;
import net.dries007.tfc.common.recipes.outputs.ItemStackProvider;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.JsonHelpers;

public class BloomeryRecipe implements ISimpleRecipe<BloomeryInventory>
{
    /**
     * Gets a recipe matching a primary input item stack.
     */
    @Nullable
    public static BloomeryRecipe get(Level level, ItemStack stack)
    {
        final ItemStackInventory inventory = new ItemStackInventory(stack);
        final HeatingRecipe heatRecipe = HeatingRecipe.getRecipe(stack);
        if (heatRecipe != null)
        {
            final FluidStack moltenFluid = heatRecipe.getOutputFluid(inventory);
            for (BloomeryRecipe recipe : Helpers.getRecipes(level, TFCRecipeTypes.BLOOMERY).values())
            {
                if (recipe.inputFluid.test(moltenFluid))
                {
                    return recipe;
                }
            }
        }
        return null;
    }

    private final ResourceLocation id;
    private final FluidStackIngredient inputFluid;
    private final Ingredient catalyst;
    private final int catalystCount;
    private final ItemStackProvider result;
    private final int duration;

    public BloomeryRecipe(ResourceLocation id, FluidStackIngredient inputFluid, Ingredient catalyst, int catalystCount, ItemStackProvider result, int duration)
    {
        this.id = id;
        this.inputFluid = inputFluid;
        this.catalyst = catalyst;
        this.catalystCount = catalystCount;
        this.result = result;
        this.duration = duration;
    }

    public int getDuration()
    {
        return duration;
    }

    public Ingredient getCatalyst()
    {
        return catalyst;
    }

    public int getCatalystCount()
    {
        return catalystCount;
    }

    public FluidStackIngredient getInputFluid()
    {
        return inputFluid;
    }

    @Override
    public boolean matches(BloomeryInventory inv, Level level)
    {
        return inputFluid.test(inv.getFluid()) && this.catalyst.test(inv.getCatalyst());
    }

    public boolean matchesInput(ItemStack stack)
    {
        final HeatingRecipe heat = HeatingRecipe.getRecipe(stack);
        if (heat != null)
        {
            final FluidStack fluid = heat.getOutputFluid(new ItemStackInventory(stack));
            return inputFluid.test(fluid);
        }
        return false;
    }

    public boolean matchesCatalyst(ItemStack stack)
    {
        return catalyst.test(stack);
    }

    @Override
    public ItemStack getResultItem()
    {
        return result.getStack(ItemStack.EMPTY);
    }

    @Override
    public ItemStack assemble(BloomeryInventory inventory)
    {
        ItemStack stack = result.getStack(inventory.getCatalyst());
        stack.setCount(inventory.getFluid().getAmount() / inputFluid.amount());
        return stack;
    }

    @Override
    public ResourceLocation getId()
    {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer()
    {
        return TFCRecipeSerializers.BLOOMERY.get();
    }

    @Override
    public RecipeType<?> getType()
    {
        return TFCRecipeTypes.BLOOMERY.get();
    }

    public static class Serializer extends RecipeSerializerImpl<BloomeryRecipe>
    {
        @Override
        public BloomeryRecipe fromJson(ResourceLocation recipeId, JsonObject json)
        {
            final FluidStackIngredient fluidStack = FluidStackIngredient.fromJson(JsonHelpers.getAsJsonObject(json, "fluid"));
            final Ingredient catalyst = Ingredient.fromJson(JsonHelpers.getAsJsonObject(json, "catalyst"));
            final int count = JsonHelpers.getAsInt(json, "catalyst_count");
            final ItemStackProvider result = ItemStackProvider.fromJson(JsonHelpers.getAsJsonObject(json, "result"));
            final int time = JsonHelpers.getAsInt(json, "duration");
            return new BloomeryRecipe(recipeId, fluidStack, catalyst, count, result, time);
        }

        @Nullable
        @Override
        public BloomeryRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer)
        {
            final FluidStackIngredient fluidStack = FluidStackIngredient.fromNetwork(buffer);
            final Ingredient catalyst = Ingredient.fromNetwork(buffer);
            final int count = buffer.readVarInt();
            final ItemStackProvider result = ItemStackProvider.fromNetwork(buffer);
            final int time = buffer.readVarInt();
            return new BloomeryRecipe(recipeId, fluidStack, catalyst, count, result, time);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, BloomeryRecipe recipe)
        {
            recipe.inputFluid.toNetwork(buffer);
            recipe.catalyst.toNetwork(buffer);
            buffer.writeVarInt(recipe.catalystCount);
            recipe.result.toNetwork(buffer);
            buffer.writeVarInt(recipe.duration);
        }
    }
}
