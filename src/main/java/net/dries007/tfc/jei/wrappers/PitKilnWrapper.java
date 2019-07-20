/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.jei.wrappers;

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import net.dries007.tfc.jei.IJEIRecipeWrapper;

public class PitKilnWrapper extends TFCRecipeWrapper
{
    public PitKilnWrapper(IJEIRecipeWrapper recipe)
    {
        super(recipe);
    }

    @Override
    public void getIngredients(IIngredients ingredients)
    {
        NonNullList<ItemStack> inputs = getRecipeWrapper().getItemIngredients().get(0).getValidInputList();
        ingredients.setInputs(VanillaTypes.ITEM, inputs);
        ingredients.setOutput(VanillaTypes.ITEM, getRecipeWrapper().getItemOutputs().get(0));
    }
}