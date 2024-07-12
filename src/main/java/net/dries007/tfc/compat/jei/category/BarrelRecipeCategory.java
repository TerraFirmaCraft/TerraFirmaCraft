/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.compat.jei.category;

import java.util.List;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import net.neoforge.neoforged.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.wood.Wood;
import net.dries007.tfc.common.recipes.BarrelRecipe;
import net.dries007.tfc.compat.jei.JEIIntegration;

public class BarrelRecipeCategory<T extends BarrelRecipe> extends BaseRecipeCategory<T>
{
    protected @Nullable IRecipeSlotBuilder inputFluidSlot;
    protected @Nullable IRecipeSlotBuilder inputItemSlot;
    protected @Nullable IRecipeSlotBuilder outputFluidSlot;
    protected @Nullable IRecipeSlotBuilder outputItemSlot;

    public BarrelRecipeCategory(RecipeType<T> type, IGuiHelper helper, int width, int height, Wood iconType)
    {
        this(type, helper, width, height, new ItemStack(TFCBlocks.WOODS.get(iconType).get(Wood.BlockType.BARREL).get()));
    }

    public BarrelRecipeCategory(RecipeType<T> type, IGuiHelper helper, int width, int height, ItemStack iconType)
    {
        super(type, helper, helper.createBlankDrawable(width, height), iconType);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, T recipe, IFocusGroup focuses)
    {
        inputFluidSlot = null;
        inputItemSlot = null;
        outputFluidSlot = null;
        outputItemSlot = null;

        final int[] positions = slotPositions(recipe);
        final List<FluidStack> inputFluid = collapse(recipe.getInputFluid());
        final List<ItemStack> inputItem = collapse(recipe.getInputItem());
        final FluidStack outputFluid = recipe.getOutputFluid();
        final List<ItemStack> outputItem = collapse(inputItem, recipe.getOutputItem());

        if (!inputFluid.isEmpty())
        {
            inputFluidSlot = builder.addSlot(RecipeIngredientRole.INPUT, inputItem.isEmpty() ? positions[1] : positions[0], 5);
            inputFluidSlot.addIngredients(JEIIntegration.FLUID_STACK, inputFluid);
            inputFluidSlot.setFluidRenderer(1, false, 16, 16);
            inputFluidSlot.setBackground(slot, -1, -1);
        }

        if (!inputItem.isEmpty())
        {
            inputItemSlot = builder.addSlot(RecipeIngredientRole.INPUT, positions[1], 5);
            inputItemSlot.addItemStacks(inputItem);
            inputItemSlot.setBackground(slot, -1, -1);
        }

        if (!outputFluid.isEmpty())
        {
            outputFluidSlot = builder.addSlot(RecipeIngredientRole.OUTPUT, positions[2], 5);
            outputFluidSlot.addIngredient(JEIIntegration.FLUID_STACK, outputFluid);
            outputFluidSlot.setFluidRenderer(1, false, 16, 16);
            outputFluidSlot.setBackground(slot, -1, -1);
        }

        if (!outputItem.isEmpty() && !outputItem.stream().allMatch(ItemStack::isEmpty))
        {
            outputItemSlot = builder.addSlot(RecipeIngredientRole.OUTPUT, outputFluid.isEmpty() ? positions[2] : positions[3], 5);
            outputItemSlot.addItemStacks(outputItem);
            outputItemSlot.setBackground(slot, -1, -1);
        }

        // Link inputs and outputs when focused
        // Only dependent if the item stack provider output internally depends on the input
        if (recipe.getOutputItem().dependsOnInput() && inputItemSlot != null && outputItemSlot != null)
        {
            builder.createFocusLink(inputItemSlot, outputItemSlot);
        }
    }

    @Override
    public void draw(T recipe, IRecipeSlotsView recipeSlots, GuiGraphics stack, double mouseX, double mouseY)
    {
        final int arrowPosition = arrowPosition(recipe);
        arrow.draw(stack, arrowPosition, 5);
        arrowAnimated.draw(stack, arrowPosition, 5);
    }

    protected int[] slotPositions(T recipe)
    {
        return new int[] {6, 26, 76, 96};
    }

    protected int arrowPosition(T recipe)
    {
        return 48;
    }
}
