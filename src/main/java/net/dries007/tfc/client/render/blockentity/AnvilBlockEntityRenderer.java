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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.dries007.tfc.common.blockentities.AnvilBlockEntity;

public class AnvilBlockEntityRenderer implements BlockEntityRenderer<AnvilBlockEntity>
{
    @Override
    public void render(AnvilBlockEntity anvil, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay)
    {
        if (anvil.getLevel() == null)
        {
            return;
        }

        final BlockState state = anvil.getLevel().getBlockState(anvil.getBlockPos());
        anvil.getCapability(Capabilities.ITEM).ifPresent(cap -> {
            final int rotation = state.hasProperty(BlockStateProperties.HORIZONTAL_FACING) ? state.getValue(BlockStateProperties.HORIZONTAL_FACING).get2DDataValue() : 0;
            final float yOffset = anvil.getTier() == 0 ? 0.875f : 0.6875f;

            poseStack.pushPose();
            poseStack.translate(0.5, 0.003125D + yOffset, 0.5);
            poseStack.scale(0.3f, 0.3f, 0.3f);
            poseStack.mulPose(Vector3f.XP.rotationDegrees(90f));
            poseStack.mulPose(Vector3f.ZP.rotationDegrees(90f * rotation + 270f));
            poseStack.translate(1.2f, 0, 0);

            final ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
            final ItemStack hammer = cap.getStackInSlot(AnvilBlockEntity.SLOT_HAMMER);
            if (!hammer.isEmpty())
            {
                itemRenderer.renderStatic(hammer, ItemTransforms.TransformType.FIXED, combinedLight, combinedOverlay, poseStack, buffer, 0);
            }

            poseStack.translate(-1.3f, 0, 0);
            final ItemStack input1 = cap.getStackInSlot(AnvilBlockEntity.SLOT_INPUT_MAIN);
            if (!input1.isEmpty())
            {
                itemRenderer.renderStatic(input1, ItemTransforms.TransformType.FIXED, combinedLight, combinedOverlay, poseStack, buffer, 0);
            }

            poseStack.translate(-0.4f, 0, -0.05f);
            final ItemStack input2 = cap.getStackInSlot(AnvilBlockEntity.SLOT_INPUT_SECOND);
            if (!input2.isEmpty())
            {
                itemRenderer.renderStatic(input2, ItemTransforms.TransformType.FIXED, combinedLight, combinedOverlay, poseStack, buffer, 0);
            }

            final ItemStack catalyst = cap.getStackInSlot(AnvilBlockEntity.SLOT_CATALYST);
            if (!catalyst.isEmpty())
            {
                poseStack.pushPose();
                poseStack.translate(0.9f, -0.25f, 0.05f);
                poseStack.scale(0.6f, 0.6f, 0.6f);
                itemRenderer.renderStatic(catalyst, ItemTransforms.TransformType.FIXED, combinedLight, combinedOverlay, poseStack, buffer, 0);
                poseStack.popPose();
            }

            poseStack.popPose();
        });
    }
}
