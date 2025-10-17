/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.compat.emi.recipe;

import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.common.recipes.SoupPotRecipe;
import net.dries007.tfc.compat.emi.EmiHelpers;
import net.dries007.tfc.compat.emi.widgets.CyclingSlotWidget;

public class EmiSoupPotRecipe extends EmiBasePotRecipe<SoupPotRecipe>
{

    public EmiSoupPotRecipe(ResourceLocation id, SoupPotRecipe recipe)
    {
        super(id, recipe, 113, 80);

        int ingredientCount = 0;
        for (Ingredient ingredient : recipe.getItemIngredients())
        {
            if (!ingredient.isEmpty())
            {
                ingredientCount++;
                inputs.add(EmiIngredient.of(ingredient));
            }
        }
        int servings = SoupPotRecipe.ingredientsToServings(ingredientCount);

        for (ItemLike soup : TFCItems.SOUPS.values())
        {
            outputs.add(EmiHelpers.nonDecayStack(soup.asItem().getDefaultInstance().copyWithCount(servings)));
        }
    }

    @Override
    protected void addOutputWidgets(WidgetHolder widgets)
    {
        widgets.add(new CyclingSlotWidget(outputs, 25, 90, 24)).recipeContext(this);
    }

}
