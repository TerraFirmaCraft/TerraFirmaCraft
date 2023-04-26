/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.Tooltips;

public class ClientDeviceImageTooltip implements ClientTooltipComponent
{
    public static final ResourceLocation TEXTURE_LOCATION = Helpers.identifier("textures/gui/device_image_tooltip.png");

    private final Tooltips.DeviceImageTooltip tooltip;

    public ClientDeviceImageTooltip(Tooltips.DeviceImageTooltip tooltip)
    {
        this.tooltip = tooltip;
    }

    @Override
    public int getHeight()
    {
        return this.gridSizeY() * 20 + 2 + 4;
    }

    @Override
    public int getWidth(Font font)
    {
        return this.gridSizeX() * 18 + 2;
    }

    private int gridSizeX()
    {
        return tooltip.width();
    }

    private int gridSizeY()
    {
        return tooltip.height();
    }

    @Override
    public void renderImage(Font font, int mouseX, int mouseY, PoseStack poseStack, ItemRenderer itemRenderer, int blitOffset)
    {
        int maxX = this.gridSizeX();
        int maxY = this.gridSizeY();
        int idx = 0;

        for (int x = 0; x < maxY; ++x)
        {
            for (int y = 0; y < maxX; ++y)
            {
                if (idx < tooltip.items().size())
                {
                    int slotX = mouseX + y * 18 + 1;
                    int slotY = mouseY + x * 20 + 1;
                    this.renderSlot(slotX, slotY, idx++, font, poseStack, itemRenderer, blitOffset);
                }
            }
        }

        this.drawBorder(mouseX, mouseY, maxX, maxY, poseStack, blitOffset);
    }


    private void renderSlot(int x, int y, int idx, Font font, PoseStack pPoseStack, ItemRenderer render, int blitOffset)
    {
        ItemStack itemstack = tooltip.items().get(idx);
        this.blit(pPoseStack, x, y, blitOffset, Texture.SLOT);
        render.renderAndDecorateItem(itemstack, x + 1, y + 1, idx);
        render.renderGuiItemDecorations(font, itemstack, x + 1, y + 1);
    }

    private void drawBorder(int x, int y, int slotWidth, int slotHeight, PoseStack poseStack, int blitOffset)
    {
        this.blit(poseStack, x, y, blitOffset, Texture.BORDER_CORNER_TOP);
        this.blit(poseStack, x + slotWidth * 18 + 1, y, blitOffset, Texture.BORDER_CORNER_TOP);

        for (int i = 0; i < slotWidth; ++i)
        {
            this.blit(poseStack, x + 1 + i * 18, y, blitOffset, Texture.BORDER_HORIZONTAL_TOP);
            this.blit(poseStack, x + 1 + i * 18, y + slotHeight * 20, blitOffset, Texture.BORDER_HORIZONTAL_BOTTOM);
        }

        for (int j = 0; j < slotHeight; ++j)
        {
            this.blit(poseStack, x, y + j * 20 + 1, blitOffset, Texture.BORDER_VERTICAL);
            this.blit(poseStack, x + slotWidth * 18 + 1, y + j * 20 + 1, blitOffset, Texture.BORDER_VERTICAL);
        }

        this.blit(poseStack, x, y + slotHeight * 20, blitOffset, Texture.BORDER_CORNER_BOTTOM);
        this.blit(poseStack, x + slotWidth * 18 + 1, y + slotHeight * 20, blitOffset, Texture.BORDER_CORNER_BOTTOM);
    }

    private void blit(PoseStack poseStack, int x, int y, int blitOffset, Texture texture)
    {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE_LOCATION);
        GuiComponent.blit(poseStack, x, y, blitOffset, (float) texture.x, (float) texture.y, texture.w, texture.h, 128, 128);
    }


    public enum Texture
    {
        SLOT(0, 0, 18, 20),
        BLOCKED_SLOT(0, 40, 18, 20),
        BORDER_VERTICAL(0, 18, 1, 20),
        BORDER_HORIZONTAL_TOP(0, 20, 18, 1),
        BORDER_HORIZONTAL_BOTTOM(0, 60, 18, 1),
        BORDER_CORNER_TOP(0, 20, 1, 1),
        BORDER_CORNER_BOTTOM(0, 60, 1, 1);

        public final int x;
        public final int y;
        public final int w;
        public final int h;

        Texture(int x, int y, int w, int h)
        {
            this.x = x;
            this.y = y;
            this.w = w;
            this.h = h;
        }
    }
}
