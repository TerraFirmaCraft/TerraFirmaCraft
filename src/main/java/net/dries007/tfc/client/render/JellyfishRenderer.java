/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.render;

import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.dries007.tfc.client.model.JellyfishModel;
import net.dries007.tfc.common.entities.aquatic.JellyfishEntity;

public class JellyfishRenderer extends MobRenderer<JellyfishEntity, JellyfishModel>
{
    public JellyfishRenderer(EntityRendererManager renderManagerIn)
    {
        super(renderManagerIn, new JellyfishModel(), 0.4F);
    }

    @Override
    public ResourceLocation getTextureLocation(JellyfishEntity entityIn)
    {
        return entityIn.getTexture();
    }

    @Override
    protected void setupRotations(JellyfishEntity entityLiving, MatrixStack matrixStackIn, float ageInTicks, float rotationYaw, float partialTicks)
    {
        super.setupRotations(entityLiving, matrixStackIn, ageInTicks, rotationYaw, partialTicks);
        matrixStackIn.translate(0.0D, -0.4D, 0.0D); // model is too high
    }
}
