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
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.dries007.tfc.client.RenderHelpers;
import net.dries007.tfc.common.blockentities.IngotPileBlockEntity;
import net.dries007.tfc.common.blocks.devices.IngotPileBlock;
import net.dries007.tfc.util.Metal;

public class IngotPileBlockEntityRenderer implements BlockEntityRenderer<IngotPileBlockEntity>
{

    @Override
    public void render(IngotPileBlockEntity pile, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay)
    {
        final BlockState state = pile.getBlockState();
        if (state.hasProperty(IngotPileBlock.COUNT))
        {
            final Function<ResourceLocation, TextureAtlasSprite> textureAtlas = Minecraft.getInstance().getTextureAtlas(RenderHelpers.BLOCKS_ATLAS);
            final VertexConsumer buffer = bufferSource.getBuffer(RenderType.cutout());

            for (int i = 0; i < state.getValue(IngotPileBlock.COUNT); i++)
            {
                final Metal metal = pile.getOrCacheMetal(i);
                final TextureAtlasSprite sprite = textureAtlas.apply(metal.getTextureId());

                final int layer = (i + 8) / 8;
                final boolean oddLayer = (layer % 2) == 1;
                final float x = (i % 4) * 0.25f;
                final float y = (layer - 1) * 0.125f;
                final float z = i % 8 >= 4 ? 0.5f : 0;

                poseStack.pushPose();
                if (oddLayer)
                {
                    // Rotate 90 degrees every other layer
                    poseStack.translate(0.5f, 0f, 0.5f);
                    poseStack.mulPose(RenderHelpers.rotateDegreesY(90f));
                    poseStack.translate(-0.5f, 0f, -0.5f);
                }

                poseStack.translate(x, y, z);

                final float scale = 0.0625f / 2f;
                final float minX = scale * 0.5f;
                final float minY = scale * 0f;
                final float minZ = scale * 0.5f;
                final float maxX = scale * (minX + 7);
                final float maxY = scale * (minY + 4);
                final float maxZ = scale * (minZ + 15);

                RenderHelpers.renderTexturedTrapezoidalCuboid(poseStack, buffer, sprite, packedLight, packedOverlay, minX, maxX, minZ, maxZ, minX + scale, maxX - scale, minZ + scale, maxZ - scale, minY, maxY, 7, 4, 15);

                poseStack.popPose();

            }
        }
    }
}
