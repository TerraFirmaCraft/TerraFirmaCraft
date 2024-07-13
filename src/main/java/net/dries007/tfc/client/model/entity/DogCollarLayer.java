/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.model.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;

import net.dries007.tfc.common.entities.livestock.pet.Dog;
import net.dries007.tfc.util.Helpers;

public class DogCollarLayer extends RenderLayer<Dog, DogModel>
{
    private static final ResourceLocation WOLF_COLLAR_LOCATION = Helpers.identifierMC("textures/entity/wolf/wolf_collar.png");

    public DogCollarLayer(RenderLayerParent<Dog, DogModel> renderer)
    {
        super(renderer);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, Dog entity, float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float yaw, float pitch)
    {
        if (entity.getOwnerUUID() != null && !entity.isInvisible())
        {
            renderColoredCutoutModel(this.getParentModel(), WOLF_COLLAR_LOCATION, poseStack, buffer, packedLight, entity, entity.getCollarColor().getTextureDiffuseColor());
        }
    }
}
