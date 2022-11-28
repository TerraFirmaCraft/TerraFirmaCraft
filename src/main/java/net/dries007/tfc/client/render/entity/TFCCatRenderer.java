/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

import net.dries007.tfc.client.RenderHelpers;
import net.dries007.tfc.client.model.entity.TFCCatCollarLayer;
import net.dries007.tfc.client.model.entity.TFCCatModel;
import net.dries007.tfc.common.entities.livestock.pet.TFCCat;

public class TFCCatRenderer extends SimpleMobRenderer<TFCCat, TFCCatModel>
{
    public TFCCatRenderer(EntityRendererProvider.Context ctx)
    {
        super(ctx, new TFCCatModel(RenderHelpers.bakeSimple(ctx, "cat")), "cat", 0.4f, false, 0.8f, false, false, TFCCat::getTextureLocation);
        addLayer(new TFCCatCollarLayer(this, ctx.getModelSet()));
    }

    @Override
    protected void setupRotations(TFCCat entity, PoseStack poseStack, float ageInTicks, float yaw, float partialTicks)
    {
        super.setupRotations(entity, poseStack, ageInTicks, yaw, partialTicks);
        if (entity.isSleeping())
        {
            poseStack.translate(0.4f, 0.15f, 0.15f);
            poseStack.mulPose(RenderHelpers.rotateDegreesZ(90f));
        }
    }
}
