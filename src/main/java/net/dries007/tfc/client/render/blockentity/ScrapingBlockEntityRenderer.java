/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.render.blockentity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemModelShaper;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.model.data.EmptyModelData;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import net.dries007.tfc.client.RenderHelpers;
import net.dries007.tfc.common.blockentities.ScrapingBlockEntity;

public class ScrapingBlockEntityRenderer implements BlockEntityRenderer<ScrapingBlockEntity>
{
    @Override
    public void render(ScrapingBlockEntity scraping, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay)
    {
        scraping.getCapability(Capabilities.ITEM).ifPresent(cap -> {
            final ItemStack baseStack = cap.getStackInSlot(0);
            final ItemStack scrapeStack = scraping.getCachedItem();
            if (!baseStack.isEmpty() && !scrapeStack.isEmpty())
            {
                ItemModelShaper shaper = Minecraft.getInstance().getItemRenderer().getItemModelShaper();
                // todo: is this the right thing to be doing here?
                final ResourceLocation base = shaper.getItemModel(baseStack).getParticleIcon(EmptyModelData.INSTANCE).getName();
                final ResourceLocation scraped = shaper.getItemModel(scrapeStack).getParticleIcon(EmptyModelData.INSTANCE).getName();
                final short positions = scraping.getScrapedPositions();
                drawTiles(buffer, poseStack, base, positions, 0, combinedLight, combinedOverlay);
                drawTiles(buffer, poseStack, scraped, positions, 1, combinedLight, combinedOverlay);
            }
        });
    }

    private void drawTiles(MultiBufferSource buffer, PoseStack poseStack, ResourceLocation texture, short positions, int condition, int combinedLight, int combinedOverlay)
    {
        Matrix4f mat = poseStack.last().pose();
        VertexConsumer builder = buffer.getBuffer(RenderType.cutout());
        TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(RenderHelpers.BLOCKS_ATLAS).apply(texture);
        for (int xOffset = 0; xOffset < 4; xOffset++)
        {
            for (int zOffset = 0; zOffset < 4; zOffset++)
            {
                // Checks the nth bit of positions
                if ((((positions >> (xOffset + 4 * zOffset)) & 1) == condition))
                {

                    builder.vertex(mat, xOffset / 4.0F, 0.01F, zOffset / 4.0F).color(1.0F, 1.0F, 1.0F, 1.0F).uv(sprite.getU(xOffset * 4D), sprite.getV(zOffset * 4D)).overlayCoords(combinedOverlay).uv2(combinedLight).normal(0, 0, 1).endVertex();
                    builder.vertex(mat, xOffset / 4.0F, 0.01F, zOffset / 4.0F + 0.25F).color(1.0F, 1.0F, 1.0F, 1.0F).uv(sprite.getU(xOffset * 4D), sprite.getV(zOffset * 4D + 4.0D)).overlayCoords(combinedOverlay).uv2(combinedLight).normal(0, 0, 1).endVertex();
                    builder.vertex(mat, xOffset / 4.0F + 0.25F, 0.01F, zOffset / 4.0F + 0.25F).color(1.0F, 1.0F, 1.0F, 1.0F).uv(sprite.getU(xOffset * 4D + 4.0D), sprite.getV(zOffset * 4D + 4.0D)).overlayCoords(combinedOverlay).uv2(combinedLight).normal(0, 0, 1).endVertex();
                    builder.vertex(mat, xOffset / 4.0F + 0.25F, 0.01F, zOffset / 4.0F).color(1.0F, 1.0F, 1.0F, 1.0F).uv(sprite.getU(xOffset * 4D + 4.0D), sprite.getV(zOffset * 4D)).overlayCoords(combinedOverlay).uv2(combinedLight).normal(0, 0, 1).endVertex();
                }
            }
        }
    }
}
