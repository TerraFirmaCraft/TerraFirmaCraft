/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraftforge.items.CapabilityItemHandler;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.dries007.tfc.common.tileentity.ScrapingTileEntity;

public class ScrapingTileEntityRenderer extends TileEntityRenderer<ScrapingTileEntity>
{
    public ScrapingTileEntityRenderer(TileEntityRendererDispatcher dispatcher)
    {
        super(dispatcher);
    }

    @Override
    public void render(ScrapingTileEntity te, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay)
    {
        te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(cap -> {
            final ItemStack baseStack = cap.getStackInSlot(0);
            final ItemStack scrapeStack = te.cachedItem;
            if (!baseStack.isEmpty() && !scrapeStack.isEmpty())
            {
                ItemModelMesher shaper = Minecraft.getInstance().getItemRenderer().getItemModelShaper();
                final ResourceLocation base = shaper.getParticleIcon(baseStack).getName();
                final ResourceLocation scraped = shaper.getParticleIcon(scrapeStack).getName();
                final short positions = te.getScrapedPositions();
                drawTiles(buffer, matrixStack, base, positions, 0, combinedLight, combinedOverlay);
                drawTiles(buffer, matrixStack, scraped, positions, 1, combinedLight, combinedOverlay);
            }
        });
    }

    private void drawTiles(IRenderTypeBuffer buffer, MatrixStack matrixStack, ResourceLocation texture, short positions, int condition, int combinedLight, int combinedOverlay)
    {
        Matrix4f mat = matrixStack.last().pose();
        IVertexBuilder builder = buffer.getBuffer(RenderType.cutout());
        //noinspection deprecation
        TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(AtlasTexture.LOCATION_BLOCKS).apply(texture);
        for (int xOffset = 0; xOffset < 4; xOffset++)
        {
            for (int zOffset = 0; zOffset < 4; zOffset++)
            {
                // Checks the nth bit of positions
                if ((((positions >> (xOffset + 4 * zOffset)) & 1) == condition))
                {

                    builder.vertex(mat, xOffset / 4.0F, 0.01F, zOffset / 4.0F).color(1.0F, 1.0F, 1.0F, 1.0F).uv(sprite.getU(xOffset * 4D), sprite.getV(zOffset * 4D)).overlayCoords(combinedOverlay).uv2(combinedLight).normal(0, 0, 1).endVertex();
                    builder.vertex(mat, xOffset / 4.0F, 0.01F, zOffset / 4.0F + 0.25F).color(1.0F, 1.0F, 1.0F, 1.0F).uv(sprite.getU(xOffset * 4D), sprite.getV(zOffset * 4D + 4.0D)).overlayCoords(combinedOverlay).uv2(combinedLight).normal(0, 0, 1).endVertex();
                    builder.vertex(mat, xOffset / 4.0F + 0.25F, 0.01F, zOffset / 4.0F + 0.25F).color(1.0F, 1.0F, 1.0F, 1.0F).uv(sprite.getU(xOffset * 4D  + 4.0D), sprite.getV(zOffset * 4D + 4.0D)).overlayCoords(combinedOverlay).uv2(combinedLight).normal(0, 0, 1).endVertex();
                    builder.vertex(mat, xOffset / 4.0F + 0.25F, 0.01F, zOffset / 4.0F).color(1.0F, 1.0F, 1.0F, 1.0F).uv(sprite.getU(xOffset * 4D + 4.0D), sprite.getV(zOffset * 4D)).overlayCoords(combinedOverlay).uv2(combinedLight).normal(0, 0, 1).endVertex();
                }
            }
        }
    }
}
