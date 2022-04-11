/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.compat.jei.category;

import com.mojang.blaze3d.vertex.PoseStack;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import net.dries007.tfc.common.blocks.wood.Wood;
import net.dries007.tfc.common.recipes.SealedBarrelRecipe;

public class SealedBarrelRecipeCategory extends BarrelRecipeCategory<SealedBarrelRecipe>
{
    public SealedBarrelRecipeCategory(RecipeType<SealedBarrelRecipe> type, IGuiHelper helper)
    {
        super(type, helper, 46, Wood.MAPLE);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, SealedBarrelRecipe recipe, IFocusGroup focuses)
    {
        super.setRecipe(builder, recipe, focuses);
        if (recipe.getOnSeal() != null)
        {
            IRecipeSlotBuilder onSeal = builder.addSlot(RecipeIngredientRole.OUTPUT, 76, 25);
            onSeal.addIngredients(itemStackProviderIngredient(recipe.getOnSeal(), recipe.getInputItem()));
        }
        if (recipe.getOnUnseal() != null)
        {
            IRecipeSlotBuilder onUnseal = builder.addSlot(RecipeIngredientRole.OUTPUT, 96, 25);
            onUnseal.addIngredients(itemStackProviderIngredient(recipe.getOnUnseal(), recipe.getInputItem()));
        }
    }

    @Override
    public void draw(SealedBarrelRecipe recipe, IRecipeSlotsView recipeSlots, PoseStack stack, double mouseX, double mouseY)
    {
        super.draw(recipe, recipeSlots, stack, mouseX, mouseY);

        if (recipe.getOnSeal() != null)
        {
            slot.draw(stack, 76, 25);
        }
        if (recipe.getOnUnseal() != null)
        {
            slot.draw(stack, 95, 25);
        }
        // todo: draw duration bottom left
    }
}
