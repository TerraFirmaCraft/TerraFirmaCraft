/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.render.blockentity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import net.dries007.tfc.client.RenderHelpers;
import net.dries007.tfc.common.blockentities.LoomBlockEntity;
import net.dries007.tfc.common.blocks.wood.TFCLoomBlock;

public class LoomBlockEntityRenderer implements BlockEntityRenderer<LoomBlockEntity>
{
    @Override
    public void render(LoomBlockEntity loom, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay)
    {
        if (!(loom.getBlockState().getBlock() instanceof TFCLoomBlock loomBlock)) return;

        poseStack.pushPose();
        poseStack.translate(0.5D, 0.03125D, 0.5D);
        int meta = loom.getBlockState().getValue(TFCLoomBlock.FACING).get2DDataValue();
        poseStack.mulPose(Vector3f.YP.rotationDegrees(meta));
        poseStack.popPose();

        final TextureAtlasSprite planksSprite = Minecraft.getInstance().getTextureAtlas(RenderHelpers.BLOCKS_ATLAS).apply(loomBlock.getTextureLocation());

        float tileZ = (float) loom.getAnimPos();

        poseStack.pushPose();
        poseStack.translate(0.5D, 0.0D, 0.5D);
        poseStack.mulPose(Vector3f.YP.rotationDegrees(180.0F - 90.0F * meta));
        poseStack.translate(-0.5D, 0.0D, -0.5D);

        VertexConsumer builder = buffer.getBuffer(RenderType.cutout());
        float z = loom.currentBoolean() ? tileZ : 0;
        RenderHelpers.renderTexturedCuboid(poseStack, builder, planksSprite, combinedLight, combinedOverlay, 0.0625F, 0.3125F, 0.5626F - z, 0.9375F, 0.375F, 0.625F - z);
        float z1 = loom.currentBoolean() ? 0 : tileZ;
        RenderHelpers.renderTexturedCuboid(poseStack, builder, planksSprite, combinedLight, combinedOverlay, 0.0625F, 0.09375F, 0.5626F - z1, 0.9375F, 0.15625F, 0.625F - z1);
        poseStack.popPose();

        if (loom.hasRecipe())
        {
            poseStack.pushPose();
            poseStack.translate(0.5D, 0.0D, 0.5D);
            poseStack.mulPose(Vector3f.YP.rotationDegrees(180.0F - 90.0F * meta));
            poseStack.translate(-0.5D, 0.0D, -0.5D);

            final TextureAtlasSprite progressSprite = Minecraft.getInstance().getTextureAtlas(RenderHelpers.BLOCKS_ATLAS).apply(loom.getInProgressTexture());

            drawMaterial(builder, poseStack, progressSprite, loom, tileZ * 2F / 3F, combinedOverlay, combinedLight);
            drawProduct(builder, poseStack, progressSprite, loom, combinedOverlay, combinedLight);
            poseStack.popPose();
        }
    }


    private void drawProduct(VertexConsumer b, PoseStack matrixStack, TextureAtlasSprite sprite, LoomBlockEntity te, int combinedOverlay, int combinedLight)
    {
        for (float[] v : RenderHelpers.getDiagonalPlaneVertices(0.1875F, 0.9375F, 0.75F - 0.001F, 0.8125F, 0.9375F - (0.625F / te.getMaxProgress()) * te.getProgress(), 0.75F - 0.001F, 0F, 0F, 1F, te.getProgress() / (float) te.getMaxProgress()))
        {
            vertex(b, matrixStack.last().pose(), matrixStack.last().normal(), v[0], v[1], v[2], sprite.getU((v[3] * 16D)), sprite.getV((v[4] * 16D)), combinedOverlay, combinedLight);
        }
    }

    private void drawMaterial(VertexConsumer b, PoseStack matrixStack, TextureAtlasSprite sprite, LoomBlockEntity te, float tileZ, int combinedOverlay, int combinedLight)
    {

        final int maxPieces = te.getMaxInputCount();
        final float Z1 = te.currentBoolean() ? tileZ : 0;
        final float Z2 = te.currentBoolean() ? 0 : tileZ;

        float y1 = 0.9375F - (0.625F / te.getMaxProgress()) * te.getProgress();
        float y2;
        float z1 = 0.75F;
        float z2;
        float texX1, texX2, texY1, texY2;
        for (int i = 0; i < te.getCount(); i++)
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

            for (float[] v : RenderHelpers.getDiagonalPlaneVertices(0.1875F + (0.625F / maxPieces) * i, y1, z1 - 0.001F, 0.1875F + (0.625F / maxPieces) * (i + 1F), y2, z2 - 0.001F, texX1, texY1, texX2, texY2))
            {
                vertex(b, matrixStack.last().pose(), matrixStack.last().normal(), v[0], v[1], v[2], sprite.getU(v[3] * 16D), sprite.getV(v[4] * 16D), combinedOverlay, combinedLight);
            }

            if (i % 2 == 0)
            {
                texX1 = 0F;
                texY1 = 0.5F;
                texX2 = 0.0625F;
            }
            else
            {
                texX1 = 0.125F;
                texY1 = 0.5F;
                texX2 = 0.1875F;
            }
            texY2 = 0.5625F;

            for (float[] v : RenderHelpers.getDiagonalPlaneVertices(0.1875F + (0.625F / maxPieces) * i, (float) 0, z1 - 0.001F, 0.1875F + (0.625F / maxPieces) * (i + 1), y2, z2 - 0.001F, texX1, texY1, texX2, texY2))
            {
                vertex(b, matrixStack.last().pose(), matrixStack.last().normal(), v[0], v[1], v[2], sprite.getU(v[3] * 16D), sprite.getV(v[4] * 16D), combinedOverlay, combinedLight);
            }
        }
    }

    private void vertex(VertexConsumer builder, Matrix4f mat, Matrix3f norm, float x, float y, float z, float u, float v, int combinedOverlay, int combinedLight)
    {
        builder.vertex(mat, x, y, z).color(1.0F, 1.0F, 1.0F, 1.0F).uv(u, v).uv2(combinedLight).overlayCoords(combinedOverlay).normal(norm, 1F, 1F, 1F).endVertex();
    }
}