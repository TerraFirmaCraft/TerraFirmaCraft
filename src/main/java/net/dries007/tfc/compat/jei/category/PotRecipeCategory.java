/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.compat.jei.category;


import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.common.recipes.PotRecipe;
import net.dries007.tfc.compat.jei.JEIIntegration;

public abstract class PotRecipeCategory<T extends PotRecipe> extends BaseRecipeCategory<T>
{
    public PotRecipeCategory(RecipeType<T> type, IGuiHelper helper, IDrawable background)
    {
        super(type, helper, background, new ItemStack(TFCItems.POT.get()));
    }

    protected void setInitialIngredients(IRecipeLayoutBuilder builder, PotRecipe recipe)
    {
        int i = 0;
        for (Ingredient ingredient : recipe.getItemIngredients())
        {
            if (!ingredient.isEmpty())
            {
                IRecipeSlotBuilder inputSlot = builder.addSlot(RecipeIngredientRole.INPUT, 6 + 20 * i, 6);
                inputSlot.addIngredients(ingredient);
                inputSlot.setBackground(slot, -1, -1);
                i++;
            }
        }

        IRecipeSlotBuilder inputFluid = builder.addSlot(RecipeIngredientRole.INPUT, 46, 26);
        inputFluid.addIngredients(JEIIntegration.FLUID_STACK, collapse(recipe.getFluidIngredient()));
        inputFluid.setFluidRenderer(1, false, 16, 16);
        inputFluid.setBackground(slot, -1, -1);
    }

}
