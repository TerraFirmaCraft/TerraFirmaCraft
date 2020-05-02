/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.compat.jei.wrappers;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.ParametersAreNonnullByDefault;

import com.google.common.collect.Lists;
import net.minecraft.item.ItemStack;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.dries007.tfc.api.recipes.BloomeryRecipe;
import net.dries007.tfc.compat.jei.TFCJEIPlugin;

@ParametersAreNonnullByDefault
public class BloomeryRecipeWrapper implements IRecipeWrapper
{
    private final List<ItemStack> ingredients;
    private final List<ItemStack> additives;
    private final ItemStack output;

    public BloomeryRecipeWrapper(BloomeryRecipe recipe)
    {
        ingredients = new ArrayList<>();
        additives = new ArrayList<>();
        // Although this looks resource-intensive, it's done one time only
        TFCJEIPlugin.getAllIngredients().forEach(stack -> {
            if (recipe.isValidInput(stack))
            {
                ingredients.add(stack);
            }
            else if (recipe.isValidAdditive(stack))
            {
                additives.add(stack);
            }
        });
        output = recipe.getOutput();
    }

    @Override
    public void getIngredients(IIngredients recipeIngredients)
    {
        List<List<ItemStack>> allInputs = new ArrayList<>();
        allInputs.add(ingredients);
        allInputs.add(additives);
        recipeIngredients.setInputLists(VanillaTypes.ITEM, allInputs);


        List<List<ItemStack>> allOutputs = new ArrayList<>();
        allOutputs.add(Lists.newArrayList(output));
        recipeIngredients.setOutputLists(VanillaTypes.ITEM, allOutputs);
    }
}
