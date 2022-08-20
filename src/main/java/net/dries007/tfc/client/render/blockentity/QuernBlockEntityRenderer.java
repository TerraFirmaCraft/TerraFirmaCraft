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
import net.minecraft.world.item.ItemStack;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.dries007.tfc.common.blockentities.QuernBlockEntity;
import net.dries007.tfc.common.capabilities.Capabilities;

public class QuernBlockEntityRenderer implements BlockEntityRenderer<QuernBlockEntity>
{
    @Override
    public void render(QuernBlockEntity quern, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay)
    {
        if (quern.getLevel() == null) return;
        quern.getCapability(Capabilities.ITEM).ifPresent(cap -> {
            ItemStack input = cap.getStackInSlot(QuernBlockEntity.SLOT_INPUT);
            ItemStack output = cap.getStackInSlot(QuernBlockEntity.SLOT_OUTPUT);
            ItemStack handstone = cap.getStackInSlot(QuernBlockEntity.SLOT_HANDSTONE);

            if (!output.isEmpty())
            {
                for (int i = 0; i < output.getCount(); i++)
                {
                    double yPos = 0.625D;
                    poseStack.pushPose();
                    switch (Math.floorDiv(i, 16))
                    {
                        case 0 -> {
                            poseStack.translate(0.125D, yPos, 0.125D + (0.046875D * i));
                            poseStack.mulPose(Vector3f.XP.rotationDegrees(75F));
                        }
                        case 1 -> {
                            poseStack.translate(0.125D + (0.046875D * (i - 16)), yPos, 0.875D);
                            poseStack.mulPose(Vector3f.YP.rotationDegrees(90F));
                            poseStack.mulPose(Vector3f.XP.rotationDegrees(75F));
                        }
                        case 2 -> {
                            poseStack.translate(0.875D, yPos, 0.875D - (0.046875D * (i - 32)));
                            poseStack.mulPose(Vector3f.YP.rotationDegrees(180F));
                            poseStack.mulPose(Vector3f.XP.rotationDegrees(75F));
                        }
                        case 3 -> {
                            poseStack.translate(0.875D - (0.046875D * (i - 48)), yPos, 0.125D);
                            poseStack.mulPose(Vector3f.YP.rotationDegrees(270F));
                            poseStack.mulPose(Vector3f.XP.rotationDegrees(75F));
                        }
                        default -> {
                            poseStack.translate(0.5D, 1.0D, 0.5D);
                            poseStack.mulPose(Vector3f.YP.rotationDegrees((quern.getLevel().getGameTime() + partialTicks) * 4F));
                        }
                    }

                    poseStack.scale(0.125F, 0.125F, 0.125F);
                    Minecraft.getInstance().getItemRenderer().renderStatic(output, ItemTransforms.TransformType.FIXED, combinedLight, combinedOverlay, poseStack, buffer, 0);

                    poseStack.popPose();
                }
            }

            if (!handstone.isEmpty())
            {
                int rotationTicks = quern.getRotationTimer();
                double center = rotationTicks > 0 ? 0.497D + (quern.getLevel().random.nextDouble() * 0.006D) : 0.5D;

                poseStack.pushPose();
                poseStack.translate(center, 0.705D, center);

                if (rotationTicks > 0)
                {
                    poseStack.mulPose(Vector3f.YP.rotationDegrees((rotationTicks - partialTicks) * 4F));
                }

                poseStack.scale(1.25F, 1.25F, 1.25F);
                Minecraft.getInstance().getItemRenderer().renderStatic(handstone, ItemTransforms.TransformType.FIXED, combinedLight, combinedOverlay, poseStack, buffer, 0);
                poseStack.popPose();
            }

            if (!input.isEmpty())
            {
                double height = (handstone.isEmpty()) ? 0.75D : 0.875D;
                int rotationTicks = quern.getRotationTimer();
                double center = rotationTicks > 0 ? 0.497D + (quern.getLevel().random.nextDouble() * 0.006D) : 0.5D;

                poseStack.pushPose();
                poseStack.translate(center, height, center);
                poseStack.mulPose(Vector3f.YP.rotationDegrees(45F));
                poseStack.scale(0.5F, 0.5F, 0.5F);

                Minecraft.getInstance().getItemRenderer().renderStatic(input, ItemTransforms.TransformType.FIXED, combinedLight, combinedOverlay, poseStack, buffer, 0);

                poseStack.popPose();
            }
        });
    }
}
