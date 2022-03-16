/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.compat.jei.category;

import com.mojang.blaze3d.vertex.PoseStack;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
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
    public void setRecipe(IRecipeLayoutBuilder builder, PotRecipe recipe, IFocusGroup focuses)
    {
        super.setRecipe(builder, recipe, focuses);
        IRecipeSlotBuilder fluidOutput = builder.addSlot(RecipeIngredientRole.OUTPUT, 146, 26);

        fluidOutput.addIngredient(VanillaTypes.FLUID, ((FluidPotRecipe) recipe).getDisplayFluid());
        fluidOutput.setFluidRenderer(1, false, 16, 16);
    }

    @Override
    public void draw(PotRecipe recipe, IRecipeSlotsView recipeSlots, PoseStack stack, double mouseX, double mouseY)
    {
        super.draw(recipe, recipeSlots, stack, mouseX, mouseY);
        slot.draw(stack, 145, 25);
    }
}
