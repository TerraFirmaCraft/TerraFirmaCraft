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

import net.dries007.tfc.common.entities.livestock.DairyAnimal;

public class YakModel extends AgeableListModel<DairyAnimal>
{
    public static LayerDefinition createBodyLayer()
    {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 46).addBox(-5.0F, -26.0F, -6.0F, 10.0F, 19.0F, 20.0F, new CubeDeformation(0.0F))
            .texOffs(0, 0).addBox(-7.0F, -24.0F, -11.0F, 14.0F, 20.0F, 26.0F, new CubeDeformation(0.0F))
            .texOffs(60, 46).addBox(-6.0F, -25.0F, -14.0F, 12.0F, 17.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 23.0F, 0.0F));

        PartDefinition neck = body.addOrReplaceChild("neck", CubeListBuilder.create().texOffs(56, 0).addBox(-3.0F, -2.0F, -3.0F, 6.0F, 5.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -20.0F, -15.0F));

        PartDefinition head = neck.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-3.0F, -3.0F, -4.0F, 7.0F, 7.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.5F, 1.0F, -2.0F, 0.8727F, 0.0F, 0.0F));

        PartDefinition hornL1 = head.addOrReplaceChild("hornL1", CubeListBuilder.create().texOffs(31, 85).addBox(-2.6F, 0.3521F, -1.9674F, 3.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, -4.0F, 0.0F, 0.0F, 0.0F, -0.2618F));

        PartDefinition hornL2 = hornL1.addOrReplaceChild("hornL2", CubeListBuilder.create().texOffs(0, 85).addBox(-1.608F, -0.2903F, -0.5F, 3.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.092F, 1.2903F, -1.1233F, 0.0F, 0.0F, -0.5236F));

        PartDefinition hornLCube3_r1 = hornL2.addOrReplaceChild("hornLCube3_r1", CubeListBuilder.create().texOffs(10, 87).addBox(-2.75F, -0.75F, 0.75F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.7014F, 1.2758F, 0.1107F, 0.0F, -1.1781F, 0.9163F));

        PartDefinition hornR1 = head.addOrReplaceChild("hornR1", CubeListBuilder.create().texOffs(18, 85).addBox(-0.4F, 0.3521F, -1.9674F, 3.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.0F, -4.0F, 0.0F, 0.0F, 0.0F, 0.2618F));

        PartDefinition hornR2 = hornR1.addOrReplaceChild("hornR2", CubeListBuilder.create().texOffs(0, 88).addBox(-2.5934F, -1.0661F, -0.4874F, 3.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.7934F, 2.5661F, -1.0126F, 0.0F, 0.0F, 0.5236F));

        PartDefinition hornRCube3_r1 = hornR2.addOrReplaceChild("hornRCube3_r1", CubeListBuilder.create().texOffs(10, 85).addBox(-0.25F, -0.75F, 0.0F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 1.1781F, -0.9163F));

        PartDefinition snout = head.addOrReplaceChild("snout", CubeListBuilder.create().texOffs(0, 13).addBox(-2.0F, -4.4102F, -4.0209F, 5.0F, 4.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 4.3499F, -4.0589F, 0.3491F, 0.0F, 0.0F));

        PartDefinition beard = head.addOrReplaceChild("beard", CubeListBuilder.create().texOffs(41, -3).addBox(0.0F, -1.0442F, -5.8944F, 0.0F, 5.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 7.0442F, -0.6163F, -0.8727F, 0.0F, 0.0F));

        PartDefinition legFL = body.addOrReplaceChild("legFL", CubeListBuilder.create().texOffs(0, 46).addBox(-1.25F, -6.0F, -1.5F, 4.0F, 15.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(3.25F, -8.0F, -7.5F));

        PartDefinition legFR = body.addOrReplaceChild("legFR", CubeListBuilder.create().texOffs(40, 46).addBox(-2.5F, -6.0F, -1.5F, 4.0F, 15.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-3.5F, -8.0F, -7.5F));

        PartDefinition legBL = body.addOrReplaceChild("legBL", CubeListBuilder.create().texOffs(60, 73).addBox(-1.5F, -3.0F, -1.5F, 4.0F, 13.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(3.5F, -9.0F, 9.5F));

        PartDefinition legBR = body.addOrReplaceChild("legBR", CubeListBuilder.create().texOffs(76, 73).addBox(-2.5F, -3.0F, -1.5F, 4.0F, 13.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-3.5F, -9.0F, 9.5F));

        PartDefinition udder = body.addOrReplaceChild("udder", CubeListBuilder.create().texOffs(56, 18).addBox(1.0F, -7.5F, 4.0F, 4.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-3.0F, 1.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }

    private final ModelPart body;
    private final ModelPart udder;
    private final ModelPart legBR;
    private final ModelPart legBL;
    private final ModelPart legFR;
    private final ModelPart legFL;
    private final ModelPart neck;
    private final ModelPart head;
    private final ModelPart hornL1;
    private final ModelPart hornL2;
    private final ModelPart hornR1;
    private final ModelPart hornR2;
    private final ModelPart snout;
    private final ModelPart beard;


    public YakModel(ModelPart root)
    {
        super(false, 0F, 0F, 1.8F, 1.8F, 19F);
        body = root.getChild("body");
        udder = body.getChild("udder");
        neck = body.getChild("neck");
        head = neck.getChild("head");
        legBR = body.getChild("legBR");
        legBL = body.getChild("legBL");
        legFR = body.getChild("legFR");
        legFL = body.getChild("legFL");
        hornL1 = head.getChild("hornL1");
        hornL2 = hornL1.getChild("hornL2");
        hornR1 = head.getChild("hornR1");
        hornR2 = hornR1.getChild("hornR2");
        beard = head.getChild("beard");
        snout = head.getChild("snout");
    }

    @Override
    public void setupAnim(DairyAnimal animal, float limbSwing, float limbSwingAmount, float ageInTicks, float headYaw, float headPitch)
    {
        hornL1.visible = hornR1.visible = !animal.isBaby();
        udder.visible = animal.displayFemaleCharacteristics();

        head.xRot = headPitch * ((float) Math.PI / 480F) + 0.873F;
        neck.xRot = headPitch * ((float) Math.PI / 720F);
        head.yRot = headYaw * ((float) Math.PI / 360F);
        neck.yRot = headYaw * ((float) Math.PI / 360F);
        legBR.xRot = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
        legBL.xRot = Mth.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.4F * limbSwingAmount;
        legFR.xRot = Mth.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.4F * limbSwingAmount;
        legFL.xRot = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
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