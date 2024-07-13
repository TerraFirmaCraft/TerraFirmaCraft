/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.render.blockentity;

import java.util.function.Function;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;

import net.dries007.tfc.client.RenderHelpers;
import net.dries007.tfc.common.blockentities.BellowsBlockEntity;
import net.dries007.tfc.common.blocks.devices.BellowsBlock;
import net.dries007.tfc.util.Helpers;

public class BellowsBlockEntityRenderer implements BlockEntityRenderer<BellowsBlockEntity>
{
    private static final ResourceLocation BACK_TEXTURE = Helpers.identifier("block/devices/bellows/back");
    private static final ResourceLocation SIDE_TEXTURE = Helpers.identifier("block/devices/bellows/side");
    // The number of planes each side of the bellows has
    private static final int planeCount = 4;
    private static final float texWidth = 16f / planeCount;

    // The width of the frond and back of the bellows. Each are 2/16th of a block wide
    private static final float headWidth = 0.125f;
    private static final float bellowsWidthMin = 0.125f;
    private static final float bellowsWidthMax = 0.875f;

    private static final float indentBase = 0.0125f;
    private static final float indentFactor = 1.8f;

    public static float[][] getVertices(float minX, float minY, float minZ, float maxX, float maxY, float maxZ, float changeX, float changeY)
    {
        return new float[][] {
            // Main +X Side
            {minX, minY, minZ, 0, 1},
            {minX + changeX, minY - changeY, maxZ, 1, 1},
            {minX + changeX, maxY + changeY, maxZ, 1, 0},
            {minX, maxY, minZ, 0, 0},

            // Main -X Side
            {maxX - changeX, minY - changeY, maxZ, 1, 0},
            {maxX, minY, minZ, 0, 0},
            {maxX, maxY, minZ, 0, 1},
            {maxX - changeX, maxY + changeY, maxZ, 1, 1},

            // Bottom
            {minX, maxY, minZ, 0, 1},
            {minX + changeX, maxY + changeY, maxZ, 1, 1},
            {maxX - changeX, maxY + changeY, maxZ, 1, 0},
            {maxX, maxY, minZ, 0, 0},

            // Top
            {minX + changeX, minY - changeY, maxZ, 1, 0},
            {minX, minY, minZ, 0, 0},
            {maxX, minY, minZ, 0, 1},
            {maxX - changeX, minY - changeY, maxZ, 1, 1}
        };
    }

    @Override
    public void render(BellowsBlockEntity bellows, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay)
    {
        int meta = bellows.getBlockState().getValue(BellowsBlock.FACING).get2DDataValue();

        poseStack.pushPose();

        final Function<ResourceLocation, TextureAtlasSprite> atlas = Minecraft.getInstance().getTextureAtlas(RenderHelpers.BLOCKS_ATLAS);

        final TextureAtlasSprite endSprite = atlas.apply(BACK_TEXTURE);
        final TextureAtlasSprite sideSprite = atlas.apply(SIDE_TEXTURE);

        final VertexConsumer buffer = bufferSource.getBuffer(RenderType.cutout());
        final float width = 1 - bellows.getExtensionLength(partialTicks);

        poseStack.translate(0.5d, 0, 0.5d);
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - 90.0F * meta));
        poseStack.translate(-0.5d, 0.0d, -0.5d);

        drawMiddle(buffer, poseStack, sideSprite, width, packedLight, packedOverlay);
        RenderHelpers.renderTexturedCuboid(poseStack, buffer, endSprite, packedLight, packedOverlay, 0, 0, width, 1, 1, 0.125f + width);

        poseStack.popPose();
    }

    private void drawMiddle(VertexConsumer buffer, PoseStack poseStack, TextureAtlasSprite sprite, float width, int packedLight, int packedOverlay)
    {
        float widthPerSection = (width - headWidth) / planeCount;
        float currentWidth = headWidth;
        float lastWidth = currentWidth;
        float change = indentBase * (indentFactor / width);
        for (int i = 0; i < planeCount; i++)
        {
            boolean isIndented = i % 2 == 0;
            currentWidth += widthPerSection;
            float min = isIndented ? bellowsWidthMin + change : bellowsWidthMin;
            float max = isIndented ? bellowsWidthMax - change : bellowsWidthMax;
            for (float[] v : getVertices(min, max, currentWidth, max, min, lastWidth, isIndented ? -change : change, isIndented ? -change : change))
            {
                // Texture needs to the reversed due to the direction the planes are rendered in
                // Otherwise the texture is cut up and displayed out of order
                RenderHelpers.renderTexturedVertex(poseStack, buffer, packedLight, packedOverlay, v[0], v[1], v[2], sprite.getU(v[3] * -texWidth + (texWidth * (i + 1))), sprite.getV(v[4] * 16f), 1, 0, 0); // todo: incorrect normal
            }
            lastWidth = currentWidth;
        }
    }
}
