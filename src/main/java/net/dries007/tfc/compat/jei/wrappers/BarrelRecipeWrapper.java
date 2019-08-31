/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.compat.jei.wrappers;

import java.util.Collections;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.dries007.tfc.api.recipes.barrel.BarrelRecipe;
import net.dries007.tfc.util.calendar.ICalendar;

@ParametersAreNonnullByDefault
public class BarrelRecipeWrapper implements IRecipeWrapper
{
    private BarrelRecipe recipe;

    public BarrelRecipeWrapper(BarrelRecipe recipe)
    {
        this.recipe = recipe;
    }

    @Override
    public void getIngredients(IIngredients ingredients)
    {
        ingredients.setInputLists(VanillaTypes.ITEM, Collections.singletonList(recipe.getItemIngredient().getValidIngredients()));
        ingredients.setInputLists(VanillaTypes.FLUID, Collections.singletonList(recipe.getFluidIngredient().getValidIngredients()));
        if (recipe.getOutputStack() != ItemStack.EMPTY)
        {
            ingredients.setOutput(VanillaTypes.ITEM, recipe.getOutputStack());
        }
        if (recipe.getOutputFluid() != null)
        {
            ingredients.setOutput(VanillaTypes.FLUID, recipe.getOutputFluid());
        }
    }

    @Override
    public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY)
    {
        String text;
        if (recipe.getDuration() > 0)
        {
            text = I18n.format("jei.tooltips.tfc.barrel.duration", recipe.getDuration() / ICalendar.TICKS_IN_HOUR);
        }
        else
        {
            text = I18n.format("jei.tooltips.tfc.barrel.instant");
        }
        float x = 61f - minecraft.fontRenderer.getStringWidth(text) / 2.0f;
        float y = 46f;
        minecraft.fontRenderer.drawString(text, x, y, 0x000000, false);
    }
}
