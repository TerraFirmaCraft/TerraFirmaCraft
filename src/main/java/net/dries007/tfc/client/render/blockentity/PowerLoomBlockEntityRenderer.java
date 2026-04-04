/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.render.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

import net.dries007.tfc.client.RenderHelpers;
import net.dries007.tfc.common.blockentities.rotation.PowerLoomBlockEntity;
import net.dries007.tfc.common.blocks.wood.TFCLoomBlock;
import net.dries007.tfc.common.recipes.LoomRecipe;
import net.dries007.tfc.util.Helpers;

public class PowerLoomBlockEntityRenderer implements BlockEntityRenderer<PowerLoomBlockEntity>
{
    private static final ResourceLocation STEEL = Helpers.identifier("block/metal/block/steel");
    private static final ResourceLocation BAR = Helpers.identifier("block/devices/power_loom/bar");
    private static final float ANIM_SPEED_MULTIPLIER = 16f;

    @Override
    public void render(PowerLoomBlockEntity loom, float partialTick, PoseStack stack, MultiBufferSource buffer, int packedLight, int packedOverlay)
    {
        if (loom.getLevel() == null)
            return;

        final Direction facing = loom.getBlockState().getValue(TFCLoomBlock.FACING);
        final int meta = facing.get2DDataValue();
        final var atlas = Minecraft.getInstance().getTextureAtlas(RenderHelpers.BLOCKS_ATLAS);
        final VertexConsumer cutout = buffer.getBuffer(RenderType.cutout());
        final TextureAtlasSprite steel = atlas.apply(STEEL);
        final TextureAtlasSprite barSprite = atlas.apply(BAR);

        final float rotationAngle = loom.getRotationAngle(partialTick);

        stack.pushPose();
        AxleBlockEntityRenderer.applyRotation(stack, facing.getCounterClockWise().getAxis(), -rotationAngle);
        RenderHelpers.renderTexturedCuboid(stack, cutout, steel, packedLight, packedOverlay, 7 / 16f, 7f / 16f, 1f / 16f, 9f / 16f, 9f / 16f, 15f / 16f, false);
        stack.popPose();

        final float animAngle = rotationAngle * ANIM_SPEED_MULTIPLIER;
        final float sinAngle = Mth.sin(animAngle);
        final float baseY = 10.25f / 16f;

        stack.pushPose();
        stack.translate(0.5D, 0.0D, 0.5D);
        stack.mulPose(Axis.YP.rotationDegrees(180.0F - 90.0F * meta));
        stack.translate(-0.5D, 0.0D, -0.5D);

        // Fabric strips
        final LoomRecipe recipe = loom.getRecipe();
        final ResourceLocation lastTex = loom.getLastTexture();
        if (recipe != null || lastTex != null)
        {
            if (recipe != null)
            {
                final TextureAtlasSprite progressSprite = atlas.apply(recipe.getInProgressTexture());
                final int count = loom.getCount();
                final int maxPieces = recipe.getInputCount();
                final float stripWidth = 12f / 16f / maxPieces;
                final float maxPeakHeight = 3f / 16f;
                final float peakOffset = Math.abs(sinAngle) * maxPeakHeight;

                // Number of strips that have been "completed" by weaving progress
                final int completedStrips = recipe.getStepCount() > 0
                    ? (int) ((float) loom.getProgress() / recipe.getStepCount() * count)
                    : 0;

                for (int i = 0; i < count; i++)
                {
                    final float stripMinX = 2f / 16f + i * stripWidth;
                    final float stripMaxX = 2f / 16f + (i + 1) * stripWidth;

                    final float vAtMinX;
                    final float vAtMaxX;
                    if (i < completedStrips)
                    {
                        // Completed strip: correct position-dependent UV
                        vAtMinX = (float) (maxPieces - i) / maxPieces * 12f / 16f;
                        vAtMaxX = (float) (maxPieces - i - 1) / maxPieces * 12f / 16f;
                    }
                    else
                    {
                        // Incomplete strip: repeated single-strip UV section
                        vAtMinX = 12f / 16f / maxPieces;
                        vAtMaxX = 0f;
                    }

                    final boolean raised = (i % 2 == 0) == (sinAngle > 0);
                    final float peakY = baseY + (raised ? peakOffset : 0);

                    // Even strips track bar 1 (center Z=6/16), odd strips track bar 2 (center Z=8/16)
                    final float apexZ = (i % 2 == 0) ? 6f / 16f : 8f / 16f;

                    if (raised && peakOffset > 0)
                    {
                        renderStripSegment(stack, cutout, progressSprite, packedLight, packedOverlay,
                            stripMinX, stripMaxX, baseY, 0f, baseY, apexZ - 4f / 16f, vAtMinX, vAtMaxX);
                        renderStripSegment(stack, cutout, progressSprite, packedLight, packedOverlay,
                            stripMinX, stripMaxX, baseY, apexZ - 4f / 16f, peakY, apexZ, vAtMinX, vAtMaxX);
                        renderStripSegment(stack, cutout, progressSprite, packedLight, packedOverlay,
                            stripMinX, stripMaxX, peakY, apexZ, baseY, apexZ + 4f / 16f, vAtMinX, vAtMaxX);
                        renderStripSegment(stack, cutout, progressSprite, packedLight, packedOverlay,
                            stripMinX, stripMaxX, baseY, apexZ + 4f / 16f, baseY, 1f, vAtMinX, vAtMaxX);
                    }
                    else
                    {
                        renderStripSegment(stack, cutout, progressSprite, packedLight, packedOverlay,
                            stripMinX, stripMaxX, baseY, 0f, baseY, 1f, vAtMinX, vAtMaxX);
                    }
                }
            }
            else
            {
                // No active recipe but product exists — render full texture as a completed product
                final TextureAtlasSprite progressSprite = atlas.apply(lastTex);
                renderStripSegment(stack, cutout, progressSprite, packedLight, packedOverlay,
                    2f / 16f, 14f / 16f, baseY, 0f, baseY, 1f, 12f / 16f, 0f);
            }
        }

        // Heddle bars, shuttle bar, and weft shuttle only animate when actively weaving
        final boolean weaving = recipe != null;
        final float barCenterY = baseY + 2f / 16f;
        final float bar1Y = weaving ? barCenterY + sinAngle * 1.5f / 16f : barCenterY;
        final float bar2Y = weaving ? barCenterY - sinAngle * 1.5f / 16f : barCenterY;

        RenderHelpers.renderTexturedCuboid(stack, cutout, barSprite, packedLight, packedOverlay,
            1f / 16f, bar1Y - 1f / 16f, 5.5f / 16f, 15f / 16f, bar1Y + 1f / 16f, 6.5f / 16f);
        RenderHelpers.renderTexturedCuboid(stack, cutout, barSprite, packedLight, packedOverlay,
            1f / 16f, bar2Y - 1f / 16f, 7.5f / 16f, 15f / 16f, bar2Y + 1f / 16f, 8.5f / 16f);

        // Shuttle bar
        final float shuttleZ = weaving ? 11f / 16f + Mth.sin(2f * animAngle) / 16f : 11f / 16f;
        RenderHelpers.renderTexturedCuboid(stack, cutout, barSprite, packedLight, packedOverlay,
            0, baseY + 1f / 16f, shuttleZ - 0.5f / 16f, 1f, baseY + 3f / 16f, shuttleZ + 0.5f / 16f);

        // Weft shuttle
        if (weaving)
        {
            final float weftX = 8f / 16f - Mth.cos(animAngle) * 6f / 16f;
            RenderHelpers.renderTexturedCuboid(stack, cutout, barSprite, packedLight, packedOverlay,
                weftX - 1f / 16f, baseY, 9.5f / 16f, weftX + 1f / 16f, baseY + 1f / 16f, 10.5f / 16f);
        }

        stack.popPose();
    }

    /**
     * Renders a single quad segment of a fabric strip (both front and back faces).
     * The quad spans from (x1, yFront, zFront) to (x2, yBack, zBack), supporting both flat and diagonal surfaces.
     * Sprite U maps to Z position, sprite V maps to strip X position.
     */
    private static void renderStripSegment(PoseStack stack, VertexConsumer buffer, TextureAtlasSprite sprite,
        int packedLight, int packedOverlay,
        float x1, float x2, float yFront, float zFront, float yBack, float zBack,
        float vAtX1, float vAtX2)
    {
        final float uFront = sprite.getU(zFront);
        final float uBack = sprite.getU(zBack);
        final float v1 = sprite.getV(vAtX1);
        final float v2 = sprite.getV(vAtX2);

        // Top face
        RenderHelpers.renderTexturedVertex(stack, buffer, packedLight, packedOverlay,
            x1, yFront, zFront, uFront, v1, 0, 1, 0);
        RenderHelpers.renderTexturedVertex(stack, buffer, packedLight, packedOverlay,
            x1, yBack, zBack, uBack, v1, 0, 1, 0);
        RenderHelpers.renderTexturedVertex(stack, buffer, packedLight, packedOverlay,
            x2, yBack, zBack, uBack, v2, 0, 1, 0);
        RenderHelpers.renderTexturedVertex(stack, buffer, packedLight, packedOverlay,
            x2, yFront, zFront, uFront, v2, 0, 1, 0);

        // Bottom face (reversed winding)
        RenderHelpers.renderTexturedVertex(stack, buffer, packedLight, packedOverlay,
            x1, yBack, zBack, uBack, v1, 0, -1, 0);
        RenderHelpers.renderTexturedVertex(stack, buffer, packedLight, packedOverlay,
            x1, yFront, zFront, uFront, v1, 0, -1, 0);
        RenderHelpers.renderTexturedVertex(stack, buffer, packedLight, packedOverlay,
            x2, yFront, zFront, uFront, v2, 0, -1, 0);
        RenderHelpers.renderTexturedVertex(stack, buffer, packedLight, packedOverlay,
            x2, yBack, zBack, uBack, v2, 0, -1, 0);
    }
}
