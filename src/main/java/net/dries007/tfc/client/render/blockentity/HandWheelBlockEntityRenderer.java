package net.dries007.tfc.client.render.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

import net.dries007.tfc.client.RenderHelpers;
import net.dries007.tfc.common.blockentities.HandWheelBlockEntity;
import net.dries007.tfc.common.blocks.mechanical.HandWheelBlock;

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
                poseStack.mulPose(RenderHelpers.rotateDegreesY(180f));
            }
            else if (facing == Direction.EAST)
            {
                poseStack.mulPose(RenderHelpers.rotateDegreesY(-90f));
            }
            else if (facing == Direction.WEST)
            {
                poseStack.mulPose(RenderHelpers.rotateDegreesY(90f));
            }
            if (wheel.getRotationTimer() > 0)
            {
                poseStack.mulPose(RenderHelpers.rotateDegreesZ(RenderHelpers.getRotationSpeed(wheel.getRotationTimer(), partialTicks)));
            }

            Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemTransforms.TransformType.FIXED, packedLight, packedOverlay, poseStack, buffers, 0);

            poseStack.popPose();
        }

    }
}
