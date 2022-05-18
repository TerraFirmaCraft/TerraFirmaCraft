/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

// Made with Blockbench 4.2.1
// Exported for Minecraft version 1.17 with Mojang mappings
// Paste this class into your mod and generate all required imports

package net.dries007.tfc.client.model.entity;

import com.google.common.collect.ImmutableMap;

import net.dries007.tfc.common.entities.predator.FelinePredator;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.dries007.tfc.client.model.Animation;
import net.dries007.tfc.client.model.Easing;

import net.minecraft.world.entity.Pose;

import java.util.Map;

public class SabertoothModel extends EntityModel<FelinePredator>
{

    public static LayerDefinition createBodyLayer()
    {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offset(0.0F, 20.0F, -2.0F));

        PartDefinition body_r1 = body.addOrReplaceChild("body_r1", CubeListBuilder.create().texOffs(0, 0).addBox(-5.0F, -9.0F, -13.0F, 10.0F, 12.0F, 13.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -6.0F, 4.0F, -0.1309F, 0.0F, 0.0F));

        PartDefinition neck = body.addOrReplaceChild("neck", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -13.75F, -7.0F, 0.6109F, 0.0F, 0.0F));

        PartDefinition neck0_r1 = neck.addOrReplaceChild("neck0_r1", CubeListBuilder.create().texOffs(27, 25).addBox(-3.0F, -2.0F, -4.0F, 6.0F, 5.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.5F, -0.866F, -0.4363F, 0.0F, 0.0F));

        PartDefinition head = neck.addOrReplaceChild("head", CubeListBuilder.create().texOffs(33, 0).addBox(-3.5F, -4.0F, -6.0F, 7.0F, 7.0F, 5.0F, new CubeDeformation(0.0F))
            .texOffs(64, 11).addBox(-2.0F, -1.0F, -10.0F, 4.0F, 3.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.25F, -1.866F, -0.5672F, 0.0F, 0.0F));

        PartDefinition toothL_r1 = head.addOrReplaceChild("toothL_r1", CubeListBuilder.create().texOffs(7, 7).addBox(-0.25F, 0.0F, 0.75F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(0, 8).addBox(-2.75F, 0.0F, 0.75F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, 2.0F, -10.0F, 0.2182F, 0.0F, 0.0F));

        PartDefinition nose = head.addOrReplaceChild("nose", CubeListBuilder.create().texOffs(64, 18).addBox(-1.0F, -2.0F, -4.7F, 2.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.0F, -5.5F, 0.3491F, 0.0F, 0.0F));

        PartDefinition jaw = head.addOrReplaceChild("jaw", CubeListBuilder.create().texOffs(39, 64).addBox(-1.5F, 1.1F, -4.5F, 3.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.75F, -5.0F));

        PartDefinition earL = head.addOrReplaceChild("earL", CubeListBuilder.create().texOffs(0, 4).addBox(-1.8237F, -1.5152F, -0.0002F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.0F, -4.0F, -3.0F, 0.0F, -0.1745F, -0.1745F));

        PartDefinition earR = head.addOrReplaceChild("earR", CubeListBuilder.create().texOffs(0, 0).addBox(0.0F, -1.5F, 0.0F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.0F, -4.0F, -3.0F, 0.0F, 0.1745F, 0.1745F));

        PartDefinition legFR = body.addOrReplaceChild("legFR", CubeListBuilder.create().texOffs(52, 25).addBox(-2.0F, 5.0F, -2.0F, 4.0F, 9.0F, 4.0F, new CubeDeformation(0.0F))
            .texOffs(58, 38).addBox(-2.0F, 14.0F, -3.0F, 4.0F, 2.0F, 5.0F, new CubeDeformation(0.0F))
            .texOffs(46, 12).addBox(-2.0F, -3.0F, -3.0F, 4.0F, 8.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(-4.0F, -12.0F, -4.0F));

        PartDefinition legFL = body.addOrReplaceChild("legFL", CubeListBuilder.create().texOffs(38, 51).addBox(-2.0F, 5.0F, -2.0F, 4.0F, 9.0F, 4.0F, new CubeDeformation(0.0F))
            .texOffs(54, 58).addBox(-2.0F, 14.0F, -3.0F, 4.0F, 2.0F, 5.0F, new CubeDeformation(0.0F))
            .texOffs(20, 45).addBox(-2.0F, -3.0F, -3.0F, 4.0F, 8.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(4.0F, -12.0F, -4.0F));

        PartDefinition rear = body.addOrReplaceChild("rear", CubeListBuilder.create().texOffs(0, 25).addBox(-4.0F, 0.0F, 0.0F, 8.0F, 9.0F, 11.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -15.0F, 4.0F));

        PartDefinition tail = rear.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(27, 59).addBox(-1.0F, -1.0F, 0.0F, 2.0F, 2.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 1.0F, 10.0F, -0.7854F, 0.0F, 0.0F));

        PartDefinition legBL = rear.addOrReplaceChild("legBL", CubeListBuilder.create().texOffs(57, 0).addBox(-2.0F, 5.0F, -2.0F, 4.0F, 7.0F, 4.0F, new CubeDeformation(0.0F))
            .texOffs(0, 45).addBox(-2.0F, -4.0F, -4.0F, 4.0F, 9.0F, 6.0F, new CubeDeformation(0.0F))
            .texOffs(0, 60).addBox(-2.0F, 12.0F, -3.0F, 4.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(3.0F, 5.0F, 8.0F));

        PartDefinition legBR = rear.addOrReplaceChild("legBR", CubeListBuilder.create().texOffs(54, 47).addBox(-2.0F, 5.0F, -2.0F, 4.0F, 7.0F, 4.0F, new CubeDeformation(0.0F))
            .texOffs(38, 36).addBox(-2.0F, -4.0F, -4.0F, 4.0F, 9.0F, 6.0F, new CubeDeformation(0.0F))
            .texOffs(15, 58).addBox(-2.0F, 12.0F, -3.0F, 4.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(-3.0F, 5.0F, 8.0F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }

    public static final Animation WALK = new Animation.Builder(1.0F)
        .bone("body", new Animation.Bone.Builder(Easing.LINEAR).rotation(0.0F, 0F, 0F, -1F).rotation(0.5F, 0F, 0F, 1F).rotation(1.0F, 0F, 0F, -1F).build())
        .bone("tail", new Animation.Bone.Builder(Easing.LINEAR).rotation(0.0F, -30.0F, 0F, 0F).rotation(0.25F, -30.43855F, 7.05302F, -7.10708F).rotation(0.75F, -30.47002F, -6.45856F, 6.51352F).rotation(1.0F, -30.0F, 0F, 0F).build())
        .bone("head", new Animation.Bone.Builder(Easing.LINEAR).rotation(0.0F, 0F, 0F, 1F).rotation(0.5F, 0F, 0F, -1F).rotation(1.0F, 0F, 0F, 1F).build())
        .bone("legFR", new Animation.Bone.Builder(Easing.LINEAR).noRotation(0.0F).rotation(0.25F, 22.5F, 0F, 0F).noRotation(0.5F).rotation(0.75F, -22.5F, 0F, 0F).noRotation(1.0F).build())
        .bone("legFL", new Animation.Bone.Builder(Easing.LINEAR).noRotation(0.0F).rotation(0.25F, -22.5F, 0F, 0F).noRotation(0.5417F).rotation(0.75F, 22.5F, 0F, 0F).noRotation(1.0F).build())
        .bone("legBL", new Animation.Bone.Builder(Easing.LINEAR).noRotation(0.0F).rotation(0.1667F, -22.5F, 0F, 0F).noRotation(0.4167F).rotation(0.75F, 22.5F, 0F, 0F).noRotation(1.0F).build())
        .bone("legBR", new Animation.Bone.Builder(Easing.LINEAR).noRotation(0.0F).rotation(0.25F, 22.5F, 0F, 0F).noRotation(0.5F).rotation(0.75F, -22.5F, 0F, 0F).noRotation(1.0F).build())
        .build();

    //TODO: Add translations to body
    public static final Animation ATTACK = new Animation.Builder(0.4F)
        .bone("body", new Animation.Bone.Builder(Easing.LINEAR).noRotation(0.0F).rotation(0.1667F, -15F, 0F, 0F).noRotation(0.4F).build())
        .bone("neck", new Animation.Bone.Builder(Easing.LINEAR).noRotation(0.0F).rotation(0.2083F, 15F, 0F, 0F).noRotation(0.4F).build())
        .bone("jaw", new Animation.Bone.Builder(Easing.LINEAR).noRotation(0.0F).rotation(0.2083F, 15F, 0F, 0F).noRotation(0.4F).build())
        .bone("legFR", new Animation.Bone.Builder(Easing.LINEAR).noRotation(0.0F).rotation(0.0833F, -21.8848F, 23.55077F, 9.29137F).rotation(0.1667F, -58.9136F, 17.66308F, 6.96853F).noRotation(0.4F).build())
        .bone("legFL", new Animation.Bone.Builder(Easing.LINEAR).noRotation(0.0F).rotation(0.0833F, -17.5F, 0F, -20F).rotation(0.1667F, -65.625F, 0F, -15F).noRotation(0.4F).build())
        .bone("rear", new Animation.Bone.Builder(Easing.LINEAR).noRotation(0.0F).noRotation(0.4F).build())
        .bone("tail", new Animation.Bone.Builder(Easing.LINEAR).noRotation(0.0F).rotation(0.1667F, 95F, 0F, 0F).noRotation(0.4F).build())
        .bone("legBL", new Animation.Bone.Builder(Easing.LINEAR).noRotation(0.0F).rotation(0.1667F, 15F, 0F, 0F).noRotation(0.4F).build())
        .bone("legBR", new Animation.Bone.Builder(Easing.LINEAR).noRotation(0.0F).rotation(0.1667F, 17.5F, 0F, 0F).noRotation(0.4F).build())
        .build();

    public static final Animation SLEEP = new Animation.Builder(3.0F)
        .bone("body", new Animation.Bone.Builder(Easing.LINEAR).rotation(0.0F, 2.5F, 0F, 0F).rotation(1.5F, 5F, 0F, 0F).rotation(3.0F, 2.5F, 0F, 0F).build())
        .bone("neck", new Animation.Bone.Builder(Easing.LINEAR).rotation(0.0F, 10F, 0F, 0F).rotation(3.0F, 10F, 0F, 0F).build())
        .bone("head", new Animation.Bone.Builder(Easing.LINEAR).rotation(0.0F, 32.5F, 0F, 0F).rotation(3.0F, 32.5F, 0F, 0F).build())
        .bone("legFR", new Animation.Bone.Builder(Easing.LINEAR).rotation(0.0F, -82.49294F, -2.4786F, -0.32652F).rotation(1.5F, -84.99294F, -2.4786F, -0.32652F).rotation(3.0F, -82.49294F, -2.4786F, -0.32652F).build())
        .bone("legFL", new Animation.Bone.Builder(Easing.LINEAR).rotation(0.0F, -82.47168F, 4.95712F, 0.65426F).rotation(1.5F, -84.97168F, 4.95712F, 0.65426F).rotation(3.0F, -82.47168F, 4.95712F, 0.65426F).build())
        .bone("rear", new Animation.Bone.Builder(Easing.LINEAR).rotation(0.0F, -17.5F, 0F, 0F).rotation(3.0F, -17.5F, 0F, 0F).build())
        .bone("legBL", new Animation.Bone.Builder(Easing.LINEAR).rotation(0.0F, -77.25431F, -12.26424F, -2.42F).rotation(1.5F, -79.75431F, -12.26424F, -2.42F).rotation(3.0F, -77.25431F, -12.26424F, -2.42F).build())
        .bone("legBR", new Animation.Bone.Builder(Easing.LINEAR).rotation(0.0F, -77.20638F, 12.19908F, 2.74715F).rotation(1.5F, -84.97168F, 4.95712F, 0.65426F).rotation(3.0F, -77.20638F, 12.19908F, 2.74715F).build())
        .bone("earR", new Animation.Bone.Builder(Easing.LINEAR).noRotation(0.0F).noRotation(2.875F).rotation(2.9583F, 26.44409F, -5.44303F, 11.3489F).noRotation(3.0F).build())
        .build();

    public static final Animation RUN = new Animation.Builder(0.5F)
        .bone("body", new Animation.Bone.Builder(Easing.LINEAR).noRotation(0.0F).rotation(0.1667F, -7.5F, 0F, 0F).noRotation(0.3333F).rotation(0.4167F, 4.69F, 0F, 0F).noRotation(0.5F).build())
        .bone("tail", new Animation.Bone.Builder(Easing.LINEAR).noRotation(0.0F).rotation(0.2083F, 60F, 0F, 0F).noRotation(0.5F).build())
        .bone("head", new Animation.Bone.Builder(Easing.LINEAR).noRotation(0.0F).rotation(0.1667F, 7.5F, 0F, 0F).noRotation(0.3333F).rotation(0.4167F, -4.69F, 0F, 0F).noRotation(0.5F).build())
        .bone("legFR", new Animation.Bone.Builder(Easing.LINEAR).noRotation(0.0F).rotation(0.1667F, -32.5F, 0F, 0F).rotation(0.375F, 25.5F, 0F, 0F).noRotation(0.5F).build())
        .bone("legFL", new Animation.Bone.Builder(Easing.LINEAR).noRotation(0.0F).rotation(0.125F, -32.5F, 0F, 0F).rotation(0.3333F, 25.5F, 0F, 0F).noRotation(0.5F).build())
        .bone("legBL", new Animation.Bone.Builder(Easing.LINEAR).noRotation(0.0F).rotation(0.125F, 40F, 0F, 0F).rotation(0.3333F, -30F, 0F, 0F).noRotation(0.5F).build())
        .bone("legBR", new Animation.Bone.Builder(Easing.LINEAR).noRotation(0.0F).rotation(0.1667F, 40F, 0F, 0F).rotation(0.375F, -30F, 0F, 0F).noRotation(0.5F).build())
        .build();


    public final Map<String, ModelPart> parts;
    public final Map<ModelPart, PartPose> defaults;

    private final ModelPart body;
    private final ModelPart tail;
    private final ModelPart neck;
    private final ModelPart head;
    private final ModelPart legFR;
    private final ModelPart legBR;
    private final ModelPart legFL;
    private final ModelPart legBL;
    private final ModelPart earL;
    private final ModelPart earR;
    private final ModelPart jaw;
    private final ModelPart nose;
    private final ModelPart rear;

    private float prevLimbSwing;

    public SabertoothModel(ModelPart root)
    {
        this.body = root.getChild("body");
        this.neck = body.getChild("neck");
        this.rear = body.getChild("rear");
        this.tail = rear.getChild("tail");
        this.head = neck.getChild("head");
        this.jaw = head.getChild("jaw");
        this.nose = head.getChild("nose");
        this.earL = head.getChild("earL");
        this.earR = head.getChild("earR");
        this.legFR = body.getChild("legFR");
        this.legFL = body.getChild("legFL");
        this.legBR = rear.getChild("legBR");
        this.legBL = rear.getChild("legBL");

        parts = new ImmutableMap.Builder<String, ModelPart>().put("body", body).put("tail", tail).put("head", head)
            .put("jaw", jaw).put("earL", earL).put("earR", earR).put("legFR", legFR).put("legFL", legFL).put("legBR", legBR)
            .put("legBL", legBL).put("nose", nose).put("neck", neck).put("rear", rear).build();
        defaults = Animation.initDefaults(parts);
    }

    @Override
    public void setupAnim(FelinePredator felinePredator, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch)
    {
        defaults.forEach(ModelPart::loadPose);

        felinePredator.setLimbSwing(Math.min(Math.max((limbSwing-prevLimbSwing)*10F, 0.4F),1.4F));
        prevLimbSwing = limbSwing;

        if (felinePredator.isSleeping())
        {
            body.y = 28f;

            legFR.y = -7f;
            legFR.z = -4f;
            legFL.y = -7f;
            legFL.z = -4f;

            legBL.y = 8f;
            legBL.z = 7f;
            legBR.y = 8f;
            legBR.z = 7f;
            SLEEP.tick(parts, ageInTicks);
        }
        else
        {
            if (felinePredator.getAttackTicks() > 0)
            {
                ATTACK.tick(parts, ageInTicks);
            }

            else if (felinePredator.walkProgress > 0 || felinePredator.isMoving())

            {
                if (felinePredator.isAggressive())
                {
                    RUN.tick(parts, ageInTicks);
                }
                else
                {
                    WALK.tick(parts, felinePredator.walkProgress);
                }
            }

            head.xRot = netHeadYaw * Mth.PI / 720F;
            neck.xRot = netHeadYaw * Mth.PI / 720F;
            head.yRot = headPitch * Mth.PI / 360F;
            neck.yRot = headPitch * Mth.PI / 360F;
        }
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha)
    {
        body.render(poseStack, buffer, packedLight, packedOverlay);
    }
}