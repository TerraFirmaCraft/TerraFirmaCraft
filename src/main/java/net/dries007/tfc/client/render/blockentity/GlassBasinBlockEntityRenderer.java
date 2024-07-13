/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.render.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.resources.ResourceLocation;

import net.dries007.tfc.client.RenderHelpers;
import net.dries007.tfc.common.blockentities.GlassBasinBlockEntity;
import net.dries007.tfc.util.Helpers;

public class GlassBasinBlockEntityRenderer implements BlockEntityRenderer<GlassBasinBlockEntity>
{
    private static final ResourceLocation TEXTURE = Helpers.identifier("block/glass/3");

    @Override
    public void render(GlassBasinBlockEntity glass, float partialTicks, PoseStack poseStack, MultiBufferSource buffers, int combinedLight, int combinedOverlay)
    {
        if (glass.getAnimationTicks() > -1)
        {
            final float lerp = Math.min(glass.getAnimationTicks(), 60) / 60f;
            RenderHelpers.renderTexturedFace(poseStack, buffers, 0xFFFFFF, 0, 0, 1, 1, lerp, combinedOverlay, combinedLight, TEXTURE);
        }
    }
}
