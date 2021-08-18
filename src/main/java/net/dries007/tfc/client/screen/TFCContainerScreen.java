/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.screen;

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.dries007.tfc.client.ClientHelpers;
import net.dries007.tfc.common.capabilities.food.Nutrient;
import net.dries007.tfc.common.capabilities.food.NutritionStats;
import net.dries007.tfc.common.capabilities.food.TFCFoodStats;
import net.dries007.tfc.util.Helpers;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

public class TFCContainerScreen<C extends AbstractContainerMenu> extends AbstractContainerScreen<C>
{
    protected static final ResourceLocation SMALL_INV = new ResourceLocation(MOD_ID, "textures/gui/small_inventory.png");
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
    protected void renderBg(PoseStack matrixStack, float partialTicks, int mouseX, int mouseY)
    {
        drawDefaultBackground(matrixStack);
    }

    protected void drawDefaultBackground(PoseStack poseStack)
    {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        RenderSystem.setShaderTexture(0, this.texture);

        blit(poseStack, leftPos, topPos, 0, 0, 0, imageWidth, imageHeight, 256, 256);
    }
}