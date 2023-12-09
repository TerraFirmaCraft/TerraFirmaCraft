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
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

import net.dries007.tfc.common.blockentities.rotation.HandWheelBlockEntity;
import net.dries007.tfc.common.blocks.rotation.HandWheelBlock;

public class HandWheelBlockEntityRenderer implements BlockEntityRenderer<HandWheelBlockEntity>
{
    @Override
    public void render(HandWheelBlockEntity wheel, float partialTicks, PoseStack poseStack, MultiBufferSource buffers, int packedLight, int packedOverlay)
    {
        final BlockState state = wheel.getBlockState();
        final ItemStack stack = wheel.viewStack();
        if (state.getBlock() instanceof HandWheelBlock && !stack.isEmpty() && wheel.getLevel() != null)
        {
            poseStack.pushPose();

            poseStack.scale(2f, 2f, 2f);

            poseStack.translate(0.25f, 0.25f, 0.25f);

            final Direction facing = state.getValue(HandWheelBlock.FACING);
            if (facing == Direction.SOUTH)
            {
                poseStack.mulPose(Axis.YP.rotationDegrees(180f));
            }
            else if (facing == Direction.EAST)
            {
                poseStack.mulPose(Axis.YP.rotationDegrees(-90f));
            }
            else if (facing == Direction.WEST)
            {
                poseStack.mulPose(Axis.YP.rotationDegrees(90f));
            }

            final float rotationAngle = wheel.getRotationAngle(partialTicks);
            if (rotationAngle != 0)
            {
                poseStack.mulPose(Axis.ZP.rotation(rotationAngle));
            }

            Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemDisplayContext.FIXED, packedLight, packedOverlay, poseStack, buffers, wheel.getLevel(), 0);

            poseStack.popPose();
        }

    }
}
