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
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;

import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.recipes.AlloyRecipe;
import net.dries007.tfc.compat.jei.JEIIntegration;
import net.dries007.tfc.util.AlloyRange;
import net.dries007.tfc.util.FluidAlloy;

public class AlloyRecipeCategory extends BaseRecipeCategory<AlloyRecipe>
{
    // Make sure this is an even number
    private static final int MAX_HEIGHT = 82;
    // Determines where the input columns start
    private static final int FIRST_COLUMN_X = 4;
    private static final int SECOND_COLUMN_X = 70;

    public AlloyRecipeCategory(RecipeType<AlloyRecipe> type, IGuiHelper helper)
    {
        super(type, helper, helper.createBlankDrawable(170, MAX_HEIGHT), new ItemStack(TFCBlocks.CRUCIBLE.get()));
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, AlloyRecipe recipe, IFocusGroup focuses)
    {
        IRecipeSlotBuilder fluidOutput = builder.addSlot(RecipeIngredientRole.OUTPUT, 149, MAX_HEIGHT / 2 - slot.getHeight() / 2 + 1);
        int[] positions = getPositions(recipe.contents().size());
        int iteration = 0;
        for (AlloyRange range : recipe.contents())
        {
            int x = (iteration % 2 == 0 ? FIRST_COLUMN_X : SECOND_COLUMN_X) + 1;
            int y = positions[Math.floorDiv(iteration, 2)] + 1;
            IRecipeSlotBuilder liquidSlot = builder.addSlot(RecipeIngredientRole.INPUT, x, y);
            liquidSlot.addIngredient(JEIIntegration.FLUID_STACK, new FluidStack(range.fluid(), 1000));
            liquidSlot.setBackground(slot, -1, -1);
            iteration++;
        }

        fluidOutput.addIngredient(JEIIntegration.FLUID_STACK, new FluidStack(recipe.result(), 1000));
        fluidOutput.setBackground(slot, -1, -1);
    }

    @Override
    public void draw(AlloyRecipe recipe, IRecipeSlotsView recipeSlots, GuiGraphics graphics, double mouseX, double mouseY)
    {
        Minecraft mc = Minecraft.getInstance();
        Font font = mc.font;
        int[] positions = getPositions(recipe.contents().size());
        int iteration = 0;
        for (AlloyRange range : recipe.contents())
        {
            // Put the text slightly after the slot
            int x = (iteration % 2 == 0 ? FIRST_COLUMN_X : SECOND_COLUMN_X) + slot.getWidth() + 2;
            // Vertically centers the text on the slot
            int y = positions[Math.floorDiv(iteration, 2)] + slot.getHeight() / 2 - Math.floorDiv(font.lineHeight, 2);
            graphics.drawString(font, Component.literal(formatRange(range)).withStyle(ChatFormatting.BLACK), x, y, 0xFFFFFF, false);
            iteration++;
        }
        fire.draw(graphics, 130, MAX_HEIGHT / 2 - fire.getHeight() / 2);
        fireAnimated.draw(graphics, 130, MAX_HEIGHT / 2 - fireAnimated.getHeight() / 2);
    }

    protected int[] getPositions(int rangesSize)
    {
        int rows = (int) Math.ceil(rangesSize / 2d);
        int spacing = 2; // Could change this to scale based off of the amount of rows
        int[] positions = new int[rows];
        int totalHeight = slot.getHeight() * rows + spacing * (rows - 1);
        int currentHeight = (MAX_HEIGHT - totalHeight) / 2;
        for (int i = 0; i < rows; i++)
        {
            positions[i] = currentHeight;
            currentHeight += slot.getHeight() + spacing;
        }

        return positions;
    }

    protected String formatRange(AlloyRange range)
    {
        // Min and max are (roughly) equal, so just so one number to display
        if (Math.abs(range.max() - range.min()) < FluidAlloy.EPSILON)
        {
            return String.format("%.0f%%", range.max() * 100);
        }
        return String.format("%.0f-%.0f%%", range.min() * 100, range.max() * 100);
    }
}
