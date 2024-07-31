/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.render.blockentity;

import java.util.List;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.client.RenderHelpers;
import net.dries007.tfc.common.component.TFCComponents;
import net.dries007.tfc.common.component.item.ItemComponent;
import net.dries007.tfc.common.items.PanItem;
import net.dries007.tfc.util.data.Deposit;

public class PanItemRenderer extends BlockEntityWithoutLevelRenderer
{
    public PanItemRenderer()
    {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext transforms, PoseStack poseStack, MultiBufferSource buffers, int packedLight, int packedOverlay)
    {
        final ClientLevel level = Minecraft.getInstance().level;
        if (level == null)
        {
            return;
        }

        final @Nullable Deposit deposit = Deposit.get(stack.getOrDefault(TFCComponents.DEPOSIT, ItemComponent.EMPTY).stack());
        final Minecraft mc = Minecraft.getInstance();
        final LocalPlayer player = mc.player;
        if (deposit != null)
        {
            final List<ResourceLocation> stages = deposit.modelStages();
            ResourceLocation location = stages.getFirst();
            if (player != null && transforms.firstPerson())
            {
                final int useTicks = player.getTicksUsingItem();
                location = stages.get(Mth.clamp(Math.round((float) useTicks / PanItem.USE_TIME * stages.size()), 0, stages.size() - 1));
            }
            final BakedModel model = mc.getModelManager().getModel(RenderHelpers.modelId(location));

            poseStack.pushPose();
            mc.getBlockRenderer().getModelRenderer().renderModel(poseStack.last(), buffers.getBuffer(Sheets.solidBlockSheet()), null, model, 1f, 1f, 1f, packedLight, packedOverlay, ModelData.EMPTY, Sheets.solidBlockSheet());
            poseStack.popPose();
        }
    }
}
