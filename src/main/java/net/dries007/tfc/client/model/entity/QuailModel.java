/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.model.entity;

// Made with Blockbench 4.2.4
// Exported for Minecraft version 1.17 - 1.18 with Mojang mappings
// Paste this class into your mod and generate all required imports


import com.google.common.collect.ImmutableList;
import net.minecraft.client.model.AgeableListModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;

import net.dries007.tfc.common.entities.livestock.OviparousAnimal;

public class QuailModel extends AgeableListModel<OviparousAnimal>
{

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 12).addBox(-3.0F, -6.0F, -4.0F, 6.0F, 6.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 21.0F, -1.0F, -0.0873F, 0.0F, 0.0F));

		PartDefinition legR = body.addOrReplaceChild("legR", CubeListBuilder.create().texOffs(14, 0).addBox(-1.0F, 1.0F, 0.0F, 1.0F, 2.0F, 0.0F, new CubeDeformation(0.0F))
		.texOffs(23, 0).addBox(-2.0F, 3.0F, -2.0F, 3.0F, 0.0F, 3.0F, new CubeDeformation(0.0F))
		.texOffs(0, 12).addBox(-1.0F, -1.0F, -0.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, 0.0F, 0.0F, 0.0873F, 0.0F, 0.0F));

		PartDefinition legL = body.addOrReplaceChild("legL", CubeListBuilder.create().texOffs(0, 15).addBox(0.0F, -1.0F, -0.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(16, 0).addBox(0.0F, 1.0F, 0.0F, 1.0F, 2.0F, 0.0F, new CubeDeformation(0.0F))
		.texOffs(23, 4).addBox(-1.0F, 3.0F, -2.0F, 3.0F, 0.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, 0.0F, 0.0F, 0.0873F, 0.0F, 0.0F));

		PartDefinition wingR = body.addOrReplaceChild("wingR", CubeListBuilder.create(), PartPose.offset(-3.0F, -5.0F, -1.0F));

		PartDefinition main_r1 = wingR.addOrReplaceChild("main_r1", CubeListBuilder.create().texOffs(26, 22).addBox(-1.0F, -1.0F, 0.0F, 1.0F, 4.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 1.0F, -2.0F, -0.0873F, 0.0F, 0.0F));

		PartDefinition neck = body.addOrReplaceChild("neck", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -4.0F, -2.5F, 0.0873F, 0.0F, 0.0F));

		PartDefinition cube_r1 = neck.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(0, 4).addBox(-2.5028F, -3.7661F, -1.1073F, 5.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 1.9166F, -0.909F, 0.6981F, 0.0044F, 0.0028F));

		PartDefinition head = neck.addOrReplaceChild("head", CubeListBuilder.create().texOffs(19, 11).addBox(-1.5F, -4.0436F, -1.001F, 4.0F, 5.0F, 3.0F, new CubeDeformation(0.0F))
		.texOffs(0, -3).addBox(0.5F, -7.0436F, -2.001F, 0.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.5F, -1.3334F, -2.909F, -0.0436F, 0.0F, 0.0F));

		PartDefinition beak_r1 = head.addOrReplaceChild("beak_r1", CubeListBuilder.create().texOffs(6, 0).addBox(-0.5F, 0.75F, -1.75F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -3.0436F, -1.001F, 0.1745F, 0.0F, 0.0F));

		PartDefinition wingL = body.addOrReplaceChild("wingL", CubeListBuilder.create(), PartPose.offset(3.0F, -6.0F, -1.0F));

		PartDefinition main_r2 = wingL.addOrReplaceChild("main_r2", CubeListBuilder.create().texOffs(14, 25).addBox(0.0F, -1.0F, 0.0F, 1.0F, 4.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 2.0F, -2.0F, -0.0873F, 0.0F, 0.0F));

		PartDefinition tail = body.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(0, 25).addBox(-2.0F, -2.0F, -1.25F, 4.0F, 4.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -3.25F, 3.25F, 0.1309F, 0.0F, 0.0F));

		PartDefinition end_r1 = tail.addOrReplaceChild("end_r1", CubeListBuilder.create().texOffs(14, 2).addBox(-1.0F, -1.5F, -0.25F, 2.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 2.0F, 0.1745F, 0.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 64, 64);
	}

    private final ModelPart body;
    private final ModelPart legR;
    private final ModelPart legL;
    private final ModelPart head;
    private final ModelPart neck;
    private final ModelPart wingR;
    private final ModelPart wingL;
    private final ModelPart tail;

    public QuailModel(ModelPart root)
    {
        super(false, 0F, 0F, 1.8F, 1.8F, 18F);
        body = root.getChild("body");
        neck = body.getChild("neck");
        head = neck.getChild("head");
        legL = body.getChild("legL");
        legR = body.getChild("legR");
        wingL = body.getChild("wingL");
        wingR = body.getChild("wingR");
        tail = body.getChild("tail");
    }

    @Override
    public void setupAnim(OviparousAnimal quail, float limbSwing, float limbSwingAmount, float ageInTicks, float headYaw, float headPitch)
    {
        head.xRot = headPitch * ((float) Math.PI / 240F);
        neck.xRot = headPitch * ((float) Math.PI / 720F);
        head.yRot = headYaw * ((float) Math.PI / 360F);
        neck.yRot = headYaw * ((float) Math.PI / 360F);
        legR.xRot = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
        legL.xRot = Mth.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.4F * limbSwingAmount;

        //Body Sway
        if (!quail.isInWater())
        {
            body.zRot = Mth.cos(limbSwing * 0.6662F + ((float) Math.PI / 2F)) * 0.3F * limbSwingAmount;
            neck.zRot = Mth.cos(limbSwing * 0.6662F + ((float) Math.PI / 2F)) * -0.25F * limbSwingAmount;
        }

        wingR.zRot = 0F;
        wingL.zRot = 0F;

        //Flapping in air
        if (!quail.isOnGround())
        {
            wingR.zRot = ageInTicks;
            wingL.zRot = -ageInTicks;
        }
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