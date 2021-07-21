/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes;

import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;

public abstract class TypedRecipeSerializer<R extends IRecipe<?>> extends RecipeSerializer<R>
{
    public abstract IRecipeType<?> getRecipeType();
}
