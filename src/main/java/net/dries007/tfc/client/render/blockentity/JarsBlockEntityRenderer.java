/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.render.blockentity;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.model.data.ModelData;

import net.dries007.tfc.client.RenderHelpers;
import net.dries007.tfc.common.blockentities.JarsBlockEntity;
import net.dries007.tfc.common.items.TFCItems;

public class JarsBlockEntityRenderer implements BlockEntityRenderer<JarsBlockEntity>
{
    public static final Map<Item, ModelResourceLocation> MODELS = Util.make(new HashMap<>(), map -> {
        map.put(TFCItems.EMPTY_JAR.get(), RenderHelpers.modelId("block/jar/empty"));
        map.put(TFCItems.EMPTY_JAR_WITH_LID.get(), RenderHelpers.modelId("block/jar"));
        TFCItems.FRUIT_PRESERVES.forEach((fruit, item) -> map.put(item.get(), RenderHelpers.modelId("block/jar/" + fruit.name().toLowerCase(Locale.ROOT))));
        TFCItems.UNSEALED_FRUIT_PRESERVES.forEach((fruit, item) -> map.put(item.get(), RenderHelpers.modelId("block/jar/" + fruit.name().toLowerCase(Locale.ROOT) + "_unsealed")));
    });

    @Override
    public void render(JarsBlockEntity jars, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay)
    {
        if (jars.getLevel() == null)
        {
            return;
        }

        final RandomSource random = RandomSource.create();
        final Minecraft mc = Minecraft.getInstance();
        for (int i = 0; i < jars.getInventory().getSlots(); i++)
        {
            final ItemStack stack = jars.getInventory().getStackInSlot(i);

            poseStack.pushPose();
            poseStack.translate((i % 2 == 0 ? 0.5 : 0), 0, (i < 2 ? 0.5 : 0));

            final BakedModel baked = mc.getModelManager().getModel(MODELS.get(stack.getItem()));
            final VertexConsumer buffer = bufferSource.getBuffer(RenderType.translucent());
            mc.getBlockRenderer().getModelRenderer().tesselateWithAO(jars.getLevel(), baked, jars.getBlockState(), jars.getBlockPos(), poseStack, buffer, true, random, packedLight, packedOverlay, ModelData.EMPTY, RenderType.translucent());

            poseStack.popPose();
        }
    }
}
