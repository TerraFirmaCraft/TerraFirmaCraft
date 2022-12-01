/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.render.entity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;

import com.mojang.blaze3d.vertex.PoseStack;
import net.dries007.tfc.client.RenderHelpers;
import net.dries007.tfc.client.model.entity.PenguinModel;
import net.dries007.tfc.common.entities.aquatic.Penguin;

public class PenguinRenderer extends SimpleMobRenderer<Penguin, PenguinModel>
{
    public PenguinRenderer(EntityRendererProvider.Context ctx)
    {
        super(ctx, new PenguinModel(ctx.bakeLayer(RenderHelpers.modelIdentifier("penguin"))), "penguin", 0.2f, false, 1f, true, false, null);
    }

    @Override
    protected void setupRotations(Penguin animal, PoseStack stack, float ageInTicks, float rotationYaw, float partialTicks)
    {
        super.setupRotations(animal, stack, ageInTicks, rotationYaw, partialTicks);
        if (animal.isInWater())
        {
            stack.translate(0.0D, 0.4D, 0.0D);
            stack.mulPose(RenderHelpers.rotateDegreesX(270.0F));
            stack.translate(0.0D, -0.4D, 0.0D);
        }
    }
}
