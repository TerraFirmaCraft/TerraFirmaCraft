/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.render.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;

import net.dries007.tfc.client.RenderHelpers;
import net.dries007.tfc.common.blockentities.AxleBlockEntity;
import net.dries007.tfc.common.blockentities.QuernBlockEntity;
import net.dries007.tfc.common.blocks.mechanical.AxleBlock;

/**
 * The trickery here is that when we aren't rotating, we don't render anything.
 * When it is rotating, we render the static state (which we already have loaded automatically by the game and rotated properly)
 * Except since we are in a BER already, we can do posestack transformations.
 */
public class AxleBlockEntityRenderer implements BlockEntityRenderer<AxleBlockEntity>
{
    @Override
    public void render(AxleBlockEntity axle, float partialTicks, PoseStack poseStack, MultiBufferSource buffers, int packedLight, int packedOverlay)
    {
        final BlockState state = axle.getBlockState();
        final Level level = axle.getLevel();
        if (state.getBlock() instanceof AxleBlock && state.getValue(AxleBlock.POWERED) && level != null)
        {
            poseStack.pushPose();
            renderAxle(axle, partialTicks, poseStack, buffers, state, level);
            poseStack.popPose();

        }

        final BlockPos below = axle.getBlockPos().below();
        if (level != null && level.getBlockEntity(below) instanceof QuernBlockEntity quern && quern.hasHandstone() && state.getValue(AxleBlock.AXIS).isVertical())
        {
            poseStack.pushPose();
            poseStack.translate(0, -1, 0);
            renderAxle(axle, partialTicks, poseStack, buffers, state, level);
            poseStack.popPose();
        }
    }

    private static void renderAxle(AxleBlockEntity axle, float partialTicks, PoseStack poseStack, MultiBufferSource buffers, BlockState state, Level level)
    {
        if (axle.getLevel() == null)
            return;
        if (state.getValue(AxleBlock.POWERED))
        {
            poseStack.translate(0.5f, 0.5f, 0.5f);
            float speed = RenderHelpers.getRotationSpeed((int) (level.getGameTime() % 24000), partialTicks);
            final var rot = switch (state.getValue(AxleBlock.AXIS))
                {
                    case X -> Axis.XP.rotationDegrees(speed);
                    case Y -> Axis.YP.rotationDegrees(speed);
                    case Z -> Axis.ZP.rotationDegrees(speed);
                };
            poseStack.mulPose(rot);
            poseStack.translate(-0.5f, -0.5f, -0.5f);
        }
        Minecraft.getInstance().getBlockRenderer().renderBatched(state.setValue(AxleBlock.POWERED, false), axle.getBlockPos(), axle.getLevel(), poseStack, buffers.getBuffer(RenderType.solid()), false, axle.getLevel().getRandom(), ModelData.EMPTY, RenderType.solid());
    }
}