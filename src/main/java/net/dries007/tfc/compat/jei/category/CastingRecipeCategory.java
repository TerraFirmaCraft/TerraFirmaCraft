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
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.common.recipes.CastingRecipe;
import net.dries007.tfc.util.Metal;

public class CastingRecipeCategory extends BaseRecipeCategory<CastingRecipe>
{
    public CastingRecipeCategory(RecipeType<CastingRecipe> type, IGuiHelper helper)
    {
        super(type, helper, helper.createBlankDrawable(120, 38), new ItemStack(TFCItems.MOLDS.get(Metal.ItemType.INGOT).get()));
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, CastingRecipe recipe, IFocusGroup focuses)
    {
        IRecipeSlotBuilder inputItem = builder.addSlot(RecipeIngredientRole.INPUT, 6, 17);
        IRecipeSlotBuilder inputLiquid = builder.addSlot(RecipeIngredientRole.INPUT, 26, 17);
        IRecipeSlotBuilder outputItem = builder.addSlot(RecipeIngredientRole.OUTPUT, 85, 17);

        inputItem.addIngredients(recipe.getIngredient());
        inputLiquid.addIngredients(VanillaTypes.FLUID, collapse(recipe.getFluidIngredient()));
        inputLiquid.setFluidRenderer(1, false, 16, 16);
        outputItem.addItemStack(recipe.getResultItem());
    }

    @Override
    public void draw(CastingRecipe recipe, IRecipeSlotsView recipeSlots, PoseStack stack, double mouseX, double mouseY)
    {
        slot.draw(stack, 5, 16);
        slot.draw(stack, 25, 16);
        arrow.draw(stack, 48, 16);
        arrowAnimated.draw(stack, 48, 16);
        slot.draw(stack, 84, 16);
    }
}
