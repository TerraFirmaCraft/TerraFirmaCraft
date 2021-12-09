/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemModelShaper;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.CapabilityItemHandler;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import net.dries007.tfc.common.blockentities.ScrapingBlockEntity;

public class ScrapingBlockEntityRenderer implements BlockEntityRenderer<ScrapingBlockEntity>
{
    @Override
    public void render(ScrapingBlockEntity te, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay)
    {
        te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(cap -> {
            final ItemStack baseStack = cap.getStackInSlot(0);
            final ItemStack scrapeStack = te.getCachedItem();
            if (!baseStack.isEmpty() && !scrapeStack.isEmpty())
            {
                ItemModelShaper shaper = Minecraft.getInstance().getItemRenderer().getItemModelShaper();
                // todo: is this the right thing to be doing here?
                final ResourceLocation base = shaper.getItemModel(baseStack).getParticleIcon().getName();
                final ResourceLocation scraped = shaper.getItemModel(scrapeStack).getParticleIcon().getName();
                final short positions = te.getScrapedPositions();
                drawTiles(buffer, matrixStack, base, positions, 0, combinedLight, combinedOverlay);
                drawTiles(buffer, matrixStack, scraped, positions, 1, combinedLight, combinedOverlay);
            }
        });
    }

    @SuppressWarnings("deprecation")
    private void drawTiles(MultiBufferSource buffer, PoseStack matrixStack, ResourceLocation texture, short positions, int condition, int combinedLight, int combinedOverlay)
    {
        Matrix4f mat = matrixStack.last().pose();
        VertexConsumer builder = buffer.getBuffer(RenderType.cutout());
        TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(texture);
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
