/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes;

import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;

public abstract class TypedRecipeSerializer<R extends Recipe<?>> extends RecipeSerializerImpl<R>
{
    public abstract RecipeType<?> getRecipeType();
}
