/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.screen.button;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

/**
 * This is borrowed from 1.20 - it renders a button but with a single texture sheet, rather than individual button textures
 */
public class LegacyImageButton extends Button
{
    public static final int SIZE = 256;
    protected final ResourceLocation texture;
    protected final int xTexStart;
    protected final int yTexStart;
    protected final int yDiffTex;
    private final boolean silent;

    public LegacyImageButton(int x, int y, int width, int height, int xTexStart, int yTexStart, int yDiffTex, boolean silent, ResourceLocation texture, Button.OnPress onPress, Component label)
    {
        super(x, y, width, height, label, onPress, DEFAULT_NARRATION);
        this.xTexStart = xTexStart;
        this.yTexStart = yTexStart;
        this.yDiffTex = yDiffTex;
        this.texture = texture;
        this.silent = silent;
    }

    @Override
    public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
    {
        int vOffset = yTexStart;
        if (!isActive())
        {
            vOffset = yTexStart + yDiffTex * 2;
        }
        else if (isHoveredOrFocused())
        {
            vOffset = yTexStart + yDiffTex;
        }

        RenderSystem.enableDepthTest();
        graphics.blit(texture, getX(), getY(), xTexStart, vOffset, width, height, SIZE, SIZE);
    }

    @Override
    public void playDownSound(SoundManager handler)
    {
        if (!silent) super.playDownSound(handler);
    }
}
