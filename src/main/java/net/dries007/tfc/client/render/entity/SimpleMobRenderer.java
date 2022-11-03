/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.render.entity;

import com.mojang.math.Vector3f;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;

import com.mojang.blaze3d.vertex.PoseStack;
import net.dries007.tfc.client.RenderHelpers;
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
    protected void setupRotations(T entity, PoseStack poseStack, float ageInTicks, float yaw, float partialTicks)
    {
        super.setupRotations(entity, poseStack, ageInTicks, yaw, partialTicks);
        if (doesFlop)
        {
            float f = 4.3F * Mth.sin(0.6F * ageInTicks);
            poseStack.mulPose(Vector3f.YP.rotationDegrees(f));
            if (!entity.isInWater())
            {
                poseStack.translate(0.1f, 0.1f, -0.1f);
                poseStack.mulPose(Vector3f.ZP.rotationDegrees(90.0F));
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
