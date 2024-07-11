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
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.recipes.AnvilRecipe;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.data.Metal;

public class AnvilRecipeCategory extends BaseRecipeCategory<AnvilRecipe>
{
    public AnvilRecipeCategory(RecipeType<AnvilRecipe> type, IGuiHelper helper)
    {
        super(type, helper, helper.createBlankDrawable(98, 26), new ItemStack(TFCBlocks.METALS.get(Metal.Default.BRONZE).get(Metal.BlockType.ANVIL).get()));
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, AnvilRecipe recipe, IFocusGroup focuses)
    {
        IRecipeSlotBuilder inputSlot = builder.addSlot(RecipeIngredientRole.INPUT, 6, 5);
        IRecipeSlotBuilder outputSlot = builder.addSlot(RecipeIngredientRole.OUTPUT, 76, 5);

        inputSlot.addIngredients(recipe.getInput());
        inputSlot.setBackground(slot, -1, -1);
        outputSlot.addItemStack(recipe.getResultItem(registryAccess()));
        outputSlot.setBackground(slot, -1, -1);
        outputSlot.addTooltipCallback((view, tooltip) -> tooltip.add(Component.translatable("tfc.tooltip.anvil_tier_required", Helpers.translateEnum(Metal.Tier.valueOf(recipe.getMinTier())))));
    }

    @Override
    public void draw(AnvilRecipe recipe, IRecipeSlotsView recipeSlots, GuiGraphics stack, double mouseX, double mouseY)
    {
        arrow.draw(stack, 36, 5);
        arrowAnimated.draw(stack, 36, 5);
    }
}
