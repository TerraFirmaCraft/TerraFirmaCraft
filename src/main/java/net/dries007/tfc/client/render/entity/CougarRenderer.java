/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.dries007.tfc.client.RenderHelpers;

import net.dries007.tfc.client.model.entity.CougarModel;
import net.dries007.tfc.common.entities.aquatic.Jellyfish;
import net.dries007.tfc.common.entities.predator.Cougar;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class CougarRenderer extends MobRenderer<Cougar, CougarModel>
{
    private static final ResourceLocation COUGAR_LOCATION = RenderHelpers.animalTexture("cougar");

    public CougarRenderer(EntityRendererProvider.Context ctx)
    {
        super(ctx, new CougarModel(ctx.bakeLayer(RenderHelpers.modelIdentifier("cougar"))), 0.9F);
    }

    @Override
    protected void scale(Cougar cougar, PoseStack poseStack, float ticks)
    {
        poseStack.scale(1.2F, 1.2F, 1.2F);
        super.scale(cougar, poseStack, ticks);
    }

    @Override
    public ResourceLocation getTextureLocation(Cougar cougar)
    {
        return cougar.getTextureLocation();
    }

}
