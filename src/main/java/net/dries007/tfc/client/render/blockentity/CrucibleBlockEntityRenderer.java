/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.render.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.neoforged.neoforge.fluids.FluidStack;

import net.dries007.tfc.client.RenderHelpers;
import net.dries007.tfc.common.blockentities.CrucibleBlockEntity;
import net.dries007.tfc.config.TFCConfig;

public class CrucibleBlockEntityRenderer implements BlockEntityRenderer<CrucibleBlockEntity>
{
    @Override
    public void render(CrucibleBlockEntity crucible, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay)
    {
        FluidStack fluidStack = crucible.getAlloy().getResultAsFluidStack();
        if (!fluidStack.isEmpty())
        {
            final float height = ((float) fluidStack.getAmount() / TFCConfig.SERVER.crucibleCapacity.get() * 13f + 2f) / 16f;
            RenderHelpers.renderFluidFace(poseStack, fluidStack, buffer, 3f / 16, 3f / 16, 13f / 16, 13f / 16, height, combinedOverlay, combinedLight);
        }
    }
}
