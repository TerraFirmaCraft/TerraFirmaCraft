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

import net.dries007.tfc.common.blockentities.PlacedItemBlockEntity;

public class PlacedItemBlockEntityRenderer<T extends PlacedItemBlockEntity> implements BlockEntityRenderer<T>
{
    @Override
    public void render(T placedItem, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay)
    {
        final ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();

        poseStack.translate(0.25D, 0.0625D, 0.25D);
        if (placedItem.holdingLargeItem())
        {
            ItemStack stack = placedItem.getInventory().getStackInSlot(0);
            if (!stack.isEmpty())
            {
                poseStack.pushPose();
                poseStack.translate(0.25D, 0, 0.25D);
                poseStack.mulPose(Axis.XP.rotationDegrees(90f));
                poseStack.mulPose(Axis.ZP.rotationDegrees(placedItem.getRotations(PlacedItemBlockEntity.SLOT_LARGE_ITEM)));
                itemRenderer.renderStatic(stack, ItemDisplayContext.FIXED, combinedLight, combinedOverlay, poseStack, buffer, placedItem.getLevel(), 0);
                poseStack.popPose();
            }
        }
        else
        {
            poseStack.scale(0.5F, 0.5F, 0.5F);
            for (int i = 0; i < placedItem.getInventory().getSlots(); i++)
            {
                ItemStack stack = placedItem.getInventory().getStackInSlot(i);
                if (stack.isEmpty()) continue;
                poseStack.pushPose();
                poseStack.translate((i % 2 == 0 ? 1 : 0), 0, (i < 2 ? 1 : 0));
                poseStack.mulPose(Axis.XP.rotationDegrees(90f));
                poseStack.mulPose(Axis.ZP.rotationDegrees(placedItem.getRotations(i)));
                itemRenderer.renderStatic(stack, ItemDisplayContext.FIXED, combinedLight, combinedOverlay, poseStack, buffer, placedItem.getLevel(), 0);
                poseStack.popPose();
            }
        }
    }

    @Override
    public int getViewDistance()
    {
        return 24;
    }
}
