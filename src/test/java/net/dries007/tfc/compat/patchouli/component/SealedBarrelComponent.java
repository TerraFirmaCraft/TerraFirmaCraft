/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.compat.patchouli.component;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.crafting.RecipeType;
import vazkii.patchouli.api.IComponentRenderContext;

import net.dries007.tfc.common.recipes.SealedBarrelRecipe;
import net.dries007.tfc.common.recipes.TFCRecipeTypes;
import net.dries007.tfc.util.calendar.Calendars;

public class SealedBarrelComponent extends BarrelComponent<SealedBarrelRecipe>
{
    @Override
    protected void renderAdditional(GuiGraphics graphics, IComponentRenderContext context, float partialTicks, int mouseX, int mouseY)
    {
        if (recipe == null) return;

        if (!recipe.isInfinite())
        {
            final Component tooltip = Calendars.CLIENT.getTimeDelta(recipe.getDuration());
            final Font font = Minecraft.getInstance().font;
            final int centerX = 64 - 8 - font.width(tooltip.getString()) / 2; // Page width = 64, Offset = 8,
            graphics.drawString(font, tooltip, centerX, 28, 0x404040, false);
        }
    }

    @Override
    protected RecipeType<SealedBarrelRecipe> getRecipeType()
    {
        return TFCRecipeTypes.BARREL_SEALED.get();
    }
}
