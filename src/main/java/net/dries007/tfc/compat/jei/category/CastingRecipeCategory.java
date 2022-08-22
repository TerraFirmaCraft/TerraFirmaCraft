/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.compat.jei.category;

import net.minecraft.world.item.ItemStack;

import com.mojang.blaze3d.vertex.PoseStack;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.common.recipes.CastingRecipe;
import net.dries007.tfc.compat.jei.JEIIntegration;
import net.dries007.tfc.util.Metal;

public class CastingRecipeCategory extends BaseRecipeCategory<CastingRecipe>
{
    public CastingRecipeCategory(RecipeType<CastingRecipe> type, IGuiHelper helper)
    {
        super(type, helper, helper.createBlankDrawable(98, 26), new ItemStack(TFCItems.MOLDS.get(Metal.ItemType.INGOT).get()));
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, CastingRecipe recipe, IFocusGroup focuses)
    {
        builder.addSlot(RecipeIngredientRole.INPUT, 6, 5)
            .addIngredients(recipe.getIngredient())
            .setBackground(slot, -1, -1);

        builder.addSlot(RecipeIngredientRole.INPUT, 26, 5)
            .addIngredients(JEIIntegration.FLUID_STACK, collapse(recipe.getFluidIngredient()))
            .setFluidRenderer(1, false, 16, 16)
            .setBackground(slot, -1, -1);

        builder.addSlot(RecipeIngredientRole.OUTPUT, 76, 5)
            .addItemStack(recipe.getResultItem())
            .setBackground(slot, -1, -1);
    }

    @Override
    public void draw(CastingRecipe recipe, IRecipeSlotsView recipeSlots, PoseStack stack, double mouseX, double mouseY)
    {
        arrow.draw(stack, 48, 5);
        arrowAnimated.draw(stack, 48, 5);
    }
}
