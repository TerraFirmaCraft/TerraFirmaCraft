/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.screen.button;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.TextComponent;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
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
        super(x, y, 9, 13, TextComponent.EMPTY, onPress);
        this.left = left;
    }

    @Override
    public void renderButton(PoseStack poseStack, int mouseX, int mouseY, float partialTick)
    {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, AnvilPlanScreen.BACKGROUND);
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);

        blit(poseStack, x, y, left ? 201 : 212, 3, 9, 13, 256, 256);
    }
}
