/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.render.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.model.data.ModelData;

import net.dries007.tfc.common.blockentities.JarsBlockEntity;
import net.dries007.tfc.common.items.JarItem;
import net.dries007.tfc.util.Helpers;

public class JarsBlockEntityRenderer implements BlockEntityRenderer<JarsBlockEntity>
{
    @Override
    public void render(JarsBlockEntity jars, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay)
    {
        final var inv = Helpers.getCapability(jars, Capabilities.ITEM);
        if (inv == null || jars.getLevel() == null)
        {
            return;
        }

        final RandomSource random = RandomSource.create();
        final Minecraft mc = Minecraft.getInstance();
        for (int i = 0; i < inv.getSlots(); i++)
        {
            final ItemStack stack = inv.getStackInSlot(i);

            poseStack.pushPose();
            poseStack.translate((i % 2 == 0 ? 0.5 : 0), 0, (i < 2 ? 0.5 : 0));

            if (stack.getItem() instanceof JarItem jarItem)
            {
                final BakedModel baked = mc.getModelManager().getModel(jarItem.getModel());
                final VertexConsumer buffer = bufferSource.getBuffer(RenderType.translucent());
                mc.getBlockRenderer().getModelRenderer().tesselateWithAO(jars.getLevel(), baked, jars.getBlockState(), jars.getBlockPos(), poseStack, buffer, true, random, packedLight, packedOverlay, ModelData.EMPTY, RenderType.translucent());
            }

            poseStack.popPose();
        }
    }
}
