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
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

import net.minecraftforge.fluids.FluidStack;

import net.dries007.tfc.common.blockentities.BloomeryBlockEntity;
import net.dries007.tfc.common.recipes.ingredients.FluidStackIngredient;
import net.dries007.tfc.common.recipes.ingredients.ItemStackIngredient;
import net.dries007.tfc.common.recipes.inventory.ItemStackInventory;
import net.dries007.tfc.util.JsonHelpers;

public class BloomeryRecipe implements ISimpleRecipe<BloomeryBlockEntity.BloomeryInventory>
{
    private final ResourceLocation id;
    private final FluidStackIngredient fluidStackIngredient;
    private final ItemStackIngredient itemStackIngredient;
    private final ItemStack result;
    private final int time;

    //todo: make catalyst optional? unsure how
    public BloomeryRecipe(ResourceLocation id, FluidStackIngredient fluidStackIngredient, ItemStackIngredient itemStackIngredient, ItemStack result, int time)
    {
        this.id = id;
        this.fluidStackIngredient = fluidStackIngredient;
        this.itemStackIngredient = itemStackIngredient;
        this.result = result;
        this.time = time;
    }

    public int getTime()
    {
        return time;
    }

    public ItemStackIngredient getCatalyst()
    {
        return itemStackIngredient;
    }

    public FluidStackIngredient getInputFluid()
    {
        return fluidStackIngredient;
    }

    @Override
    public boolean matches(BloomeryBlockEntity.BloomeryInventory inv, Level level)
    {
        Fluid inputFluid = inv.getInputFluid();
        if (inputFluid.isSame(Fluids.EMPTY))
        {
            return false;
        }
        return fluidStackIngredient.getMatchingFluids().contains(inputFluid);
    }

    @Override
    public ItemStack getResultItem()
    {
        return result;
    }

    public ItemStack getResult(FluidStack stack)
    {
        return new ItemStack(this.result.getItem(),stack.getAmount() / this.fluidStackIngredient.getAmount());
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

    public boolean isValidInput(ItemStack stack)
    {
        HeatingRecipe heatingRecipe = HeatingRecipe.getRecipe(stack);
        if (heatingRecipe != null)
        {
            return this.fluidStackIngredient.getMatchingFluids().contains(heatingRecipe.getOutputFluid(new ItemStackInventory(stack)).getFluid());
        }
        return false;
    }

    public boolean isValidCatalyst(ItemStack stack)
    {
        for (ItemStack checkStack : this.itemStackIngredient.getItem().getItems())
        {
            if (checkStack.getItem() == stack.getItem())
            {
                return true;
            }
        }
        return false;
    }

    public boolean isValidMixture(FluidStack fluidStack, ItemStack catalystStack)
    {
        if (!fluidStackIngredient.getMatchingFluids().contains(fluidStack.getFluid()) || !isValidCatalyst(catalystStack))
        {
            return false;
        }
        return catalystStack.getCount() >= (fluidStack.getAmount() / fluidStackIngredient.getAmount() * itemStackIngredient.getCount());
    }

    public static class Serializer extends RecipeSerializerImpl<BloomeryRecipe>
    {
        @Override
        public BloomeryRecipe fromJson(ResourceLocation recipeId, JsonObject json)
        {
            final FluidStackIngredient fluidStack = FluidStackIngredient.fromJson(JsonHelpers.getAsJsonObject(json, "fluid"));
            final ItemStackIngredient catalystStack = ItemStackIngredient.fromJson(JsonHelpers.getAsJsonObject(json, "catalyst"));
            final ItemStack result = JsonHelpers.getItemStack(json, "result");
            final int time = JsonHelpers.getAsInt(json, "time");
            return new BloomeryRecipe(recipeId, fluidStack, catalystStack, result, time);
        }

        @Nullable
        @Override
        public BloomeryRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer)
        {
            final FluidStackIngredient fluidStack = FluidStackIngredient.fromNetwork(buffer);
            final ItemStackIngredient catalystStack = ItemStackIngredient.fromNetwork(buffer);
            final ItemStack result = buffer.readItem();
            final int time = buffer.readInt();
            return new BloomeryRecipe(recipeId, fluidStack, catalystStack, result, time);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, BloomeryRecipe recipe)
        {
            FluidStackIngredient.toNetwork(buffer, recipe.fluidStackIngredient);
            recipe.itemStackIngredient.toNetwork(buffer);
            buffer.writeItem(recipe.result);
            buffer.writeInt(recipe.time);
        }
    }
}
