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
    }

    @Override
    protected void processRecipe()
    {
        inputs.add(EmiHelpers.toIngredient(recipe.getItemStackIngredient()));
        outputs.add(EmiStack.of(recipe.getResultItem(EmiHelpers.registryAccess())));
    }
}
