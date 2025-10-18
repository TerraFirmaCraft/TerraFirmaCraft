/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.compat.emi.recipe;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.stack.EmiIngredient;
import net.minecraft.resources.ResourceLocation;

import net.dries007.tfc.common.recipes.JamPotRecipe;
import net.dries007.tfc.compat.emi.EmiHelpers;

public class EmiJamPotRecipe extends EmiBasePotRecipe<JamPotRecipe>
{
    public EmiJamPotRecipe(ResourceLocation id, JamPotRecipe recipe)
    {
        super(id, recipe, 113, 80);
        inputs.addAll(groupSimilar(recipe.getItemIngredients(), EmiIngredient::of, Object::equals));
        outputs.add(EmiHelpers.nonDecayStack(recipe.getResultItem(EmiHelpers.registryAccess())));
    }

    @Override
    public int compareTo(EmiRecipe other)
    {
        if (other instanceof EmiJamPotRecipe jamPot)
        {
            return outputs.size() - jamPot.outputs.size();
        }
        return super.compareTo(other);
    }
}
