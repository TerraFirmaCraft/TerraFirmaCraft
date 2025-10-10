package net.dries007.tfc.compat.emi.recipe;

import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.resources.ResourceLocation;

import net.dries007.tfc.common.recipes.LoomRecipe;
import net.dries007.tfc.compat.emi.EmiIntegration;

public class EmiLoomRecipe extends GenericRecipe<LoomRecipe>
{
    public EmiLoomRecipe(ResourceLocation id, LoomRecipe recipe)
    {
        super(EmiIntegration.LOOM, id, recipe, 78, 30);
        inputs.add(toIngredient(recipe.getItemStackIngredient()));
        outputs.add(EmiStack.of(recipe.getResultItem(registryAccess())));
    }
}
