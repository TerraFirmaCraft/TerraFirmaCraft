/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.compat.jei.wrappers;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.dries007.tfc.compat.jei.IJEISimpleRecipe;
import net.dries007.tfc.objects.inventory.ingredient.IIngredient;

@ParametersAreNonnullByDefault
public class SimpleRecipeWrapper implements IRecipeWrapper
{
    private final IJEISimpleRecipe recipeWrapper;

    public SimpleRecipeWrapper(IJEISimpleRecipe recipeWrapper)
    {
        this.recipeWrapper = recipeWrapper;
    }

    @Override
    public void getIngredients(IIngredients ingredients)
    {
        List<List<ItemStack>> allInputs = new ArrayList<>();
        List<IIngredient<ItemStack>> listInputs = recipeWrapper.getIngredients();
        for (IIngredient<ItemStack> input : listInputs)
        {
            allInputs.add(input.getValidIngredients());
        }
        ingredients.setInputLists(VanillaTypes.ITEM, allInputs);

        List<List<ItemStack>> allOutputs = new ArrayList<>();
        List<ItemStack> listOutputs = recipeWrapper.getOutputs();
        for (ItemStack stack : listOutputs)
        {
            allOutputs.add(NonNullList.withSize(1, stack));
        }
        ingredients.setOutputLists(VanillaTypes.ITEM, allOutputs);
    }
}
