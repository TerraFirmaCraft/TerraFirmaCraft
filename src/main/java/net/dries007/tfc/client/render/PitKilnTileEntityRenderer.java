/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.vector.Vector3f;

import net.minecraftforge.items.CapabilityItemHandler;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.dries007.tfc.common.blocks.devices.PitKilnBlock;
import net.dries007.tfc.common.tileentity.PitKilnTileEntity;

public class PitKilnTileEntityRenderer extends TileEntityRenderer<PitKilnTileEntity>
{
    public PitKilnTileEntityRenderer(TileEntityRendererDispatcher dispatcher)
    {
        super(dispatcher);
    }

    @Override
    public void render(PitKilnTileEntity te, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay)
    {
        if (te.getBlockState().getValue(PitKilnBlock.STAGE) > 9) return;

        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(cap -> {
            float timeD = (float) (360.0 * (System.currentTimeMillis() & 0x3FFFL) / 0x3FFFL);
            matrixStack.translate(0.25D, 0.25D, 0.25D);
            if (te.holdingLargeItem())
            {
                ItemStack stack = cap.getStackInSlot(0);
                if (!stack.isEmpty())
                {
                    matrixStack.pushPose();
                    matrixStack.translate(0.25D, 0, 0.25D);
                    matrixStack.mulPose(Vector3f.YP.rotationDegrees(90F));
                    itemRenderer.renderStatic(stack, ItemCameraTransforms.TransformType.FIXED, combinedLight, combinedOverlay, matrixStack, buffer);
                    matrixStack.popPose();
                }
            }
            else
            {
                matrixStack.scale(0.5F, 0.5F, 0.5F);
                for (int i = 0; i < cap.getSlots(); i++)
                {
                    ItemStack stack = cap.getStackInSlot(i);
                    if (stack.isEmpty()) continue;
                    matrixStack.pushPose();
                    matrixStack.translate((i % 2 == 0 ? 1 : 0), 0, (i < 2 ? 1 : 0));
                    matrixStack.mulPose(Vector3f.YP.rotationDegrees(timeD));
                    itemRenderer.renderStatic(stack, ItemCameraTransforms.TransformType.FIXED, combinedLight, combinedOverlay, matrixStack, buffer);
                    matrixStack.popPose();
                }
            }
        });
    }
}
