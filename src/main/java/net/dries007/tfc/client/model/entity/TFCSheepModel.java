/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.model.entity;

import com.google.common.collect.ImmutableList;
import com.mojang.math.Constants;
import net.minecraft.client.model.AgeableListModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import net.minecraft.client.model.geom.builders.*;

import net.dries007.tfc.common.entities.livestock.WoolyAnimal;

public class TFCSheepModel extends AgeableListModel<WoolyAnimal>
{
    public static LayerDefinition createBodyLayer()
    {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 29).addBox(-4.0F, -5.0F, -8.0F, 8.0F, 8.0F, 16.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 11.0F, 0.0F));

        PartDefinition woolBody = body.addOrReplaceChild("woolBody", CubeListBuilder.create().texOffs(0, 0).addBox(-5.5F, -7.0F, -9.0F, 11.0F, 11.0F, 18.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition leftFrontLeg = body.addOrReplaceChild("leftFrontLeg", CubeListBuilder.create().texOffs(78, 0).addBox(2.0F, 1.0F, -7.0F, 3.0F, 12.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition woolLeftFrontLeg = leftFrontLeg.addOrReplaceChild("woolLeftFrontLeg", CubeListBuilder.create().texOffs(0, 53).addBox(8.0F, 1.0F, -8.0F, 5.0F, 6.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(-7.0F, 0.0F, 0.0F));

        PartDefinition rightFrontLeg = body.addOrReplaceChild("rightFrontLeg", CubeListBuilder.create().texOffs(66, 0).addBox(2.0F, 1.0F, -7.0F, 3.0F, 12.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(-7.0F, 0.0F, 0.0F));

        PartDefinition woolRightFrontLeg = rightFrontLeg.addOrReplaceChild("woolRightFrontLeg", CubeListBuilder.create().texOffs(53, 24).addBox(1.0F, 1.0F, -8.0F, 5.0F, 6.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition leftHindLeg = body.addOrReplaceChild("leftHindLeg", CubeListBuilder.create().texOffs(0, 29).addBox(2.0F, 1.0F, -7.0F, 3.0F, 12.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 12.25F));

        PartDefinition woolLeftHindLeg = leftHindLeg.addOrReplaceChild("woolLeftHindLeg", CubeListBuilder.create().texOffs(20, 53).addBox(8.0F, 1.0F, 4.25F, 5.0F, 6.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(-7.0F, 0.0F, -12.25F));

        PartDefinition rightHindLeg = body.addOrReplaceChild("rightHindLeg", CubeListBuilder.create().texOffs(0, 0).addBox(2.0F, 1.0F, -7.0F, 3.0F, 12.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(-7.0F, 0.0F, 12.25F));

        PartDefinition woolRightHindLeg = rightHindLeg.addOrReplaceChild("woolRightHindLeg", CubeListBuilder.create().texOffs(43, 48).addBox(1.0F, 1.0F, 4.25F, 5.0F, 6.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, -12.25F));

        PartDefinition head = body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(40, 0).addBox(-3.0F, -6.0F, -5.0F, 6.0F, 6.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -3.0F, -9.0F));

        PartDefinition woolHead = head.addOrReplaceChild("woolHead", CubeListBuilder.create().texOffs(32, 29).addBox(-3.5F, -6.5F, -3.75F, 7.0F, 7.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition leftHorn = head.addOrReplaceChild("leftHorn", CubeListBuilder.create().texOffs(90, 0).addBox(-1.0F, -2.0F, -1.0F, 3.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.5F, -5.0F, 0.0F, 0.0F, 0.0F, -0.6981F));

        PartDefinition cube_r1 = leftHorn.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(90, 8).addBox(-1.5039F, -1.0643F, -4.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.0F, 3.0F, 0.0F, 0.0F, 0.0F, -0.3491F));

        PartDefinition cube_r2 = leftHorn.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(90, 4).addBox(1.5F, 0.5F, -3.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0873F));

        PartDefinition cube_r3 = leftHorn.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(90, 10).addBox(0.75F, -0.75F, -2.0F, 2.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.48F));

        PartDefinition rightHorn = head.addOrReplaceChild("rightHorn", CubeListBuilder.create().texOffs(90, 0).mirror().addBox(-2.0F, -2.0F, -1.0F, 3.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-2.5F, -5.0F, 0.0F, 0.0F, 0.0F, 0.6981F));

        PartDefinition cube_r4 = rightHorn.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(90, 8).mirror().addBox(-0.4961F, -1.0643F, -4.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-2.0F, 3.0F, 0.0F, 0.0F, 0.0F, 0.3491F));

        PartDefinition cube_r5 = rightHorn.addOrReplaceChild("cube_r5", CubeListBuilder.create().texOffs(90, 4).mirror().addBox(-3.5F, 0.5F, -3.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.0873F));

        PartDefinition cube_r6 = rightHorn.addOrReplaceChild("cube_r6", CubeListBuilder.create().texOffs(90, 10).mirror().addBox(-2.75F, -0.75F, -2.0F, 2.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.48F));

        return LayerDefinition.create(meshdefinition, 128, 64);
    }

    private final ModelPart body;
    private final ModelPart head;
    private final ModelPart leftHindLeg;
    private final ModelPart rightHindLeg;
    private final ModelPart leftFrontLeg;
    private final ModelPart rightFrontLeg;
    private final ModelPart woolBody;
    private final ModelPart woolHead;
    private final ModelPart woolLeftFrontLeg;
    private final ModelPart woolLeftHindLeg;
    private final ModelPart woolRightFrontLeg;
    private final ModelPart woolRightHindLeg;
    private final ModelPart leftHorn;
    private final ModelPart rightHorn;

    public TFCSheepModel(ModelPart root)
    {
        super(false, 0F, 0F, 1.8F, 1.8F, 18F);
        body = root.getChild("body");
        head = body.getChild("head");
        woolBody = body.getChild("woolBody");
        woolHead = head.getChild("woolHead");
        rightHorn = head.getChild("rightHorn");
        leftHorn = head.getChild("leftHorn");
        leftHindLeg = body.getChild("leftHindLeg");
        rightHindLeg = body.getChild("rightHindLeg");
        leftFrontLeg = body.getChild("leftFrontLeg");
        rightFrontLeg = body.getChild("rightFrontLeg");
        woolLeftHindLeg = leftHindLeg.getChild("woolLeftHindLeg");
        woolLeftFrontLeg = leftFrontLeg.getChild("woolLeftFrontLeg");
        woolRightHindLeg = rightHindLeg.getChild("woolRightHindLeg");
        woolRightFrontLeg = rightFrontLeg.getChild("woolRightFrontLeg");
    }

    @Override
    public void setupAnim(WoolyAnimal sheep, float limbSwing, float limbSwingAmount, float ageInTicks, float headYaw, float headPitch)
    {
        rightHorn.visible = leftHorn.visible = sheep.displayMaleCharacteristics();
        woolBody.visible = woolHead.visible = woolLeftFrontLeg.visible = woolLeftHindLeg.visible = woolRightFrontLeg.visible = woolRightHindLeg.visible = sheep.hasProduct();

        head.xRot = headPitch * Constants.DEG_TO_RAD;
        head.yRot = headYaw * Constants.DEG_TO_RAD;
        rightHindLeg.xRot = Mth.cos(limbSwing * 0.9F) * 0.4f * limbSwingAmount;
        leftHindLeg.xRot = Mth.cos(limbSwing * 0.9F + Mth.PI) * 0.4f * limbSwingAmount;
        rightFrontLeg.xRot = Mth.cos(limbSwing * 0.9F + Mth.PI) * 0.4f * limbSwingAmount;
        leftFrontLeg.xRot = Mth.cos(limbSwing * 0.9F) * 0.4f * limbSwingAmount;
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