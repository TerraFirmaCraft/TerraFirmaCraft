/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.render.entity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.dries007.tfc.client.RenderHelpers;
import net.dries007.tfc.client.model.entity.RatModel;
import net.dries007.tfc.common.entities.prey.Pest;

public class RatRenderer extends SimpleMobRenderer<Pest, RatModel>
{
    public RatRenderer(EntityRendererProvider.Context ctx)
    {
        super(ctx, new RatModel(RenderHelpers.bakeSimple(ctx, "rat")), "rat", 0.2f, false, 1f, true, true, null);
    }

    @Override
    protected void setupRotations(Pest entity, PoseStack poseStack, float age, float yaw, float partialTicks)
    {
        super.setupRotations(entity, poseStack, age, yaw, partialTicks);
        if (entity.isClimbing())
        {
            poseStack.pushPose();
            poseStack.mulPose(Vector3f.ZP.rotationDegrees(90f));
            poseStack.popPose();
        }
        if (entity.draggingAnimation.isStarted())
        {
            poseStack.pushPose();
            poseStack.mulPose(Vector3f.YP.rotationDegrees(180f));
            poseStack.popPose();
        }
    }
}
