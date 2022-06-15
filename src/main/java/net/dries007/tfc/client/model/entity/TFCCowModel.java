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
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import net.minecraft.client.model.geom.builders.*;

import net.dries007.tfc.common.entities.livestock.DairyAnimal;

public class TFCCowModel extends AgeableListModel<DairyAnimal>
{
    public static LayerDefinition createBodyLayer()
    {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition root = meshdefinition.getRoot();

        PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-12.0F, -9.0F, -8.0F, 12.0F, 10.0F, 18.0F, new CubeDeformation(0.0F)), PartPose.offset(6.0F, 11.0F, 0.0F));

        PartDefinition leftFrontLeg = body.addOrReplaceChild("leftFrontLeg", CubeListBuilder.create().texOffs(0, 42).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.0F, 1.0F, -6.0F));

        PartDefinition rightHindLeg = body.addOrReplaceChild("rightHindLeg", CubeListBuilder.create().texOffs(40, 40).addBox(-2.0F, 0.0F, -3.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-10.0F, 1.0F, 8.0F));

        PartDefinition rightFrontLeg = body.addOrReplaceChild("rightFrontLeg", CubeListBuilder.create().texOffs(28, 28).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-10.0F, 1.0F, -6.0F));

        PartDefinition leftHindLeg = body.addOrReplaceChild("leftHindLeg", CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, 0.0F, -3.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.0F, 1.0F, 8.0F));

        PartDefinition udder = body.addOrReplaceChild("udder", CubeListBuilder.create().texOffs(42, 0).addBox(-2.0F, -11.0F, 4.05F, 4.0F, 2.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(-6.0F, 11.0F, 0.0F));

        PartDefinition head = body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 28).addBox(-4.0F, -3.0F, -6.0F, 8.0F, 8.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(-6.0F, -8.0F, -8.0F));

        PartDefinition hornR = head.addOrReplaceChild("hornR", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, -2.0F, -1.0F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(-4.0F, -2.0F, -2.0F));

        PartDefinition hornR2 = hornR.addOrReplaceChild("hornR2", CubeListBuilder.create().texOffs(42, 8).addBox(0.0F, -2.0F, -1.25F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, -2.0F, 0.0F, 0.0F, 0.0F, 0.4363F));

        PartDefinition hornL = head.addOrReplaceChild("hornL", CubeListBuilder.create().texOffs(12, 0).addBox(0.0F, -2.0F, -1.0F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(4.0F, -2.0F, -2.0F));

        PartDefinition hornL2 = hornL.addOrReplaceChild("hornL2", CubeListBuilder.create().texOffs(42, 11).addBox(-1.0F, -2.0F, -1.25F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, -2.0F, 0.0F, 0.0F, 0.0F, -0.4363F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    private final ModelPart head;
    private final ModelPart body;
    private final ModelPart leftHindLeg;
    private final ModelPart rightHindLeg;
    private final ModelPart leftFrontLeg;
    private final ModelPart rightFrontLeg;
    private final ModelPart udder;
    private final ModelPart hornR;
    private final ModelPart hornR2;
    private final ModelPart hornL;
    private final ModelPart hornL2;


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

    public TFCCowModel(ModelPart root)
    {
        super(false, 0F, 0F, 1.8F, 1.8F, 19F);
        body = root.getChild("body");
        head = body.getChild("head");
        leftHindLeg = body.getChild("leftHindLeg");
        rightHindLeg = body.getChild("rightHindLeg");
        leftFrontLeg = body.getChild("leftFrontLeg");
        rightFrontLeg = body.getChild("rightFrontLeg");

        this.hornL = head.getChild("hornL");
        this.hornL2 = hornL.getChild("hornL2");
        this.hornR = head.getChild("hornR");
        this.hornR2 = hornR.getChild("hornR2");
        this.udder = body.getChild("udder");
    }

    @Override
    public void setupAnim(DairyAnimal entity, float limbSwing, float limbSwingAmount, float ageInTicks, float headYaw, float headPitch)
    {
        hornR2.visible = hornL2.visible = entity.displayMaleCharacteristics();
        udder.visible = entity.displayFemaleCharacteristics();


        head.xRot = headPitch * ((float) Math.PI / 180F);
        head.yRot = headYaw * ((float) Math.PI / 180F);
        rightHindLeg.xRot = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
        leftHindLeg.xRot = Mth.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.4F * limbSwingAmount;
        rightFrontLeg.xRot = Mth.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.4F * limbSwingAmount;
        leftFrontLeg.xRot = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
    }

}
