/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.SquidModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.SquidRenderer;

import net.dries007.tfc.common.entities.aquatic.TFCSquid;

public class TFCSquidRenderer<T extends TFCSquid> extends SquidRenderer<T>
{
    public TFCSquidRenderer(EntityRendererProvider.Context context, SquidModel<T> model)
    {
        super(context, model);
    }

    @Override
    protected void setupRotations(T entity, PoseStack poseStack, float bob, float yBodyRot, float partialTick, float scale)
    {
        super.setupRotations(entity, poseStack, bob, yBodyRot, partialTick, scale);
        poseStack.scale(entity.getVisualScale(), entity.getVisualScale(), entity.getVisualScale());
    }
}
