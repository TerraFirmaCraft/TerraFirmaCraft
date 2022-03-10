/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.compat.jei.category;

import net.minecraft.resources.ResourceLocation;

import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import net.dries007.tfc.common.recipes.SoupPotRecipe;

public class SoupPotRecipeCategory extends PotRecipeCategory<SoupPotRecipe>
{
    public SoupPotRecipeCategory(ResourceLocation uId, IGuiHelper helper)
    {
        super(uId, helper, helper.createBlankDrawable(175, 80), SoupPotRecipe.class);
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, SoupPotRecipe recipe, IIngredients ingredients)
    {
        super.setRecipe(recipeLayout, recipe, ingredients);
    }

    //todo soup pot recipes are not actually implemented
}
