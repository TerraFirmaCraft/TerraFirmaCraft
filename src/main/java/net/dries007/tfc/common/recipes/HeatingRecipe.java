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
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;

import net.dries007.tfc.common.capabilities.FluidIngredient;
import net.dries007.tfc.common.capabilities.heat.HeatCapability;
import net.dries007.tfc.util.collections.IndirectHashCollection;

public class HeatingRecipe implements ISimpleRecipe<ItemStackRecipeWrapper>
{
    public static final IndirectHashCollection<Item, HeatingRecipe> CACHE = new IndirectHashCollection<>(HeatingRecipe::getValidItems);

    @Nullable
    public static HeatingRecipe getRecipe(World world, ItemStackRecipeWrapper wrapper)
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
    public boolean matches(ItemStackRecipeWrapper inv, World worldIn)
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
    public IRecipeSerializer<?> getSerializer()
    {
        return TFCRecipeSerializers.HEATING.get();
    }

    @Override
    public IRecipeType<?> getType()
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
            final float temperature = JSONUtils.getAsFloat(json, "temperature");
            return new HeatingRecipe(recipeId, ingredient, outputItem, outputFluid, temperature);
        }

        @Nullable
        @Override
        public HeatingRecipe fromNetwork(ResourceLocation recipeId, PacketBuffer buffer)
        {
            Ingredient ingredient = Ingredient.fromNetwork(buffer);
            ItemStack outputItem = buffer.readItem();
            FluidStack outputFluid = buffer.readFluidStack();
            float temperature = buffer.readFloat();
            return new HeatingRecipe(recipeId, ingredient, outputItem, outputFluid, temperature);
        }

        @Override
        public void toNetwork(PacketBuffer buffer, HeatingRecipe recipe)
        {
            recipe.ingredient.toNetwork(buffer);
            buffer.writeItem(recipe.outputItem);
            buffer.writeFluidStack(recipe.outputFluid);
            buffer.writeFloat(recipe.temperature);
        }
    }
}
