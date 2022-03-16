/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.compat.jei.category;

import java.util.List;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import com.mojang.blaze3d.vertex.PoseStack;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.common.recipes.PotRecipe;

public abstract class PotRecipeCategory<T extends PotRecipe> extends BaseRecipeCategory<T>
{
    public PotRecipeCategory(RecipeType<T> type, IGuiHelper helper, IDrawable background)
    {
        super(type, helper, background, new ItemStack(TFCItems.POT.get()));
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, PotRecipe recipe, IFocusGroup focuses)
    {
        List<Ingredient> inputs = recipe.getItemIngredients();
        for (int i = 0; i < 5; i++)
        {
            IRecipeSlotBuilder inputSlot = builder.addSlot(RecipeIngredientRole.INPUT, 6 + 20 * i, 6);
            if (i < inputs.size())
            {
                // Functionally the same as using collapse
                inputSlot.addIngredients(inputs.get(i));
            }
        }

        IRecipeSlotBuilder inputFluid = builder.addSlot(RecipeIngredientRole.INPUT, 46, 26);
        inputFluid.addIngredients(VanillaTypes.FLUID, collapse(recipe.getFluidIngredient()));
        inputFluid.setFluidRenderer(1, false, 16, 16);
    }

    @Override
    public void draw(PotRecipe recipe, IRecipeSlotsView recipeSlots, PoseStack stack, double mouseX, double mouseY)
    {
        // Water Input
        slot.draw(stack, 45, 25);
        // item slots
        slot.draw(stack, 5, 5);
        slot.draw(stack, 25, 5);
        slot.draw(stack, 45, 5);
        slot.draw(stack, 65, 5);
        slot.draw(stack, 85, 5);
        // fire
        fire.draw(stack, 47, 45);
        fireAnimated.draw(stack, 47, 45);
        // arrow
        arrow.draw(stack, 103, 26);
        arrowAnimated.draw(stack, 103, 26);
    }
}
