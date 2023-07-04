/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.FishingHookRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

import net.dries007.tfc.common.entities.misc.TFCFishingHook;

public class TFCFishingHookRenderer extends FishingHookRenderer
{
    public TFCFishingHookRenderer(EntityRendererProvider.Context context)
    {
        super(context);
    }

    @Override
    public void render(FishingHook entity, float age, float swing, PoseStack poseStack, MultiBufferSource buffer, int light)
    {
        if (entity instanceof TFCFishingHook hook && entity.getPlayerOwner() != null)
        {
            ItemStack bait = hook.getBait();
            if (!bait.isEmpty())
            {
                poseStack.pushPose();

                poseStack.translate(0f, -0.05f, 0f);
                poseStack.scale(0.5F, 0.5F, 0.5F);
                poseStack.mulPose(entityRenderDispatcher.cameraOrientation());
                poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));

                Minecraft.getInstance().getItemRenderer().renderStatic(bait, ItemDisplayContext.FIXED, light, OverlayTexture.NO_OVERLAY, poseStack, buffer, entity.level(), 0);

                poseStack.popPose();
            }
        }
        super.render(entity, age, swing, poseStack, buffer, light);

    }
}
