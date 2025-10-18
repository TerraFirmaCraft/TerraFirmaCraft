/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.compat.emi.recipe;

import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.resources.ResourceLocation;

import net.dries007.tfc.common.recipes.BloomeryRecipe;
import net.dries007.tfc.compat.emi.EmiHelpers;
import net.dries007.tfc.compat.emi.EmiIntegration;

public class EmiBloomeryRecipe extends AutoLayoutRecipe<BloomeryRecipe>
{
    public EmiBloomeryRecipe(ResourceLocation id, BloomeryRecipe recipe)
    {
        super(EmiIntegration.BLOOMERY, id, recipe);
        init(recipe);
    }

    @Override
    protected void processRecipe(BloomeryRecipe recipe)
    {
        inputs.add(EmiHelpers.toIngredient(recipe.getInputFluid()));
        inputs.add(EmiHelpers.toIngredient(recipe.getCatalyst()));
        outputs.add(EmiStack.of(recipe.getResultItem(EmiHelpers.registryAccess())));
    }
}
