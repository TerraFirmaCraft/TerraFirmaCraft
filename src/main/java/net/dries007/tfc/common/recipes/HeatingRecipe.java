/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;
import javax.annotation.Nullable;

import com.google.gson.JsonObject;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;

import net.dries007.tfc.common.capabilities.FluidIngredient;
import net.dries007.tfc.common.capabilities.heat.HeatCapability;
import net.dries007.tfc.util.collections.IndirectHashCollection;

public class HeatingRecipe implements ISimpleRecipe<ItemStackRecipeWrapper>
{
    public static final IndirectHashCollection<Item, HeatingRecipe> CACHE = new IndirectHashCollection<>(HeatingRecipe::getValidItems);

    @Nullable
    public static HeatingRecipe getRecipe(Level world, ItemStackRecipeWrapper wrapper)
    {
        for (HeatingRecipe recipe : CACHE.getAll(wrapper.getStack().getItem()))
        {
            if (recipe.matches(wrapper, world))
            {
                return recipe;
            }
        }
        return null;
    }

    private final ResourceLocation id;
    private final Ingredient ingredient;
    private final ItemStack outputItem;
    private final FluidStack outputFluid;
    private final float temperature;

    public HeatingRecipe(ResourceLocation id, Ingredient ingredient, ItemStack outputItem, FluidStack outputFluid, float temperature)
    {
        this.id = id;
        this.ingredient = ingredient;
        this.outputItem = outputItem;
        this.outputFluid = outputFluid;
        this.temperature = temperature;
    }

    @Override
    public boolean matches(ItemStackRecipeWrapper inv, Level worldIn)
    {
        return ingredient.test(inv.getStack());
    }

    @Override
    public ItemStack getResultItem()
    {
        return outputItem;
    }

    @Override
    public ResourceLocation getId()
    {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer()
    {
        return TFCRecipeSerializers.HEATING.get();
    }

    @Override
    public RecipeType<?> getType()
    {
        return TFCRecipeTypes.HEATING;
    }

    @Override
    public ItemStack assemble(ItemStackRecipeWrapper inventory)
    {
        final ItemStack inputStack = inventory.getStack();
        final ItemStack outputStack = outputItem.copy();
        inputStack.getCapability(HeatCapability.CAPABILITY).ifPresent(oldCap ->
            outputStack.getCapability(HeatCapability.CAPABILITY).ifPresent(newCap ->
                newCap.setTemperature(oldCap.getTemperature())));
        return outputStack;
    }

    public FluidStack getOutputFluid(ItemStackRecipeWrapper inventory)
    {
        return outputFluid.copy();
    }

    public float getTemperature()
    {
        return temperature;
    }

    public boolean isValidTemperature(float temperatureIn)
    {
        return temperatureIn >= temperature;
    }

    public Collection<Item> getValidItems()
    {
        return Arrays.stream(this.ingredient.getItems()).map(ItemStack::getItem).collect(Collectors.toSet());
    }

    public static class Serializer extends RecipeSerializer<HeatingRecipe>
    {
        @Override
        public HeatingRecipe fromJson(ResourceLocation recipeId, JsonObject json)
        {
            final Ingredient ingredient = Ingredient.fromJson(json.get("ingredient"));
            final ItemStack outputItem = json.has("result_item") ? ShapedRecipe.itemFromJson(json.getAsJsonObject("result_item")) : ItemStack.EMPTY;
            final FluidStack outputFluid = json.has("result_fluid") ? FluidIngredient.fluidStackFromJson(json.getAsJsonObject("result_fluid")) : FluidStack.EMPTY;
            final float temperature = GsonHelper.getAsFloat(json, "temperature");
            return new HeatingRecipe(recipeId, ingredient, outputItem, outputFluid, temperature);
        }

        @Nullable
        @Override
        public HeatingRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer)
        {
            Ingredient ingredient = Ingredient.fromNetwork(buffer);
            ItemStack outputItem = buffer.readItem();
            FluidStack outputFluid = buffer.readFluidStack();
            float temperature = buffer.readFloat();
            return new HeatingRecipe(recipeId, ingredient, outputItem, outputFluid, temperature);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, HeatingRecipe recipe)
        {
            recipe.ingredient.toNetwork(buffer);
            buffer.writeItem(recipe.outputItem);
            buffer.writeFluidStack(recipe.outputFluid);
            buffer.writeFloat(recipe.temperature);
        }
    }
}
