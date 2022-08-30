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
import org.jetbrains.annotations.Nullable;

public class BloomeryRecipe implements ISimpleRecipe<BloomeryInventory>
{
    /**
     * Gets a recipe matching a primary input item stack.
     */
    @Nullable
    public static BloomeryRecipe get(Level level, ItemStack stack)
    {
        final ItemStackInventory inventory = new ItemStackInventory(stack);
        final HeatingRecipe heatRecipe = HeatingRecipe.getRecipe(inventory);
        if (heatRecipe != null)
        {
            final FluidStack moltenFluid = heatRecipe.assembleFluid(inventory);
            for (BloomeryRecipe recipe : Helpers.getRecipes(level, TFCRecipeTypes.BLOOMERY).values())
            {
                if (recipe.inputFluid.ingredient().test(moltenFluid.getFluid()))
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
    private final ItemStackProvider result;
    private final int duration;

    public BloomeryRecipe(ResourceLocation id, FluidStackIngredient inputFluid, Ingredient catalyst, ItemStackProvider result, int duration)
    {
        this.id = id;
        this.inputFluid = inputFluid;
        this.catalyst = catalyst;
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
        final ItemStackInventory inventory = new ItemStackInventory(stack);
        final HeatingRecipe heat = HeatingRecipe.getRecipe(inventory);
        if (heat != null)
        {
            // small ores still need to be able to be added to the bloomery, so we cannot test the FluidStack's amount
            final FluidStack fluid = heat.assembleFluid(inventory);
            return inputFluid.ingredient().test(fluid.getFluid());
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
        return result.getEmptyStack();
    }

    @Override
    public ItemStack assemble(BloomeryInventory inventory)
    {
        final ItemStack stack = result.getEmptyStack(); // There isn't a proper input stack, so we use the empty result
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
            final ItemStackProvider result = ItemStackProvider.fromJson(JsonHelpers.getAsJsonObject(json, "result"));
            final int time = JsonHelpers.getAsInt(json, "duration");
            return new BloomeryRecipe(recipeId, fluidStack, catalyst, result, time);
        }

        @Nullable
        @Override
        public BloomeryRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer)
        {
            final FluidStackIngredient fluidStack = FluidStackIngredient.fromNetwork(buffer);
            final Ingredient catalyst = Ingredient.fromNetwork(buffer);
            final ItemStackProvider result = ItemStackProvider.fromNetwork(buffer);
            final int time = buffer.readVarInt();
            return new BloomeryRecipe(recipeId, fluidStack, catalyst, result, time);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, BloomeryRecipe recipe)
        {
            recipe.inputFluid.toNetwork(buffer);
            recipe.catalyst.toNetwork(buffer);
            recipe.result.toNetwork(buffer);
            buffer.writeVarInt(recipe.duration);
        }
    }
}
