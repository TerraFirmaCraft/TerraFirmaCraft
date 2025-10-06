package net.dries007.tfc.compat.emi.recipe;

import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.crafting.SizedIngredient;

import net.dries007.tfc.common.recipes.BloomeryRecipe;
import net.dries007.tfc.compat.emi.EmiIntegration;

public class EmiBloomeryRecipe extends GenericRecipe<BloomeryRecipe>
{
    // Including the catalyst in the inputs gives an inaccurate amount in recipe trees, since it is displayed 1x catalyst per 1mb input fluid
    private final EmiIngredient catalyst;

    public EmiBloomeryRecipe(ResourceLocation id, BloomeryRecipe recipe)
    {
        super(EmiIntegration.BLOOMERY, id, recipe, 98, 26);
        inputs.add(toIngredient(recipe.getInputFluid()));
        catalyst = toIngredient(recipe.getCatalyst());
        outputs.add(EmiStack.of(recipe.getResultItem(registryAccess())));
    }

    @Override
    public void addWidgets(WidgetHolder widgets)
    {
        widgets.addSlot(inputs.getFirst(), 6, 5);
        widgets.addSlot(catalyst, 26, 5);
        widgets.addSlot(outputs.getFirst(), 76, 5).recipeContext(this);
        widgets.addFillingArrow(48, 5, 3000);
    }
}
