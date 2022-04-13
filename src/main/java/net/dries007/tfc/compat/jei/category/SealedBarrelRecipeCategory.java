/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.compat.jei.category;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

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
        if (recipe.isInfinite())
        {
            RecipeResult<Ingredient> intermediateItem = itemStackProviderIngredient(recipe.getOnSeal(), recipe.getInputItem());
            IRecipeSlotBuilder intermediateSlot = builder.addSlot(RecipeIngredientRole.RENDER_ONLY, 76, 5).setSlotName("intermediate");
            intermediateSlot.addIngredients(intermediateItem.result());
            intermediateSlot.addTooltipCallback((slots, tooltip) -> tooltip.add(1, new TranslatableComponent("tfc.jei.misc.barrel_sealed_full").withStyle(ChatFormatting.DARK_PURPLE, ChatFormatting.ITALIC)));
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

        MutableComponent text = getHourText(recipe.getDuration()).withStyle(ChatFormatting.BLACK);
        Minecraft mc = Minecraft.getInstance();
        Font font = mc.font;
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
        return recipe.getOnSeal() != null ? new int[] {6, 26, 125, 96} : new int[] {21, 41, 91, 0};
    }

    private static MutableComponent getHourText(int ticks)
    {
        if (ticks < 0)
        {
            return new TranslatableComponent("tfc.jei.misc.barrel_sealed");
        }
        int hours = (int) Math.ceil((double) ticks / 1000);
        return new TranslatableComponent("tfc.jei.misc.hours_" + (hours > 1 ? "multiple" : "single"), hours);
    }

    @Override
    protected RecipeResult<Ingredient> getItemResult(SealedBarrelRecipe recipe)
    {
        ItemStackProvider unsealProvider = recipe.getOnUnseal();
        ItemStackProvider outputProvider = recipe.getOutputItem();
        if (unsealProvider != null)
        {
            return itemStackProviderIngredient(unsealProvider, recipe.getInputItem());
        }
        RecipeResult<Ingredient> output = itemStackProviderIngredient(outputProvider, recipe.getInputItem());
        return output.result().isEmpty() ? new RecipeResult<>(!isSame(recipe.getResultItem(), recipe.getInputItem().ingredient().getItems(), ItemStack::getItem), Ingredient.of(recipe.getResultItem())) : output;
    }
}
