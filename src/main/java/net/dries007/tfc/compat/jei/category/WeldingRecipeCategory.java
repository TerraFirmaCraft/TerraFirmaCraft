/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.compat.jei.category;

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
import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.common.recipes.WeldingRecipe;
import net.dries007.tfc.util.Metal;

public class WeldingRecipeCategory extends BaseRecipeCategory<WeldingRecipe>
{
    public WeldingRecipeCategory(RecipeType<WeldingRecipe> type, IGuiHelper helper)
    {
        super(type, helper, helper.createBlankDrawable(118, 26), new ItemStack(TFCItems.METAL_ITEMS.get(Metal.Default.WROUGHT_IRON).get(Metal.ItemType.HAMMER).get()));
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, WeldingRecipe recipe, IFocusGroup focuses)
    {
        IRecipeSlotBuilder inputSlot = builder.addSlot(RecipeIngredientRole.INPUT, 6, 5);
        IRecipeSlotBuilder inputSlot2 = builder.addSlot(RecipeIngredientRole.INPUT, 26, 5);
        IRecipeSlotBuilder flux = builder.addSlot(RecipeIngredientRole.CATALYST, 46, 5);
        IRecipeSlotBuilder outputSlot = builder.addSlot(RecipeIngredientRole.OUTPUT, 96, 5);

        inputSlot.addIngredients(recipe.getFirstInput());
        inputSlot2.addIngredients(recipe.getSecondInput());
        flux.addIngredients(Ingredient.of(TFCTags.Items.FLUX));
        outputSlot.addItemStack(recipe.getResultItem());
    }

    @Override
    public void draw(WeldingRecipe recipe, IRecipeSlotsView recipeSlots, PoseStack stack, double mouseX, double mouseY)
    {
        slot.draw(stack, 5, 4);
        slot.draw(stack, 25, 4);
        slot.draw(stack, 45, 4);
        slot.draw(stack, 95, 4);
        arrow.draw(stack, 68, 5);
        arrowAnimated.draw(stack, 68, 5);
    }
}
