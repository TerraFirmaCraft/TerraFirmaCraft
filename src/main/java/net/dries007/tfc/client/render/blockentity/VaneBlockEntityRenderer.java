/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.render.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

import net.dries007.tfc.client.RenderHelpers;
import net.dries007.tfc.client.model.entity.VaneModel;
import net.dries007.tfc.common.blockentities.VaneBlockEntity;
import net.dries007.tfc.util.Helpers;

import static net.dries007.tfc.common.blocks.TFCBlockStateProperties.*;

public class VaneBlockEntityRenderer implements BlockEntityRenderer<VaneBlockEntity>
{
    public static final ResourceLocation TEXTURE = Helpers.identifier("textures/entity/vane.png");

    private final VaneModel model;

    public VaneBlockEntityRenderer(BlockEntityRendererProvider.Context context)
    {
        this.model = new VaneModel(context.bakeLayer(RenderHelpers.layerId("vane")));
    }

    @Override
    public void render(VaneBlockEntity vane, float partialTick, PoseStack stack, MultiBufferSource buffer, int packedLight, int packedOverlay)
    {
        if (vane.getLevel() == null)
        {
            return;
        }

        stack.pushPose();

        stack.translate(0.5f, -0f, 0.5f);
        stack.translate(0, 1.0625f + 0.4375f, 0);
        stack.scale(-1, -1, 1);

        if (vane.getBlockState().getValue(ATTACHED_WIND_DEVICES))
        {
            stack.translate(0, 0.5625f, 0);
        }
        else
        {
            model.renderBase(stack, buffer.getBuffer(RenderType.entityCutoutNoCull(TEXTURE)), packedLight, packedOverlay, -1);
        }

        model.setupAnim(vane, partialTick);
        model.renderToBuffer(stack, buffer.getBuffer(RenderType.entityCutoutNoCull(TEXTURE)), packedLight, packedOverlay, -1);

        stack.popPose();
    }
}