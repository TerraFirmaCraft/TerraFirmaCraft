/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.tooltip.Tooltips;

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
    public void renderImage(Font font, int mouseX, int mouseY, GuiGraphics graphics)
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
                    this.renderSlot(slotX, slotY, idx++, font, graphics);
                }
            }
        }

        this.drawBorder(mouseX, mouseY, maxX, maxY, graphics);
    }


    private void renderSlot(int x, int y, int idx, Font font, GuiGraphics graphics)
    {
        ItemStack itemstack = tooltip.items().get(idx);
        this.blit(graphics, x, y, Texture.SLOT);
        graphics.renderItem(itemstack, x + 1, y + 1, idx);
        graphics.renderItemDecorations(font, itemstack, x + 1, y + 1);
    }

    private void drawBorder(int x, int y, int slotWidth, int slotHeight, GuiGraphics poseStack)
    {
        this.blit(poseStack, x, y, Texture.BORDER_CORNER_TOP);
        this.blit(poseStack, x + slotWidth * 18 + 1, y, Texture.BORDER_CORNER_TOP);

        for (int i = 0; i < slotWidth; ++i)
        {
            this.blit(poseStack, x + 1 + i * 18, y, Texture.BORDER_HORIZONTAL_TOP);
            this.blit(poseStack, x + 1 + i * 18, y + slotHeight * 20, Texture.BORDER_HORIZONTAL_BOTTOM);
        }

        for (int j = 0; j < slotHeight; ++j)
        {
            this.blit(poseStack, x, y + j * 20 + 1, Texture.BORDER_VERTICAL);
            this.blit(poseStack, x + slotWidth * 18 + 1, y + j * 20 + 1, Texture.BORDER_VERTICAL);
        }

        this.blit(poseStack, x, y + slotHeight * 20, Texture.BORDER_CORNER_BOTTOM);
        this.blit(poseStack, x + slotWidth * 18 + 1, y + slotHeight * 20, Texture.BORDER_CORNER_BOTTOM);
    }

    private void blit(GuiGraphics graphics, int x, int y, Texture texture)
    {
        graphics.blit(TEXTURE_LOCATION, x, y, 0, (float) texture.x, (float) texture.y, texture.w, texture.h, 128, 128);
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
