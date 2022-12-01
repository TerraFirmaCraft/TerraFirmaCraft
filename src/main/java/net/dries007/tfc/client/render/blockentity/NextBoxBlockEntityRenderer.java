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
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.item.ItemStack;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.dries007.tfc.client.RenderHelpers;
import net.dries007.tfc.common.blockentities.NestBoxBlockEntity;
import net.dries007.tfc.common.capabilities.Capabilities;

public class NextBoxBlockEntityRenderer implements BlockEntityRenderer<NestBoxBlockEntity>
{
    @Override
    public void render(NestBoxBlockEntity nestBox, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay)
    {
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        nestBox.getCapability(Capabilities.ITEM).ifPresent(cap -> {
            float timeD = RenderHelpers.itemTimeRotation();
            poseStack.translate(0.5D, 0.25D, 0.5D);
            poseStack.scale(0.5f, 0.5f, 0.5f);
            for (int i = 0; i < cap.getSlots(); i++)
            {
                ItemStack stack = cap.getStackInSlot(i);
                if (stack.isEmpty()) continue;
                poseStack.pushPose();
                poseStack.translate((i % 2 == 0 ? -1 : 1) * 0.33f, 0, (i < 2 ? -1 : 1) * 0.33f);
                poseStack.mulPose(Vector3f.YP.rotationDegrees(timeD));
                itemRenderer.renderStatic(stack, ItemTransforms.TransformType.FIXED, combinedLight, combinedOverlay, poseStack, buffer, 0);
                poseStack.popPose();
            }
        });
    }
}
