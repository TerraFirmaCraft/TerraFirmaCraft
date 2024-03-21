/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes;

import com.google.gson.JsonObject;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;

import net.dries007.tfc.common.recipes.ingredients.FluidStackIngredient;
import net.dries007.tfc.common.recipes.ingredients.ItemStackIngredient;
import net.dries007.tfc.common.recipes.inventory.EmptyInventory;
import net.dries007.tfc.common.recipes.inventory.ItemStackInventory;
import net.dries007.tfc.common.recipes.outputs.ItemStackProvider;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.JsonHelpers;

import org.jetbrains.annotations.Nullable;

public class BloomeryRecipe implements ISimpleRecipe<EmptyInventory>
{
    private final ResourceLocation id;
    private final FluidStackIngredient inputFluid;
    private final ItemStackIngredient catalyst;
    private final ItemStackProvider result;
    private final int duration;

    public BloomeryRecipe(ResourceLocation id, FluidStackIngredient inputFluid, ItemStackIngredient catalyst, ItemStackProvider result, int duration)
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

    public ItemStackIngredient getCatalyst()
    {
        return catalyst;
    }

    public FluidStackIngredient getInputFluid()
    {
        return inputFluid;
    }

    @Override
    public boolean matches(EmptyInventory inv, Level level)
    {
        return false;
    }

    /**
     * @return {@code true} if {@code stack} could be melted down to form part of the primary (fluid) input to this recipe.
     */
    public boolean matchesInput(ItemStack stack)
    {
        return consumeInput(stack) != null;
    }

    /**
     * @return {@code true} if {@code stack} could form part of the primary (fluid) input to this recipe.
     */
    public boolean matchesInput(FluidStack stack)
    {
        return inputFluid.ingredient().test(stack.getFluid());
    }

    /**
     * @return The fluid that would be produced by the primary input {@code stack}, or {@code null} if {@code stack} is not a primary input.
     */
    public @Nullable FluidStack consumeInput(ItemStack stack)
    {
        final ItemStackInventory inventory = new ItemStackInventory(stack);
        final HeatingRecipe heat = HeatingRecipe.getRecipe(inventory);
        if (heat != null)
        {
            // Don't test amount here, because we just want to know if this stack melts into the correct metal - not how much
            final FluidStack fluid = heat.assembleFluid(inventory);
            if (matchesInput(fluid))
            {
                return fluid;
            }
        }
        return null;
    }

    /**
     * @return {@code true} if {@code stack} is the correct catalyst for this recipe.
     */
    public boolean matchesCatalyst(ItemStack stack)
    {
        return catalyst.test(stack);
    }

    public ItemStack assembleOutput()
    {
        return result.getEmptyStack();
    }

    @Override
    public ItemStack getResultItem(RegistryAccess registryAccess)
    {
        return result.getEmptyStack();
    }

    @Override
    public ItemStack assemble(EmptyInventory inventory, RegistryAccess registryAccess)
    {
        throw new UnsupportedOperationException();
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
            final ItemStackIngredient catalyst = ItemStackIngredient.fromJson(JsonHelpers.getAsJsonObject(json, "catalyst"));
            final ItemStackProvider result = ItemStackProvider.fromJson(JsonHelpers.getAsJsonObject(json, "result"));
            final int time = JsonHelpers.getAsInt(json, "duration");
            return new BloomeryRecipe(recipeId, fluidStack, catalyst, result, time);
        }

        @Nullable
        @Override
        public BloomeryRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer)
        {
            final FluidStackIngredient fluidStack = FluidStackIngredient.fromNetwork(buffer);
            final ItemStackIngredient catalyst = ItemStackIngredient.fromNetwork(buffer);
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
