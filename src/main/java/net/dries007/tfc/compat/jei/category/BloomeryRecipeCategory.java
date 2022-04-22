/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.compat.jei.category;

import net.minecraft.world.item.ItemStack;

import com.mojang.blaze3d.vertex.PoseStack;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.recipes.BloomeryRecipe;
import net.dries007.tfc.common.recipes.ingredients.ItemStackIngredient;

/**
 * Superficially similar to CastingRecipeCategory
 */
public class BloomeryRecipeCategory extends BaseRecipeCategory<BloomeryRecipe>
{
    public BloomeryRecipeCategory(RecipeType<BloomeryRecipe> type, IGuiHelper helper)
    {
        super(type, helper, helper.createBlankDrawable(98, 26), new ItemStack(TFCBlocks.BLOOMERY.get()));
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, BloomeryRecipe recipe, IFocusGroup focuses)
    {
        IRecipeSlotBuilder inputItem = builder.addSlot(RecipeIngredientRole.INPUT, 6, 5);
        IRecipeSlotBuilder inputLiquid = builder.addSlot(RecipeIngredientRole.INPUT, 26, 5);
        IRecipeSlotBuilder outputItem = builder.addSlot(RecipeIngredientRole.OUTPUT, 76, 5);

        inputItem.addItemStacks(collapse(new ItemStackIngredient(recipe.getCatalyst(), recipe.getCatalystCount())));
        inputLiquid.addIngredients(VanillaTypes.FLUID, collapse(recipe.getInputFluid()));
        inputLiquid.setFluidRenderer(1, false, 16, 16);
        outputItem.addItemStack(recipe.getResultItem());
    }

    @Override
    public void draw(BloomeryRecipe recipe, IRecipeSlotsView recipeSlots, PoseStack stack, double mouseX, double mouseY)
    {
        slot.draw(stack, 5, 4);
        slot.draw(stack, 25, 4);
        slot.draw(stack, 75, 4);
        arrow.draw(stack, 48, 5);
        arrowAnimated.draw(stack, 48, 5);
    }
}
