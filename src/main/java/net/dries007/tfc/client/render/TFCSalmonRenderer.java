/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.render;

import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.model.SalmonModel;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.dries007.tfc.common.entities.aquatic.TFCSalmonEntity;

public class TFCSalmonRenderer extends MobRenderer<TFCSalmonEntity, SalmonModel<TFCSalmonEntity>>
{
    private static final ResourceLocation SALMON_LOCATION = new ResourceLocation("textures/entity/fish/salmon.png");

    public TFCSalmonRenderer(EntityRendererManager renderManagerIn)
    {
        super(renderManagerIn, new SalmonModel<>(), 0.4F);
    }

    @Override
    public ResourceLocation getTextureLocation(TFCSalmonEntity entityIn)
    {
        return SALMON_LOCATION;
    }

    @Override
    protected void setupRotations(TFCSalmonEntity salmon, MatrixStack matrixStack, float ageInTicks, float rotationYaw, float partialTicks)
    {
        super.setupRotations(salmon, matrixStack, ageInTicks, rotationYaw, partialTicks);
        float amplitude = 1.0F;
        float stretch = 1.0F;
        if (!salmon.isInWater())
        {
            amplitude = 1.3F;
            stretch = 1.7F;
        }

        float oscillation = amplitude * 4.3F * MathHelper.sin(stretch * 0.6F * ageInTicks);
        matrixStack.mulPose(Vector3f.YP.rotationDegrees(oscillation));
        matrixStack.translate(0.0D, 0.0D, -0.4F);
        if (!salmon.isInWater())
        {
            matrixStack.translate(0.2F, 0.1F, 0.0D);
            matrixStack.mulPose(Vector3f.ZP.rotationDegrees(90.0F));
        }
    }
}
