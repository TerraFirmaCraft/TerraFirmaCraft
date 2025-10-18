/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.compat.emi.recipe;

import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.resources.ResourceLocation;

import net.dries007.tfc.common.recipes.BlastFurnaceRecipe;
import net.dries007.tfc.compat.emi.EmiHelpers;
import net.dries007.tfc.compat.emi.EmiIntegration;

public class EmiBlastFurnaceRecipe extends BasicRecipe<BlastFurnaceRecipe>
{
    // Including the catalyst in the inputs gives an inaccurate amount in recipe trees, since it is displayed 1x catalyst per 1mb input fluid
    private final EmiIngredient catalyst;

    public EmiBlastFurnaceRecipe(ResourceLocation id, BlastFurnaceRecipe recipe)
    {
        super(EmiIntegration.BLAST_FURNACE, id, 98, 26);
        inputs.add(EmiHelpers.toIngredient(recipe.inputFluid()));
        catalyst = EmiIngredient.of(recipe.catalyst());
        outputs.add(EmiStack.of(recipe.outputFluid().getFluid(), recipe.outputFluid().getAmount()));
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
