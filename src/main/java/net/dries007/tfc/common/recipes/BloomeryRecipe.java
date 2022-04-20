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
import net.dries007.tfc.util.JsonHelpers;

public class BloomeryRecipe implements ISimpleRecipe<BloomeryInventory>
{
    private final ResourceLocation id;
    private final FluidStackIngredient inputFluid;
    private final Ingredient catalyst;
    private final ItemStackProvider result;
    private final int time;

    public BloomeryRecipe(ResourceLocation id, FluidStackIngredient inputFluid, Ingredient catalyst, ItemStackProvider result, int time)
    {
        this.id = id;
        this.inputFluid = inputFluid;
        this.catalyst = catalyst;
        this.result = result;
        this.time = time;
    }

    public int getTime()
    {
        return time;
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
            final ItemStackProvider result = ItemStackProvider.fromJson(JsonHelpers.getAsJsonObject(json, "result"));
            final int time = JsonHelpers.getAsInt(json, "time");
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
            buffer.writeVarInt(recipe.time);
        }
    }
}
