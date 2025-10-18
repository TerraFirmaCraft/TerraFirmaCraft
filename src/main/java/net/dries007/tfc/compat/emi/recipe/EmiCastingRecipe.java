/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.compat.emi.recipe;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.resources.ResourceLocation;

import net.dries007.tfc.common.recipes.CastingRecipe;
import net.dries007.tfc.compat.emi.EmiHelpers;
import net.dries007.tfc.compat.emi.EmiIntegration;

public class EmiCastingRecipe extends AutoLayoutRecipe<CastingRecipe>
{
    public EmiCastingRecipe(ResourceLocation id, CastingRecipe recipe)
    {
        super(EmiIntegration.CASTING, id, recipe);
        init(recipe);
    }

    @Override
    protected void processRecipe(CastingRecipe recipe)
    {
        inputs.add(EmiIngredient.of(recipe.getIngredient()));
        inputs.add(EmiHelpers.toIngredient(recipe.getFluidIngredient()));
        outputs.add(EmiStack.of(recipe.getResultItem()));
    }

    @Override
    public int compareTo(EmiRecipe other)
    {
        if (other instanceof EmiCastingRecipe r)
        {
            ResourceLocation fluidA = inputs.getLast().getEmiStacks().getFirst().getId();
            ResourceLocation fluidB = r.getInputs().getLast().getEmiStacks().getFirst().getId();
            int fluidCompare = fluidA.compareTo(fluidB);
            if (fluidCompare == 0)
            {
                ResourceLocation moldA = inputs.getFirst().getEmiStacks().getFirst().getId();
                ResourceLocation moldB = r.getInputs().getFirst().getEmiStacks().getFirst().getId();
                return moldA.compareTo(moldB);
            }
            return fluidCompare;
        }
        return super.compareTo(other);
    }
}
