/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;

import net.dries007.tfc.common.capabilities.heat.HeatCapability;
import net.dries007.tfc.common.recipes.inventory.ItemStackInventory;
import net.dries007.tfc.common.recipes.outputs.ItemStackProvider;
import net.dries007.tfc.util.JsonHelpers;
import net.dries007.tfc.util.collections.IndirectHashCollection;
import org.jetbrains.annotations.Nullable;

public class HeatingRecipe implements ISimpleRecipe<ItemStackInventory>
{
    public static final IndirectHashCollection<Item, HeatingRecipe> CACHE = IndirectHashCollection.createForRecipe(HeatingRecipe::getValidItems, TFCRecipeTypes.HEATING);

    @Nullable
    public static HeatingRecipe getRecipe(ItemStack stack)
    {
        return getRecipe(new ItemStackInventory(stack));
    }

    @Nullable
    public static HeatingRecipe getRecipe(ItemStackInventory wrapper)
    {
        for (HeatingRecipe recipe : CACHE.getAll(wrapper.getStack().getItem()))
        {
            if (recipe.matches(wrapper, null))
            {
                return recipe;
            }
        }
        return null;
    }

    private final ResourceLocation id;
    private final Ingredient ingredient;
    private final ItemStackProvider outputItem;
    private final FluidStack outputFluid;
    private final float temperature;
    private final boolean useDurability;

    public HeatingRecipe(ResourceLocation id, Ingredient ingredient, ItemStackProvider outputItem, FluidStack outputFluid, float temperature, boolean useDurability)
    {
        this.id = id;
        this.ingredient = ingredient;
        this.outputItem = outputItem;
        this.outputFluid = outputFluid;
        this.temperature = temperature;
        this.useDurability = useDurability;
    }

    @Override
    public boolean matches(ItemStackInventory inventory, @Nullable Level level)
    {
        return getIngredient().test(inventory.getStack());
    }

    @Override
    public ItemStack getResultItem()
    {
        return outputItem.getEmptyStack();
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
        return TFCRecipeTypes.HEATING.get();
    }

    @Override
    public ItemStack assemble(ItemStackInventory inventory)
    {
        final ItemStack inputStack = inventory.getStack();
        final ItemStack outputStack = outputItem.getSingleStack(inputStack);
        // We always upgrade the heat regardless
        inputStack.getCapability(HeatCapability.CAPABILITY).ifPresent(oldCap ->
            outputStack.getCapability(HeatCapability.CAPABILITY).ifPresent(newCap ->
                newCap.setTemperature(oldCap.getTemperature())));
        return outputStack;
    }

    /**
     * Assemble the fluid output. Use for recipe completions.
     * @return A new {@link FluidStack}
     */
    public FluidStack assembleFluid(ItemStackInventory inventory)
    {
        final ItemStack inputStack = inventory.getStack();
        final FluidStack outputFluid = this.outputFluid.copy();
        if (useDurability && !outputFluid.isEmpty() && inputStack.getMaxDamage() > 0 && inputStack.isDamageableItem())
        {
            outputFluid.setAmount(Mth.floor(outputFluid.getAmount() * (1 - (float) inputStack.getDamageValue() / inputStack.getMaxDamage())));
        }
        return outputFluid;
    }

    /**
     * @deprecated Use {@link #assembleFluid(ItemStackInventory)}
     */
    @Deprecated(forRemoval = true)
    public FluidStack getOutputFluid()
    {
        return outputFluid.copy();
    }

    /**
     * Get the output fluid for display only. Similar function to {@link #getResultItem()}
     * @return An approximation of the output fluid from this recipe.
     */
    public FluidStack getDisplayOutputFluid()
    {
        return outputFluid;
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
        return Arrays.stream(this.getIngredient().getItems()).map(ItemStack::getItem).collect(Collectors.toSet());
    }

    public Ingredient getIngredient()
    {
        return ingredient;
    }

    public static class Serializer extends RecipeSerializerImpl<HeatingRecipe>
    {
        @Override
        public HeatingRecipe fromJson(ResourceLocation recipeId, JsonObject json)
        {
            final Ingredient ingredient = Ingredient.fromJson(json.get("ingredient"));
            final ItemStackProvider outputItem = json.has("result_item") ? ItemStackProvider.fromJson(json.getAsJsonObject("result_item")): ItemStackProvider.empty();
            final FluidStack outputFluid = json.has("result_fluid") ? JsonHelpers.getFluidStack(json.getAsJsonObject("result_fluid")) : FluidStack.EMPTY;
            final float temperature = JsonHelpers.getAsFloat(json, "temperature");
            final boolean useDurability = JsonHelpers.getAsBoolean(json, "use_durability", false);
            return new HeatingRecipe(recipeId, ingredient, outputItem, outputFluid, temperature, useDurability);
        }

        @Nullable
        @Override
        public HeatingRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer)
        {
            final Ingredient ingredient = Ingredient.fromNetwork(buffer);
            final ItemStackProvider outputItem = ItemStackProvider.fromNetwork(buffer);
            final FluidStack outputFluid = buffer.readFluidStack();
            final float temperature = buffer.readFloat();
            final boolean useDurability = buffer.readBoolean();
            return new HeatingRecipe(recipeId, ingredient, outputItem, outputFluid, temperature, useDurability);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, HeatingRecipe recipe)
        {
            recipe.getIngredient().toNetwork(buffer);
            recipe.outputItem.toNetwork(buffer);
            buffer.writeFluidStack(recipe.outputFluid);
            buffer.writeFloat(recipe.temperature);
            buffer.writeBoolean(recipe.useDurability);
        }
    }
}
