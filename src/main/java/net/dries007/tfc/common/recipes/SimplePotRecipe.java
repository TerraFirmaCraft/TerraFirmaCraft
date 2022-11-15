/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

import net.dries007.tfc.common.blockentities.PotBlockEntity;
import net.dries007.tfc.common.capabilities.food.FoodCapability;
import net.dries007.tfc.common.fluids.FluidHelpers;
import net.dries007.tfc.common.recipes.ingredients.FluidStackIngredient;
import net.dries007.tfc.util.JsonHelpers;

public class SimplePotRecipe extends PotRecipe
{
    protected final FluidStack outputFluid;
    protected final List<ItemStack> outputStacks;

    public SimplePotRecipe(ResourceLocation id, List<Ingredient> itemIngredients, FluidStackIngredient fluidIngredient, int duration, float minTemp, FluidStack outputFluid, List<ItemStack> outputStacks)
    {
        super(id, itemIngredients, fluidIngredient, duration, minTemp);
        this.outputFluid = outputFluid;
        this.outputStacks = outputStacks;
        this.outputStacks.forEach(FoodCapability::setStackNonDecaying);
    }

    public FluidStack getDisplayFluid()
    {
        return outputFluid;
    }

    public List<ItemStack> getOutputStacks()
    {
        return outputStacks;
    }

    @Override
    public Output getOutput(PotBlockEntity.PotInventory inventory)
    {
        return new SimpleOutput(outputFluid, outputStacks);
    }

    @Override
    public RecipeSerializer<?> getSerializer()
    {
        return TFCRecipeSerializers.POT_SIMPLE.get();
    }

    /**
     * Has no persistent output, thus uses the {@link PotRecipe#EMPTY} output type.
     */
    record SimpleOutput(FluidStack stack, List<ItemStack> items) implements Output
    {
        @Override
        public void onFinish(PotBlockEntity.PotInventory inventory)
        {
            for (int i = 0; i < Math.min(items.size(), inventory.getSlots()); i++)
            {
                inventory.setStackInSlot(i + PotBlockEntity.SLOT_EXTRA_INPUT_START, items.get(i).copy());
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
            buffer.writeVarInt(recipe.outputStacks.size());
            recipe.outputStacks.forEach(buffer::writeItem);
        }

        @Override
        protected SimplePotRecipe fromJson(ResourceLocation recipeId, JsonObject json, List<Ingredient> ingredients, FluidStackIngredient fluidIngredient, int duration, float minTemp)
        {
            final FluidStack output = json.has("fluid_output") ? JsonHelpers.getFluidStack(json, "fluid_output") : FluidStack.EMPTY;
            final List<ItemStack> stacks = new ArrayList<>(5);
            if (json.has("item_output"))
            {
                final JsonArray array = json.getAsJsonArray("item_output");
                for (JsonElement element : array)
                {
                    ItemStack stack = JsonHelpers.getItemStack(element.getAsJsonObject());
                    if (stack.getCount() != 1)
                    {
                        throw new JsonParseException("Item stacks for pot outputs must be of size 1");
                    }
                    stacks.add(stack);
                }
            }
            if (stacks.size() > 5)
            {
                throw new JsonParseException("Cannot have more than five item stack outputs for pot recipe.");
            }
            return new SimplePotRecipe(recipeId, ingredients, fluidIngredient, duration, minTemp, output, stacks);
        }

        @Override
        protected SimplePotRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer, List<Ingredient> ingredients, FluidStackIngredient fluidIngredient, int duration, float minTemp)
        {
            final FluidStack fluid = buffer.readFluidStack();
            final int size = buffer.readVarInt();
            List<ItemStack> stacks = new ArrayList<>(size);
            if (size > 0)
            {
                for (int i = 0; i < size; i++)
                {
                    stacks.add(buffer.readItem());
                }
            }
            return new SimplePotRecipe(recipeId, ingredients, fluidIngredient, duration, minTemp, fluid, stacks);
        }
    }
}
