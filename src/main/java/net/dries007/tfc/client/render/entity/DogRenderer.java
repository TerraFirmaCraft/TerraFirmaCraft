/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

import net.dries007.tfc.client.RenderHelpers;
import net.dries007.tfc.client.model.entity.DogCollarLayer;
import net.dries007.tfc.client.model.entity.DogModel;
import net.dries007.tfc.common.entities.livestock.pet.Dog;

public class DogRenderer extends SimpleMobRenderer<Dog, DogModel>
{
    private static final ResourceLocation WOLF_TAME_LOCATION = new ResourceLocation("textures/entity/wolf/wolf_tame.png");

    public DogRenderer(EntityRendererProvider.Context ctx)
    {
        super(ctx, new DogModel(RenderHelpers.bakeSimple(ctx, "dog")), "dog", 0.5f, false, 1f, false, false, dog -> WOLF_TAME_LOCATION);
        addLayer(new DogCollarLayer(this));
    }

    @Override
    protected void setupRotations(Dog entity, PoseStack stack, float age, float yaw, float partialTicks)
    {
        super.setupRotations(entity, stack, age, yaw, partialTicks);
        if (entity.isSleeping())
        {
            stack.translate(0.2F, 0.1F, 0.0D);
            stack.mulPose(RenderHelpers.rotateDegreesZ(90f));
        }
    }
}
