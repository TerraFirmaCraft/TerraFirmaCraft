/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.render.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;

import net.dries007.tfc.client.RenderHelpers;
import net.dries007.tfc.client.model.entity.WaterWheelModel;
import net.dries007.tfc.common.blockentities.rotation.WaterWheelBlockEntity;
import net.dries007.tfc.common.blocks.rotation.WaterWheelBlock;

public class WaterWheelBlockEntityRenderer implements BlockEntityRenderer<WaterWheelBlockEntity>
{
    private final WaterWheelModel model;

    public WaterWheelBlockEntityRenderer(BlockEntityRendererProvider.Context context)
    {
        this.model = new WaterWheelModel(context.bakeLayer(RenderHelpers.layerId("water_wheel")));
    }

    @Override
    public void render(WaterWheelBlockEntity wheel, float partialTick, PoseStack stack, MultiBufferSource buffer, int packedLight, int packedOverlay)
    {
        if (!(wheel.getBlockState().getBlock() instanceof WaterWheelBlock wheelBlock) || wheel.getLevel() == null)
        {
            return;
        }

        stack.pushPose();
        stack.translate(0.5f, -0.5f, 0.5f);

        if (wheel.getBlockState().getValue(WaterWheelBlock.AXIS) == Direction.Axis.Z)
        {
            stack.mulPose(Axis.YN.rotationDegrees(90f));
        }

        model.setupAnim(wheel, partialTick);
        model.renderToBuffer(stack, buffer.getBuffer(RenderType.entityCutout(wheelBlock.getTextureLocation())), packedLight, packedOverlay, -1);

        stack.popPose();
    }

    @Override
    public boolean shouldRenderOffScreen(WaterWheelBlockEntity entity)
    {
        return true;
    }

    @Override
    public AABB getRenderBoundingBox(WaterWheelBlockEntity blockEntity)
    {
        return AABB.INFINITE;
    }
}
