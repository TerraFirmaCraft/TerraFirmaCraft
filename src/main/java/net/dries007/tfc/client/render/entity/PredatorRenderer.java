/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.dries007.tfc.client.RenderHelpers;

import net.minecraft.client.model.EntityModel;

import net.dries007.tfc.client.model.entity.CougarModel;
import net.dries007.tfc.common.entities.aquatic.Jellyfish;
import net.dries007.tfc.common.entities.predator.FelinePredator;
import net.dries007.tfc.common.entities.predator.Predator;
import net.dries007.tfc.util.Helpers;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;

public class PredatorRenderer<T extends Mob, M extends EntityModel<T>> extends MobRenderer<T, M>
{
    private final ResourceLocation texture;

    public PredatorRenderer(EntityRendererProvider.Context ctx, M model, String name)
    {
        this(ctx, model, name, 1.0F);
    }

    public PredatorRenderer(EntityRendererProvider.Context ctx, M model, String name, float shadow)
    {
        super(ctx, model, shadow);
        texture = Helpers.animalTexture(name);
    }

    @Override
    public ResourceLocation getTextureLocation(T entity)
    {
        return texture;
    }

}
