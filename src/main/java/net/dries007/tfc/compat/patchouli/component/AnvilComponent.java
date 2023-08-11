/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.compat.patchouli.component;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeType;

import net.dries007.tfc.common.recipes.AnvilRecipe;
import net.dries007.tfc.common.recipes.TFCRecipeTypes;

public class AnvilComponent extends InputOutputComponent<AnvilRecipe>
{
    @Override
    protected RecipeType<AnvilRecipe> getRecipeType()
    {
        return TFCRecipeTypes.ANVIL.get();
    }

    @Override
    public Ingredient getIngredient(AnvilRecipe recipe)
    {
        return recipe.getInput();
    }

    @Override
    public ItemStack getOutput(AnvilRecipe recipe)
    {
        return recipe.getResultItem(null);
    }
}
