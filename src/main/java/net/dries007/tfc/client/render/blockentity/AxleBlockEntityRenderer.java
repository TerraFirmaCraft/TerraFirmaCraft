/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.render.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import net.dries007.tfc.client.ClientRotationNetworkHandler;
import net.dries007.tfc.client.RenderHelpers;
import net.dries007.tfc.common.blockentities.rotation.AxleBlockEntity;
import net.dries007.tfc.common.blocks.rotation.AxleBlock;
import net.dries007.tfc.common.blocks.rotation.ConnectedAxleBlock;


public class AxleBlockEntityRenderer implements BlockEntityRenderer<AxleBlockEntity>
{
    public static void renderAxle(PoseStack stack, MultiBufferSource bufferSource, ConnectedAxleBlock axle, Direction.Axis axis, int packedLight, int packedOverlay, float rotationAngle)
    {
        final TextureAtlasSprite sprite = RenderHelpers.blockTexture(axle.getAxleTextureLocation());
        final VertexConsumer buffer = bufferSource.getBuffer(RenderType.cutout());

        stack.pushPose();

        applyRotation(stack, axis, rotationAngle);

        RenderHelpers.renderTexturedCuboid(stack, buffer, sprite, packedLight, packedOverlay, 6f / 16f, 6f / 16f, 0f, 10f / 16f, 10f / 16f, 1f, false);

        stack.popPose();
    }

    public static void applyRotation(PoseStack stack, Direction.Axis axis, float rotationAngle)
    {
        stack.translate(0.5f, 0.5f, 0.5f);

        switch (axis) {
            case X -> stack.mulPose(Axis.YP.rotationDegrees(90));
            case Y -> stack.mulPose(Axis.XP.rotationDegrees(90));
            case Z -> {}
        }

        stack.mulPose(Axis.ZP.rotation(rotationAngle));
        stack.translate(-0.5f, -0.5f, -0.5f);
    }

    @Override
    public void render(AxleBlockEntity axle, float partialTick, PoseStack stack, MultiBufferSource bufferSource, int packedLight, int packedOverlay)
    {
        final BlockState state = axle.getBlockState();
        final Level level = axle.getLevel();

        if (!(state.getBlock() instanceof AxleBlock axleBlock) || level == null)
        {
            return;
        }

        final Direction.Axis axis = state.getValue(AxleBlock.AXIS);

        renderAxle(stack, bufferSource, axleBlock, axis, packedLight, packedOverlay, -ClientRotationNetworkHandler.getRotationAngle(axle, partialTick));
    }
}
