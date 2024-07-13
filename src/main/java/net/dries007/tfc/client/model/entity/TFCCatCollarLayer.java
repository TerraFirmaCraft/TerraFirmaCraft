/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.model.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;

import net.dries007.tfc.client.RenderHelpers;
import net.dries007.tfc.common.entities.livestock.pet.TFCCat;
import net.dries007.tfc.util.Helpers;

public class TFCCatCollarLayer extends RenderLayer<TFCCat, TFCCatModel>
{
    private static final ResourceLocation CAT_COLLAR_LOCATION = Helpers.identifierMC("textures/entity/cat/cat_collar.png");
    private final TFCCatModel catModel;

    public TFCCatCollarLayer(RenderLayerParent<TFCCat, TFCCatModel> renderer, EntityModelSet ctx)
    {
        super(renderer);
        this.catModel = new TFCCatModel(ctx.bakeLayer(RenderHelpers.layerId("cat_collar")));
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, TFCCat entity, float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float yaw, float pitch)
    {
        if (entity.getOwnerUUID() != null && !entity.isInvisible())
        {
            final int color = entity.getCollarColor().getTextureDiffuseColor();
            coloredCutoutModelCopyLayerRender(this.getParentModel(), this.catModel, CAT_COLLAR_LOCATION, poseStack, buffer, packedLight, entity, limbSwing, limbSwingAmount, ageInTicks, yaw, pitch, partialTick, color);
        }
    }
}
