/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.jei.wrappers;

import mezz.jei.api.ingredients.IIngredients;
import net.dries007.tfc.jei.IJEIRecipeWrapper;

public class WeldingWrapper extends TFCRecipeWrapper
{
    public WeldingWrapper(IJEIRecipeWrapper recipe)
    {
        super(recipe);
    }

    @Override
    public void getIngredients(IIngredients ingredients)
    {
        //todo fix this
        //NonNullList<ItemStack> inputs1 = getRecipeWrapper().getItemIngredients().get(0).getValidInputList();
        //NonNullList<ItemStack> inputs2 = getRecipeWrapper().getItemIngredients().get(1).getValidInputList();
        //List<NonNullList<ItemStack>> allInputs = new ArrayList<>();
        //allInputs.add(inputs1);
        //allInputs.add(inputs2);
        //ingredients.setInputLists(VanillaTypes.ITEM, allInputs);
        //ingredients.setInputLists(VanillaTypes.ITEM, allInputs);
        //ingredients.setOutput(VanillaTypes.ITEM, getRecipeWrapper().getItemOutputs().get(0));
    }
}