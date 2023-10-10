/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.render.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

import net.dries007.tfc.client.RenderHelpers;
import net.dries007.tfc.client.model.entity.WaterWheelModel;

public class WaterWheelItemRenderer extends BlockEntityWithoutLevelRenderer
{
    private final WaterWheelModel model;

    public WaterWheelItemRenderer()
    {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
        this.model = new WaterWheelModel(Minecraft.getInstance().getEntityModels().bakeLayer(RenderHelpers.modelIdentifier("water_wheel")));
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext transforms, PoseStack poseStack, MultiBufferSource buffers, int packedLight, int packedOverlay)
    {
        poseStack.pushPose();
        poseStack.scale(1.0F, -1.0F, -1.0F);
        VertexConsumer buffer = ItemRenderer.getFoilBufferDirect(buffers, this.model.renderType(WaterWheelBlockEntityRenderer.TEXTURE), false, stack.hasFoil());
        model.renderToBuffer(poseStack, buffer, packedLight, packedOverlay, 1f, 1f, 1f, 1f);
        poseStack.popPose();
    }
}
