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


public class CougarModel extends EntityModel<FelinePredator>
{

    public static LayerDefinition createBodyLayer()
    {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-3.5F, -15.0F, -8.0F, 7.0F, 8.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, -2.0F));

        PartDefinition neck = body.addOrReplaceChild("neck", CubeListBuilder.create(), PartPose.offset(0.0F, -14.0F, -7.0F));

        PartDefinition neck0_r1 = neck.addOrReplaceChild("neck0_r1", CubeListBuilder.create().texOffs(0, 36).addBox(-2.0F, -1.5F, -3.0F, 4.0F, 5.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, -1.0F, -0.4363F, 0.0F, 0.0F));

        PartDefinition head = neck.addOrReplaceChild("head", CubeListBuilder.create().texOffs(26, 0).addBox(-3.0F, -4.0F, -6.0F, 6.0F, 6.0F, 5.0F, new CubeDeformation(0.0F))
            .texOffs(0, 8).addBox(-2.0F, -1.0F, -8.0F, 4.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 1.0F, -1.0F));

        PartDefinition nose = head.addOrReplaceChild("nose", CubeListBuilder.create().texOffs(12, 48).addBox(-1.0F, -1.0F, -2.5F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -1.0F, -5.5F));

        PartDefinition jaw = head.addOrReplaceChild("jaw", CubeListBuilder.create().texOffs(13, 36).addBox(-1.0F, -1.1743F, -3.9924F, 2.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 2.0F, -4.0F, 0.0873F, 0.0F, 0.0F));

        PartDefinition earL = head.addOrReplaceChild("earL", CubeListBuilder.create().texOffs(18, 21).addBox(-1.0F, -1.5F, 0.0F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.0F, -4.0F, -3.0F, 0.0F, -0.1745F, -0.1745F));

        PartDefinition earR = head.addOrReplaceChild("earR", CubeListBuilder.create().texOffs(0, 21).addBox(-1.0F, -1.5F, 0.0F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.0F, -4.0F, -3.0F, 0.0F, 0.1745F, 0.1745F));

        PartDefinition legFR = body.addOrReplaceChild("legFR", CubeListBuilder.create().texOffs(35, 35).addBox(-1.0F, -2.0F, 0.0F, 3.0F, 14.0F, 3.0F, new CubeDeformation(0.0F))
            .texOffs(47, 38).addBox(-1.0F, 12.0F, -1.0F, 3.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-3.0F, -14.0F, -7.0F));

        PartDefinition legFL = body.addOrReplaceChild("legFL", CubeListBuilder.create().texOffs(26, 21).addBox(-2.0F, -2.0F, 0.0F, 3.0F, 14.0F, 3.0F, new CubeDeformation(0.0F))
            .texOffs(47, 44).addBox(-2.0F, 12.0F, -1.0F, 3.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(3.0F, -14.0F, -7.0F));

        PartDefinition rear = body.addOrReplaceChild("rear", CubeListBuilder.create().texOffs(0, 21).addBox(-2.5F, 0.0F, 0.0F, 5.0F, 7.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -15.0F, 4.0F));

        PartDefinition tail = rear.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(38, 23).addBox(-2.0F, -1.5F, 0.0F, 2.0F, 2.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, 1.0F, 7.0F, -0.7854F, 0.0F, 0.0F));

        PartDefinition tail1 = tail.addOrReplaceChild("tail1", CubeListBuilder.create().texOffs(0, 46).addBox(-1.0F, -0.5F, -0.366F, 2.0F, 2.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, -1.0F, 7.0F, 1.0472F, 0.0F, 0.0F));

        PartDefinition legBL = rear.addOrReplaceChild("legBL", CubeListBuilder.create().texOffs(49, 6).addBox(-1.0F, 5.0F, 1.0F, 3.0F, 5.0F, 3.0F, new CubeDeformation(0.0F))
            .texOffs(18, 38).addBox(-1.0F, -2.0F, -1.0F, 3.0F, 7.0F, 5.0F, new CubeDeformation(0.0F))
            .texOffs(48, 0).addBox(-1.0F, 10.0F, 0.0F, 3.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(2.0F, 3.0F, 3.0F));

        PartDefinition legBR = rear.addOrReplaceChild("legBR", CubeListBuilder.create().texOffs(0, 0).addBox(-6.0F, 5.0F, 2.0F, 3.0F, 5.0F, 3.0F, new CubeDeformation(0.0F))
            .texOffs(38, 11).addBox(-6.0F, -2.0F, 0.0F, 3.0F, 7.0F, 5.0F, new CubeDeformation(0.0F))
            .texOffs(44, 32).addBox(-6.0F, 10.0F, 1.0F, 3.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(2.0F, 3.0F, 2.0F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    public static final Animation WALK = new Animation.Builder(1.0F)
        .bone("body", new Animation.Bone.Builder(Easing.LINEAR).rotation(0.0F, 0F, 0F, -1F).rotation(0.5F, 0F, 0F, 1F).rotation(1.0F, 0F, 0F, -1F).build())
        .bone("tail", new Animation.Bone.Builder(Easing.LINEAR).rotation(0.0F, -30.0F, 0F, 0F).rotation(0.25F, -30.43855F, 7.05302F, -7.10708F).rotation(0.75F, -30.47002F, -6.45856F, 6.51352F).rotation(1.0F, -30.0F, 0F, 0F).build())
        .bone("tail1", new Animation.Bone.Builder(Easing.LINEAR).rotation(0.0F, 50.0F, 0F, 0F).rotation(0.375F, 51.16156F, 8.64738F, 15.27269F).rotation(0.875F, 67.99327F, -4.47045F, -7.80797F).rotation(1.0F, 50.0F, 0F, 0F).build())
        .bone("head", new Animation.Bone.Builder(Easing.LINEAR).rotation(0.0F, 0F, 0F, 1F).rotation(0.5F, 0F, 0F, -1F).rotation(1.0F, 0F, 0F, 1F).build())
        .bone("legFR", new Animation.Bone.Builder(Easing.LINEAR).noRotation(0.0F).rotation(0.25F, 22.5F, 0F, 0F).noRotation(0.5F).rotation(0.75F, -22.5F, 0F, 0F).noRotation(1.0F).build())
        .bone("legFL", new Animation.Bone.Builder(Easing.LINEAR).noRotation(0.0F).rotation(0.25F, -22.5F, 0F, 0F).noRotation(0.5417F).rotation(0.75F, 22.5F, 0F, 0F).noRotation(1.0F).build())
        .bone("legBL", new Animation.Bone.Builder(Easing.LINEAR).noRotation(0.0F).rotation(0.1667F, -22.5F, 0F, 0F).noRotation(0.4167F).rotation(0.75F, 22.5F, 0F, 0F).noRotation(1.0F).build())
        .bone("legBR", new Animation.Bone.Builder(Easing.LINEAR).noRotation(0.0F).rotation(0.25F, 22.5F, 0F, 0F).noRotation(0.5F).rotation(0.75F, -22.5F, 0F, 0F).noRotation(1.0F).build())
        .build();

    public static final Animation RUN = new Animation.Builder(0.5F)
        .bone("body", new Animation.Bone.Builder(Easing.LINEAR).noRotation(0.0F).rotation(0.1667F, -7.5F, 0F, 0F).noRotation(0.3333F).rotation(0.4167F, 4.69F, 0F, 0F).noRotation(0.5F).build())
        .bone("tail", new Animation.Bone.Builder(Easing.LINEAR).noRotation(0.0F).rotation(0.2083F, 60F, 0F, 0F).noRotation(0.5F).build())
        .bone("tail1", new Animation.Bone.Builder(Easing.LINEAR).noRotation(0.0F).rotation(0.25F, -90F, 0F, 0F).rotation(0.4167F, -2.5F, 0F, 0F).noRotation(0.5F).build())
        .bone("head", new Animation.Bone.Builder(Easing.LINEAR).noRotation(0.0F).rotation(0.1667F, 7.5F, 0F, 0F).noRotation(0.3333F).rotation(0.4167F, -4.69F, 0F, 0F).noRotation(0.5F).build())
        .bone("legFR", new Animation.Bone.Builder(Easing.LINEAR).noRotation(0.0F).rotation(0.1667F, -32.5F, 0F, 0F).rotation(0.375F, 25.5F, 0F, 0F).noRotation(0.5F).build())
        .bone("legFL", new Animation.Bone.Builder(Easing.LINEAR).noRotation(0.0F).rotation(0.125F, -32.5F, 0F, 0F).rotation(0.3333F, 25.5F, 0F, 0F).noRotation(0.5F).build())
        .bone("legBL", new Animation.Bone.Builder(Easing.LINEAR).noRotation(0.0F).rotation(0.125F, 40F, 0F, 0F).rotation(0.3333F, -30F, 0F, 0F).noRotation(0.5F).build())
        .bone("legBR", new Animation.Bone.Builder(Easing.LINEAR).noRotation(0.0F).rotation(0.1667F, 40F, 0F, 0F).rotation(0.375F, -30F, 0F, 0F).noRotation(0.5F).build())
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
        .bone("tail1", new Animation.Bone.Builder(Easing.LINEAR).noRotation(0.0F).rotation(0.1667F, -109.17912F, 0F, 0F).rotation(0.25F, -32.51868F, 0F, 0F).noRotation(0.4F).build())
        .bone("legBL", new Animation.Bone.Builder(Easing.LINEAR).noRotation(0.0F).rotation(0.1667F, 15F, 0F, 0F).noRotation(0.4F).build())
        .bone("legBR", new Animation.Bone.Builder(Easing.LINEAR).noRotation(0.0F).rotation(0.1667F, 17.5F, 0F, 0F).noRotation(0.4F).build())
        .build();

    public static final Animation SLEEP = new Animation.Builder(2.0F)
        .bone("body", new Animation.Bone.Builder(Easing.LINEAR).rotation(0.0F, 5F, 0F, 72.5F).rotation(0.5F, 5F, 0F, 72.5F).rotation(2.0F, 5F, 0F, 72.5F).build()) //.translation(0.0F,-7F,0F,2F).translation(1.0F,-7F,0F,2F)
        .bone("neck", new Animation.Bone.Builder(Easing.LINEAR).rotation(0.0F, 90F, 0F, 0F).rotation(2.0F, 90F, 0F, 0F).build()) //.translation(0.0F,0F,-2F,0F).translation(1.0F,0F,-2F,0F)
        .bone("tail", new Animation.Bone.Builder(Easing.LINEAR).rotation(0.0F, -35.17255F, 2.54237F, -1.37822F).rotation(0.0833F, -34.83923F, -11.59079F, 19.36245F).rotation(0.5F, -35.17255F, 2.54237F, -1.37822F).rotation(0.75F, -35.28937F, 4F, -3.43513F).rotation(2.0F, -35.17255F, 2.54237F, -1.37822F).build())
        .bone("tail1", new Animation.Bone.Builder(Easing.LINEAR).rotation(0.0F, 14F, -12.27659F, 12.56853F).rotation(0.0833F, -13F, 14F, -14.43276F).rotation(0.1667F, 11F, -16.87977F, 18.66145F).rotation(0.5F, -14F, -12.27659F, 12.56853F).rotation(0.75F, -15F, -8.80349F, 8.90928F).rotation(2.0F, -14F, -12.27659F, 12.56853F).build())
        .bone("legFR", new Animation.Bone.Builder(Easing.LINEAR).rotation(0.0F, 0.22267F, 4.02155F, 6.33586F).rotation(0.5F, 0.22267F, 4.02155F, 6.33586F).rotation(2.0F, 0.22267F, 4.02155F, 6.33586F).build())
        .bone("legFL", new Animation.Bone.Builder(Easing.LINEAR).rotation(0.0F, 15F, 0F, 12.5F).rotation(0.5F, 15F, 0F, 12.5F).rotation(2.0F, 15F, 0F, 12.5F).build())
        .bone("legBL", new Animation.Bone.Builder(Easing.LINEAR).rotation(0.0F, 12.5F, 0F, 12.5F).rotation(0.5F, 12.5F, 0F, 12.5F).rotation(2.0F, 12.5F, 0F, 12.5F).build())
        .bone("rear", new Animation.Bone.Builder(Easing.LINEAR).rotation(0.0F, -45F, 0F, 0F).rotation(0.5F, -45F, 0F, 0F).rotation(2.0F, -45F, 0F, 0F).build())
        .build();

    public static final Animation CROUCH = new Animation.Builder(1.0F)
        .bone("body", new Animation.Bone.Builder(Easing.LINEAR).rotation(0.0F, 20F, 0F, -1F).rotation(0.5F, 18F, 0F, 1F).rotation(1.0F, 20F, 0F, -1F).build())
        .bone("tail", new Animation.Bone.Builder(Easing.LINEAR).rotation(0.0F, -55F, 0F, 0F).rotation(0.25F, -45.4385F, 7.05302F, -7.10708F).rotation(0.75F, -45.47F, -6.45856F, 6.51352F).rotation(1.0F, -55F, 0F, 0F).build())
        .bone("tail1", new Animation.Bone.Builder(Easing.LINEAR).rotation(0.0F, 60F, 0F, 0F).rotation(0.375F, 61.1616F, 8.64738F, 15.27269F).rotation(0.875F, 77.9933F, -4.47045F, -7.80797F).rotation(1.0F, 60F, 0F, 0F).build())
        .bone("head", new Animation.Bone.Builder(Easing.LINEAR).rotation(0.0F, 0F, 0F, 1F).rotation(0.5F, 0F, 0F, -1F).rotation(1.0F, 0F, 0F, 1F).build())
        .bone("legFR", new Animation.Bone.Builder(Easing.LINEAR).rotation(0.0F, -75F, 0F, 0F).rotation(0.25F, -65F, 0F, 0F).rotation(0.5F, -75F, 0F, 0F).rotation(0.75F, -80F, 0F, 0F).rotation(1.0F, -75F, 0F, 0F).build())
        .bone("legFL", new Animation.Bone.Builder(Easing.LINEAR).rotation(0.0F, -75F, 0F, 0F).rotation(0.25F, -80F, 0F, 0F).rotation(0.5417F, -75F, 0F, 0F).rotation(0.75F, -65F, 0F, 0F).rotation(1.0F, -75F, 0F, 0F).build())
        .bone("legBL", new Animation.Bone.Builder(Easing.LINEAR).rotation(0.0F, -10F, 0F, 0F).rotation(0.1667F, -22.5F, 0F, 0F).rotation(0.4167F, -10F, 0F, 0F).rotation(0.75F, 2.5F, 0F, 0F).rotation(1.0F, -10F, 0F, 0F).build())
        .bone("legBR", new Animation.Bone.Builder(Easing.LINEAR).rotation(0.0F, -10F, 0F, 0F).rotation(0.25F, 2.5F, 0F, 0F).rotation(0.5F, -10F, 0F, 0F).rotation(0.75F, -22.5F, 0F, 0F).rotation(1.0F, -10F, 0F, 0F).build())
        .bone("neck", new Animation.Bone.Builder(Easing.LINEAR).rotation(0.0F, -7.5F, 0F, 0F).rotation(0.5F, -5.5F, 0F, 0F).rotation(1.0F, -7.5F, 0F, 0F).build())
        .build();

    public final Map<String, ModelPart> parts;
    public final Map<ModelPart, PartPose> defaults;

    private final ModelPart body;
    private final ModelPart tail1;
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
    
    public CougarModel(ModelPart root)
    {
        this.body = root.getChild("body");
        this.neck = body.getChild("neck");
        this.rear = body.getChild("rear");
        this.tail = rear.getChild("tail");
        this.tail1 = tail.getChild("tail1");
        this.head = neck.getChild("head");
        this.jaw = head.getChild("jaw");
        this.nose = head.getChild("nose");
        this.earL = head.getChild("earL");
        this.earR = head.getChild("earR");
        this.legFR = body.getChild("legFR");
        this.legFL = body.getChild("legFL");
        this.legBR = rear.getChild("legBR");
        this.legBL = rear.getChild("legBL");

        parts = new ImmutableMap.Builder<String, ModelPart>().put("body", body).put("tail", tail).put("tail1", tail1).put("head", head)
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
            body.x = -8f;
            body.z = -2f;
            SLEEP.tick(parts, ageInTicks);
        }
        else
        {
            if (felinePredator.getAttackTicks() > 0)
            {
                ATTACK.tick(parts, ageInTicks);
            }
            else if (felinePredator.getPose() == Pose.CROUCHING)
            {
                body.y = 28f;
                CROUCH.tick(parts, felinePredator.walkProgress);
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