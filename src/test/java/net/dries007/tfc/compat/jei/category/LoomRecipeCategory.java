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

import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.common.recipes.LoomRecipe;

public class LoomRecipeCategory extends BaseRecipeCategory<LoomRecipe>
{
    public LoomRecipeCategory(RecipeType<LoomRecipe> type, IGuiHelper helper)
    {
        super(type, helper, helper.createBlankDrawable(78, 26), new ItemStack(TFCItems.BURLAP_CLOTH.get()));
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, LoomRecipe recipe, IFocusGroup focuses)
    {
        IRecipeSlotBuilder inputItem = builder.addSlot(RecipeIngredientRole.INPUT, 6, 5);
        IRecipeSlotBuilder outputItem = builder.addSlot(RecipeIngredientRole.OUTPUT, 56, 5);

        // The ingredient doesn't come with an amount, but recipes take more than one
        inputItem.addItemStacks(collapse(recipe.getItemStackIngredient()));
        outputItem.addItemStack(recipe.getResultItem(registryAccess()));

        inputItem.setBackground(slot, -1, -1);
        outputItem.setBackground(slot, -1, -1);
    }

    @Override
    public void draw(LoomRecipe recipe, IRecipeSlotsView recipeSlots, GuiGraphics stack, double mouseX, double mouseY)
    {
        arrow.draw(stack, 28, 5);
        arrowAnimated.draw(stack, 28, 5);
    }
}
