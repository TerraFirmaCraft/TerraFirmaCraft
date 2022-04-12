/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.render.entity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

import com.mojang.blaze3d.vertex.PoseStack;
import net.dries007.tfc.client.RenderHelpers;
import net.dries007.tfc.client.model.TFCPolarBearModel;
import net.dries007.tfc.common.entities.predator.Predator;

public class TFCPolarBearRenderer extends MobRenderer<Predator, TFCPolarBearModel>
{
    private static final ResourceLocation POLAR_BEAR_LOCATION = RenderHelpers.animalTexture("polar_bear");

    public TFCPolarBearRenderer(EntityRendererProvider.Context ctx)
    {
        super(ctx, new TFCPolarBearModel(ctx.bakeLayer(RenderHelpers.modelIdentifier("polar_bear"))), 0.9F);
    }

    @Override
    protected void scale(Predator predator, PoseStack poseStack, float ticks)
    {
        poseStack.scale(1.2F, 1.2F, 1.2F);
        super.scale(predator, poseStack, ticks);
    }

    @Override
    public ResourceLocation getTextureLocation(Predator predator)
    {
        return POLAR_BEAR_LOCATION;
    }
}
