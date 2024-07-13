/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.model.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;

import net.dries007.tfc.common.entities.aquatic.TFCDolphin;

public class OrcaModel extends EntityModel<TFCDolphin>
{
    public static LayerDefinition createBodyLayer()
    {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-8.0F, -9.0F, -10.0F, 16.0F, 14.0F, 27.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 19.0F, -3.0F));

        PartDefinition right = body.addOrReplaceChild("right", CubeListBuilder.create().texOffs(72, 49).addBox(0.2066F, -2.0017F, -1.0F, 12.0F, 2.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(7.0F, 0.0F, -2.0F, 0.0F, 0.0F, 0.6109F));

        PartDefinition top = body.addOrReplaceChild("top", CubeListBuilder.create().texOffs(0, 11).addBox(-1.5F, -5.8264F, 6.1853F, 3.0F, 6.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -8.3075F, -2.0851F));

        PartDefinition fin = top.addOrReplaceChild("fin", CubeListBuilder.create().texOffs(0, 41).addBox(-2.0F, -1.2098F, 7.9685F, 2.0F, 6.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, -8.6925F, 0.0851F, -0.1745F, 0.0F, 0.0F));

        PartDefinition tip = fin.addOrReplaceChild("tip", CubeListBuilder.create().texOffs(18, 11).addBox(-0.5F, -4.9963F, 17.9519F, 1.0F, 5.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, -0.3075F, -8.0851F, -0.0873F, 0.0F, 0.0F));

        PartDefinition knob = tip.addOrReplaceChild("knob", CubeListBuilder.create().texOffs(0, 0).addBox(-0.5F, -7.1116F, 1.5618F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.587F, 18.0544F, -0.0873F, 0.0F, 0.0F));

        PartDefinition left = body.addOrReplaceChild("left", CubeListBuilder.create().texOffs(72, 58).addBox(-12.8192F, -2.5736F, -3.0F, 12.0F, 2.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-6.0F, 1.0F, 0.0F, 0.0F, 0.0F, -0.6109F));

        PartDefinition rump = body.addOrReplaceChild("rump", CubeListBuilder.create().texOffs(0, 41).addBox(-6.0F, -7.6634F, 0.361F, 12.0F, 11.0F, 16.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.0F, 13.0F, -0.0873F, 0.0F, 0.0F));

        PartDefinition tail = rump.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(41, 53).addBox(-4.0F, -1.9103F, -0.3887F, 8.0F, 6.0F, 15.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -3.5534F, 15.3256F, -0.0873F, 0.0F, 0.0F));

        PartDefinition backleft = tail.addOrReplaceChild("backleft", CubeListBuilder.create().texOffs(0, 68).addBox(-13.6466F, -0.1549F, -6.127F, 14.0F, 2.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, 2.8791F, 15.4092F, 0.0F, 0.1745F, 0.0F));

        PartDefinition backright = tail.addOrReplaceChild("backright", CubeListBuilder.create().texOffs(40, 41).addBox(2.1348F, -0.9352F, -5.8312F, 14.0F, 2.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, 3.2264F, 14.3789F, 0.0F, -0.1745F, 0.0F));

        PartDefinition front = body.addOrReplaceChild("front", CubeListBuilder.create().texOffs(59, 0).addBox(-7.0F, -7.1559F, -11.6333F, 14.0F, 11.0F, 9.0F, new CubeDeformation(0.0F))
            .texOffs(35, 74).addBox(-5.3258F, -5.8344F, -15.1797F, 10.0F, 9.0F, 5.0F, new CubeDeformation(0.0F))
            .texOffs(0, 0).addBox(-4.3258F, -3.0997F, -18.2554F, 8.0F, 6.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, -6.0F, 0.0873F, 0.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }

    private final ModelPart body;
    private final ModelPart rump;
    private final ModelPart front;
    private final ModelPart top;
    private final ModelPart left;
    private final ModelPart right;


    public OrcaModel(ModelPart root)
    {
        body = root.getChild("body");
        rump = body.getChild("rump");
        front = body.getChild("front");
        top = body.getChild("top");
        left = body.getChild("left");
        right = body.getChild("right");
    }

    @Override
    public void setupAnim(TFCDolphin entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch)
    {
        float pitch = headPitch * (float) Math.PI / -180F;
        float yaw = netHeadYaw * (float) Math.PI / 180F;
        body.xRot = pitch;
        body.yRot = yaw;

        if (entity.getDeltaMovement().horizontalDistanceSqr() > 1.0E-7D)
        {
            float oscillation = 0.1F - 0.2F * Mth.cos(ageInTicks * 0.3F);
            rump.zRot = oscillation;
            front.zRot = oscillation;
            top.zRot = oscillation;
            left.zRot = -1 * oscillation;
            right.zRot = -1 * oscillation;
        }
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, int color)
    {
        body.render(poseStack, buffer, packedLight, packedOverlay);
    }
}
