/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.compat.emi.recipe;

import dev.emi.emi.api.recipe.EmiCraftingRecipe;
import dev.emi.emi.api.stack.EmiIngredient;
import net.minecraft.resources.ResourceLocation;

import net.dries007.tfc.common.recipes.AdvancedShapedRecipe;
import net.dries007.tfc.compat.emi.EmiHelpers;

// TODO this does not handle shaped recipes that depend on input
public class EmiAdvancedShapedRecipe extends EmiCraftingRecipe
{
    public EmiAdvancedShapedRecipe(ResourceLocation id, AdvancedShapedRecipe recipe)
    {
        super(recipe.getIngredients().stream().map(EmiIngredient::of).toList(), EmiHelpers.nonDecayStack(recipe.getResultItem(EmiHelpers.registryAccess())), id, false);
    }
}
