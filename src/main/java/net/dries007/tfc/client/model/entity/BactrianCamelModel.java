/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.model.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;

import net.dries007.tfc.client.animation.BactrianCamelAnimation;
import net.dries007.tfc.common.entities.livestock.camel.BactrianCamel;

public class BactrianCamelModel extends HierarchicalAnimatedModel<BactrianCamel>
{
    public static LayerDefinition createBodyLayer()
    {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition base = partdefinition.addOrReplaceChild("base", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition body = base.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 25).addBox(-8.0F, -12.0F, -23.5F, 15.0F, 12.0F, 27.0F, new CubeDeformation(0.0F)), PartPose.offset(0.5F, -20.0F, 9.5F));

        PartDefinition wool_body = body.addOrReplaceChild("wool_body", CubeListBuilder.create().texOffs(1, 89).addBox(-8.0F, -12.0F, -23.5F, 15.0F, 12.0F, 27.0F, new CubeDeformation(0.1F))
            .texOffs(20, -11).addBox(7.0F, 0.0F, -15.5F, 0.0F, 3.0F, 14.0F, new CubeDeformation(0.0F))
            .texOffs(16, -14).addBox(-8.0F, 0.0F, -15.5F, 0.0F, 3.0F, 14.0F, new CubeDeformation(0.0F))
            .texOffs(92, 52).addBox(-5.0F, -20.0F, -21.5F, 9.0F, 8.0F, 9.0F, new CubeDeformation(0.1F))
            .texOffs(72, 1).addBox(-5.0F, -20.0F, -7.5F, 9.0F, 8.0F, 9.0F, new CubeDeformation(0.1F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition tail = body.addOrReplaceChild("tail", CubeListBuilder.create(), PartPose.offset(-0.5F, -9.0F, 3.5F));

        PartDefinition tail_r1 = tail.addOrReplaceChild("tail_r1", CubeListBuilder.create().texOffs(21, 34).addBox(-1.5F, 0.0F, 0.0F, 3.0F, 14.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 3.1416F, 0.0F));

        PartDefinition head = body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(64, 92).addBox(-4.0F, -18.0F, -17.0F, 7.0F, 3.0F, 9.0F, new CubeDeformation(0.04F))
            .texOffs(58, 25).addBox(-4.0F, -4.0F, -15.0F, 7.0F, 8.0F, 19.0F, new CubeDeformation(0.01F))
            .texOffs(20, 7).addBox(-4.0F, -15.0F, -15.0F, 7.0F, 11.0F, 7.0F, new CubeDeformation(0.0F))
            .texOffs(58, 105).addBox(-3.0F, -15.0F, -21.0F, 5.0F, 5.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -6.0F, -19.5F));

        PartDefinition wool_shey = head.addOrReplaceChild("wool_shey", CubeListBuilder.create().texOffs(0, 64).addBox(-4.0F, -10.0F, -17.0F, 7.0F, 6.0F, 9.0F, new CubeDeformation(0.02F))
            .texOffs(53, 64).addBox(-4.0F, -15.0F, -13.0F, 7.0F, 5.0F, 5.0F, new CubeDeformation(0.01F))
            .texOffs(82, 89).addBox(-4.0F, -4.0F, -16.0F, 7.0F, 8.0F, 16.0F, new CubeDeformation(0.1F))
            .texOffs(67, 64).addBox(-4.0F, -2.0F, -14.0F, 7.0F, 14.0F, 11.0F, new CubeDeformation(-0.3F))
            .texOffs(104, 70).addBox(-4.0F, -4.0F, -18.0F, 7.0F, 14.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition left_ear = head.addOrReplaceChild("left_ear", CubeListBuilder.create().texOffs(50, 19).addBox(0.0F, -0.5F, -1.0F, 3.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(2.5F, -14.0F, -9.5F));

        PartDefinition right_ear = head.addOrReplaceChild("right_ear", CubeListBuilder.create().texOffs(79, 20).addBox(-3.0F, -0.5F, -1.0F, 3.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-3.5F, -14.0F, -9.5F));

        PartDefinition hump = body.addOrReplaceChild("hump", CubeListBuilder.create().texOffs(92, 113).addBox(-5.0F, -6.0F, -12.0F, 9.0F, 6.0F, 9.0F, new CubeDeformation(0.0F))
            .texOffs(23, 74).addBox(-5.0F, -6.0F, 2.0F, 9.0F, 6.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -12.0F, -9.5F));

        PartDefinition right_front_leg = base.addOrReplaceChild("right_front_leg", CubeListBuilder.create().texOffs(0, 26).addBox(-2.5F, 2.0F, -2.5F, 5.0F, 21.0F, 5.0F, new CubeDeformation(0.0F))
            .texOffs(0, 82).addBox(-2.6F, 2.0F, -3.5F, 6.0F, 9.0F, 8.0F, new CubeDeformation(0.01F)), PartPose.offset(-4.9F, -23.0F, -10.5F));

        PartDefinition left_front_leg = base.addOrReplaceChild("left_front_leg", CubeListBuilder.create().texOffs(0, 0).addBox(-2.5F, 2.0F, -2.5F, 5.0F, 21.0F, 5.0F, new CubeDeformation(0.0F))
            .texOffs(0, 99).addBox(-3.4F, 2.0F, -3.5F, 6.0F, 9.0F, 8.0F, new CubeDeformation(0.01F)), PartPose.offset(4.9F, -23.0F, -10.5F));

        PartDefinition left_hind_leg = base.addOrReplaceChild("left_hind_leg", CubeListBuilder.create().texOffs(104, 26).addBox(-3.4F, 2.0F, -3.5F, 6.0F, 12.0F, 6.0F, new CubeDeformation(0.01F))
            .texOffs(57, 18).addBox(-2.5F, 2.0F, -2.5F, 5.0F, 21.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(4.9F, -23.0F, 9.5F));

        PartDefinition right_hind_leg = base.addOrReplaceChild("right_hind_leg", CubeListBuilder.create().texOffs(48, 0).addBox(-2.6F, 2.0F, -3.5F, 6.0F, 12.0F, 6.0F, new CubeDeformation(0.01F))
            .texOffs(108, 0).addBox(-2.5F, 2.0F, -2.5F, 5.0F, 21.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(-4.9F, -23.0F, 9.5F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }

    private final ModelPart base;
    private final ModelPart body;
    private final ModelPart wool_body;
    private final ModelPart tail;
    private final ModelPart head;
    private final ModelPart wool_shey;
    private final ModelPart left_ear;
    private final ModelPart right_ear;
    private final ModelPart hump;
    private final ModelPart right_front_leg;
    private final ModelPart left_front_leg;
    private final ModelPart left_hind_leg;
    private final ModelPart right_hind_leg;

    public BactrianCamelModel(ModelPart root) {
        super(root);
        this.base = root.getChild("base");
        this.body = base.getChild("body");
        this.wool_body = body.getChild("wool_body");
        this.tail = body.getChild("tail");
        this.head = body.getChild("head");
        this.wool_shey = head.getChild("wool_shey");
        this.left_ear = head.getChild("left_ear");
        this.right_ear = head.getChild("right_ear");
        this.hump = body.getChild("hump");
        this.right_front_leg = base.getChild("right_front_leg");
        this.left_front_leg = base.getChild("left_front_leg");
        this.left_hind_leg = base.getChild("left_hind_leg");
        this.right_hind_leg = base.getChild("right_hind_leg");
    }

    private void applyHeadRotation(float netHeadYaw, float headPitch) {
        netHeadYaw = Mth.clamp(netHeadYaw, -30.0F, 30.0F);
        headPitch = Mth.clamp(headPitch, -25.0F, 45.0F);

        this.head.yRot = netHeadYaw * (float) (Math.PI / 180.0);
        this.head.xRot = headPitch * (float) (Math.PI / 180.0);
    }

    @Override
    public void setupAnim(BactrianCamel animal, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch)
    {
        this.root().getAllParts().forEach(ModelPart::resetPose);
        this.applyHeadRotation(netHeadYaw, headPitch);
        wool_body.visible = animal.hasProduct();
        wool_shey.visible = animal.hasProduct();
        this.animateWalk(BactrianCamelAnimation.CAMEL_WALK, limbSwing, limbSwingAmount, 2.0F, 2.5F);
        this.animate(animal.sitAnimationState, BactrianCamelAnimation.CAMEL_SIT, ageInTicks, 1.0F);
        this.animate(animal.sitPoseAnimationState, BactrianCamelAnimation.CAMEL_SIT_POSE, ageInTicks, 1.0F);
        this.animate(animal.sitUpAnimationState, BactrianCamelAnimation.CAMEL_STANDUP, ageInTicks, 1.0F);
        this.animate(animal.idleAnimationState, BactrianCamelAnimation.CAMEL_IDLE, ageInTicks, 1.0F);
        this.animate(animal.dashAnimationState, BactrianCamelAnimation.CAMEL_DASH, ageInTicks, 1.0F);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, int color)
    {
        if (this.young)
        {
            poseStack.pushPose();
            poseStack.scale(0.45F, 0.45F, 0.45F);
            poseStack.translate(0.0F, 1.834375F, 0.0F);
            this.root().render(poseStack, buffer, packedLight, packedOverlay, color);
            poseStack.popPose();
        } else {
            this.root().render(poseStack, buffer, packedLight, packedOverlay, color);
        }
    }
}