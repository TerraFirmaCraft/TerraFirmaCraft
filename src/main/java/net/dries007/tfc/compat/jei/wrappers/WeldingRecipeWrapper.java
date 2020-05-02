/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.compat.jei.wrappers;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;

import net.dries007.tfc.api.recipes.WeldingRecipe;

public class WeldingRecipeWrapper extends SimpleRecipeWrapper
{
    private final WeldingRecipe recipe;

    public WeldingRecipeWrapper(WeldingRecipe recipeWrapper)
    {
        super(recipeWrapper);
        recipe = recipeWrapper;
    }

    @Override
    public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY)
    {
        //Draw tier requirement info
        String text = I18n.format("tfc.enum.tier." + recipe.getTier().name().toLowerCase());
        float xPos = 88f - minecraft.fontRenderer.getStringWidth(text) / 2.0f;
        float yPos = 6f;
        minecraft.fontRenderer.drawString(text, xPos, yPos, 0x000000, false);
    }
}
