/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

import net.dries007.tfc.client.RenderHelpers;
import net.dries007.tfc.client.model.entity.PenguinModel;
import net.dries007.tfc.common.entities.aquatic.Penguin;

public class PenguinRenderer extends SimpleMobRenderer<Penguin, PenguinModel>
{
    public PenguinRenderer(EntityRendererProvider.Context ctx)
    {
        super(ctx, new PenguinModel(ctx.bakeLayer(RenderHelpers.layerId("penguin"))), "penguin", 0.2f, false, 1f, true, false, null);
    }

    @Override
    protected void setupRotations(Penguin entity, PoseStack stack, float bob, float yBodyRot, float partialTick, float scale)
    {
        super.setupRotations(entity, stack, bob, yBodyRot, partialTick, scale);
        if (entity.isInWater())
        {
            stack.translate(0.0D, 0.4D, 0.0D);
            stack.mulPose(Axis.XP.rotationDegrees(270.0F));
            stack.translate(0.0D, -0.4D, 0.0D);
        }
    }
}
