/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

// Made with Blockbench 4.8.3
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports

package net.dries007.tfc.client.model.entity;

import com.mojang.math.Constants;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

import net.dries007.tfc.common.entities.prey.Prey;

public class BongoModel extends HierarchicalAnimatedModel<Prey> {
    private final ModelPart head;

    public BongoModel(ModelPart root)
    {
        super(root);
        this.head = root.getChild("body").getChild("neck").getChild("head");
    }

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-3.0F, -20.0F, -9.0F, 7.0F, 10.0F, 17.0F, new CubeDeformation(0.0F)), PartPose.offset(-0.5F, 25.0F, 0.0F));

		PartDefinition neck = body.addOrReplaceChild("neck", CubeListBuilder.create(), PartPose.offset(0.5F, -14.0F, -7.0F));

		PartDefinition neck0_r1 = neck.addOrReplaceChild("neck0_r1", CubeListBuilder.create().texOffs(0, 0).addBox(-1.5872F, -9.4021F, -1.1846F, 3.0F, 11.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0872F, 0.0038F, -1.0F, 0.6545F, 0.0F, 0.0F));

		PartDefinition head = neck.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 27).addBox(-2.5872F, -4.0038F, -4.0F, 5.0F, 5.0F, 5.0F, new CubeDeformation(0.0F))
		.texOffs(8, 37).addBox(-1.5872F, -2.0038F, -7.0F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0872F, -6.9962F, -5.0F));

		PartDefinition earL = head.addOrReplaceChild("earL", CubeListBuilder.create(), PartPose.offsetAndRotation(2.0F, -4.0F, 0.0F, -0.0873F, 0.0175F, 0.1396F));

		PartDefinition ear_r1 = earL.addOrReplaceChild("ear_r1", CubeListBuilder.create().texOffs(37, 12).addBox(-1.0F, -1.0F, 0.0F, 3.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.3491F));

		PartDefinition earR = head.addOrReplaceChild("earR", CubeListBuilder.create(), PartPose.offsetAndRotation(-2.0F, -4.0F, 0.0F, -0.0873F, 0.0175F, -0.1396F));

		PartDefinition ear_r2 = earR.addOrReplaceChild("ear_r2", CubeListBuilder.create().texOffs(15, 27).addBox(-2.0F, -1.0F, 0.0F, 3.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.3491F));

		PartDefinition hornR = head.addOrReplaceChild("hornR", CubeListBuilder.create(), PartPose.offsetAndRotation(-1.25F, -4.0F, -1.5F, -0.7201F, 0.262F, 0.0998F));

		PartDefinition antler0_r1 = hornR.addOrReplaceChild("antler0_r1", CubeListBuilder.create().texOffs(42, 37).addBox(-1.0F, -3.5F, 0.0F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.3155F, 0.1624F, -0.4068F));

		PartDefinition hornR2 = hornR.addOrReplaceChild("hornR2", CubeListBuilder.create(), PartPose.offsetAndRotation(-2.0F, -4.0F, -1.25F, 0.0422F, 0.2137F, 0.845F));

		PartDefinition antler0_r2 = hornR2.addOrReplaceChild("antler0_r2", CubeListBuilder.create().texOffs(32, 42).addBox(-1.0627F, -3.7629F, 0.767F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.75F, 0.5F, 0.0F, 0.3701F, -0.2343F, -0.727F));

		PartDefinition hornL = head.addOrReplaceChild("hornL", CubeListBuilder.create(), PartPose.offsetAndRotation(1.0757F, -4.0F, -1.5F, -0.7201F, -0.262F, -0.0998F));

		PartDefinition antler0_r3 = hornL.addOrReplaceChild("antler0_r3", CubeListBuilder.create().texOffs(28, 39).addBox(0.0F, -3.5F, 0.0F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.3155F, -0.1624F, 0.4068F));

		PartDefinition hornL2 = hornL.addOrReplaceChild("hornL2", CubeListBuilder.create(), PartPose.offsetAndRotation(2.0F, -4.0F, -1.25F, 0.0422F, -0.2137F, -0.845F));

		PartDefinition antler0_r4 = hornL2.addOrReplaceChild("antler0_r4", CubeListBuilder.create().texOffs(0, 27).addBox(0.0627F, -3.7629F, 0.767F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.75F, 0.5F, 0.0F, 0.3701F, 0.2343F, 0.727F));

		PartDefinition tail = body.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(36, 38).addBox(-0.5F, -1.0F, 0.0F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.5F, -19.0F, 7.25F, -0.8727F, 0.0F, 0.0F));

		PartDefinition tail2 = tail.addOrReplaceChild("tail2", CubeListBuilder.create().texOffs(31, 12).addBox(-0.51F, 2.8302F, -3.2139F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.25F, 8.5F, -0.8727F, 0.0F, 0.0F));

		PartDefinition tail3 = tail2.addOrReplaceChild("tail3", CubeListBuilder.create().texOffs(0, 12).addBox(-0.51F, -0.8217F, -3.0658F, 0.0F, 2.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.5F, 3.25F, 0.5F, -2.7053F, 0.0F, 0.0F));

		PartDefinition legBR = body.addOrReplaceChild("legBR", CubeListBuilder.create().texOffs(31, 0).addBox(-1.0F, -3.0F, -2.0F, 2.0F, 8.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(40, 27).addBox(-1.0F, 5.0F, 0.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-3.0F, -14.0F, 5.0F));

		PartDefinition legBL = body.addOrReplaceChild("legBL", CubeListBuilder.create().texOffs(20, 27).addBox(0.0F, -3.0F, -2.0F, 2.0F, 8.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(20, 39).addBox(0.0F, 5.0F, 0.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(3.0F, -14.0F, 5.0F));

		PartDefinition legFR = body.addOrReplaceChild("legFR", CubeListBuilder.create().texOffs(32, 27).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 13.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-3.0F, -14.0F, -6.0F));

		PartDefinition legFL = body.addOrReplaceChild("legFL", CubeListBuilder.create().texOffs(0, 37).addBox(0.0F, 0.0F, -1.0F, 2.0F, 13.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(3.0F, -14.0F, -6.0F));

		return LayerDefinition.create(meshdefinition, 64, 64);
	}

    @Override
    public void setupAnim(Prey entity, float limbSwing, float limbSwingAmount, float ageInTicks, float headYaw, float headPitch)
    {
        super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, headYaw, headPitch);
        final float speed = getAdjustedLandSpeed(entity);
        if (speed > 1.1f)
        {
            this.animateWalk(DeerModel.DEER_RUN, limbSwing, limbSwingAmount, 1f, 2.5f);
        }
        else
        {
            this.animateWalk(DeerModel.DEER_WALK, limbSwing, limbSwingAmount, 2.5f, 2.5f);
        }


        this.head.xRot = headPitch * Constants.DEG_TO_RAD;
        this.head.yRot = headYaw * Constants.DEG_TO_RAD;
    }
}