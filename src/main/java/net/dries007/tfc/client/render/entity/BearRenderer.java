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
import net.dries007.tfc.client.model.entity.BearModel;
import net.dries007.tfc.common.entities.predator.Predator;

public class BearRenderer extends MobRenderer<Predator, BearModel>
{
    private static float SCALE;
    private final ResourceLocation texture;

    public BearRenderer(EntityRendererProvider.Context ctx, float scale, String name)
    {
        super(ctx, new BearModel(ctx.bakeLayer(RenderHelpers.modelIdentifier("polar_bear"))), 0.9F);
        SCALE = scale;
        texture = RenderHelpers.animalTexture(name);
    }

    @Override
    protected void scale(Predator predator, PoseStack poseStack, float ticks)
    {
        poseStack.scale(SCALE, SCALE, SCALE);
        super.scale(predator, poseStack, ticks);
    }

    @Override
    public ResourceLocation getTextureLocation(Predator predator)
    {
        return texture;
    }
}
