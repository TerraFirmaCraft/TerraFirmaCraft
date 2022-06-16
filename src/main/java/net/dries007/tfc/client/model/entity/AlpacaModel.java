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

import net.dries007.tfc.common.entities.livestock.WoolyAnimal;

public class AlpacaModel extends AgeableListModel<WoolyAnimal>
{
    public static LayerDefinition createBodyLayer()
    {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 31).addBox(-3.0F, -18.0F, -9.0F, 6.0F, 9.0F, 17.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 25.0F, 0.0F));
        PartDefinition wool_body_f = body.addOrReplaceChild("wool_body_f", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -19.0F, -10.0F, 8.0F, 12.0F, 19.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition neck = body.addOrReplaceChild("neck", CubeListBuilder.create().texOffs(46, 42).addBox(-1.5F, -12.3983F, -4.1846F, 3.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -15.0F, -6.0F, 0.0436F, 0.0F, 0.0F));
        PartDefinition head = neck.addOrReplaceChild("head", CubeListBuilder.create().texOffs(46, 9).addBox(-2.0872F, -4.5038F, -1.75F, 4.0F, 5.0F, 5.0F, new CubeDeformation(0.0F))
            .texOffs(54, 19).addBox(-1.0872F, -2.5038F, -3.75F, 2.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0872F, -12.4962F, -4.0F, -0.0436F, 0.0F, 0.0F));
        PartDefinition earL = head.addOrReplaceChild("earL", CubeListBuilder.create().texOffs(39, 42).addBox(-1.0F, -2.0F, 0.0F, 2.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.0F, -4.5F, 0.5F, 0.0873F, 0.0175F, 0.1396F));
        PartDefinition earR = head.addOrReplaceChild("earR", CubeListBuilder.create().texOffs(13, 0).addBox(-1.0F, -2.0F, 0.0F, 2.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.0F, -4.5F, 0.5F, 0.0873F, 0.0175F, -0.1396F));
        PartDefinition wool_head_f = head.addOrReplaceChild("wool_head_f", CubeListBuilder.create().texOffs(29, 31).addBox(-3.0F, -5.0F, -0.75F, 6.0F, 6.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition wool_neck_f = neck.addOrReplaceChild("wool_neck_f", CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, -13.0F, -4.75F, 4.0F, 13.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition tail = body.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(29, 42).addBox(-1.0F, -1.0F, 0.0F, 2.0F, 2.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -17.5F, 7.5F, 0.9599F, 0.0F, 0.0F));
        PartDefinition legBR = body.addOrReplaceChild("right_hind_leg", CubeListBuilder.create().texOffs(8, 57).addBox(-1.0F, -2.0F, -1.0F, 2.0F, 15.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-3.0F, -14.0F, 6.0F));
        PartDefinition wool_legBR_f = legBR.addOrReplaceChild("wool_legBR_f", CubeListBuilder.create().texOffs(35, 0).addBox(-2.0F, -3.0F, -2.0F, 4.0F, 10.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition legBL = body.addOrReplaceChild("left_hind_leg", CubeListBuilder.create().texOffs(0, 57).addBox(-1.0F, -2.0F, -1.0F, 2.0F, 15.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(3.0F, -14.0F, 6.0F));
        PartDefinition wool_legBL_f = legBL.addOrReplaceChild("wool_legBL_f", CubeListBuilder.create().texOffs(0, 31).addBox(4.0F, -3.0F, -2.0F, 4.0F, 10.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-6.0F, 0.0F, 0.0F));
        PartDefinition legFR = body.addOrReplaceChild("right_front_leg", CubeListBuilder.create().texOffs(16, 57).addBox(-1.0F, -1.0F, -1.0F, 2.0F, 11.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-3.0F, -11.0F, -6.0F));
        PartDefinition wool_legFR_f = legFR.addOrReplaceChild("wool_legFR_f", CubeListBuilder.create().texOffs(56, 54).addBox(-2.0F, -3.0F, -2.0F, 4.0F, 7.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition legFL = body.addOrReplaceChild("left_front_leg", CubeListBuilder.create().texOffs(24, 57).addBox(-1.0F, -1.0F, -1.0F, 2.0F, 11.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(3.0F, -11.0F, -6.0F));
        PartDefinition wool_legFL_f = legFL.addOrReplaceChild("wool_legFL_f", CubeListBuilder.create().texOffs(51, 27).addBox(-2.0F, -3.0F, -2.0F, 4.0F, 7.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));
        return LayerDefinition.create(mesh, 128, 128);
    }

    private final ModelPart neck;
    private final ModelPart head;
    private final ModelPart body;
    private final ModelPart leftHindLeg;
    private final ModelPart rightHindLeg;
    private final ModelPart leftFrontLeg;
    private final ModelPart rightFrontLeg;
    private final ModelPart wool_body_f;
    private final ModelPart wool_head_f;
    private final ModelPart wool_neck_f;
    private final ModelPart wool_legBR_f;
    private final ModelPart wool_legBL_f;
    private final ModelPart wool_legFR_f;
    private final ModelPart wool_legFL_f;

    public AlpacaModel(ModelPart root)
    {
        super(false, 0F, 0F, 1.8F, 1.8F, 19F);
        body = root.getChild("body");
        neck = body.getChild("neck");
        head = neck.getChild("head");
        wool_body_f = body.getChild("wool_body_f");
        wool_head_f = head.getChild("wool_head_f");
        wool_neck_f = neck.getChild("wool_neck_f");
        leftHindLeg = body.getChild("left_hind_leg");
        wool_legBL_f = leftHindLeg.getChild("wool_legBL_f");
        rightHindLeg = body.getChild("right_hind_leg");
        wool_legBR_f = rightHindLeg.getChild("wool_legBR_f");
        leftFrontLeg = body.getChild("left_front_leg");
        wool_legFL_f = leftFrontLeg.getChild("wool_legFL_f");
        rightFrontLeg = body.getChild("right_front_leg");
        wool_legFR_f = rightFrontLeg.getChild("wool_legFR_f");
    }

    @Override
    public void setupAnim(WoolyAnimal animal, float limbSwing, float limbSwingAmount, float ageInTicks, float headYaw, float headPitch)
    {
        wool_body_f.visible = wool_head_f.visible = wool_neck_f.visible = wool_legBL_f.visible = wool_legBR_f.visible = wool_legFL_f.visible = wool_legFR_f.visible = animal.hasProduct();
        head.xRot = headPitch * ((float) Math.PI / 240F);
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
