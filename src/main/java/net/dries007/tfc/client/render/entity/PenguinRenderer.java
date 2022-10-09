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
import com.mojang.math.Vector3f;
import net.dries007.tfc.client.RenderHelpers;
import net.dries007.tfc.client.model.entity.PenguinModel;
import net.dries007.tfc.common.entities.aquatic.Penguin;
import net.dries007.tfc.util.Helpers;

public class PenguinRenderer extends MobRenderer<Penguin, PenguinModel>
{
    private static final ResourceLocation LOCATION = Helpers.animalTexture("penguin");

    public PenguinRenderer(EntityRendererProvider.Context ctx)
    {
        super(ctx, new PenguinModel(ctx.bakeLayer(RenderHelpers.modelIdentifier("penguin"))), 0.2F);
    }

    @Override
    protected void setupRotations(Penguin animal, PoseStack stack, float ageInTicks, float rotationYaw, float partialTicks)
    {
        super.setupRotations(animal, stack, ageInTicks, rotationYaw, partialTicks);
        if (animal.isInWater())
        {
            stack.translate(0.0D, 0.4D, 0.0D);
            stack.mulPose(Vector3f.XP.rotationDegrees(270.0F));
            stack.translate(0.0D, -0.4D, 0.0D);
        }
    }

    @Override
    public ResourceLocation getTextureLocation(Penguin model)
    {
        return LOCATION;
    }
}
