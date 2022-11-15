/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.compat.jei.category;

import java.util.List;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;

import net.minecraftforge.fluids.FluidStack;

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
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.calendar.Calendars;
import net.dries007.tfc.util.calendar.ICalendar;

public class SealedBarrelRecipeCategory extends BarrelRecipeCategory<SealedBarrelRecipe>
{
    public SealedBarrelRecipeCategory(RecipeType<SealedBarrelRecipe> type, IGuiHelper helper)
    {
        super(type, helper, 148, 32, Wood.MAPLE);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, SealedBarrelRecipe recipe, IFocusGroup focuses)
    {
        super.setRecipe(builder, recipe, focuses);

        if (recipe.getOnSeal() != null)
        {
            // Assumes that input -> onSeal -> onUnseal apply and reverse a transformation, so we only need to show the intermediate, sealed, state.
            final List<ItemStack> inputItem = collapse(recipe.getInputItem());
            final List<ItemStack> intermediateItem = collapse(inputItem, recipe.getOnSeal());
            final List<ItemStack> outputItem = collapse(inputItem, recipe.getOutputItem());

            final IRecipeSlotBuilder intermediateSlot = builder.addSlot(RecipeIngredientRole.RENDER_ONLY, 76, 5);
            intermediateSlot.addItemStacks(intermediateItem);
            intermediateSlot.addTooltipCallback((slots, tooltip) -> tooltip.add(1, Helpers.translatable("tfc.tooltip.while_sealed_description").withStyle(ChatFormatting.DARK_PURPLE, ChatFormatting.ITALIC)));
            intermediateSlot.setBackground(slot, -1, -1);

            // Note that the output item might be empty as parsed by the super() call, so we need to re-check it.
            if ((outputItem.isEmpty() || outputItem.stream().allMatch(ItemStack::isEmpty)) && recipe.getOnUnseal() != null)
            {
                // Re-do the output items, but this time collapsing from the intermediate slot
                if (outputItemSlot == null)
                {
                    final int[] positions = slotPositions(recipe);
                    final FluidStack outputFluid = recipe.getOutputFluid();
                    outputItemSlot = builder.addSlot(RecipeIngredientRole.OUTPUT, outputFluid.isEmpty() ? positions[2] : positions[3], 5);
                }

                final List<ItemStack> outputFromIntermediate = collapse(intermediateItem, recipe.getOnUnseal());
                outputItemSlot.addItemStacks(outputFromIntermediate);
                outputItemSlot.setBackground(slot, -1, -1);
            }

            // Create a link between all item slots if the seal behavior depends on the input
            if (recipe.getOnSeal().dependsOnInput() && inputItemSlot != null && outputItemSlot != null)
            {
                builder.createFocusLink(intermediateSlot, inputItemSlot, outputItemSlot);
            }
        }
    }

    @Override
    public void draw(SealedBarrelRecipe recipe, IRecipeSlotsView recipeSlots, PoseStack stack, double mouseX, double mouseY)
    {
        super.draw(recipe, recipeSlots, stack, mouseX, mouseY);

        if (recipe.getOnSeal() != null)
        {
            arrow.draw(stack, 98, 5);
            arrowAnimated.draw(stack, 98, 5);
        }

        final MutableComponent text = (recipe.isInfinite() ?
            Helpers.translatable("tfc.tooltip.while_sealed") :
            Calendars.CLIENT.getTimeDelta(recipe.getDuration())).withStyle(ChatFormatting.BLACK);
        final Font font = Minecraft.getInstance().font;
        font.draw(stack, text, 74f - font.width(text) / 2.0f, 24f, 0xFFFFFF);
    }

    @Override
    protected int arrowPosition(SealedBarrelRecipe recipe)
    {
        return recipe.getOnSeal() != null ? super.arrowPosition(recipe) : 63;
    }

    @Override
    protected int[] slotPositions(SealedBarrelRecipe recipe)
    {
        return recipe.getOnSeal() != null ? new int[] {6, 26, 125, 96} : new int[] {21, 41, 91, 111};
    }
}
