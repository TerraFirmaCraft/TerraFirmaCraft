/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

import net.dries007.tfc.client.RenderHelpers;
import net.dries007.tfc.client.model.entity.DogCollarLayer;
import net.dries007.tfc.client.model.entity.DogModel;
import net.dries007.tfc.common.entities.livestock.pet.Dog;
import net.dries007.tfc.util.Helpers;

public class DogRenderer extends SimpleMobRenderer<Dog, DogModel>
{
    private static final ResourceLocation WOLF_TAME_LOCATION = Helpers.identifierMC("textures/entity/wolf/wolf_tame.png");

    public DogRenderer(EntityRendererProvider.Context ctx)
    {
        super(ctx, new DogModel(RenderHelpers.bakeSimple(ctx, "dog")), "dog", 0.5f, false, 1f, false, false, dog -> WOLF_TAME_LOCATION);
        addLayer(new DogCollarLayer(this));
    }

    @Override
    protected void setupRotations(Dog entity, PoseStack stack, float bob, float yBodyRot, float partialTick, float scale)
    {
        super.setupRotations(entity, stack, bob, yBodyRot, partialTick, scale);
        if (entity.isSleeping())
        {
            stack.translate(0.2F, 0.1F, 0.0D);
            stack.mulPose(Axis.ZP.rotationDegrees(90f));
        }
    }
}
