package net.dries007.tfc.compat.emi.recipe;

import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.resources.ResourceLocation;

import net.dries007.tfc.common.recipes.QuernRecipe;
import net.dries007.tfc.compat.emi.EmiHelpers;
import net.dries007.tfc.compat.emi.EmiIntegration;

public class EmiQuernRecipe extends AutoLayoutRecipe<QuernRecipe>
{
    public EmiQuernRecipe(ResourceLocation id, QuernRecipe recipe)
    {
        super(EmiIntegration.QUERN, id, recipe);
    }

    @Override
    protected void processRecipe()
    {
        inputs.add(EmiIngredient.of(recipe.getIngredient()));
        outputs.add(EmiStack.of(recipe.getResultItem(EmiHelpers.registryAccess())));
    }
}
