/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.compat.jei.wrappers;

import java.util.Collections;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.client.config.GuiUtils;

import net.dries007.tfc.api.recipes.anvil.AnvilRecipe;
import net.dries007.tfc.client.gui.GuiAnvilTFC;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.forge.ForgeRule;
import net.dries007.tfc.util.forge.ForgeStep;

public class AnvilRecipeWrapper extends SimpleRecipeWrapper
{
    private final AnvilRecipe recipe;

    public AnvilRecipeWrapper(AnvilRecipe recipeWrapper)
    {
        super(recipeWrapper);
        recipe = recipeWrapper;
    }

    @Override
    public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY)
    {
        minecraft.renderEngine.bindTexture(GuiAnvilTFC.ANVIL_BACKGROUND);
        for (int i = 0; i < recipe.getRules().length; i++)
        {
            ForgeRule rule = recipe.getRules()[i];
            if (rule != null)
            {
                int xOffset = i * 19;
                int x = 53 + xOffset;
                int y = 3;
                // The rule icon
                Gui.drawScaledCustomSizeModalRect(x, y, rule.getU(), rule.getV(), 32, 32, 10, 10, 256, 256);
                GlStateManager.color(0f, 0.6f, 0.2f);
                Gui.drawScaledCustomSizeModalRect(x - 5, y - 3, 198, rule.getW(), 20, 22, 20, 22, 256, 256);
                GlStateManager.color(1f, 1f, 1f);
            }
        }
        //Draw steps
        for (ForgeStep step : ForgeStep.values())
        {
            int x = step.getX() - 11;
            int y = step.getY() - 7;
            Gui.drawScaledCustomSizeModalRect(x, y, step.getU(), step.getV(), 32, 32, 16, 16, 256, 256);
        }
        //Draw tier requirement info
        String text = I18n.format("tfc.enum.tier." + recipe.getTier().name().toLowerCase());
        float xPos = 20f - minecraft.fontRenderer.getStringWidth(text) / 2.0f;
        float yPos = 33f;
        minecraft.fontRenderer.drawString(text, xPos, yPos, 0x000000, false);
        //Please don't optimize this, if you do, #drawHoveringText will cause everything that is drawed afterwards to be glitchy.
        for (int i = 0; i < recipe.getRules().length; i++)
        {
            ForgeRule rule = recipe.getRules()[i];
            if (rule != null)
            {
                int xOffset = i * 19;
                int x = 50 + xOffset;
                int y = 0;
                if (mouseX >= x && mouseX <= x + 14 && mouseY >= y && mouseY <= y + 19)
                {
                    GuiUtils.drawHoveringText(Collections.singletonList(I18n.format(Helpers.getEnumName(rule))), mouseX, mouseY, 154, 80, -1, minecraft.fontRenderer);
                }
            }
        }

    }
}
