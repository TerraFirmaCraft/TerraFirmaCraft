/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import net.dries007.tfc.client.ClientHelpers;
import net.dries007.tfc.common.blockentities.LoomBlockEntity;
import net.dries007.tfc.common.blocks.wood.TFCLoomBlock;

public class LoomBlockEntityRenderer implements BlockEntityRenderer<LoomBlockEntity>
{
    public LoomBlockEntityRenderer(BlockEntityRendererProvider.Context context)
    {
    }

    @Override
    public void render(LoomBlockEntity te, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay)
    {
        if (!(te.getBlockState().getBlock() instanceof TFCLoomBlock)) return;

        matrixStack.pushPose();
        matrixStack.translate(0.5D, 0.03125D, 0.5D);
        int meta = te.getBlockState().getValue(TFCLoomBlock.FACING).get2DDataValue();
        matrixStack.mulPose(Vector3f.YP.rotationDegrees(meta));
        matrixStack.popPose();

        TextureAtlasSprite planksSprite = Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(((TFCLoomBlock) te.getBlockState().getBlock()).getTextureLocation());

        float tileZ = (float) te.getAnimPos();

        matrixStack.pushPose();
        matrixStack.translate(0.5D, 0.0D, 0.5D);
        matrixStack.mulPose(Vector3f.YP.rotationDegrees(180.0F - 90.0F * meta));
        matrixStack.translate(-0.5D, 0.0D, -0.5D);

        VertexConsumer builder = buffer.getBuffer(RenderType.cutout());
        drawUpper(builder, matrixStack, planksSprite, te.currentBoolean() ? tileZ : 0, combinedOverlay, combinedLight);
        drawLower(builder, matrixStack, planksSprite, te.currentBoolean() ? 0 : tileZ, combinedOverlay, combinedLight);
        matrixStack.popPose();

        if (te.hasRecipe())
        {
            matrixStack.pushPose();
            matrixStack.translate(0.5D, 0.0D, 0.5D);
            matrixStack.mulPose(Vector3f.YP.rotationDegrees(180.0F - 90.0F * meta));
            matrixStack.translate(-0.5D, 0.0D, -0.5D);

            TextureAtlasSprite progressSprite = Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(te.getInProgressTexture());

            drawMaterial(builder, matrixStack, progressSprite, te, tileZ * 2F / 3F, combinedOverlay, combinedLight);
            drawProduct(builder, matrixStack, progressSprite, te, combinedOverlay, combinedLight);
            matrixStack.popPose();
        }
    }


    private void drawProduct(VertexConsumer b, PoseStack matrixStack, TextureAtlasSprite sprite, LoomBlockEntity te, int combinedOverlay, int combinedLight)
    {
        for (float[] v : getPlaneVertices(0.1875F, 0.9375F, 0.75F - 0.001F, 0.8125F, 0.9375F - (0.625F / te.getMaxProgress()) * te.getProgress(), 0.75F - 0.001F, 0F, 0F, 1F, te.getProgress() / (float) te.getMaxProgress()))
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

            for (float[] v : getPlaneVertices(0.1875F + (0.625F / maxPieces) * i, y1, z1 - 0.001F, 0.1875F + (0.625F / maxPieces) * (i + 1F), y2, z2 - 0.001F, texX1, texY1, texX2, texY2))
            {
                //TerraFirmaCraft.LOGGER.info("mat u {} v {}",sprite.getU(v[3] * 16D), sprite.getV(v[4] * 16D));
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

            for (float[] v : getPlaneVertices(0.1875F + (0.625F / maxPieces) * i, 0, z1 - 0.001F, 0.1875F + (0.625F / maxPieces) * (i + 1), y2, z2 - 0.001F, texX1, texY1, texX2, texY2))
            {
                vertex(b, matrixStack.last().pose(), matrixStack.last().normal(), v[0], v[1], v[2], sprite.getU(v[3] * 16D), sprite.getV(v[4] * 16D), combinedOverlay, combinedLight);
            }
        }
    }

    private float[][] getPlaneVertices(float x1, float y1, float z1, float x2, float y2, float z2, float u1, float v1, float u2, float v2)
    {
        return new float[][] {
            {x1, y1, z1, u1, v1},
            {x2, y1, z1, u2, v1},
            {x2, y2, z2, u2, v2},
            {x1, y2, z2, u1, v2},

            {x2, y1, z1, u2, v1},
            {x1, y1, z1, u1, v1},
            {x1, y2, z2, u1, v2},
            {x2, y2, z2, u2, v2}
        };
    }

    private void drawUpper(VertexConsumer b, PoseStack matrixStack, TextureAtlasSprite sprite, float z, int combinedOverlay, int combinedLight)
    {
        float[][] sidesX = ClientHelpers.getVerticesBySide(0.0625F, 0.3125F, 0.5626F - z, 0.9375F, 0.375F, 0.625F - z, "x");
        float[][] sidesY = ClientHelpers.getVerticesBySide(0.0625F, 0.3125F, 0.5626F - z, 0.9375F, 0.375F, 0.625F - z, "y");
        float[][] sidesZ = ClientHelpers.getVerticesBySide(0.0625F, 0.3125F, 0.5626F - z, 0.9375F, 0.375F, 0.625F - z, "z");
        draw3D(b, matrixStack, sprite, sidesX, sidesY, sidesZ, combinedOverlay, combinedLight);
    }

    private void drawLower(VertexConsumer b, PoseStack matrixStack, TextureAtlasSprite sprite, float z, int combinedOverlay, int combinedLight)
    {
        float[][] sidesX = ClientHelpers.getVerticesBySide(0.0625F, 0.09375F, 0.5626F - z, 0.9375F, 0.15625F, 0.625F - z, "x");
        float[][] sidesY = ClientHelpers.getVerticesBySide(0.0625F, 0.09375F, 0.5626F - z, 0.9375F, 0.15625F, 0.625F - z, "y");
        float[][] sidesZ = ClientHelpers.getVerticesBySide(0.0625F, 0.09375F, 0.5626F - z, 0.9375F, 0.15625F, 0.625F - z, "z");
        draw3D(b, matrixStack, sprite, sidesX, sidesY, sidesZ, combinedOverlay, combinedLight);
    }

    private void draw3D(VertexConsumer b, PoseStack matrixStack, TextureAtlasSprite sprite, float[][] sidesX, float[][] sidesY, float[][] sidesZ, int combinedOverlay, int combinedLight)
    {
        Matrix4f mat = matrixStack.last().pose();
        Matrix3f norm = matrixStack.last().normal();

        for (float[] v : sidesX)
        {
            vertex(b, mat, norm, v[0], v[1], v[2], sprite.getU(v[3]), sprite.getV(v[4]), combinedOverlay, combinedLight);
        }

        for (float[] v : sidesY)
        {
            vertex(b, mat, norm, v[0], v[1], v[2], sprite.getU(v[3]), sprite.getV(v[4] * 14D), combinedOverlay, combinedLight);
        }

        for (float[] v : sidesZ)
        {
            vertex(b, mat, norm, v[0], v[1], v[2], sprite.getU(v[3] * 14D), sprite.getV(v[4]), combinedOverlay, combinedLight);
        }
    }

    private void vertex(VertexConsumer builder, Matrix4f mat, Matrix3f norm, float x, float y, float z, float u, float v, int combinedOverlay, int combinedLight)
    {
        builder.vertex(mat, x, y, z).color(1.0F, 1.0F, 1.0F, 1.0F).uv(u, v).uv2(combinedLight).overlayCoords(combinedOverlay).normal(norm, 1F, 1F, 1F).endVertex();
    }
}