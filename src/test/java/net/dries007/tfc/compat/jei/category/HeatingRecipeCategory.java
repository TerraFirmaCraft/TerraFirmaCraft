/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.compat.jei.category;

import java.util.Arrays;
import java.util.List;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotView;
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
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;
import net.neoforge.neoforged.fluids.FluidStack;

import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.capabilities.heat.HeatCapability;
import net.dries007.tfc.common.recipes.HeatingRecipe;
import net.dries007.tfc.compat.jei.JEIIntegration;
import net.dries007.tfc.config.TFCConfig;

public class HeatingRecipeCategory extends BaseRecipeCategory<HeatingRecipe>
{
    public HeatingRecipeCategory(RecipeType<HeatingRecipe> type, IGuiHelper helper)
    {
        super(type, helper, helper.createBlankDrawable(120, 38), new ItemStack(TFCBlocks.FIREPIT.get()));
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, HeatingRecipe recipe, IFocusGroup focuses)
    {
        IRecipeSlotBuilder inputSlot = builder.addSlot(RecipeIngredientRole.INPUT, 21, 17);
        IRecipeSlotBuilder outputSlot = builder.addSlot(RecipeIngredientRole.OUTPUT, 85, 17);

        inputSlot.addIngredients(recipe.getIngredient());
        inputSlot.setBackground(slot, -1,-1);

        final List<ItemStack> outputItems = Arrays.stream(recipe.getIngredient().getItems())
            .map(stack -> recipe.assembleStacked(stack, Integer.MAX_VALUE, 1f))
            .toList();
        final FluidStack resultFluid = recipe.getDisplayOutputFluid();

        if (!outputItems.isEmpty() && !outputItems.stream().allMatch(ItemStack::isEmpty))
        {
            outputSlot.addItemStacks(outputItems);
            if (recipe.getChance() < 1f)
                outputSlot.addTooltipCallback((slot, tooltip) -> tooltip.add(1, Component.translatable("tfc.tooltip.chance", String.format("%.0f", recipe.getChance() * 100f)).withStyle(ChatFormatting.ITALIC)));
        }

        if (!resultFluid.isEmpty())
        {
            outputSlot.addIngredient(JEIIntegration.FLUID_STACK, resultFluid);
            outputSlot.setFluidRenderer(1, false, 16, 16);
        }
        outputSlot.setBackground(slot, -1,-1);
    }

    @Override
    public void draw(HeatingRecipe recipe, IRecipeSlotsView recipeSlots, GuiGraphics graphics, double mouseX, double mouseY)
    {
        fire.draw(graphics, 54, 16);
        fireAnimated.draw(graphics, 54, 16);

        MutableComponent color = TFCConfig.CLIENT.heatTooltipStyle.get().formatColored(recipe.getTemperature());
        if (color != null)
        {
            Minecraft mc = Minecraft.getInstance();
            Font font = mc.font;
            graphics.drawString(font, color, 60 - font.width(color) / 2, 4, 0xFFFFFF, true);
        }

        for (IRecipeSlotView view : recipeSlots.getSlotViews())
        {
            view.getDisplayedItemStack()
                .ifPresent(stack -> HeatCapability.setTemperature(stack, recipe.getTemperature()));
        }
    }
}
