/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.render.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.client.RenderHelpers;
import net.dries007.tfc.common.blockentities.QuernBlockEntity;
import net.dries007.tfc.common.blocks.rotation.ConnectedAxleBlock;
import net.dries007.tfc.util.network.RotationOwner;

public class QuernBlockEntityRenderer implements BlockEntityRenderer<QuernBlockEntity>
{
    @Override
    public void render(QuernBlockEntity quern, float partialTick, PoseStack stack, MultiBufferSource bufferSource, int packedLight, int packedOverlay)
    {
        final Level level = quern.getLevel();

        if (level == null)
        {
            return;
        }

        final ItemStack input = quern.getInventory().getStackInSlot(QuernBlockEntity.SLOT_INPUT);
        final ItemStack output = quern.getInventory().getStackInSlot(QuernBlockEntity.SLOT_OUTPUT);
        final ItemStack handstone = quern.getInventory().getStackInSlot(QuernBlockEntity.SLOT_HANDSTONE);

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
                        float degrees = (level.getGameTime() + partialTick) * 4F;
                        stack.mulPose(Axis.YP.rotationDegrees(degrees));
                    }
                }

                stack.scale(0.125F, 0.125F, 0.125F);
                Minecraft.getInstance().getItemRenderer().renderStatic(output, ItemDisplayContext.FIXED, packedLight, packedOverlay, stack, bufferSource, quern.getLevel(), 0);

                stack.popPose();
            }
        }

        final @Nullable RotationOwner owner = quern.getConnectedNetworkOwner(level);
        final float rotationAngle = quern.getRotationAngle(owner, partialTick);

        // If connected to the network, with a connected axle above, then render the axle connection to the quern
        if (owner != null && level.getBlockState(quern.getBlockPos().above()).getBlock() instanceof ConnectedAxleBlock axleBlock)
        {
            final VertexConsumer buffer = bufferSource.getBuffer(RenderType.cutout());
            final TextureAtlasSprite sprite = RenderHelpers.blockTexture(axleBlock.getAxleTextureLocation());

            stack.pushPose();
            stack.translate(0.5f, 0.5f, 0.5f);
            stack.mulPose(Axis.XP.rotationDegrees(90));
            stack.mulPose(Axis.ZN.rotation(rotationAngle));
            stack.translate(-0.5f, -0.5f, -0.5f);

            RenderHelpers.renderTexturedCuboid(stack, buffer, sprite, packedLight, packedOverlay, 6f / 16f, 6f / 16f, 0f, 10f / 16f, 10f / 16f, 0.5f, false);

            stack.popPose();
        }

        if (!handstone.isEmpty())
        {
            // Cause the handstone to shake a little, if being manually moved
            final float center = owner == null ? 0.498f + (level.random.nextFloat() * 0.004f) : 0.5f;

            stack.pushPose();
            stack.translate(center, 0.705D, center);
            stack.mulPose(Axis.YP.rotation(rotationAngle));
            stack.translate(0.5f - center, 0, 0.5f - center);


            stack.scale(1.25F, 1.25F, 1.25F);
            Minecraft.getInstance().getItemRenderer().renderStatic(handstone, ItemDisplayContext.FIXED, packedLight, packedOverlay, stack, bufferSource, quern.getLevel(), 0);
            stack.popPose();
        }

        if (!input.isEmpty())
        {
            final float height = handstone.isEmpty() ? 0.75f : 0.875f;

            stack.pushPose();
            stack.translate(0.5f, height, 0.5f);
            if (owner != null)
            {
                stack.mulPose(Axis.ZP.rotationDegrees(90F));
                stack.scale(0.8f, 0.8f, 0.8f);
            }
            else
            {
                stack.mulPose(Axis.YP.rotationDegrees(45F));
                stack.scale(0.5F, 0.5F, 0.5F);
            }
            Minecraft.getInstance().getItemRenderer().renderStatic(input, ItemDisplayContext.FIXED, packedLight, packedOverlay, stack, bufferSource, quern.getLevel(), 0);

            stack.popPose();
        }
    }
}
