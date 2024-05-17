/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.RabbitRenderer;
import net.minecraft.world.entity.animal.Rabbit;

import net.dries007.tfc.common.entities.livestock.TFCAnimalProperties;

public class TFCRabbitRenderer extends RabbitRenderer
{
    public TFCRabbitRenderer(EntityRendererProvider.Context context)
    {
        super(context);
    }

    @Override
    protected void scale(Rabbit rabbit, PoseStack poseStack, float ticks)
    {
        if (rabbit instanceof TFCAnimalProperties animal)
        {
            final float scale = animal.getAgeScale();
            poseStack.scale(scale, scale, scale);
        }
        super.scale(rabbit, poseStack, ticks);
    }
}
