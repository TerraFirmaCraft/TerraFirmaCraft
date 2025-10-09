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
import net.minecraft.resources.ResourceLocation;

import net.dries007.tfc.client.RenderHelpers;
import net.dries007.tfc.client.model.entity.AnemometerModel;
import net.dries007.tfc.common.blockentities.AnemometerBlockEntity;
import net.dries007.tfc.util.Helpers;

public class AnemometerBlockEntityRenderer implements BlockEntityRenderer<AnemometerBlockEntity>
{
    public static final ResourceLocation TEXTURE = Helpers.identifier("textures/entity/anemometer.png");

    private final AnemometerModel model;

    public AnemometerBlockEntityRenderer(BlockEntityRendererProvider.Context context)
    {
        this.model = new AnemometerModel(context.bakeLayer(RenderHelpers.layerId("anemometer")));
    }

    @Override
    public void render(AnemometerBlockEntity anemometer, float partialTick, PoseStack stack, MultiBufferSource buffer, int packedLight, int packedOverlay)
    {
        if (anemometer.getLevel() == null)
        {
            return;
        }

        stack.pushPose();

        stack.translate(0.5f, -0f, 0.5f);
        stack.translate(0, 1.0625f + 0.4375f, 0);
        stack.scale(-1, -1, 1);
        stack.mulPose(Axis.YP.rotationDegrees(0));

        model.setupAnim(anemometer, partialTick);
        model.renderToBuffer(stack, buffer.getBuffer(RenderType.entityCutoutNoCull(TEXTURE)), packedLight, packedOverlay, -1);

        stack.popPose();
    }
}
