/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.model.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.dries007.tfc.common.entities.aquatic.FreshWaterCritter;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;

public class CrayfishModel extends EntityModel<FreshWaterCritter>
{
    public static LayerDefinition createBodyLayer()
    {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, -3.0F, -3.0F, 3.0F, 3.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition tail1 = body.addOrReplaceChild("tail1", CubeListBuilder.create().texOffs(0, 13).addBox(-1.0F, 0.0F, 0.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.5F, -3.0F, 1.0F, 0.2618F, 0.0F, 0.0F));

        PartDefinition tail2 = tail1.addOrReplaceChild("tail2", CubeListBuilder.create().texOffs(13, 5).addBox(-1.0F, 0.0F, 0.0F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 2.0F, 0.3054F, 0.0F, 0.0F));

        PartDefinition tail3 = tail2.addOrReplaceChild("tail3", CubeListBuilder.create().texOffs(10, 0).addBox(-2.5F, 0.0F, 0.0F, 5.0F, 0.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 2.0F, 0.3054F, 0.0F, 0.0F));

        PartDefinition leftAntenna = body.addOrReplaceChild("leftAntenna", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition skinny_r1 = leftAntenna.addOrReplaceChild("skinny_r1", CubeListBuilder.create().texOffs(8, 14).addBox(0.0F, -4.75F, -0.5F, 0.0F, 5.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, -3.0F, -2.5F, -0.6981F, 0.0F, 0.6109F));

        PartDefinition rightAntenna = body.addOrReplaceChild("rightAntenna", CubeListBuilder.create(), PartPose.offset(-1.0F, 0.0F, 0.0F));

        PartDefinition skinny_r2 = rightAntenna.addOrReplaceChild("skinny_r2", CubeListBuilder.create().texOffs(0, 6).addBox(0.0F, -4.75F, -0.5F, 0.0F, 5.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, -3.0F, -2.5F, -0.6981F, 0.0F, -0.6109F));

        PartDefinition armLeft = body.addOrReplaceChild("armLeft", CubeListBuilder.create().texOffs(10, 9).addBox(0.0F, 0.0F, -3.0F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, -2.0F, -3.0F, 0.0F, -0.5236F, 0.0F));

        PartDefinition clawLeft = armLeft.addOrReplaceChild("clawLeft", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 0.0F, -3.0F, 0.0F, 0.3054F, 0.0F));

        PartDefinition clawBottomLeft = clawLeft.addOrReplaceChild("clawBottomLeft", CubeListBuilder.create().texOffs(14, 2).addBox(0.0F, 0.0F, -2.0F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.2182F, 0.0F, 0.0F));

        PartDefinition clawTopLeft = clawLeft.addOrReplaceChild("clawTopLeft", CubeListBuilder.create().texOffs(14, 14).addBox(0.0F, -1.0F, -2.0F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.2618F, 0.0F, 0.0F));

        PartDefinition armRight = body.addOrReplaceChild("armRight", CubeListBuilder.create().texOffs(0, 9).addBox(-1.0F, 0.0F, -3.0F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.0F, -2.0F, -3.0F, 0.0F, 0.5236F, 0.0F));

        PartDefinition clawRight = armRight.addOrReplaceChild("clawRight", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 0.0F, -3.0F, 0.0F, -0.3054F, 0.0F));

        PartDefinition clawBottomRight = clawRight.addOrReplaceChild("clawBottomRight", CubeListBuilder.create().texOffs(6, 12).addBox(-1.0F, 0.0F, -2.0F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.2182F, 0.0F, 0.0F));

        PartDefinition clawTopRight = clawRight.addOrReplaceChild("clawTopRight", CubeListBuilder.create().texOffs(10, 13).addBox(-1.0F, -1.0F, -2.0F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.2618F, 0.0F, 0.0F));

        PartDefinition legsLeft = body.addOrReplaceChild("legsLeft", CubeListBuilder.create(), PartPose.offset(1.0F, -1.0F, -1.0F));

        PartDefinition cube_r1 = legsLeft.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(4, 7).addBox(0.0F, 0.0F, -2.5F, 2.0F, 0.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.6109F));

        PartDefinition legsRight = body.addOrReplaceChild("legsRight", CubeListBuilder.create(), PartPose.offset(-2.0F, -1.0F, -1.0F));

        PartDefinition cube_r2 = legsRight.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(0, 7).addBox(-2.0F, 0.0F, -2.5F, 2.0F, 0.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.6109F));

        return LayerDefinition.create(meshdefinition, 32, 32);
    }
    private final ModelPart body;
    private final ModelPart tail1;
    private final ModelPart rightAntenna;
    private final ModelPart leftAntenna;
    private final ModelPart armLeft;
    private final ModelPart armRight;
    private final ModelPart clawTopLeft;
    private final ModelPart clawTopRight;

    public CrayfishModel(ModelPart root)
    {
        this.body = root.getChild("body");
        this.tail1 = body.getChild("tail1");
        this.rightAntenna = body.getChild("rightAntenna");
        this.leftAntenna = body.getChild("leftAntenna");
        this.armLeft = body.getChild("armLeft");
        this.armRight = body.getChild("armRight");
        this.clawTopLeft = armLeft.getChild("clawLeft").getChild("clawTopLeft");
        this.clawTopRight = armRight.getChild("clawRight").getChild("clawTopRight");
    }

    @Override
    public void setupAnim(FreshWaterCritter entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch)
    {
        float oscillation = 0.2F * Mth.cos(0.2F * ageInTicks);
        armLeft.xRot = oscillation;
        armRight.xRot = -1 * oscillation;
        tail1.xRot = oscillation * 0.5F;
        rightAntenna.zRot = oscillation * 0.1F;
        leftAntenna.zRot = oscillation * -0.1F;
        clawTopLeft.xRot = oscillation * 0.1F;
        clawTopRight.xRot = oscillation * -0.1F;
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha)
    {
        body.render(poseStack, buffer, packedLight, packedOverlay);
    }
}
