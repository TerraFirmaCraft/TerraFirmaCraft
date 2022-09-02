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
import net.dries007.tfc.common.recipes.inventory.EmptyInventory;
import net.dries007.tfc.common.recipes.inventory.ItemStackInventory;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.JsonHelpers;
import org.jetbrains.annotations.Nullable;

public class BlastFurnaceRecipe implements ISimpleRecipe<BlastFurnaceRecipe.Inventory>
{
    /**
     * @return A recipe matching a primary input item stack.
     */
    @Nullable
    public static BlastFurnaceRecipe get(Level level, ItemStack stack)
    {
        final ItemStackInventory inventory = new ItemStackInventory(stack);
        final HeatingRecipe heatRecipe = HeatingRecipe.getRecipe(inventory);
        if (heatRecipe != null)
        {
            final FluidStack moltenFluid = heatRecipe.assembleFluid(inventory);
            for (BlastFurnaceRecipe recipe : Helpers.getRecipes(level, TFCRecipeTypes.BLAST_FURNACE).values())
            {
                if (recipe.inputFluid.ingredient().test(moltenFluid.getFluid()))
                {
                    return recipe;
                }
            }
        }
        return null;
    }

    /**
     * @return A recipe matching just the input fluid, ignoring amounts.
     */
    @Nullable
    public static BlastFurnaceRecipe get(Level level, FluidStack inputFluid)
    {
        for (BlastFurnaceRecipe recipe : Helpers.getRecipes(level, TFCRecipeTypes.BLAST_FURNACE).values())
        {
            if (recipe.inputFluid.ingredient().test(inputFluid.getFluid()))
            {
                return recipe;
            }
        }
        return null;
    }

    private final ResourceLocation id;
    private final FluidStackIngredient inputFluid;
    private final Ingredient catalyst;
    private final FluidStack outputFluid;

    public BlastFurnaceRecipe(ResourceLocation id, FluidStackIngredient inputFluid, Ingredient catalyst, FluidStack outputFluid)
    {
        this.id = id;
        this.inputFluid = inputFluid;
        this.catalyst = catalyst;
        this.outputFluid = outputFluid;
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
    public boolean matches(Inventory inventory, @Nullable Level level)
    {
        return inputFluid.test(inventory.getFluid()) && catalyst.test(inventory.getCatalyst());
    }

    public boolean matchesInput(ItemStack stack)
    {
        final ItemStackInventory inventory = new ItemStackInventory(stack);
        final HeatingRecipe heat = HeatingRecipe.getRecipe(inventory);
        if (heat != null)
        {
            // Ignore count, since the blast furnace will aggregate all inputs
            final FluidStack fluid = heat.assembleFluid(inventory);
            return inputFluid.ingredient().test(fluid.getFluid());
        }
        return false;
    }

    public boolean matchesCatalyst(ItemStack stack)
    {
        return catalyst.test(stack);
    }

    /**
     * Consumes amounts of {@code inputFluid}, and returns the amount of output fluid that was produced.
     *
     * @param inputFluid The input fluid, which will contain the remainder after producing output.
     */
    public FluidStack assembleFluidOutput(FluidStack inputFluid)
    {
        final int maximumRatio = inputFluid.getAmount() / this.inputFluid.amount();
        final FluidStack outputFluid = this.outputFluid.copy();

        inputFluid.shrink(maximumRatio * this.inputFluid.amount());
        outputFluid.setAmount(this.outputFluid.getAmount() * maximumRatio);

        return outputFluid;
    }

    @Override
    public ItemStack getResultItem()
    {
        return ItemStack.EMPTY;
    }

    @Override
    public ResourceLocation getId()
    {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer()
    {
        return TFCRecipeSerializers.BLAST_FURNACE.get();
    }

    @Override
    public RecipeType<?> getType()
    {
        return TFCRecipeTypes.BLAST_FURNACE.get();
    }

    public interface Inventory extends EmptyInventory
    {
        FluidStack getFluid();

        ItemStack getCatalyst();
    }

    public static class Serializer extends RecipeSerializerImpl<BlastFurnaceRecipe>
    {
        @Override
        public BlastFurnaceRecipe fromJson(ResourceLocation recipeId, JsonObject json)
        {
            final FluidStackIngredient inputFluid = FluidStackIngredient.fromJson(JsonHelpers.getAsJsonObject(json, "fluid"));
            final Ingredient catalyst = Ingredient.fromJson(JsonHelpers.getAsJsonObject(json, "catalyst"));
            final FluidStack outputFluid = JsonHelpers.getFluidStack(json, "result");
            return new BlastFurnaceRecipe(recipeId, inputFluid, catalyst, outputFluid);
        }

        @Nullable
        @Override
        public BlastFurnaceRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer)
        {
            final FluidStackIngredient inputFluid = FluidStackIngredient.fromNetwork(buffer);
            final Ingredient catalyst = Ingredient.fromNetwork(buffer);
            final FluidStack outputFluid = FluidStack.readFromPacket(buffer);
            return new BlastFurnaceRecipe(recipeId, inputFluid, catalyst, outputFluid);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, BlastFurnaceRecipe recipe)
        {
            recipe.inputFluid.toNetwork(buffer);
            recipe.catalyst.toNetwork(buffer);
            recipe.outputFluid.writeToPacket(buffer);
        }
    }
}
