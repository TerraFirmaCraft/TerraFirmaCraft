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
import net.dries007.tfc.common.blockentities.PlacedItemBlockEntity;
import net.dries007.tfc.common.capabilities.Capabilities;

public class PlacedItemBlockEntityRenderer<T extends PlacedItemBlockEntity> implements BlockEntityRenderer<T>
{
    @Override
    public void render(T placedItem, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay)
    {
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        placedItem.getCapability(Capabilities.ITEM).ifPresent(cap -> {
            float timeD = RenderHelpers.itemTimeRotation();
            poseStack.translate(0.25D, 0.25D, 0.25D);
            if (placedItem.holdingLargeItem())
            {
                ItemStack stack = cap.getStackInSlot(0);
                if (!stack.isEmpty())
                {
                    poseStack.pushPose();
                    poseStack.translate(0.25D, 0, 0.25D);
                    poseStack.mulPose(Vector3f.YP.rotationDegrees(90F));
                    itemRenderer.renderStatic(stack, ItemTransforms.TransformType.FIXED, combinedLight, combinedOverlay, poseStack, buffer, 0);
                    poseStack.popPose();
                }
            }
            else
            {
                poseStack.scale(0.5F, 0.5F, 0.5F);
                for (int i = 0; i < cap.getSlots(); i++)
                {
                    ItemStack stack = cap.getStackInSlot(i);
                    if (stack.isEmpty()) continue;
                    poseStack.pushPose();
                    poseStack.translate((i % 2 == 0 ? 1 : 0), 0, (i < 2 ? 1 : 0));
                    poseStack.mulPose(Vector3f.YP.rotationDegrees(timeD));
                    itemRenderer.renderStatic(stack, ItemTransforms.TransformType.FIXED, combinedLight, combinedOverlay, poseStack, buffer, 0);
                    poseStack.popPose();
                }
            }
        });
    }
}
