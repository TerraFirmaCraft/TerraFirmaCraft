/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.screen;

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
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
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks)
    {
        renderBackground(poseStack);
        super.render(poseStack, mouseX, mouseY, partialTicks);
        renderTooltip(poseStack, mouseX, mouseY);
    }

    @Override
    protected void renderBg(PoseStack poseStack, float partialTicks, int mouseX, int mouseY)
    {
        drawDefaultBackground(poseStack);
    }

    protected void drawDefaultBackground(PoseStack poseStack)
    {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        RenderSystem.setShaderTexture(0, this.texture);

        blit(poseStack, leftPos, topPos, 0, 0, 0, imageWidth, imageHeight, 256, 256);
    }

    protected void drawCenteredLine(PoseStack stack, MutableComponent text, int y)
    {
        final int x = (imageWidth - font.width(text)) / 2;
        font.draw(stack, text, x, y, 0x404040);
    }

    @Deprecated(forRemoval = true)
    protected void drawCenteredLine(PoseStack stack, String text, int y)
    {
        final int x = (imageWidth - font.width(text)) / 2;
        font.draw(stack, text, x, y, 0x404040);
    }
}