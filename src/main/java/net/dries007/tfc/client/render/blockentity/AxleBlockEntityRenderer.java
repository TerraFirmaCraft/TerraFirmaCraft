package net.dries007.tfc.client.render.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import net.dries007.tfc.client.RenderHelpers;
import net.dries007.tfc.common.blockentities.AxleBlockEntity;
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
        if (state.getBlock() instanceof AxleBlock && state.getValue(AxleBlock.AXLE_STATE) != AxleBlock.AxleState.NONE && level != null)
        {
            poseStack.pushPose();
            poseStack.translate(0.5f, 0.5f, 0.5f);
            final float speed = RenderHelpers.getRotationSpeed((int) (level.getGameTime() % 24000), partialTicks);
            final var rot = switch (state.getValue(AxleBlock.AXIS))
            {
                case X -> RenderHelpers.rotateDegreesX(speed);
                case Y -> RenderHelpers.rotateDegreesY(speed);
                case Z -> RenderHelpers.rotateDegreesZ(speed);
            };
            poseStack.mulPose(rot);
            poseStack.translate(-0.5f, -0.5f, -0.5f);

            Minecraft.getInstance().getBlockRenderer().renderSingleBlock(state.setValue(AxleBlock.AXLE_STATE, AxleBlock.AxleState.NONE), poseStack, buffers, packedLight, packedOverlay, EmptyModelData.INSTANCE);

            poseStack.popPose();
        }

    }
}
