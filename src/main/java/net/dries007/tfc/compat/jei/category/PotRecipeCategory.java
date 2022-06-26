/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.compat.jei.category;


import net.minecraft.world.item.ItemStack;

import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.RecipeType;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.common.recipes.PotRecipe;

public abstract class PotRecipeCategory<T extends PotRecipe> extends BaseRecipeCategory<T>
{
    public PotRecipeCategory(RecipeType<T> type, IGuiHelper helper, IDrawable background)
    {
        super(type, helper, background, new ItemStack(TFCItems.POT.get()));
    }
}
