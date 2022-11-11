/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.model.entity;

import net.minecraft.client.model.QuadrupedModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;

import net.dries007.tfc.common.entities.aquatic.AmphibiousAnimal;

public class TFCTurtleModel extends QuadrupedModel<AmphibiousAnimal>
{
    public static LayerDefinition createBodyLayer()
    {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(27, 16).addBox(-4.0F, -5.0F, -5.0F, 8.0F, 1.0F, 10.0F, new CubeDeformation(0.0F))
            .texOffs(0, 16).addBox(-4.0F, -1.0F, -4.0F, 8.0F, 1.0F, 11.0F, new CubeDeformation(0.0F))
            .texOffs(0, 0).addBox(-5.0F, -4.0F, -5.0F, 10.0F, 3.0F, 13.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));
        PartDefinition right_hind_leg = partdefinition.addOrReplaceChild("right_hind_leg", CubeListBuilder.create().texOffs(13, 28).addBox(-4.9F, -0.9F, -1.1F, 6.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.0F, 23.0F, 6.0F, 0.0F, 0.7854F, 0.0F));
        PartDefinition left_hind_leg = partdefinition.addOrReplaceChild("left_hind_leg", CubeListBuilder.create().texOffs(28, 29).addBox(-1.1F, -0.9F, -1.1F, 6.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.0F, 23.0F, 6.0F, 0.0F, -0.7854F, 0.0F));
        PartDefinition right_front_leg = partdefinition.addOrReplaceChild("right_front_leg", CubeListBuilder.create().texOffs(33, 4).addBox(-5.9F, -0.9F, -2.0F, 6.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(-4.0F, 23.0F, -2.0F));
        PartDefinition left_front_leg = partdefinition.addOrReplaceChild("left_front_leg", CubeListBuilder.create().texOffs(33, 0).addBox(4.1F, -1.9F, -4.0F, 6.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));
        PartDefinition head = partdefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 28).addBox(-2.0F, -4.5F, -9.0F, 4.0F, 4.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));
        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    public TFCTurtleModel(ModelPart root)
    {
        super(root, false, 0F, 0F, 0.5F, 0.5F, 0);
    }

    /**
     * Limb swing based on the vanilla turtle. Since we don't want the turtle to have a 'walking' animation.
     */
    @Override
    public void setupAnim(AmphibiousAnimal animal, float limbSwing, float limbSwingAmount, float ageInTicks, float headYaw, float headPitch)
    {
        final boolean extraPartsVisible = !animal.isPlayingDead();
        head.visible = extraPartsVisible;
        rightFrontLeg.visible = extraPartsVisible;
        rightHindLeg.visible = extraPartsVisible;
        leftFrontLeg.visible = extraPartsVisible;
        leftHindLeg.visible = extraPartsVisible;
        if (extraPartsVisible)
        {
            super.setupAnim(animal, limbSwing, limbSwingAmount, ageInTicks, headYaw, Mth.clamp(headPitch, -45F, 5F));

            rightHindLeg.xRot = Mth.cos(limbSwing * 0.6662F * 0.6F) * 0.5F * limbSwingAmount;
            leftHindLeg.xRot = Mth.cos(limbSwing * 0.6662F * 0.6F + Mth.PI) * 0.5F * limbSwingAmount;
            rightFrontLeg.zRot = Mth.cos(limbSwing * 0.6662F * 0.6F + Mth.PI) * 0.5F * limbSwingAmount;
            leftFrontLeg.zRot = Mth.cos(limbSwing * 0.6662F * 0.6F) * 0.5F * limbSwingAmount;
            rightFrontLeg.xRot = 0.0F;
            leftFrontLeg.xRot = 0.0F;
            rightFrontLeg.yRot = 0.0F;
            leftFrontLeg.yRot = 0.0F;
            rightHindLeg.yRot = 0.0F;
            leftHindLeg.yRot = 0.0F;
            if (!animal.isInWater() && animal.isOnGround())
            {
                final float scale = 5.0F;
                rightFrontLeg.yRot = Mth.cos(limbSwing * scale + Mth.PI) * 4.0F * limbSwingAmount;
                rightFrontLeg.zRot = 0.0F;
                leftFrontLeg.yRot = Mth.cos(limbSwing * scale) * 4.0F * limbSwingAmount;
                leftFrontLeg.zRot = 0.0F;
                rightHindLeg.yRot = Mth.cos(limbSwing * scale + Mth.PI) * 1.5F * limbSwingAmount;
                rightHindLeg.xRot = 0.0F;
                leftHindLeg.yRot = Mth.cos(limbSwing * scale) * 1.5F * limbSwingAmount;
                leftHindLeg.xRot = 0.0F;
            }
        }
    }
}
