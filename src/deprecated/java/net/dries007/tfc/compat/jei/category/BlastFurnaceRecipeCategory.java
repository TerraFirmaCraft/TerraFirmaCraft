/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.compat.jei.category;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;

import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.recipes.BlastFurnaceRecipe;
import net.dries007.tfc.compat.jei.JEIIntegration;

public class BlastFurnaceRecipeCategory extends BaseRecipeCategory<BlastFurnaceRecipe>
{
    public BlastFurnaceRecipeCategory(RecipeType<BlastFurnaceRecipe> type, IGuiHelper helper)
    {
        super(type, helper, helper.createBlankDrawable(98, 26), new ItemStack(TFCBlocks.BLAST_FURNACE.get()));
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, BlastFurnaceRecipe recipe, IFocusGroup focuses)
    {
        builder.addSlot(RecipeIngredientRole.INPUT, 6, 5)
            .addIngredients(recipe.catalyst())
            .setBackground(slot, -1, -1);

        builder.addSlot(RecipeIngredientRole.INPUT, 26, 5)
            .addIngredients(JEIIntegration.FLUID_STACK, collapse(recipe.inputFluid()))
            .setFluidRenderer(1, false, 16, 16)
            .setBackground(slot, -1, -1);

        builder.addSlot(RecipeIngredientRole.OUTPUT, 76, 5)
            .addIngredient(JEIIntegration.FLUID_STACK, recipe.outputFluid())
            .setFluidRenderer(1, false, 16, 16)
            .setBackground(slot, -1, -1);
    }

    @Override
    public void draw(BlastFurnaceRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics graphics, double mouseX, double mouseY)
    {
        arrow.draw(graphics, 48, 5);
        arrowAnimated.draw(graphics, 48, 5);
    }
}
