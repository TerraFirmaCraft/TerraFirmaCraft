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
 * todo: I want to remove the "render static geometry when we're not rotating" and remove the POWERED state entirely, but idk how...
 */
public class AxleBlockEntityRenderer implements BlockEntityRenderer<AxleBlockEntity>
{
    @Override
    public void render(AxleBlockEntity axle, float partialTicks, PoseStack poseStack, MultiBufferSource buffers, int packedLight, int packedOverlay)
    {
        final BlockState state = axle.getBlockState();
        final Level level = axle.getLevel();
        if (state.getBlock() instanceof AxleBlock && level != null)
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

    private static void renderAxle(AxleBlockEntity axle, float partialTick, PoseStack poseStack, MultiBufferSource buffers, BlockState state, Level level)
    {
        poseStack.translate(0.5f, 0.5f, 0.5f);
        final Axis axis = switch (state.getValue(AxleBlock.AXIS))
            {
                case X -> Axis.XP;
                case Y -> Axis.YP;
                case Z -> Axis.ZP;
            };
        poseStack.mulPose(axis.rotation(-axle.getRotationAngle(partialTick)));
        poseStack.translate(-0.5f, -0.5f, -0.5f);
        Minecraft.getInstance().getBlockRenderer().renderBatched(state.setValue(AxleBlock.POWERED, false), axle.getBlockPos(), level, poseStack, buffers.getBuffer(RenderType.solid()), false, level.getRandom(), ModelData.EMPTY, RenderType.solid());
    }
}
