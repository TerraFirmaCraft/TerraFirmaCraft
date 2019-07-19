/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.jei.wrappers;

import mezz.jei.api.recipe.IRecipeWrapper;
import net.dries007.tfc.jei.IJEIRecipeWrapper;

public abstract class TFCRecipeWrapper implements IRecipeWrapper
{
    private IJEIRecipeWrapper recipeWrapper;

    protected TFCRecipeWrapper(IJEIRecipeWrapper recipeWrapper)
    {
        this.recipeWrapper = recipeWrapper;
    }

    protected IJEIRecipeWrapper getRecipeWrapper()
    {
        return this.recipeWrapper;
    }
}
