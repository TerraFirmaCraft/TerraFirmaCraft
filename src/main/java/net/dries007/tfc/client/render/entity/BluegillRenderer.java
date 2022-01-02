/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.render.entity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.dries007.tfc.client.ClientHelpers;
import net.dries007.tfc.client.model.entity.BluegillModel;
import net.dries007.tfc.common.entities.aquatic.Bluegill;

public class BluegillRenderer extends MobRenderer<Bluegill, BluegillModel>
{
    private static final ResourceLocation LOCATION = ClientHelpers.animalTexture("bluegill");

    public BluegillRenderer(EntityRendererProvider.Context ctx)
    {
        super(ctx, new BluegillModel(ctx.bakeLayer(ClientHelpers.modelIdentifier("bluegill"))), 0.3F);
    }

    @Override
    public ResourceLocation getTextureLocation(Bluegill bluegill)
    {
        return LOCATION;
    }

    /**
     * Standard flopping animation, from SalmonRenderer#setupRotations
     */
    @Override
    protected void setupRotations(Bluegill fish, PoseStack stack, float pAgeInTicks, float pRotationYaw, float pPartialTicks)
    {
        super.setupRotations(fish, stack, pAgeInTicks, pRotationYaw, pPartialTicks);
        float amplitude = 1.0F;
        float deg = 1.0F;
        if (!fish.isInWater())
        {
            amplitude = 1.3F;
            deg = 1.7F;
        }

        float yRot = amplitude * 4.3F * Mth.sin(deg * 0.6F * pAgeInTicks);
        stack.mulPose(Vector3f.YP.rotationDegrees(yRot));
        stack.translate(0.0D, 0.0D, -0.4F);
        if (!fish.isInWater())
        {
            stack.translate(0.2F, 0.1F, 0.0D);
            stack.mulPose(Vector3f.ZP.rotationDegrees(90.0F));
        }
    }
}
