/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.screen;

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.systems.RenderSystem;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

public class TFCContainerScreen<C extends AbstractContainerMenu> extends AbstractContainerScreen<C>
{
    protected static final ResourceLocation SMALL_INV = new ResourceLocation(MOD_ID, "textures/gui/small_inventory.png");
    protected final ResourceLocation texture;

    public TFCContainerScreen(C container, Inventory playerInventory, Component name, ResourceLocation texture)
    {
        super(container, playerInventory, name);
        this.texture = texture;
    }

    @Override
    public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        renderTooltip(matrixStack, mouseX, mouseY);
    }

    @Override
    protected void renderBg(PoseStack matrixStack, float partialTicks, int mouseX, int mouseY)
    {
        drawDefaultBackground(matrixStack);
    }

    @SuppressWarnings({"ConstantConditions", "deprecation"})
    protected void drawDefaultBackground(PoseStack matrixStack)
    {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        minecraft.getTextureManager().bind(texture);
        blit(matrixStack, leftPos, topPos, 0, 0, 0, imageWidth, imageHeight, 256, 256);
    }
}