/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;

import net.dries007.tfc.util.Helpers;

public class TFCContainerScreen<C extends AbstractContainerMenu> extends AbstractContainerScreen<C>
{
    public static final ResourceLocation INVENTORY_1x1 = Helpers.identifier("textures/gui/single_inventory.png");
    public static final ResourceLocation INVENTORY_2x2 = Helpers.identifier("textures/gui/small_inventory.png");

    protected final ResourceLocation texture;
    protected final Inventory playerInventory;

    public TFCContainerScreen(C container, Inventory playerInventory, Component name, ResourceLocation texture)
    {
        super(container, playerInventory, name);
        this.texture = texture;
        this.playerInventory = playerInventory;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
    {
        renderBackground(graphics, mouseX, mouseY, partialTick);
        super.render(graphics, mouseX, mouseY, partialTick);
        renderTooltip(graphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics poseStack, float partialTicks, int mouseX, int mouseY)
    {
        drawDefaultBackground(poseStack);
    }

    protected void drawDefaultBackground(GuiGraphics graphics)
    {
        graphics.blit(texture, leftPos, topPos, 0, 0, 0, imageWidth, imageHeight, 256, 256);
    }

    protected void drawCenteredLine(GuiGraphics graphics, Component text, int y)
    {
        final int x = (imageWidth - font.width(text)) / 2;
        graphics.drawString(font, text, x, y, 0x404040, false);
    }

    public Inventory getPlayerInventory()
    {
        return playerInventory;
    }
}