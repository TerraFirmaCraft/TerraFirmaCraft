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
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.dries007.tfc.client.RenderHelpers;
import net.dries007.tfc.common.blockentities.SheetPileBlockEntity;
import net.dries007.tfc.common.blocks.DirectionPropertyBlock;
import net.dries007.tfc.common.blocks.devices.SheetPileBlock;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.Metal;

public class SheetPileBlockEntityRenderer implements BlockEntityRenderer<SheetPileBlockEntity>
{
    @Override
    public void render(SheetPileBlockEntity pile, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay)
    {
        final BlockState state = pile.getBlockState();
        if (state.getBlock() instanceof DirectionPropertyBlock)
        {
            @SuppressWarnings("deprecation") final Function<ResourceLocation, TextureAtlasSprite> textureAtlas = Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS);
            final VertexConsumer builder = buffer.getBuffer(RenderType.cutout());

            poseStack.pushPose();
            for (Direction direction : Helpers.DIRECTIONS)
            {
                if (state.getValue(DirectionPropertyBlock.getProperty(direction))) // The properties are authoritative on which sides should be rendered
                {
                    final Metal metal = pile.getOrCacheMetal(direction);
                    final TextureAtlasSprite sprite = textureAtlas.apply(metal.getTextureId());

                    renderSheet(poseStack, sprite, builder, direction, packedLight, packedOverlay);
                }
            }
            poseStack.popPose();
        }
    }

    private void renderSheet(PoseStack poseStack, TextureAtlasSprite sprite, VertexConsumer builder, Direction direction, int packedLight, int packedOverlay)
    {
        final float[][] vertices = RenderHelpers.getVerticesByShape(SheetPileBlock.getShapeForSingleFace(direction), "xyz");

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
