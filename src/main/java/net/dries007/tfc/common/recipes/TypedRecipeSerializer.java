package net.dries007.tfc.common.recipes;

import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;

public abstract class TypedRecipeSerializer<R extends IRecipe<?>> extends RecipeSerializer<R>
{
    public abstract IRecipeType<?> getRecipeType();
}
