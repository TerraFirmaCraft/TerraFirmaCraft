/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.render.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import net.dries007.tfc.client.ClientRotationNetworkHandler;
import net.dries007.tfc.client.RenderHelpers;
import net.dries007.tfc.common.blockentities.rotation.BladedAxleBlockEntity;
import net.dries007.tfc.common.blocks.rotation.BladedAxleBlock;
import net.dries007.tfc.util.Helpers;

public class BladedAxleBlockEntityRenderer implements BlockEntityRenderer<BladedAxleBlockEntity>
{
    private static final ResourceLocation BLADE_TEXTURE = Helpers.identifier("block/metal/block/steel");

    public static void renderBlade(PoseStack stack, MultiBufferSource bufferSource, Direction.Axis axis, int packedLight, int packedOverlay, float rotationAngle)
    {
        final TextureAtlasSprite sprite = RenderHelpers.blockTexture(BLADE_TEXTURE);
        final VertexConsumer buffer = bufferSource.getBuffer(RenderType.cutout());

        stack.pushPose();

        AxleBlockEntityRenderer.applyRotation(stack, axis, rotationAngle);

        RenderHelpers.renderTexturedCuboid(stack, buffer, sprite, packedLight, packedOverlay, 7f / 16f, 10f / 16f, 6f / 16f, 9f / 16f, 17.5f / 16f, 10f / 16f, false);

        stack.popPose();
    }

    @Override
    public void render(BladedAxleBlockEntity axle, float partialTick, PoseStack stack, MultiBufferSource bufferSource, int packedLight, int packedOverlay)
    {
        final BlockState state = axle.getBlockState();
        final Level level = axle.getLevel();

        if (!(state.getBlock() instanceof BladedAxleBlock axleBlock) || level == null)
        {
            return;
        }

        final Direction.Axis axis = state.getValue(BladedAxleBlock.AXIS);
        final float angle = ClientRotationNetworkHandler.getRotationAngle(axle, partialTick);

        AxleBlockEntityRenderer.renderAxle(stack, bufferSource, axleBlock, axis, packedLight, packedOverlay, -angle);
        renderBlade(stack, bufferSource, axis, packedLight, packedOverlay, -angle);
    }
}
