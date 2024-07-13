/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.compat.jei.category;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;

import net.dries007.tfc.common.blocks.wood.Wood;
import net.dries007.tfc.common.recipes.InstantFluidBarrelRecipe;
import net.dries007.tfc.compat.jei.JEIIntegration;

public class InstantFluidBarrelRecipeCategory extends BarrelRecipeCategory<InstantFluidBarrelRecipe>
{
    public InstantFluidBarrelRecipeCategory(RecipeType<InstantFluidBarrelRecipe> type, IGuiHelper helper)
    {
        super(type, helper, 118, 26, Wood.KAPOK);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, InstantFluidBarrelRecipe recipe, IFocusGroup focuses)
    {
        super.setRecipe(builder, recipe, focuses);
        final int[] positions = slotPositions(recipe);
        final FluidStackIngredient otherFluid = recipe.getAddedFluid();

        inputFluidSlot = builder.addSlot(RecipeIngredientRole.INPUT, positions[0], 5);
        inputFluidSlot.addIngredients(JEIIntegration.FLUID_STACK, collapse(otherFluid));
        inputFluidSlot.setFluidRenderer(1, false, 16, 16);
        inputFluidSlot.setBackground(slot, -1, -1);
    }
}
