/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.render.entity;

import java.util.function.Function;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;

import com.mojang.blaze3d.vertex.PoseStack;

import net.dries007.tfc.client.RenderHelpers;
import net.dries007.tfc.common.entities.GenderedRenderAnimal;
import net.dries007.tfc.common.entities.WildAnimal;
import net.dries007.tfc.common.entities.livestock.TFCAnimalProperties;
import net.dries007.tfc.util.Helpers;

import org.jetbrains.annotations.Nullable;

public class SimpleMobRenderer<T extends Mob, M extends EntityModel<T>> extends MobRenderer<T, M>
{
    private final ResourceLocation texture;
    @Nullable
    private final ResourceLocation babyTexture;
    @Nullable
    private final Function<T, ResourceLocation> textureGetter;
    private final boolean doesFlop;
    private final float scale;

    public SimpleMobRenderer(EntityRendererProvider.Context ctx, M model, String name, float shadow, boolean flop, float scale, boolean hasBabyTexture, boolean itemInMouth, @Nullable Function<T, ResourceLocation> textureGetter)
    {
        super(ctx, model, shadow);
        doesFlop = flop;
        texture = Helpers.animalTexture(name);
        babyTexture = hasBabyTexture ? Helpers.animalTexture(name + "_young") : null;
        this.textureGetter = textureGetter != null ? textureGetter : e -> babyTexture != null && e.isBaby() ? babyTexture : texture;
        this.scale = scale;
        // todo: re-add item in mouth layer when i can figure out how the heck to render it right.
    }

    @Override
    protected void setupRotations(T entity, PoseStack poseStack, float ageInTicks, float yaw, float partialTicks)
    {
        super.setupRotations(entity, poseStack, ageInTicks, yaw, partialTicks);
        if (doesFlop)
        {
            poseStack.mulPose(RenderHelpers.rotateDegreesZ(Mth.sin(0.6F * ageInTicks)));
            if (!entity.isInWater())
            {
                poseStack.translate(0.1f, 0.1f, -0.1f);
                poseStack.mulPose(RenderHelpers.rotateDegreesZ(90f));
            }
        }
    }

    @Override
    protected void scale(T entity, PoseStack poseStack, float scale)
    {
        float amount = entity.isBaby() ? this.scale * 0.7f : this.scale;
        if (entity instanceof TFCAnimalProperties animal)
        {
            amount *= animal.getAgeScale();
        }
        poseStack.scale(amount, amount, amount);
        super.scale(entity, poseStack, scale);
    }

    @Override
    public ResourceLocation getTextureLocation(T entity)
    {
        return textureGetter.apply(entity);
    }

    public static class Builder<T extends Mob, M extends EntityModel<T>>
    {
        private final EntityRendererProvider.Context ctx;
        private final Function<ModelPart, M> model;
        private final String name;

        private float shadow = 0.3f;
        private boolean flop = false;
        private float scale = 1f;
        private boolean hasBabyTexture = false;
        private boolean itemInMouth = false;
        @Nullable private Function<T, ResourceLocation> textureGetter = null;

        public Builder(EntityRendererProvider.Context ctx, Function<ModelPart, M> model, String name)
        {
            this.ctx = ctx;
            this.model = model;
            this.name = name;
        }

        public Builder<T, M> flops()
        {
            this.flop = true;
            return this;
        }

        public Builder<T, M> shadow(float size)
        {
            this.shadow = size;
            return this;
        }

        public Builder<T, M> scale(float scale)
        {
            this.scale = scale;
            return this;
        }

        public Builder<T, M> hasBabyTexture()
        {
            this.hasBabyTexture = true;
            return this;
        }

        public Builder<T, M> mouthy()
        {
            this.itemInMouth = true;
            return this;
        }

        public Builder<T, M> texture(Function<T, ResourceLocation> getter)
        {
            this.textureGetter = getter;
            return this;
        }

        public SimpleMobRenderer<T, M> build()
        {
            return new SimpleMobRenderer<>(ctx, model.apply(RenderHelpers.bakeSimple(ctx, name)), name, shadow, flop, scale, hasBabyTexture, itemInMouth, textureGetter);
        }
    }

}
