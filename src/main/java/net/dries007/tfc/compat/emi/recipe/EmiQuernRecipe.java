/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.compat.emi.recipe;

import dev.emi.emi.api.stack.EmiIngredient;
import net.minecraft.resources.ResourceLocation;

import net.dries007.tfc.common.recipes.QuernRecipe;
import net.dries007.tfc.compat.emi.EmiHelpers;
import net.dries007.tfc.compat.emi.EmiIntegration;

public class EmiQuernRecipe extends AutoLayoutRecipe<QuernRecipe>
{
    public EmiQuernRecipe(ResourceLocation id, QuernRecipe recipe)
    {
        super(EmiIntegration.QUERN, id, recipe);
        init(recipe);
    }

    @Override
    protected void processRecipe(QuernRecipe recipe)
    {
        inputs.add(EmiIngredient.of(recipe.getIngredient()));
        outputs.add(EmiHelpers.nonDecayStack(recipe.getResultItem(EmiHelpers.registryAccess())));
    }
}
