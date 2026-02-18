/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.joml.Matrix4f;

import net.dries007.tfc.client.RenderHelpers;
import net.dries007.tfc.common.blockentities.KnappingBlockEntity;
import net.dries007.tfc.common.blockentities.TFCBlockEntities;
import net.dries007.tfc.common.blocks.devices.KnappingBlock;

public enum KnappingBlockModel implements SimpleStaticBlockEntityModel<KnappingBlockModel, KnappingBlockEntity>
{
    INSTANCE;

    @Override
    public TextureAtlasSprite render(KnappingBlockEntity knapping, PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay)
    {
        final ResourceLocation texture = knapping.getTexture();
        if (texture != null)
        {
            // Rotate the tile grid so it appears correctly from the player's facing direction.
            // The coordinate normalisation in KnappingBlock.normalizeHit() stores bits in the
            // canonical north-facing orientation; we apply the inverse rotation here so the
            // visible tiles match what the player physically clicked.
            if (knapping.getBlockState().hasProperty(KnappingBlock.FACING))
            {
                final Direction facing = knapping.getBlockState().getValue(KnappingBlock.FACING);
                final float angle = switch (facing)
                {
                    case SOUTH -> 180f;
                    case EAST  ->  90f;  // CCW from above: (x,z) -> (1-z, x)
                    case WEST  -> -90f;  // CW  from above: (x,z) -> (z, 1-x)
                    default    ->   0f;  // NORTH – no rotation
                };
                if (angle != 0f)
                {
                    poseStack.translate(0.5, 0.0, 0.5);
                    poseStack.mulPose(Axis.YP.rotationDegrees(angle));
                    poseStack.translate(-0.5, 0.0, -0.5);
                }
            }
            drawTiles(buffer, poseStack, texture, knapping.getPositions(), packedLight, packedOverlay);
            return Minecraft.getInstance().getTextureAtlas(RenderHelpers.BLOCKS_ATLAS).apply(texture);
        }
        return RenderHelpers.missingTexture();
    }

    @Override
    public BlockEntityType<KnappingBlockEntity> type()
    {
        return TFCBlockEntities.KNAPPING.get();
    }

    @Override
    public int faces(KnappingBlockEntity blockEntity)
    {
        // Up to 25 tiles in the 5x5 grid
        return 25;
    }

    /**
     * Draws all tiles that are still "on" (bit = 1 in positions) using the given texture.
     * The 5x5 grid covers the full top face of the block, each tile is 0.2 x 0.2 units.
     * The texture is divided equally across all 25 tiles.
     */
    private void drawTiles(VertexConsumer buffer, PoseStack poseStack, ResourceLocation texture, int positions, int packedLight, int packedOverlay)
    {
        final Matrix4f mat = poseStack.last().pose();
        final TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(RenderHelpers.BLOCKS_ATLAS).apply(texture);

        for (int xOffset = 0; xOffset < 5; xOffset++)
        {
            for (int zOffset = 0; zOffset < 5; zOffset++)
            {
                final int index = xOffset + zOffset * 5;
                // Only render tiles that are still present (bit = 1)
                if (((positions >> index) & 1) == 1)
                {
                    final float x0 = xOffset / 5.0F;
                    final float x1 = x0 + 0.2F;
                    final float z0 = zOffset / 5.0F;
                    final float z1 = z0 + 0.2F;

                    // UV coordinates: each tile covers 1/5 of the sprite
                    final float u0 = sprite.getU(xOffset / 5.0F);
                    final float u1 = sprite.getU((xOffset + 1) / 5.0F);
                    final float v0 = sprite.getV(zOffset / 5.0F);
                    final float v1 = sprite.getV((zOffset + 1) / 5.0F);

                    // Four vertices of a horizontal quad at Y = 0.01 (just above the block surface)
                    buffer.addVertex(mat, x0, 0.01F, z0).setColor(-1).setUv(u0, v0).setOverlay(packedOverlay).setLight(packedLight).setNormal(0, 1, 0);
                    buffer.addVertex(mat, x0, 0.01F, z1).setColor(-1).setUv(u0, v1).setOverlay(packedOverlay).setLight(packedLight).setNormal(0, 1, 0);
                    buffer.addVertex(mat, x1, 0.01F, z1).setColor(-1).setUv(u1, v1).setOverlay(packedOverlay).setLight(packedLight).setNormal(0, 1, 0);
                    buffer.addVertex(mat, x1, 0.01F, z0).setColor(-1).setUv(u1, v0).setOverlay(packedOverlay).setLight(packedLight).setNormal(0, 1, 0);
                }
            }
        }
    }
}
