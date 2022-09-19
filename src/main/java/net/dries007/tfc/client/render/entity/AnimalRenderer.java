/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.render.entity;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

import com.mojang.blaze3d.vertex.PoseStack;
import net.dries007.tfc.client.RenderHelpers;
import net.dries007.tfc.common.entities.livestock.TFCAnimal;
import net.dries007.tfc.util.Helpers;

public class AnimalRenderer<T extends TFCAnimal, M extends EntityModel<T>> extends MobRenderer<T, M>
{
    private final ResourceLocation young;
    private final ResourceLocation old;

    public AnimalRenderer(EntityRendererProvider.Context ctx, M model, String name)
    {
        this(ctx, model, name, 0.3F);
    }

    public AnimalRenderer(EntityRendererProvider.Context ctx, M model, String name, float shadow)
    {
        super(ctx, model, shadow);
        this.young = Helpers.animalTexture(name + "_young");
        this.old = Helpers.animalTexture(name + "_old");
    }

    @Override
    protected void scale(T animal, PoseStack poseStack, float ticks)
    {
        final float scale = animal.getAgeScale();
        poseStack.scale(scale, scale, scale);
        super.scale(animal, poseStack, ticks);
    }

    @Override
    public ResourceLocation getTextureLocation(T entity)
    {
        return RenderHelpers.getTextureForAge(entity, young, old);
    }
}
