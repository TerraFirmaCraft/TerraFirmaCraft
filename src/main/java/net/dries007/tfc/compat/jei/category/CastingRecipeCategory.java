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
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.common.recipes.CastingRecipe;
import net.dries007.tfc.util.Metal;

public class CastingRecipeCategory extends BaseRecipeCategory<CastingRecipe>
{
    public CastingRecipeCategory(ResourceLocation uId, IGuiHelper helper)
    {
        super(uId, helper, helper.createBlankDrawable(120, 38), new ItemStack(TFCItems.MOLDS.get(Metal.ItemType.INGOT).get()), CastingRecipe.class);
    }

    @Override
    public void setIngredients(CastingRecipe recipe, IIngredients ingredients)
    {
        ingredients.setInputIngredients(List.of(recipe.getIngredient()));
        ingredients.setInputs(VanillaTypes.FLUID, collapse(recipe.getFluidIngredient()));
        ingredients.setOutput(VanillaTypes.ITEM, recipe.getResultItem());
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, CastingRecipe recipe, IIngredients ingredients)
    {
        var fluidStacks = recipeLayout.getFluidStacks();
        var itemStacks = recipeLayout.getItemStacks();

        itemStacks.init(0, true, 5, 16);
        fluidStacks.init(1, true, 26, 17);
        itemStacks.init(2, false, 84, 16);

        itemStacks.set(0, collapse(ingredients.getInputs(VanillaTypes.ITEM)));
        fluidStacks.set(1, collapse(ingredients.getInputs(VanillaTypes.FLUID)));
        itemStacks.set(2, ingredients.getOutputs(VanillaTypes.ITEM).get(0));
    }

    @Override
    public void draw(CastingRecipe recipe, PoseStack stack, double mouseX, double mouseY)
    {
        slot.draw(stack, 5, 16);
        slot.draw(stack, 25, 16);
        arrow.draw(stack, 48, 16);
        arrowAnimated.draw(stack, 48, 16);
        slot.draw(stack, 84, 16);
    }
}
