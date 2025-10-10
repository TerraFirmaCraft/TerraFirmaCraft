package net.dries007.tfc.compat.emi.recipe;

import dev.emi.emi.api.recipe.EmiRecipe;

public interface ComparableRecipe
{
    int compareTo(EmiRecipe other);
}
