/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.compat.emi.recipe;

import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.resources.ResourceLocation;

import net.dries007.tfc.common.recipes.LoomRecipe;
import net.dries007.tfc.compat.emi.EmiHelpers;
import net.dries007.tfc.compat.emi.EmiIntegration;

public class EmiLoomRecipe extends AutoLayoutRecipe<LoomRecipe>
{
    public EmiLoomRecipe(ResourceLocation id, LoomRecipe recipe)
    {
        super(EmiIntegration.LOOM, id, recipe);
        init(recipe);
    }

    @Override
    protected void processRecipe(LoomRecipe recipe)
    {
        inputs.add(EmiHelpers.toIngredient(recipe.getItemStackIngredient()));
        outputs.add(EmiStack.of(recipe.getResultItem(EmiHelpers.registryAccess())));
    }
}
