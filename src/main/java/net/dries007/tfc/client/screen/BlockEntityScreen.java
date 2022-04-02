/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.screen;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.InventoryMenu;

import net.minecraftforge.fluids.FluidStack;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.dries007.tfc.client.RenderHelpers;
import net.dries007.tfc.common.blockentities.InventoryBlockEntity;
import net.dries007.tfc.common.container.BlockEntityContainer;

public class BlockEntityScreen<T extends InventoryBlockEntity<?>, C extends BlockEntityContainer<T>> extends TFCContainerScreen<C>
{
    protected final T blockEntity;

    public BlockEntityScreen(C container, Inventory playerInventory, Component name, ResourceLocation texture)
    {
        super(container, playerInventory, name, texture);
        this.blockEntity = container.getBlockEntity();
    }

    public void resetToBackgroundSprite()
    {
        RenderSystem.setShaderColor(1, 1, 1, 1);
        RenderSystem.setShaderTexture(0, texture);
    }

    public TextureAtlasSprite getAndBindFluidSprite(FluidStack fluid)
    {
        final TextureAtlasSprite sprite = getMinecraft().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(fluid.getFluid().getAttributes().getStillTexture(fluid));

        RenderHelpers.setShaderColor(fluid.getFluid().getAttributes().getColor(fluid));
        RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);

        return sprite;
    }

    public void fillAreaWithSprite(TextureAtlasSprite sprite, PoseStack poseStack, int startX, int endX, int endY, int fillHeight)
    {
        int yPos = endY;
        while (fillHeight > 0)
        {
            int yPixels = Math.min(fillHeight, 16);
            int fillWidth = endX - startX;
            int xPos = endX;
            while (fillWidth > 0)
            {
                int xPixels = Math.min(fillWidth, 16);
                blit(poseStack, leftPos + xPos - xPixels, topPos + yPos - yPixels, 0, xPixels, yPixels, sprite);
                fillWidth -= 16;
                xPos -= 16;
            }
            fillHeight -= 16;
            yPos -= 16;
        }
    }
}
