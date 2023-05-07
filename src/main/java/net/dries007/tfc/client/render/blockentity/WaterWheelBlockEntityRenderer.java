package net.dries007.tfc.client.render.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

import net.dries007.tfc.client.RenderHelpers;
import net.dries007.tfc.client.model.entity.WaterWheelModel;
import net.dries007.tfc.common.blockentities.WaterWheelBlockEntity;
import net.dries007.tfc.common.blocks.mechanical.WaterWheelBlock;
import net.dries007.tfc.util.Helpers;

public class WaterWheelBlockEntityRenderer implements BlockEntityRenderer<WaterWheelBlockEntity>
{
    public static final ResourceLocation TEXTURE = Helpers.identifier("textures/entity/misc/water_wheel.png");

    private final WaterWheelModel model;

    public WaterWheelBlockEntityRenderer(BlockEntityRendererProvider.Context context)
    {
        this.model = new WaterWheelModel(context.bakeLayer(RenderHelpers.modelIdentifier("water_wheel")));
    }

    @Override
    public void render(WaterWheelBlockEntity wheel, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay)
    {
        if (wheel.getLevel() == null)
        {
            return;
        }
        poseStack.pushPose();

        if (wheel.isPowered())
        {
            //poseStack.mulPose(RenderHelpers.rotateDegreesX(RenderHelpers.getRotationSpeed((int) (wheel.getLevel().getGameTime() % 24000), partialTicks)));
        }
        poseStack.translate(0.5f, -0.5f, 0.5f);

        if (wheel.getBlockState().getValue(WaterWheelBlock.AXIS) == Direction.Axis.Z)
        {
            poseStack.mulPose(RenderHelpers.rotateDegreesY(90f));
        }

        model.setupAnim(wheel, partialTicks);
        model.renderToBuffer(poseStack, buffer.getBuffer(RenderType.entityCutout(TEXTURE)), combinedLight, combinedOverlay, 1f, 1f, 1f, 1f);

        poseStack.popPose();
    }

    @Override
    public boolean shouldRender(WaterWheelBlockEntity blockEntity, Vec3 cameraPos)
    {
        return true;
    }
}
