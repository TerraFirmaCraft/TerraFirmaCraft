/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.render.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

import net.dries007.tfc.client.RenderHelpers;
import net.dries007.tfc.common.blockentities.NestBoxBlockEntity;

public class NestBoxBlockEntityRenderer implements BlockEntityRenderer<NestBoxBlockEntity>
{
    @Override
    public void render(NestBoxBlockEntity nestBox, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay)
    {
        final ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        final float timeD = RenderHelpers.itemTimeRotation();

        poseStack.translate(0.5D, 0.25D, 0.5D);
        poseStack.scale(0.5f, 0.5f, 0.5f);
        for (int i = 0; i < nestBox.getInventory().getSlots(); i++)
        {
            ItemStack stack = nestBox.getInventory().getStackInSlot(i);
            if (stack.isEmpty()) continue;
            poseStack.pushPose();
            poseStack.translate((i % 2 == 0 ? -1 : 1) * 0.33f, 0, (i < 2 ? -1 : 1) * 0.33f);
            poseStack.mulPose(Axis.YP.rotationDegrees(timeD));
            itemRenderer.renderStatic(stack, ItemDisplayContext.FIXED, combinedLight, combinedOverlay, poseStack, buffer, nestBox.getLevel(), 0);
            poseStack.popPose();
        }
    }
}
