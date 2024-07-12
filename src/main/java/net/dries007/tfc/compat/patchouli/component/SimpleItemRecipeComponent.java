/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.compat.patchouli.component;


import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import net.dries007.tfc.common.recipes.ItemRecipe;

public abstract class SimpleItemRecipeComponent<T extends ItemRecipe> extends InputOutputComponent<T>
{
    @Override
    public Ingredient getIngredient(T recipe)
    {
        return recipe.getIngredient();
    }

    @Override
    public ItemStack getOutput(T recipe)
    {
        return recipe.getResultItem(null);
    }
}
