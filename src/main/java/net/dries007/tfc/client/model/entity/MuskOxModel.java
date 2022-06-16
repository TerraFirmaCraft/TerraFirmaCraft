/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.model.entity;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.model.AgeableListModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;

// Made with Blockbench 4.2.4
// Exported for Minecraft version 1.17 - 1.18 with Mojang mappings
// Paste this class into your mod and generate all required imports


import net.dries007.tfc.common.entities.livestock.TFCAnimalProperties;
import net.dries007.tfc.common.entities.livestock.WoolyAnimal;

public class MuskOxModel extends AgeableListModel<WoolyAnimal>
{

    public static LayerDefinition createBodyLayer()
    {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(62, 21).addBox(-5.0F, -22.0F, -6.0F, 10.0F, 16.0F, 22.0F, new CubeDeformation(0.0F))
            .texOffs(56, 61).addBox(-6.0F, -19.0F, -11.0F, 12.0F, 16.0F, 24.0F, new CubeDeformation(0.0F))
            .texOffs(71, 102).addBox(-7.0F, -23.0F, -13.0F, 14.0F, 16.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 22.0F, 0.0F));

        PartDefinition neck = body.addOrReplaceChild("neck", CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, -2.0F, -3.0F, 4.0F, 5.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -19.0F, -13.0F));

        PartDefinition head = neck.addOrReplaceChild("head", CubeListBuilder.create().texOffs(56, 0).addBox(-4.0F, -3.0F, -5.0F, 8.0F, 7.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 1.0F, -2.0F, 0.8727F, 0.0F, 0.0F));

        PartDefinition hornL1 = head.addOrReplaceChild("hornL1", CubeListBuilder.create().texOffs(13, 59).addBox(-2.6F, 0.3521F, -1.9674F, 3.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.5F, -4.0F, 0.0F, 0.0F, 0.0F, -0.2618F));

        PartDefinition hornL2 = hornL1.addOrReplaceChild("hornL2", CubeListBuilder.create().texOffs(0, 23).addBox(-1.608F, -0.2903F, -0.5F, 3.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.092F, 1.2903F, -1.1233F, 0.0F, 0.0F, -0.5236F));

        PartDefinition hornLCube3_r1 = hornL2.addOrReplaceChild("hornLCube3_r1", CubeListBuilder.create().texOffs(0, 0).addBox(-2.75F, -0.75F, 0.75F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.7014F, 1.2758F, 0.1107F, 0.0F, -1.1781F, 0.9163F));

        PartDefinition hornR1 = head.addOrReplaceChild("hornR1", CubeListBuilder.create().texOffs(14, 39).addBox(-0.4F, 0.3521F, -1.9674F, 3.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.5F, -4.0F, 0.0F, 0.0F, 0.0F, 0.2618F));

        PartDefinition hornR2 = hornR1.addOrReplaceChild("hornR2", CubeListBuilder.create().texOffs(10, 23).addBox(-2.5934F, -1.0661F, -0.4874F, 3.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.7934F, 2.5661F, -1.0126F, 0.0F, 0.0F, 0.5236F));

        PartDefinition hornRCube3_r1 = hornR2.addOrReplaceChild("hornRCube3_r1", CubeListBuilder.create().texOffs(17, 0).addBox(-0.25F, -0.75F, 0.0F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 1.1781F, -0.9163F));

        PartDefinition snout = head.addOrReplaceChild("snout", CubeListBuilder.create().texOffs(0, 117).addBox(-3.0F, -4.7102F, -3.799F, 6.0F, 5.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 4.3499F, -4.0589F, 0.3491F, 0.0F, 0.0F));

        PartDefinition beard = head.addOrReplaceChild("beard", CubeListBuilder.create().texOffs(0, 0).addBox(0.0F, -1.0442F, -5.8944F, 0.0F, 5.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 7.0442F, -0.6163F, -0.8727F, 0.0F, 0.0F));

        PartDefinition leftFrontLeg = body.addOrReplaceChild("leftFrontLeg", CubeListBuilder.create().texOffs(0, 38).addBox(-1.5F, -7.0F, -2.5F, 4.0F, 17.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(3.25F, -8.0F, -7.5F));

        PartDefinition rightFrontLeg = body.addOrReplaceChild("rightFrontLeg", CubeListBuilder.create().texOffs(86, 0).addBox(-2.25F, -7.0F, -2.5F, 4.0F, 17.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-3.5F, -8.0F, -7.5F));

        PartDefinition leftHindLeg = body.addOrReplaceChild("leftHindLeg", CubeListBuilder.create().texOffs(102, 0).addBox(-1.75F, -2.0F, -1.5F, 4.0F, 13.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(3.5F, -9.0F, 9.5F));

        PartDefinition rightHindLeg = body.addOrReplaceChild("rightHindLeg", CubeListBuilder.create().texOffs(55, 103).addBox(-2.25F, -2.0F, -1.5F, 4.0F, 13.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-3.5F, -9.0F, 9.5F));

        PartDefinition quiviut = body.addOrReplaceChild("quiviut", CubeListBuilder.create().texOffs(0, 88).addBox(-8.0F, -24.0F, -13.5F, 16.0F, 18.0F, 11.0F, new CubeDeformation(0.0F))
            .texOffs(0, 43).addBox(-6.5F, -22.5F, -10.5F, 13.0F, 15.0F, 27.0F, new CubeDeformation(0.0F))
            .texOffs(0, 0).addBox(-7.5F, -20.0F, -11.5F, 15.0F, 13.0F, 26.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }

    private final ModelPart neck;
    private final ModelPart head;
    private final ModelPart body;
    private final ModelPart leftHindLeg;
    private final ModelPart rightHindLeg;
    private final ModelPart leftFrontLeg;
    private final ModelPart rightFrontLeg;
    private final ModelPart hornL1;
    private final ModelPart hornL2;
    private final ModelPart hornR1;
    private final ModelPart hornR2;
    private final ModelPart snout;
    private final ModelPart beard;
    private final ModelPart quiviut;

    public MuskOxModel(ModelPart root)
    {
        super(false, 0F, 0F, 1.8F, 1.8F, 19F);
        body = root.getChild("body");
        neck = body.getChild("neck");
        head = neck.getChild("head");
        hornL1 = head.getChild("hornL1");
        hornL2 = hornL1.getChild("hornL2");
        hornR1 = head.getChild("hornR1");
        hornR2 = hornR1.getChild("hornR2");
        beard = head.getChild("beard");
        snout = head.getChild("snout");
        leftHindLeg = body.getChild("leftHindLeg");
        rightHindLeg = body.getChild("rightHindLeg");
        leftFrontLeg = body.getChild("leftFrontLeg");
        rightFrontLeg = body.getChild("rightFrontLeg");
        quiviut = body.getChild("quiviut");
    }

    @Override
    public void setupAnim(WoolyAnimal animal, float limbSwing, float limbSwingAmount, float ageInTicks, float headYaw, float headPitch)
    {
        hornL1.visible = hornR1.visible = animal.displayMaleCharacteristics();
        quiviut.visible = animal.hasProduct();

        head.xRot = headPitch * ((float) Math.PI / 480F) + 0.873F;
        neck.xRot = headPitch * ((float) Math.PI / 720F);
        head.yRot = headYaw * ((float) Math.PI / 360F);
        neck.yRot = headYaw * ((float) Math.PI / 360F);
        rightHindLeg.xRot = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
        leftHindLeg.xRot = Mth.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.4F * limbSwingAmount;
        rightFrontLeg.xRot = Mth.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.4F * limbSwingAmount;
        leftFrontLeg.xRot = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
    }

    @Override
    protected Iterable<ModelPart> headParts()
    {
        return ImmutableList.of();
    }

    @Override
    protected Iterable<ModelPart> bodyParts()
    {
        return ImmutableList.of(this.body);
    }
}