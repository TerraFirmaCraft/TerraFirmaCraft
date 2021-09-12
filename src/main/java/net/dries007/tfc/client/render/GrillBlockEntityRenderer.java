/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.CapabilityItemHandler;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.dries007.tfc.common.blockentities.GrillBlockEntity;

import static net.dries007.tfc.common.blockentities.GrillBlockEntity.SLOT_EXTRA_INPUT_END;
import static net.dries007.tfc.common.blockentities.GrillBlockEntity.SLOT_EXTRA_INPUT_START;

public class GrillBlockEntityRenderer implements BlockEntityRenderer<GrillBlockEntity>
{
    @Override
    public void render(GrillBlockEntity te, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay)
    {
        te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(cap -> {
            for (int i = SLOT_EXTRA_INPUT_START; i <= SLOT_EXTRA_INPUT_END; i++)
            {
                ItemStack item = cap.getStackInSlot(i);
                if (!item.isEmpty())
                {
                    float yOffset = 0.625f;
                    matrixStack.pushPose();
                    matrixStack.translate(0.3, 0.003125D + yOffset, 0.28);
                    matrixStack.scale(0.3f, 0.3f, 0.3f);
                    matrixStack.mulPose(Vector3f.XP.rotationDegrees(90F));
                    matrixStack.mulPose(Vector3f.ZP.rotationDegrees(180F));

                    float translateAmount = -1.4F;
                    int ordinal = i - SLOT_EXTRA_INPUT_START;
                    if (ordinal == 1 || ordinal == 3)
                    {
                        matrixStack.translate(translateAmount, 0, 0);
                    }
                    if (ordinal == 2 || ordinal == 3)
                    {
                        matrixStack.translate(0, translateAmount, 0);
                    }
                    if (ordinal == 4)
                    {
                        matrixStack.translate(translateAmount / 2, translateAmount / 2, 0);
                    }

                    Minecraft.getInstance().getItemRenderer().renderStatic(item, ItemTransforms.TransformType.FIXED, combinedLight, combinedOverlay, matrixStack, buffer, 0);
                    matrixStack.popPose();
                }
            }
        });
    }
}
