/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.screen.button;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

import net.dries007.tfc.client.RenderHelpers;
import net.dries007.tfc.client.screen.AnvilPlanScreen;

public class NextPageButton extends Button
{
    public static NextPageButton left(int x, int y, OnPress onPress)
    {
        return new NextPageButton(x, y, onPress, true);
    }

    public static NextPageButton right(int x, int y, OnPress onPress)
    {
        return new NextPageButton(x, y, onPress, false);
    }

    private final boolean left;

    private NextPageButton(int x, int y, OnPress onPress, boolean left)
    {
        super(x, y, 9, 13, Component.empty(), onPress, RenderHelpers.NARRATION);
        this.left = left;
    }

    @Override
    public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
    {
        graphics.blit(AnvilPlanScreen.BACKGROUND, getX(), getY(), left ? 201 : 212, 3, 9, 13, 256, 256);
    }
}
