/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.render.blockentity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.world.level.block.state.BlockState;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.dries007.tfc.client.RenderHelpers;
import net.dries007.tfc.common.blockentities.BarrelBlockEntity;
import net.dries007.tfc.common.blocks.devices.BarrelBlock;
import net.dries007.tfc.common.capabilities.Capabilities;
import net.dries007.tfc.config.TFCConfig;

public class BarrelBlockEntityRenderer implements BlockEntityRenderer<BarrelBlockEntity>
{
    @Override
    public void render(BarrelBlockEntity barrel, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay)
    {
        BlockState state = barrel.getBlockState();
        if (state.getValue(BarrelBlock.SEALED)) return;

        barrel.getCapability(Capabilities.FLUID).map(handler -> handler.getFluidInTank(0)).filter(fluid -> !fluid.isEmpty()).ifPresent(fluidStack -> {
            RenderHelpers.renderFluidFace(poseStack, fluidStack, buffer, 0.1875F, 0.1875F, 0.8125F, 0.8125F, 0.140625F + (0.75F - 0.015625F) * fluidStack.getAmount() / TFCConfig.SERVER.barrelCapacity.get(), combinedOverlay, combinedLight);
        });

        barrel.getCapability(Capabilities.ITEM).map(inv -> inv.getStackInSlot(BarrelBlockEntity.SLOT_ITEM)).filter(item -> !item.isEmpty()).ifPresent(itemStack -> {
            poseStack.pushPose();
            poseStack.translate(0.5F, 0.15625F, 0.5F);
            poseStack.scale(0.5F, 0.5F, 0.5F);
            poseStack.mulPose(Vector3f.XP.rotationDegrees(90f));

            Minecraft.getInstance().getItemRenderer().renderStatic(itemStack, ItemTransforms.TransformType.FIXED, combinedLight, combinedOverlay, poseStack, buffer, 0);

            poseStack.popPose();
        });
    }
}
