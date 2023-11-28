/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

// Made with Blockbench 4.8.3
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports

package net.dries007.tfc.client.model.entity;

import java.util.stream.Stream;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Constants;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;

import net.dries007.tfc.common.entities.prey.Prey;

public class GazelleModel extends HierarchicalAnimatedModel<Prey> {
	// This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
    private final ModelPart head;

    public GazelleModel(ModelPart root)
    {
        super(root);
        this.head = root.getChild("body").getChild("neck").getChild("head");
    }

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-2.5F, -18.0F, -7.0F, 5.0F, 7.0F, 15.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 27.0F, 0.0F));

		PartDefinition neck = body.addOrReplaceChild("neck", CubeListBuilder.create(), PartPose.offset(0.0F, -15.0F, -6.0F));

		PartDefinition neck0_r1 = neck.addOrReplaceChild("neck0_r1", CubeListBuilder.create().texOffs(0, 0).addBox(-1.5872F, -6.4021F, -0.1846F, 3.0F, 7.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0872F, 0.0038F, -1.0F, 0.5236F, 0.0F, 0.0F));

		PartDefinition head = neck.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 22).addBox(-2.0872F, -3.0038F, -3.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(32, 22).addBox(-1.0872F, -2.0038F, -5.0F, 2.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0872F, -5.9962F, -2.0F, 0.3927F, 0.0F, 0.0F));

		PartDefinition earR = head.addOrReplaceChild("earR", CubeListBuilder.create(), PartPose.offsetAndRotation(-3.0F, -2.5F, 0.0F, -0.6269F, 0.3301F, -0.3362F));

		PartDefinition ear_r1 = earR.addOrReplaceChild("ear_r1", CubeListBuilder.create().texOffs(33, 0).addBox(-1.0F, -2.0F, 1.75F, 2.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.154F, -0.8513F, -0.5015F, -0.2618F, 0.0F, -0.48F));

		PartDefinition earL = head.addOrReplaceChild("earL", CubeListBuilder.create(), PartPose.offsetAndRotation(2.8257F, -2.5F, 0.0F, -0.6269F, -0.3301F, 0.3362F));

		PartDefinition ear_r2 = earL.addOrReplaceChild("ear_r2", CubeListBuilder.create().texOffs(8, 10).addBox(-1.0F, -2.0F, 1.75F, 2.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.154F, -0.8513F, -0.5015F, -0.2618F, 0.0F, 0.48F));

		PartDefinition hornR = head.addOrReplaceChild("hornR", CubeListBuilder.create(), PartPose.offset(-1.0F, -2.75F, -0.75F));

		PartDefinition tip_r1 = hornR.addOrReplaceChild("tip_r1", CubeListBuilder.create().texOffs(33, 4).addBox(-1.5F, -0.5F, 0.0F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -3.0F, 3.0F, 0.9599F, 0.0F, 0.0F));

		PartDefinition mid_r1 = hornR.addOrReplaceChild("mid_r1", CubeListBuilder.create().texOffs(32, 27).addBox(-1.25F, -0.25F, -0.75F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.25F, 1.0F, 0.5236F, 0.0F, 0.0F));

		PartDefinition base_r1 = hornR.addOrReplaceChild("base_r1", CubeListBuilder.create().texOffs(0, 22).addBox(-1.0F, -2.0F, -1.0F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.6109F, 0.0F, 0.0F));

		PartDefinition hornL2 = head.addOrReplaceChild("hornL2", CubeListBuilder.create(), PartPose.offset(0.8257F, -2.75F, -0.75F));

		PartDefinition tip_r2 = hornL2.addOrReplaceChild("tip_r2", CubeListBuilder.create().texOffs(9, 0).addBox(0.5F, -0.5F, 0.0F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -3.0F, 3.0F, 0.9599F, 0.0F, 0.0F));

		PartDefinition mid_r2 = hornL2.addOrReplaceChild("mid_r2", CubeListBuilder.create().texOffs(32, 31).addBox(0.25F, -0.25F, -0.75F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.25F, 1.0F, 0.5236F, 0.0F, 0.0F));

		PartDefinition base_r2 = hornL2.addOrReplaceChild("base_r2", CubeListBuilder.create().texOffs(0, 10).addBox(0.0F, -2.0F, -1.0F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.6109F, 0.0F, 0.0F));

		PartDefinition tail = body.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(0, 10).addBox(-1.0F, -1.0F, 0.0F, 2.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -17.0F, 7.75F, -1.1781F, 0.0F, 0.0F));

		PartDefinition legBR = body.addOrReplaceChild("legBR", CubeListBuilder.create().texOffs(19, 36).addBox(-1.0F, -3.0F, -1.0F, 2.0F, 7.0F, 3.0F, new CubeDeformation(0.0F))
		.texOffs(0, 30).addBox(-1.0F, 4.0F, 0.0F, 2.0F, 7.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.5F, -14.0F, 5.0F));

		PartDefinition legBL = body.addOrReplaceChild("legBL", CubeListBuilder.create().texOffs(8, 35).addBox(-1.0F, -3.0F, -1.0F, 2.0F, 7.0F, 3.0F, new CubeDeformation(0.0F))
		.texOffs(40, 0).addBox(-1.0F, 4.0F, 0.0F, 2.0F, 7.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(2.5F, -14.0F, 5.0F));

		PartDefinition legFR = body.addOrReplaceChild("legFR", CubeListBuilder.create().texOffs(16, 22).addBox(-1.0F, -1.0F, -1.0F, 2.0F, 12.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.5F, -14.0F, -4.0F));

		PartDefinition legFL = body.addOrReplaceChild("legFL", CubeListBuilder.create().texOffs(24, 22).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 12.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(2.5F, -15.0F, -4.0F));

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