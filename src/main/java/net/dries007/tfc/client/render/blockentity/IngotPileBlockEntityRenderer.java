/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.render.blockentity;

import java.util.function.Function;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import net.dries007.tfc.client.RenderHelpers;
import net.dries007.tfc.common.blockentities.IngotPileBlockEntity;
import net.dries007.tfc.common.blocks.devices.IngotPileBlock;
import net.dries007.tfc.util.Metal;

public class IngotPileBlockEntityRenderer implements BlockEntityRenderer<IngotPileBlockEntity>
{
    @Override
    public void render(IngotPileBlockEntity pile, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay)
    {
        final BlockState state = pile.getBlockState();
        if (state.hasProperty(IngotPileBlock.COUNT))
        {
            @SuppressWarnings("deprecation") final Function<ResourceLocation, TextureAtlasSprite> textureAtlas = Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS);
            final VertexConsumer builder = buffer.getBuffer(RenderType.cutout());

            for (int i = 0; i < state.getValue(IngotPileBlock.COUNT); i++)
            {
                final Metal metal = pile.getOrCacheMetal(i);
                final TextureAtlasSprite sprite = textureAtlas.apply(metal.getTextureId());

                final int layer = (i + 8) / 8;
                final float x = (i % 4) * 0.25f;
                final float y = (layer - 1) * 0.125f;
                final float z = i % 8 >= 4 ? 0.5f : 0;

                poseStack.pushPose();
                if (layer % 2 == 1)
                {
                    // Rotate 90 degrees every other layer
                    poseStack.translate(0.5f, 0f, 0.5f);
                    poseStack.mulPose(Vector3f.YP.rotationDegrees(90f));
                    poseStack.translate(-0.5f, 0f, -0.5f);

                    // And translate into position
                    poseStack.translate(x, y, z + 0.5f);
                }
                else
                {
                    poseStack.translate(z, y, x);
                }

                renderIngot(poseStack, sprite, builder, packedLight, packedOverlay);

                poseStack.popPose();

            }
        }
    }

    private void renderIngot(PoseStack poseStack, TextureAtlasSprite sprite, VertexConsumer builder, int packedLight, int packedOverlay)
    {
        final float scale = 0.0625f / 2f;
        final float minX = 0.5f;
        final float minY = 0f;
        final float minZ = 0.5f;
        final float maxX = minX + 15;
        final float maxY = minY + 4;
        final float maxZ = minZ + 7;

        final float[][] vertices = RenderHelpers.getVerticesBySide(minX * scale, minY * scale, minZ * scale, maxX * scale, maxY * scale, maxZ * scale, "xyz");

        for (float[] v : vertices)
        {
            builder.vertex(poseStack.last().pose(), v[0], v[1], v[2])
                .color(1, 1, 1, 1)
                .uv(sprite.getU(v[3]), sprite.getV(v[4]))
                .uv2(packedLight)
                .overlayCoords(packedOverlay)
                .normal(poseStack.last().normal(), 1, 1, 1)
                .endVertex();
        }
    }
}
