/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.render.blockentity;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.model.data.ModelData;

import net.dries007.tfc.client.RenderHelpers;
import net.dries007.tfc.common.blockentities.AbstractFirepitBlockEntity;
import net.dries007.tfc.common.blocks.devices.FirepitBlock;
import net.dries007.tfc.util.Helpers;

public class FirepitBlockEntityRenderer<T extends AbstractFirepitBlockEntity<?>> implements BlockEntityRenderer<T>
{
    public static final Map<AbstractFirepitBlockEntity.BurnStage, List<ModelResourceLocation>> BURN_STAGE_MODELS = Helpers.mapOf(AbstractFirepitBlockEntity.BurnStage.class, e -> {
        final String name = e.name().toLowerCase(Locale.ROOT);
        return List.of(
            RenderHelpers.modelId("block/firepit_log_1_" + name),
            RenderHelpers.modelId("block/firepit_log_2_" + name),
            RenderHelpers.modelId("block/firepit_log_3_" + name),
            RenderHelpers.modelId("block/firepit_log_4_" + name)
        );
    });

    @Override
    public void render(T firepit, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay)
    {
        if (firepit.getLevel() == null)
        {
            return;
        }

        final Minecraft mc = Minecraft.getInstance();
        final RandomSource random = RandomSource.create();
        for (int i = AbstractFirepitBlockEntity.SLOT_FUEL_CONSUME; i <= AbstractFirepitBlockEntity.SLOT_FUEL_INPUT; i++)
        {
            final ItemStack stack = firepit.getInventory().getStackInSlot(i);
            if (!stack.isEmpty())
            {
                poseStack.pushPose();
                if (firepit.getBlockState().getValue(FirepitBlock.AXIS) == Direction.Axis.Z)
                {
                    poseStack.translate(0.5f, 0.5f, 0.5f);
                    poseStack.mulPose(Axis.YP.rotationDegrees(90f));
                    poseStack.translate(-0.5f, -0.5f, -0.5f);
                }
                AbstractFirepitBlockEntity.BurnStage stage = firepit.getBurnStage(i);
                final BakedModel baked = mc.getModelManager().getModel(BURN_STAGE_MODELS.get(stage).get(i));
                final VertexConsumer buffer = bufferSource.getBuffer(RenderType.solid());
                mc.getBlockRenderer().getModelRenderer().tesselateWithAO(firepit.getLevel(), baked, firepit.getBlockState(), firepit.getBlockPos(), poseStack, buffer, true, random, combinedLight, combinedOverlay, ModelData.EMPTY, RenderType.solid());
                poseStack.popPose();
            }
        }
    }
}
