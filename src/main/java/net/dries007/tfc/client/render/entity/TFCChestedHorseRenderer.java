/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.render.entity;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.ChestedHorseRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

import com.mojang.blaze3d.vertex.PoseStack;
import net.dries007.tfc.common.entities.livestock.horse.TFCChestedHorse;

public class TFCChestedHorseRenderer<T extends TFCChestedHorse> extends ChestedHorseRenderer<T>
{
    private final ResourceLocation texture;

    public TFCChestedHorseRenderer(EntityRendererProvider.Context ctx, float shadow, ModelLayerLocation layer, String name)
    {
        this(ctx, shadow, layer, new ResourceLocation("textures/entity/horse/" + name + ".png"));
    }

    public TFCChestedHorseRenderer(EntityRendererProvider.Context ctx, float shadow, ModelLayerLocation layer, ResourceLocation texture)
    {
        super(ctx, shadow, layer);
        this.texture = texture;
    }

    @Override
    protected void scale(T animal, PoseStack poseStack, float ticks)
    {
        final float scale = animal.getAgeScale();
        poseStack.scale(scale, scale, scale);
        super.scale(animal, poseStack, ticks);
    }

    @Override
    public ResourceLocation getTextureLocation(T horse)
    {
        return texture;
    }
}
