/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.dries007.tfc.common.blockentities.PotBlockEntity;
import net.dries007.tfc.common.fluids.FluidHelpers;
import net.dries007.tfc.common.recipes.ingredients.FluidStackIngredient;
import net.dries007.tfc.common.recipes.outputs.ItemStackProvider;
import net.dries007.tfc.util.JsonHelpers;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

import java.util.ArrayList;
import java.util.List;

public class SimplePotRecipe extends PotRecipe
{
    protected final FluidStack outputFluid;
    protected final List<ItemStackProvider> outputProviders;

    public SimplePotRecipe(ResourceLocation id, List<Ingredient> itemIngredients, FluidStackIngredient fluidIngredient, int duration, float minTemp, FluidStack outputFluid, List<ItemStackProvider> outputProviders)
    {
        super(id, itemIngredients, fluidIngredient, duration, minTemp);
        this.outputFluid = outputFluid;
        this.outputProviders = outputProviders;
    }

    public FluidStack getDisplayFluid()
    {
        return outputFluid;
    }

    public List<ItemStackProvider> getOutputProviders()
    {
        return outputProviders;
    }

    @Override
    public Output getOutput(PotBlockEntity.PotInventory inventory)
    {
        return new AdvancedOutput(outputFluid, outputProviders);
    }

    @Override
    public RecipeSerializer<?> getSerializer()
    {
        return TFCRecipeSerializers.POT_SIMPLE.get();
    }

    /**
     * Has no persistent output, thus uses the {@link PotRecipe#EMPTY} output type.
     */
    record AdvancedOutput(FluidStack stack, List<ItemStackProvider> providers) implements Output
    {
        @Override
        public void onFinish(PotBlockEntity.PotInventory inventory)
        {
            for (int i = 0; i < Math.min(providers.size(), inventory.getSlots()); i++)
            {
                final ItemStack input = inventory.getStackInSlot(i);
                inventory.setStackInSlot(i + PotBlockEntity.SLOT_EXTRA_INPUT_START, providers.get(i).getSingleStack(input));
            }
            inventory.drain(FluidHelpers.BUCKET_VOLUME, IFluidHandler.FluidAction.EXECUTE);
            inventory.fill(stack.copy(), IFluidHandler.FluidAction.EXECUTE);
        }
    }

    public static class Serializer extends PotRecipe.Serializer<SimplePotRecipe>
    {
        @Override
        public void toNetwork(FriendlyByteBuf buffer, SimplePotRecipe recipe)
        {
            super.toNetwork(buffer, recipe);
            buffer.writeFluidStack(recipe.outputFluid);
            buffer.writeVarInt(recipe.outputProviders.size());
            recipe.outputProviders.forEach(provider -> provider.toNetwork(buffer));
        }

        @Override
        protected SimplePotRecipe fromJson(ResourceLocation recipeId, JsonObject json, List<Ingredient> ingredients, FluidStackIngredient fluidIngredient, int duration, float minTemp)
        {
            final FluidStack output = json.has("fluid_output") ? JsonHelpers.getFluidStack(json, "fluid_output") : FluidStack.EMPTY;
            final List<ItemStackProvider> stacks = new ArrayList<>(5);

            boolean anyProvidersDependOnInput = false;
            if (json.has("item_output"))
            {
                final JsonArray array = json.getAsJsonArray("item_output");
                for (JsonElement element : array)
                {
                    final ItemStackProvider provider = ItemStackProvider.fromJson(element.getAsJsonObject());
                    stacks.add(provider);
                    anyProvidersDependOnInput |= provider.dependsOnInput();
                }
            }
            if (stacks.size() > 5)
            {
                throw new JsonParseException("Cannot have more than five item stack outputs for pot recipe.");
            }
            if (anyProvidersDependOnInput && stacks.size() != ingredients.size())
            {
                throw new JsonParseException("At least one output is an ItemStackProvider that depends on the input. This is only allowed if there are (1) equal number of inputs and outputs, and (2) All inputs and outputs are the same");
            }
            return new SimplePotRecipe(recipeId, ingredients, fluidIngredient, duration, minTemp, output, stacks);
        }

        @Override
        protected SimplePotRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer, List<Ingredient> ingredients, FluidStackIngredient fluidIngredient, int duration, float minTemp)
        {
            final FluidStack fluid = buffer.readFluidStack();
            final int size = buffer.readVarInt();
            List<ItemStackProvider> stacks = new ArrayList<>(size);
            if (size > 0)
            {
                for (int i = 0; i < size; i++)
                {
                    stacks.add(ItemStackProvider.fromNetwork(buffer));
                }
            }
            return new SimplePotRecipe(recipeId, ingredients, fluidIngredient, duration, minTemp, fluid, stacks);
        }
    }
}
