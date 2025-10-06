package net.dries007.tfc.compat.emi.recipe;

import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.resources.ResourceLocation;

import net.dries007.tfc.common.recipes.BlastFurnaceRecipe;
import net.dries007.tfc.compat.emi.EmiIntegration;

public class EmiBlastFurnaceRecipe extends GenericRecipe<BlastFurnaceRecipe>
{
    public EmiBlastFurnaceRecipe(ResourceLocation id, BlastFurnaceRecipe recipe)
    {
        super(EmiIntegration.BLAST_FURNACE, id, recipe, 50, 30);
        inputs.add(toIngredient(recipe.inputFluid()));
        outputs.add(EmiStack.of(recipe.outputFluid().getFluid(), recipe.outputFluid().getAmount()));
    }

    @Override
    public void addWidgets(WidgetHolder widgets)
    {
        widgets.addSlot(inputs.getFirst(), 0, 0);
        widgets.addSlot(outputs.getFirst(), 20, 0).recipeContext(this);
    }
}
