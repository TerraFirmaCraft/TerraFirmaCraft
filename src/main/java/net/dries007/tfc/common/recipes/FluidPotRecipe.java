/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes;

import java.util.List;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

import net.dries007.tfc.common.blockentities.PotBlockEntity;
import net.dries007.tfc.common.recipes.ingredients.FluidIngredient;
import net.dries007.tfc.util.JsonHelpers;

public class FluidPotRecipe extends PotRecipe
{
    protected final FluidStack outputFluid;

    protected FluidPotRecipe(ResourceLocation id, List<Ingredient> itemIngredients, FluidIngredient fluidIngredient, int duration, float minTemp, FluidStack outputFluid)
    {
        super(id, itemIngredients, fluidIngredient, duration, minTemp);
        this.outputFluid = outputFluid;
    }

    @Override
    public Output getOutput(PotBlockEntity.PotInventory inventory)
    {
        return new FluidOutput(outputFluid);
    }

    @Override
    public RecipeSerializer<?> getSerializer()
    {
        return TFCRecipeSerializers.POT_FLUID.get();
    }

    /**
     * Has no persistent output, thus uses the {@link PotRecipe#EMPTY} output type.
     */
    static class FluidOutput implements Output
    {
        private final FluidStack stack;

        FluidOutput(FluidStack stack)
        {
            this.stack = stack;
        }

        @Override
        public void onFinish(PotBlockEntity.PotInventory inventory)
        {
            inventory.fill(stack.copy(), IFluidHandler.FluidAction.EXECUTE);
        }
    }

    public static class Serializer extends PotRecipe.Serializer<FluidPotRecipe>
    {
        @Override
        public void toNetwork(FriendlyByteBuf buffer, FluidPotRecipe recipe)
        {
            super.toNetwork(buffer, recipe);
            buffer.writeFluidStack(recipe.outputFluid);
        }

        @Override
        protected FluidPotRecipe fromJson(ResourceLocation recipeId, JsonObject json, List<Ingredient> ingredients, FluidIngredient fluidIngredient, int duration, float minTemp)
        {
            JsonObject output = GsonHelper.getAsJsonObject(json, "fluid_output");
            return new FluidPotRecipe(recipeId, ingredients, fluidIngredient, duration, minTemp, JsonHelpers.getFluidStack(output));
        }

        @Override
        protected FluidPotRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer, List<Ingredient> ingredients, FluidIngredient fluidIngredient, int duration, float minTemp)
        {
            return new FluidPotRecipe(recipeId, ingredients, fluidIngredient, duration, minTemp, buffer.readFluidStack());
        }
    }
}
