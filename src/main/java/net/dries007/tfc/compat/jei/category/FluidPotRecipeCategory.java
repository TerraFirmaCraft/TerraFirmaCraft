/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.compat.jei.category;

import com.mojang.blaze3d.vertex.PoseStack;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.ingredient.IGuiFluidStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.RecipeType;
import net.dries007.tfc.common.recipes.FluidPotRecipe;
import net.dries007.tfc.common.recipes.PotRecipe;

public class FluidPotRecipeCategory extends PotRecipeCategory<PotRecipe>
{
    public FluidPotRecipeCategory(RecipeType<PotRecipe> type, IGuiHelper helper)
    {
        super(type, helper, helper.createBlankDrawable(175, 80));
    }

    @Override
    public void setIngredients(PotRecipe recipe, IIngredients ingredients)
    {
        super.setIngredients(recipe, ingredients);
        ingredients.setOutputs(VanillaTypes.FLUID, collapse(ingredients.getOutputs(VanillaTypes.FLUID)));
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, PotRecipe recipe, IIngredients ingredients)
    {
        super.setRecipe(recipeLayout, recipe, ingredients);
        IGuiFluidStackGroup fluidStacks = recipeLayout.getFluidStacks();
        fluidStacks.init(7, false, 146, 26);
        fluidStacks.set(7, ((FluidPotRecipe) recipe).getDisplayFluid());
    }

    @Override
    public void draw(PotRecipe recipe, PoseStack stack, double mouseX, double mouseY)
    {
        super.draw(recipe, stack, mouseX, mouseY);
        slot.draw(stack, 145, 25);
    }
}
