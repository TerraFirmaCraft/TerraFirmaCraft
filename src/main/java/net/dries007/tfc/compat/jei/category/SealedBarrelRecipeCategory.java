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
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.ItemStack;

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
import net.dries007.tfc.common.recipes.outputs.ItemStackProvider;
import net.dries007.tfc.util.calendar.Calendars;
import net.dries007.tfc.util.calendar.ICalendar;
import org.jetbrains.annotations.NotNull;

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
            RecipeResult<List<ItemStack>> intermediateItem = itemStackProviderIngredient(recipe.getOnSeal(), recipe.getInputItem());
            IRecipeSlotBuilder intermediateSlot = builder.addSlot(RecipeIngredientRole.RENDER_ONLY, 76, 5);
            intermediateSlot.addItemStacks(intermediateItem.result());
            intermediateSlot.addTooltipCallback((slots, tooltip) -> tooltip.add(1, new TranslatableComponent("tfc.tooltip.while_sealed_description").withStyle(ChatFormatting.DARK_PURPLE, ChatFormatting.ITALIC)));
            if (!intermediateItem.transforms())
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
            slot.draw(stack, 75, 4);
            arrow.draw(stack, 98, 5);
            arrowAnimated.draw(stack, 98, 5);
        }

        MutableComponent text = getDurationText(recipe).withStyle(ChatFormatting.BLACK);
        final Minecraft mc = Minecraft.getInstance();
        final Font font = mc.font;
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

    @Override
    @NotNull
    protected RecipeResult<List<ItemStack>> getItemResult(SealedBarrelRecipe recipe)
    {
        ItemStackProvider unsealProvider = recipe.getOnUnseal();
        if (unsealProvider != null)
        {
            return itemStackProviderIngredient(unsealProvider, recipe.getInputItem());
        }
        return super.getItemResult(recipe);
    }

    protected static MutableComponent getDurationText(SealedBarrelRecipe recipe)
    {
        if (recipe.isInfinite())
        {
            return new TranslatableComponent("tfc.tooltip.while_sealed");
        }
        return ICalendar.getTimeDelta(recipe.getDuration(), Calendars.CLIENT.getCalendarDaysInMonth());
    }

}
