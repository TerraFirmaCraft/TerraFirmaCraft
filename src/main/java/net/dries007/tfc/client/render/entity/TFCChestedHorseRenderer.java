/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.AbstractHorseRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

import net.dries007.tfc.client.RenderHelpers;
import net.dries007.tfc.client.model.entity.HorseChestLayer;
import net.dries007.tfc.client.model.entity.TFCChestedHorseModel;
import net.dries007.tfc.common.entities.livestock.horse.TFCChestedHorse;
import net.dries007.tfc.util.Helpers;

public class TFCChestedHorseRenderer<T extends TFCChestedHorse> extends AbstractHorseRenderer<T, TFCChestedHorseModel<T>>
{
    private final ResourceLocation texture;

    public TFCChestedHorseRenderer(EntityRendererProvider.Context ctx, float scale, ModelLayerLocation layer, String name)
    {
        this(ctx, scale, layer, Helpers.identifierMC("textures/entity/horse/" + name + ".png"));
    }

    public TFCChestedHorseRenderer(EntityRendererProvider.Context ctx, float scale, ModelLayerLocation layer, ResourceLocation texture)
    {
        super(ctx, new TFCChestedHorseModel<>(ctx.bakeLayer(layer), false), scale);
        addLayer(new HorseChestLayer<>(this, new TFCChestedHorseModel<>(ctx.bakeLayer(RenderHelpers.layerId("horse_chest")), true)));
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
