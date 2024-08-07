/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.render.blockentity;

import java.util.Map;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

import net.dries007.tfc.client.RenderHelpers;
import net.dries007.tfc.common.blockentities.LoomBlockEntity;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.wood.TFCLoomBlock;
import net.dries007.tfc.common.blocks.wood.Wood;
import net.dries007.tfc.common.recipes.LoomRecipe;
import net.dries007.tfc.util.Helpers;

public class LoomBlockEntityRenderer implements BlockEntityRenderer<LoomBlockEntity>
{
    public static final Map<Block, ResourceLocation> TEXTURES = RenderHelpers.mapOf(map ->
        TFCBlocks.WOODS.forEach((wood, m) ->
            map.accept(m.get(Wood.BlockType.LOOM), Helpers.identifier("block/wood/planks/" + wood.getSerializedName()))));

    @Override
    public void render(LoomBlockEntity loom, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay)
    {
        final ResourceLocation texture = TEXTURES.get(loom.getBlockState().getBlock());
        if (texture == null)
        {
            return;
        }

        poseStack.pushPose();
        poseStack.translate(0.5D, 0.03125D, 0.5D);
        int meta = loom.getBlockState().getValue(TFCLoomBlock.FACING).get2DDataValue();
        poseStack.mulPose(Axis.YP.rotationDegrees((float) meta));
        poseStack.popPose();

        final TextureAtlasSprite planksSprite = Minecraft.getInstance().getTextureAtlas(RenderHelpers.BLOCKS_ATLAS).apply(texture);

        float tileZ = (float) loom.getAnimPos();

        poseStack.pushPose();
        poseStack.translate(0.5D, 0.0D, 0.5D);
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - 90.0F * meta));
        poseStack.translate(-0.5D, 0.0D, -0.5D);

        VertexConsumer builder = buffer.getBuffer(RenderType.cutout());
        float z = loom.currentBoolean() ? tileZ : 0;
        RenderHelpers.renderTexturedCuboid(poseStack, builder, planksSprite, combinedLight, combinedOverlay, 0.0625F, 0.3125F, 0.5626F - z, 0.9375F, 0.375F, 0.625F - z);
        float z1 = loom.currentBoolean() ? 0 : tileZ;
        RenderHelpers.renderTexturedCuboid(poseStack, builder, planksSprite, combinedLight, combinedOverlay, 0.0625F, 0.09375F, 0.5626F - z1, 0.9375F, 0.15625F, 0.625F - z1);
        poseStack.popPose();

        final LoomRecipe recipe = loom.getRecipe();
        final ResourceLocation lastTex = loom.getLastTexture();
        if (recipe != null || lastTex != null)
        {
            poseStack.pushPose();
            poseStack.translate(0.5D, 0.0D, 0.5D);
            poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - 90.0F * meta));
            poseStack.translate(-0.5D, 0.0D, -0.5D);
            if (recipe != null)
            {
                final TextureAtlasSprite progressSprite = Minecraft.getInstance().getTextureAtlas(RenderHelpers.BLOCKS_ATLAS).apply(recipe.getInProgressTexture());

                drawMaterial(builder, poseStack, progressSprite, loom, recipe, tileZ * 2F / 3F, combinedOverlay, combinedLight);
                drawProduct(builder, poseStack, progressSprite, loom, recipe, combinedOverlay, combinedLight);
            }
            else
            {
                final TextureAtlasSprite progressSprite = Minecraft.getInstance().getTextureAtlas(RenderHelpers.BLOCKS_ATLAS).apply(lastTex);
                drawProduct(builder, poseStack, progressSprite, 1f, combinedOverlay, combinedLight);
            }

            poseStack.popPose();
        }

    }

    private void drawProduct(VertexConsumer buffer, PoseStack poseStack, TextureAtlasSprite sprite, float progress, int packedOverlay, int packedLight)
    {
        for (float[] v : RenderHelpers.getDiagonalPlaneVertices(0.1875F, 0.9375F, 0.75F - 0.001F, 0.8125F, 0.9375F - 0.625F * progress, 0.75F - 0.001F, 0F, 0F, 1F, progress))
        {
            RenderHelpers.renderTexturedVertex(poseStack, buffer, packedLight, packedOverlay, v[0], v[1], v[2], sprite.getU(v[3] * 16f), sprite.getV(v[4] * 16f), 0, 1, 0);
        }
    }

    private void drawProduct(VertexConsumer buffer, PoseStack poseStack, TextureAtlasSprite sprite, LoomBlockEntity loom, LoomRecipe recipe, int packedOverlay, int packedLight)
    {
        drawProduct(buffer, poseStack, sprite, loom.getProgress() / (float) recipe.getStepCount(), packedOverlay, packedLight);
    }

    private void drawMaterial(VertexConsumer buffer, PoseStack poseStack, TextureAtlasSprite sprite, LoomBlockEntity loom, LoomRecipe recipe, float tileZ, int packedOverlay, int packedLight)
    {

        final int maxPieces = recipe.getInputCount();
        final float Z1 = loom.currentBoolean() ? tileZ : 0;
        final float Z2 = loom.currentBoolean() ? 0 : tileZ;

        float y1 = 0.9375F - 0.625F / recipe.getStepCount() * loom.getProgress();
        float y2;
        float z1 = 0.75F;
        float z2;
        float texX1, texX2, texY1, texY2;
        for (int i = 0; i < loom.getCount(); i++)
        {
            if (i % 2 == 0)
            {
                texX1 = 0;
                texY1 = 0;
                texX2 = 0.0625F;
                texY2 = 0.125F;
                z2 = 0.75F - Z1;
                y2 = 0.34375F;
            }
            else
            {
                texX1 = 0.125F;
                texY1 = 0F;
                texX2 = 0.1875F;
                texY2 = 0.1875F;
                z2 = 0.75F - Z2;
                y2 = 0.125F;
            }

            for (float[] v : RenderHelpers.getDiagonalPlaneVertices(0.1875F + 0.625F / maxPieces * i, y1, z1 - 0.001F, 0.1875F + 0.625F / maxPieces * (i + 1F), y2, z2 - 0.001F, texX1, texY1, texX2, texY2))
            {
                RenderHelpers.renderTexturedVertex(poseStack, buffer, packedLight, packedOverlay, v[0], v[1], v[2], sprite.getU(v[3] * 16f), sprite.getV(v[4] * 16f), 0, 1, 0);
            }

            if (i % 2 == 0)
            {
                texX1 = 0F;
            }
            texY1 = 0.5F;
            texY2 = 0.5625F;

            for (float[] v : RenderHelpers.getDiagonalPlaneVertices(0.1875F + 0.625F / maxPieces * i, (float) 0, z1 - 0.001F, 0.1875F + 0.625F / maxPieces * (i + 1), y2, z2 - 0.001F, texX1, texY1, texX2, texY2))
            {
                RenderHelpers.renderTexturedVertex(poseStack, buffer, packedLight, packedOverlay, v[0], v[1], v[2], sprite.getU(v[3] * 16f), sprite.getV(v[4] * 16f), 0, 1, 0);
            }
        }
    }
}