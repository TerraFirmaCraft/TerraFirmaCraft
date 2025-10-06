package net.dries007.tfc.compat.emi.recipe;

import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.resources.ResourceLocation;

import net.dries007.tfc.common.recipes.CastingRecipe;
import net.dries007.tfc.compat.emi.EmiIntegration;

public class EmiCastingRecipe extends GenericRecipe<CastingRecipe>
{
    public EmiCastingRecipe(ResourceLocation id, CastingRecipe recipe)
    {
        super(EmiIntegration.CASTING, id, recipe, 98, 26);
        inputs.add(EmiIngredient.of(recipe.getIngredient()));
        inputs.add(toIngredient(recipe.getFluidIngredient()));
        outputs.add(EmiStack.of(recipe.getResultItem()));
    }

    @Override
    public void addWidgets(WidgetHolder widgets)
    {
        widgets.addSlot(inputs.get(0), 6, 5);
        widgets.addSlot(inputs.get(1), 26, 5);
        widgets.addSlot(outputs.getFirst(), 76, 5).recipeContext(this);
        widgets.addFillingArrow(48, 5, 3000);
    }
}
