/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.compat.jei.category;

import java.util.List;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.fluids.FluidStack;

import net.dries007.tfc.common.recipes.PotRecipe;
import net.dries007.tfc.common.recipes.SimplePotRecipe;
import net.dries007.tfc.common.recipes.outputs.ItemStackProvider;
import net.dries007.tfc.compat.jei.JEIIntegration;

public class SimplePotRecipeCategory extends PotRecipeCategory<PotRecipe>
{
    private static final int[] INPUT_X = {15, 5, 25, 5, 25};
    private static final int[] INPUT_Y = {5, 25, 25, 45, 45};
    private static final int[] OUTPUT_X = {75, 65, 85, 65, 85};
    private static final int[] OUTPUT_Y = {5, 25, 25, 45, 45};

    public SimplePotRecipeCategory(RecipeType<PotRecipe> type, IGuiHelper helper)
    {
        super(type, helper, helper.createBlankDrawable(110, 100));
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, PotRecipe potRecipe, IFocusGroup focuses)
    {
        SimplePotRecipe recipe = (SimplePotRecipe) potRecipe;

        final List<Ingredient> ingredients = recipe.getItemIngredients();

        int i = 0;
        for (Ingredient ingredient : ingredients)
        {
            if (!ingredient.isEmpty())
            {
                IRecipeSlotBuilder input = builder.addSlot(RecipeIngredientRole.INPUT, INPUT_X[i] + 1, INPUT_Y[i] + 1);
                input.addIngredients(ingredient);
                input.setBackground(slot, -1, -1);
                i++;
            }
        }

        final List<FluidStack> inputFluids = collapse(recipe.getFluidIngredient());
        if (!inputFluids.isEmpty())
        {
            IRecipeSlotBuilder fluidInput = builder.addSlot(RecipeIngredientRole.INPUT, 16, 66);
            fluidInput.addIngredients(JEIIntegration.FLUID_STACK, inputFluids);
            fluidInput.setFluidRenderer(1, false, 16, 16);
            fluidInput.setBackground(slot, -1, -1);
        }

        int j = 0;
        for (ItemStackProvider provider : recipe.getOutputItems())
        {
            // Pot recipes can operate either with providers that depend on inputs, which must be 1-1 with ingredients
            // Otherwise, we default to assuming they operate on empty stacks. See TerraFirmaCraft#2712
            final List<ItemStack> stacks = provider.dependsOnInput()
                ? collapse(List.of(ingredients.get(j).getItems()), provider)
                : collapse(provider);

            if (!stacks.isEmpty())
            {
                IRecipeSlotBuilder output = builder.addSlot(RecipeIngredientRole.OUTPUT, OUTPUT_X[j] + 1, OUTPUT_Y[j] + 1);
                output.addItemStacks(stacks);
                output.setBackground(slot, -1, -1);
                j++;
            }
        }

        final FluidStack outputFluid = recipe.getDisplayFluid();
        if (!outputFluid.isEmpty())
        {
            IRecipeSlotBuilder fluidOutput = builder.addSlot(RecipeIngredientRole.OUTPUT, 76, 66);
            fluidOutput.addIngredient(JEIIntegration.FLUID_STACK, outputFluid);
            fluidOutput.setFluidRenderer(1, false, 16, 16);
            fluidOutput.setBackground(slot, -1, -1);
        }
    }

    /**
     * <pre>
     *              y
     *  X      X    5
     * X X -> X X   25
     * X X    X X   45
     *  X      X    65
     * </pre>
     */
    @Override
    public void draw(PotRecipe recipe, IRecipeSlotsView recipeSlots, GuiGraphics stack, double mouseX, double mouseY)
    {
        // fire
        fire.draw(stack, 47, 45);
        fireAnimated.draw(stack, 47, 45);
    }
}
