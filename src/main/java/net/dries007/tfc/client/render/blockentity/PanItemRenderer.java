/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.render.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.model.data.EmptyModelData;

import net.dries007.tfc.common.items.PanItem;
import net.dries007.tfc.util.Pannable;

public class PanItemRenderer extends BlockEntityWithoutLevelRenderer
{
    public PanItemRenderer()
    {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
    }

    @Override
    public void renderByItem(ItemStack stack, ItemTransforms.TransformType transforms, PoseStack poseStack, MultiBufferSource buffers, int packedLight, int packedOverlay)
    {
        final Pannable pannable = PanItem.readPannable(stack);
        final Minecraft mc = Minecraft.getInstance();
        final LocalPlayer player = mc.player;
        if (pannable != null)
        {
            final ResourceLocation[] stages = pannable.getModelStages();
            ResourceLocation location = stages[0];
            if (player != null && transforms.firstPerson())
            {
                final int useTicks = player.getTicksUsingItem();
                location = stages[Mth.clamp(Math.round((float) useTicks / PanItem.USE_TIME * stages.length), 0, stages.length - 1)];
            }
            final BakedModel model = mc.getModelManager().getModel(location);

            poseStack.pushPose();
            mc.getBlockRenderer().getModelRenderer().renderModel(poseStack.last(), buffers.getBuffer(ItemBlockRenderTypes.getRenderType(stack, false)), null, model, 1f, 1f, 1f, packedLight, packedOverlay, EmptyModelData.INSTANCE);
            poseStack.popPose();
        }
    }
}
