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
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.dries007.tfc.util.Helpers;

public class SimpleMobRenderer<T extends Mob, M extends EntityModel<T>> extends MobRenderer<T, M>
{
    private final ResourceLocation texture;
    private final boolean doesFlop;
    private final float scale;

    public SimpleMobRenderer(EntityRendererProvider.Context ctx, M model, String name)
    {
        this(ctx, model, name, 0.3F, false, 1f);
    }

    public SimpleMobRenderer(EntityRendererProvider.Context ctx, M model, String name, float shadow)
    {
        this(ctx, model, name, shadow, false, 1f);
    }

    public SimpleMobRenderer(EntityRendererProvider.Context ctx, M model, String name, boolean flop)
    {
        this(ctx, model, name, 0.3F, flop, 1f);
    }

    public SimpleMobRenderer(EntityRendererProvider.Context ctx, M model, String name, float shadow, boolean flop, float scale)
    {
        super(ctx, model, shadow);
        doesFlop = flop;
        texture = Helpers.animalTexture(name);
        this.scale = scale;
    }

    @Override
    protected void setupRotations(T entity, PoseStack stack, float age, float yaw, float partialTicks)
    {
        super.setupRotations(entity, stack, age, yaw, partialTicks);
        if (doesFlop)
        {
            float amplitude = 1.0F;
            float deg = 1.0F;
            if (!entity.isInWater())
            {
                amplitude = 1.3F;
                deg = 1.7F;
            }

            float yRot = amplitude * 4.3F * Mth.sin(deg * 0.6F * age);
            stack.mulPose(Vector3f.YP.rotationDegrees(yRot));
            stack.translate(0.0D, 0.0D, -0.4F);
            if (!entity.isInWater())
            {
                stack.translate(0.2F, 0.1F, 0.0D);
                stack.mulPose(Vector3f.ZP.rotationDegrees(90.0F));
            }
        }
    }

    @Override
    protected void scale(T entity, PoseStack poseStack, float scale)
    {
        poseStack.scale(this.scale, this.scale, this.scale);
        super.scale(entity, poseStack, scale);
    }

    @Override
    public ResourceLocation getTextureLocation(T entity)
    {
        return texture;
    }
}
