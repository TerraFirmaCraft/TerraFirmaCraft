/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.compat.jei.category;

import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.RecipeType;

import net.dries007.tfc.common.blocks.wood.Wood;
import net.dries007.tfc.common.recipes.InstantBarrelRecipe;

public class InstantBarrelRecipeCategory extends BarrelRecipeCategory<InstantBarrelRecipe>
{
    public InstantBarrelRecipeCategory(RecipeType<InstantBarrelRecipe> type, IGuiHelper helper)
    {
        super(type, helper, 118, 26, Wood.DOUGLAS_FIR);
    }
}
