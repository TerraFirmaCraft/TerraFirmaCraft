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
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.model.data.ModelData;

import net.dries007.tfc.common.blockentities.AbstractFirepitBlockEntity;
import net.dries007.tfc.common.blocks.devices.FirepitBlock;
import net.dries007.tfc.util.Helpers;

public class FirepitBlockEntityRenderer<T extends AbstractFirepitBlockEntity<?>> implements BlockEntityRenderer<T>
{
    @Override
    public void render(T firepit, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay)
    {
        final var inv = Helpers.getCapability(firepit, Capabilities.ITEM);
        if (inv == null || firepit.getLevel() == null)
            return;
        final Minecraft mc = Minecraft.getInstance();
        final RandomSource random = RandomSource.create();
        for (int i = AbstractFirepitBlockEntity.SLOT_FUEL_CONSUME; i <= AbstractFirepitBlockEntity.SLOT_FUEL_INPUT; i++)
        {
            final ItemStack stack = inv.getStackInSlot(i);
            if (!stack.isEmpty())
            {
                poseStack.pushPose();
                if (firepit.getBlockState().getValue(FirepitBlock.AXIS) == Direction.Axis.Z)
                {
                    poseStack.translate(0.5f, 0.5f, 0.5f);
                    poseStack.mulPose(Axis.YP.rotationDegrees(90f));
                    poseStack.translate(-0.5f, -0.5f, -0.5f);
                }
                final BakedModel baked = mc.getModelManager().getModel(firepit.getBurnStage(i).getModel(i));
                final VertexConsumer buffer = bufferSource.getBuffer(RenderType.solid());
                mc.getBlockRenderer().getModelRenderer().tesselateWithAO(firepit.getLevel(), baked, firepit.getBlockState(), firepit.getBlockPos(), poseStack, buffer, true, random, combinedLight, combinedOverlay, ModelData.EMPTY, RenderType.solid());
                poseStack.popPose();
            }
        }
    }
}
