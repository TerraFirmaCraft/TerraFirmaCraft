package net.dries007.tfc.compat.emi.recipe;

import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.resources.ResourceLocation;

import net.dries007.tfc.common.recipes.BloomeryRecipe;
import net.dries007.tfc.compat.emi.EmiHelpers;
import net.dries007.tfc.compat.emi.EmiIntegration;

public class EmiBloomeryRecipe extends AutoLayoutRecipe<BloomeryRecipe>
{
    public EmiBloomeryRecipe(ResourceLocation id, BloomeryRecipe recipe)
    {
        super(EmiIntegration.BLOOMERY, id, recipe);
    }

    @Override
    protected void processRecipe()
    {
        inputs.add(EmiHelpers.toIngredient(recipe.getInputFluid()));
        inputs.add(EmiHelpers.toIngredient(recipe.getCatalyst()));
        outputs.add(EmiStack.of(recipe.getResultItem(EmiHelpers.registryAccess())));
    }
}
