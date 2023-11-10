/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.render.blockentity;

import java.util.function.Function;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

import net.dries007.tfc.client.RenderHelpers;
import net.dries007.tfc.common.blockentities.QuernBlockEntity;
import net.dries007.tfc.common.capabilities.Capabilities;
import net.dries007.tfc.util.Helpers;

public class QuernBlockEntityRenderer implements BlockEntityRenderer<QuernBlockEntity>
{
    @Override
    public void render(QuernBlockEntity quern, float partialTicks, PoseStack stack, MultiBufferSource buffer, int packedLight, int packedOverlay)
    {
        final IItemHandler cap = Helpers.getCapability(quern, Capabilities.ITEM);

        if (cap == null || quern.getLevel() == null)
        {
            return;
        }

        final ItemStack input = cap.getStackInSlot(QuernBlockEntity.SLOT_INPUT);
        final ItemStack output = cap.getStackInSlot(QuernBlockEntity.SLOT_OUTPUT);
        final ItemStack handstone = cap.getStackInSlot(QuernBlockEntity.SLOT_HANDSTONE);

        if (!output.isEmpty())
        {
            for (int i = 0; i < output.getCount(); i++)
            {
                double yPos = 0.625D;
                stack.pushPose();
                switch (Math.floorDiv(i, 16))
                {
                    case 0 ->
                    {
                        stack.translate(0.125D, yPos, 0.125D + (0.046875D * i));
                        stack.mulPose(Axis.XP.rotationDegrees(75F));
                    }
                    case 1 ->
                    {
                        stack.translate(0.125D + (0.046875D * (i - 16)), yPos, 0.875D);
                        stack.mulPose(Axis.YP.rotationDegrees(90F));
                        stack.mulPose(Axis.XP.rotationDegrees(75F));
                    }
                    case 2 ->
                    {
                        stack.translate(0.875D, yPos, 0.875D - (0.046875D * (i - 32)));
                        stack.mulPose(Axis.YP.rotationDegrees(180F));
                        stack.mulPose(Axis.XP.rotationDegrees(75F));
                    }
                    case 3 ->
                    {
                        stack.translate(0.875D - (0.046875D * (i - 48)), yPos, 0.125D);
                        stack.mulPose(Axis.YP.rotationDegrees(270F));
                        stack.mulPose(Axis.XP.rotationDegrees(75F));
                    }
                    default ->
                    {
                        stack.translate(0.5D, 1.0D, 0.5D);
                        float degrees = (quern.getLevel().getGameTime() + partialTicks) * 4F;
                        stack.mulPose(Axis.YP.rotationDegrees(degrees));
                    }
                }

                stack.scale(0.125F, 0.125F, 0.125F);
                Minecraft.getInstance().getItemRenderer().renderStatic(output, ItemDisplayContext.FIXED, packedLight, packedOverlay, stack, buffer, quern.getLevel(), 0);

                stack.popPose();
            }
        }

        final boolean isConnectedToNetwork = quern.isConnectedToNetwork();
        final float rotationAngle = quern.getRotationAngle(partialTicks);

        if (!handstone.isEmpty())
        {
            final float center = !isConnectedToNetwork ? 0.498f + (quern.getLevel().random.nextFloat() * 0.004f) : 0.5f;

            stack.pushPose();
            stack.translate(center, 0.705D, center);
            stack.mulPose(Axis.YP.rotation(rotationAngle));
            stack.translate(0.5f - center, 0, 0.5f - center);


            stack.scale(1.25F, 1.25F, 1.25F);
            Minecraft.getInstance().getItemRenderer().renderStatic(handstone, ItemDisplayContext.FIXED, packedLight, packedOverlay, stack, buffer, quern.getLevel(), 0);
            stack.popPose();
        }

        if (!input.isEmpty())
        {
            final float height = handstone.isEmpty() ? 0.75f : 0.875f;

            stack.pushPose();
            stack.translate(0.5f, height, 0.5f);
            if (quern.isConnectedToNetwork())
            {
                stack.mulPose(Axis.ZP.rotationDegrees(90F));
                stack.scale(0.8f, 0.8f, 0.8f);
            }
            else
            {
                stack.mulPose(Axis.YP.rotationDegrees(45F));
                stack.scale(0.5F, 0.5F, 0.5F);
            }
            Minecraft.getInstance().getItemRenderer().renderStatic(input, ItemDisplayContext.FIXED, packedLight, packedOverlay, stack, buffer, quern.getLevel(), 0);

            stack.popPose();
        }
    }
}
