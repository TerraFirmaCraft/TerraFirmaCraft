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
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import net.dries007.tfc.client.ClientHelpers;
import net.dries007.tfc.common.blockentities.BellowsBlockEntity;
import net.dries007.tfc.common.blocks.devices.BellowsBlock;
import net.dries007.tfc.util.Helpers;

public class BellowsEntityRenderer implements BlockEntityRenderer<BellowsBlockEntity>
{
    private static final ResourceLocation TEXTURE = Helpers.identifier("block/bellows_side.png");

    @Override
    public void render(BellowsBlockEntity bellows, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay)
    {
        int meta = bellows.getBlockState().getValue(BellowsBlock.FACING).get2DDataValue();

        poseStack.pushPose();
        TextureAtlasSprite endSprite = Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(Helpers.identifier("block/bellows_back"));
        TextureAtlasSprite sideSprite = Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(Helpers.identifier("block/bellows_side"));

        VertexConsumer builder = buffer.getBuffer(RenderType.cutout());
        poseStack.translate(0.5d, 0, 0.5d);
        poseStack.mulPose(Vector3f.YP.rotationDegrees(180.0F - 90.0F * meta));
        poseStack.translate(-0.5d, 0.0d, -0.5d);

        float tileY = 1 - bellows.getHeight();
        drawMiddle(builder, poseStack, sideSprite, tileY, combinedOverlay, combinedLight);
        drawTop(builder, poseStack, endSprite, tileY, combinedOverlay, combinedLight);
        poseStack.popPose();
    }

    private void drawMiddle(VertexConsumer b, PoseStack matrixStack, TextureAtlasSprite sprite, float y, int combinedOverlay, int combinedLight)
    {
        float[][] sides = ClientHelpers.getVerticesBySide(0.125f, 0.875f, y, 0.875f, 0.125f, 0.125f, "xy");

        for (float[] v : sides)
        {
            vertex(b, matrixStack.last().pose(), matrixStack.last().normal(), v[0], v[1], v[2], sprite.getU((v[3] * 16D)), sprite.getV((v[4] * 16D)), combinedOverlay, combinedLight);
        }
    }

    private void drawTop(VertexConsumer b, PoseStack matrixStack, TextureAtlasSprite sprite, float y, int combinedOverlay, int combinedLight)
    {
        float[][] sides = ClientHelpers.getVerticesBySide(0, 1, 0.125f + y, 1, 0, y, "xy");
        float[][] tops = ClientHelpers.getVerticesBySide(0, 1, 0.125f + y, 1, 0, y, "z");

        for (float[] v : sides)
        {
            vertex(b, matrixStack.last().pose(), matrixStack.last().normal(), v[0], v[1], v[2], sprite.getU(v[3] * 2D), sprite.getV(v[4] * 16D), combinedOverlay, combinedLight);
        }
        for (float[] v : tops)
        {
            vertex(b, matrixStack.last().pose(), matrixStack.last().normal(), v[0], v[1], v[2], sprite.getU(v[3] * 16D), sprite.getV(v[4] * 16D), combinedOverlay, combinedLight);
        }
    }

    private void vertex(VertexConsumer builder, Matrix4f mat, Matrix3f norm, float x, float y, float z, float u, float v, int combinedOverlay, int combinedLight)
    {
        builder.vertex(mat, x, y, z).color(1.0F, 1.0F, 1.0F, 1.0F).uv(u, v).uv2(combinedLight).overlayCoords(combinedOverlay).normal(norm, 1F, 1F, 1F).endVertex();
    }

}
