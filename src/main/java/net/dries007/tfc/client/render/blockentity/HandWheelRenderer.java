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

public class HandWheelRenderer implements BlockEntityRenderer<HandWheelBlockEntity>
{
    @Override
    public void render(HandWheelBlockEntity wheel, float partialTicks, PoseStack poseStack, MultiBufferSource buffers, int packedLight, int packedOverlay)
    {
        final BlockState state = wheel.getBlockState();
        final ItemStack stack = wheel.viewStack();
        if (state.getBlock() instanceof HandWheelBlock && !stack.isEmpty())
        {
            poseStack.pushPose();

            poseStack.translate(0.5f, 0.5f, 0f);
            poseStack.mulPose(RenderHelpers.rotateDegreesZ(RenderHelpers.itemTimeRotation()));

            final Direction facing = state.getValue(HandWheelBlock.FACING);
            if (facing == Direction.NORTH)
            {
                poseStack.translate(1F, 0F, 1F);
            }
            else if (facing == Direction.WEST)
            {
                poseStack.translate(1F, 0F, 0F);
                poseStack.mulPose(RenderHelpers.rotateDegreesY(180F));
            }
            else if (facing == Direction.EAST)
            {
                poseStack.translate(0F, 0F, 1F);
                poseStack.mulPose(RenderHelpers.rotateDegreesY(180F));
            }
            poseStack.mulPose(RenderHelpers.rotateDegreesY(state.getValue(HandWheelBlock.FACING).get2DDataValue() * 90F));


            Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemTransforms.TransformType.FIXED, packedLight, packedOverlay, poseStack, buffers, 0);

            poseStack.popPose();
        }

    }
}
