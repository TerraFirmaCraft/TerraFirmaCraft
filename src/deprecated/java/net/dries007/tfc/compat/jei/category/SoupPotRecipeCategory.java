/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.compat.jei.category;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.common.recipes.PotRecipe;


public class SoupPotRecipeCategory extends PotRecipeCategory<PotRecipe>
{
    public SoupPotRecipeCategory(RecipeType<PotRecipe> type, IGuiHelper helper)
    {
        super(type, helper, helper.createBlankDrawable(175, 50));
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, PotRecipe recipe, IFocusGroup focuses)
    {
        setInitialIngredients(builder, recipe);

        int ingredientCount = 0;
        for (Ingredient ingredient : recipe.getItemIngredients())
        {
            if (!ingredient.isEmpty())
                ingredientCount++;
        }
        final int servings = (int) (ingredientCount / 2f) + 1;

        IRecipeSlotBuilder outputItem = builder.addSlot(RecipeIngredientRole.OUTPUT, 126, 6);
        outputItem.addItemStacks(TFCItems.SOUPS.values().stream().map(reg -> new ItemStack(reg.get(), servings)).toList());
        outputItem.setBackground(slot, -1, -1);
    }

    @Override
    public void draw(PotRecipe recipe, IRecipeSlotsView recipeSlots, GuiGraphics stack, double mouseX, double mouseY)
    {
        // fire
        fire.draw(stack, 27, 25);
        fireAnimated.draw(stack, 27, 25);
        // arrow
        arrow.draw(stack, 103, 26);
        arrowAnimated.draw(stack, 103, 26);
    }
}
