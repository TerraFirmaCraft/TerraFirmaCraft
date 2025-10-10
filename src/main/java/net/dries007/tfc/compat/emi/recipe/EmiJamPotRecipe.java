package net.dries007.tfc.compat.emi.recipe;

import java.util.Objects;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.resources.ResourceLocation;

import net.dries007.tfc.common.recipes.JamPotRecipe;
import net.dries007.tfc.compat.emi.EmiHelpers;

public class EmiJamPotRecipe extends EmiBasePotRecipe<JamPotRecipe>
{
    public EmiJamPotRecipe(ResourceLocation id, JamPotRecipe recipe)
    {
        super(id, recipe, 113, 80);
        inputs.addAll(groupSimilar(recipe.getItemIngredients(), EmiIngredient::of, Objects::equals));
        outputs.add(EmiStack.of(recipe.getResultItem(EmiHelpers.registryAccess())));
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
