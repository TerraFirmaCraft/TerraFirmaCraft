/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.compat.jei.category;

import java.util.List;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import com.mojang.blaze3d.vertex.PoseStack;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IGuiFluidStackGroup;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
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
    public void setIngredients(T recipe, IIngredients ingredients)
    {
        ingredients.setInputIngredients(recipe.getItemIngredients());
        ingredients.setInputs(VanillaTypes.FLUID, collapse(recipe.getFluidIngredient()));
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, T recipe, IIngredients ingredients)
    {
        IGuiItemStackGroup itemStacks = recipeLayout.getItemStacks();
        IGuiFluidStackGroup fluidStacks = recipeLayout.getFluidStacks();

        itemStacks.init(0, true, 25, 25);
        itemStacks.init(1, true, 45, 25);
        itemStacks.init(2, true, 65, 25);
        itemStacks.init(3, true, 85, 25);
        itemStacks.init(4, true, 105, 25);
        fluidStacks.init(5, true, 6, 26);

        List<List<ItemStack>> inputs = ingredients.getInputs(VanillaTypes.ITEM);
        for (int i = 0; i < inputs.size(); i++)
        {
            List<ItemStack> ingredientItems = inputs.get(i);
            if (!ingredientItems.isEmpty())
            {
                itemStacks.set(i, inputs.get(i));
            }
        }
        fluidStacks.set(5, collapse(ingredients.getInputs(VanillaTypes.FLUID)));
    }

    @Override
    public void draw(T recipe, PoseStack stack, double mouseX, double mouseY)
    {
        // Water Input
        slot.draw(stack, 5, 25);
        // item slots
        slot.draw(stack, 25, 25);
        slot.draw(stack, 45, 25);
        slot.draw(stack, 65, 25);
        slot.draw(stack, 85, 25);
        slot.draw(stack, 105, 25);
        // fire
        fire.draw(stack, 127, 27);
        fireAnimated.draw(stack, 127, 27);
    }
}
