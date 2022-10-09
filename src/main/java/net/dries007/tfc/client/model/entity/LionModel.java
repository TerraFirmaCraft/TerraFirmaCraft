/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

// Made with Blockbench 4.2.2
// Exported for Minecraft version 1.17 with Mojang mappings
// Paste this class into your mod and generate all required imports


package net.dries007.tfc.client.model.entity;

import net.dries007.tfc.client.model.animation.AnimationChannel;
import net.dries007.tfc.client.model.animation.AnimationDefinition;
import net.dries007.tfc.common.entities.predator.FelinePredator;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;

import static net.dries007.tfc.client.model.animation.VanillaAnimations.*;

public class LionModel extends FelinePredatorModel<FelinePredator>
{
    public static LayerDefinition createBodyLayer()
    {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -15.0F, -9.0F, 8.0F, 11.0F, 14.0F, new CubeDeformation(0.0F))
            .texOffs(0, 41).addBox(-3.0F, -15.0F, 5.0F, 6.0F, 9.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 21.0F, -2.0F));

        PartDefinition maneBone = body.addOrReplaceChild("maneBone", CubeListBuilder.create().texOffs(0, 87).addBox(-5.0F, -17.0F, -10.0F, 10.0F, 6.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition neck = body.addOrReplaceChild("neck", CubeListBuilder.create(), PartPose.offset(0.0F, -13.0F, -6.0F));

        PartDefinition neck0_r1 = neck.addOrReplaceChild("neck0_r1", CubeListBuilder.create().texOffs(0, 76).addBox(-3.0F, -2.0F, -4.0F, 6.0F, 5.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.0F, -2.0F, -0.4363F, 0.0F, 0.0F));

        PartDefinition head = neck.addOrReplaceChild("head", CubeListBuilder.create().texOffs(43, 19).addBox(-4.0F, -4.0F, -7.0F, 8.0F, 8.0F, 6.0F, new CubeDeformation(0.0F))
            .texOffs(0, 7).addBox(-2.0F, -1.0F, -10.0F, 4.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -2.0F, -3.0F));

        PartDefinition maneHeadBone = head.addOrReplaceChild("maneHeadBone", CubeListBuilder.create().texOffs(0, 25).addBox(-7.0F, -17.0F, -10.0F, 14.0F, 9.0F, 7.0F, new CubeDeformation(0.0F))
            .texOffs(36, 35).addBox(-6.0F, -19.0F, -14.0F, 12.0F, 12.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 14.0F, 9.0F));

        PartDefinition nose = head.addOrReplaceChild("nose", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, -2.0F, -4.7F, 2.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.0F, -5.5F, 0.3491F, 0.0F, 0.0F));

        PartDefinition jaw = head.addOrReplaceChild("jaw", CubeListBuilder.create().texOffs(14, 60).addBox(-2.0F, -0.1743F, -5.9924F, 4.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 2.0F, -4.0F, 0.0873F, 0.0F, 0.0F));

        PartDefinition earL = head.addOrReplaceChild("earL", CubeListBuilder.create().texOffs(38, 9).addBox(-1.8237F, -2.5152F, -0.0002F, 3.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.0F, -4.0F, -3.0F, 0.0F, -0.1745F, -0.1745F));

        PartDefinition earR = head.addOrReplaceChild("earR", CubeListBuilder.create().texOffs(30, 9).addBox(-1.0F, -2.5F, 0.0F, 3.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.0F, -4.0F, -3.0F, 0.0F, 0.1745F, 0.1745F));

        PartDefinition tail = body.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(72, 19).addBox(-0.5F, -1.0F, 0.0F, 1.0F, 1.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -14.0F, 14.0F, -0.7854F, 0.0F, 0.0F));

        PartDefinition tail1 = tail.addOrReplaceChild("tail1", CubeListBuilder.create().texOffs(22, 41).addBox(-0.5F, -0.366F, -0.366F, 1.0F, 1.0F, 5.0F, new CubeDeformation(0.0F))
            .texOffs(0, 0).addBox(0.0F, -0.866F, 3.634F, 0.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
            .texOffs(7, 0).addBox(-1.0F, 0.134F, 3.634F, 2.0F, 0.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.0F, 7.0F, 1.0472F, 0.0F, 0.0F));

        PartDefinition legFR = body.addOrReplaceChild("right_front_leg", CubeListBuilder.create().texOffs(48, 53).addBox(-1.99F, -2.0F, -1.0F, 4.0F, 17.0F, 4.0F, new CubeDeformation(0.0F))
            .texOffs(65, 12).addBox(-1.99F, 15.0F, -2.0F, 4.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(-3.0F, -14.0F, -7.0F));

        PartDefinition legFL = body.addOrReplaceChild("left_front_leg", CubeListBuilder.create().texOffs(32, 53).addBox(-2.01F, -2.0F, -1.0F, 4.0F, 17.0F, 4.0F, new CubeDeformation(0.0F))
            .texOffs(66, 33).addBox(-2.0F, 15.0F, -2.0F, 4.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(3.0F, -14.0F, -7.0F));

        PartDefinition legBL = body.addOrReplaceChild("left_hind_leg", CubeListBuilder.create().texOffs(64, 64).addBox(-1.0F, 6.0F, 1.0F, 4.0F, 7.0F, 4.0F, new CubeDeformation(0.0F))
            .texOffs(0, 60).addBox(-1.0F, -2.0F, -1.0F, 4.0F, 8.0F, 6.0F, new CubeDeformation(0.0F))
            .texOffs(15, 69).addBox(-1.0F, 13.0F, 0.0F, 4.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(2.0F, -12.0F, 8.0F));

        PartDefinition legBR = body.addOrReplaceChild("right_hind_leg", CubeListBuilder.create().texOffs(64, 53).addBox(-7.0F, 6.0F, 1.0F, 4.0F, 7.0F, 4.0F, new CubeDeformation(0.0F))
            .texOffs(50, 3).addBox(-7.0F, -2.0F, -1.0F, 4.0F, 8.0F, 6.0F, new CubeDeformation(0.0F))
            .texOffs(64, 0).addBox(-7.0F, 13.0F, 0.0F, 4.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(2.0F, -12.0F, 8.0F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }

    public static final AnimationDefinition WALK = AnimationDefinition.Builder.withLength(1.0F).looping()
        .addAnimation("body", new AnimationChannel(AnimationChannel.Targets.ROTATION, rotation(0.0F, 0F, 0F, -1F), rotation(0.5F, 0F, 0F, 1F), rotation(1.0F, 0F, 0F, -1F)))
        .addAnimation("tail", new AnimationChannel(AnimationChannel.Targets.ROTATION, rotation(0.0F, -30.0F, 0F, 0F), rotation(0.25F, -30.43855F, 7.05302F, -7.10708F), rotation(0.75F, -30.47002F, -6.45856F, 6.51352F), rotation(1.0F, -30.0F, 0F, 0F)))
        .addAnimation("tail1", new AnimationChannel(AnimationChannel.Targets.ROTATION, rotation(0.0F, 50.0F, 0F, 0F), rotation(0.375F, 51.16156F, 8.64738F, 15.27269F), rotation(0.875F, 67.99327F, -4.47045F, -7.80797F), rotation(1.0F, 50.0F, 0F, 0F)))
        .addAnimation("head", new AnimationChannel(AnimationChannel.Targets.ROTATION, rotation(0.0F, 0F, 0F, 1F), rotation(0.5F, 0F, 0F, -1F), rotation(1.0F, 0F, 0F, 1F)))
        .addAnimation("right_front_leg", new AnimationChannel(AnimationChannel.Targets.ROTATION, noRotation(0.0F), rotation(0.25F, 22.5F, 0F, 0F), noRotation(0.5F), rotation(0.75F, -22.5F, 0F, 0F), noRotation(1.0F)))
        .addAnimation("left_front_leg", new AnimationChannel(AnimationChannel.Targets.ROTATION, noRotation(0.0F), rotation(0.25F, -22.5F, 0F, 0F), noRotation(0.5417F), rotation(0.75F, 22.5F, 0F, 0F), noRotation(1.0F)))
        .addAnimation("left_hind_leg", new AnimationChannel(AnimationChannel.Targets.ROTATION, noRotation(0.0F), rotation(0.1667F, -22.5F, 0F, 0F), noRotation(0.4167F), rotation(0.75F, 22.5F, 0F, 0F), noRotation(1.0F)))
        .addAnimation("right_hind_leg", new AnimationChannel(AnimationChannel.Targets.ROTATION, noRotation(0.0F), rotation(0.25F, 22.5F, 0F, 0F), noRotation(0.5F), rotation(0.75F, -22.5F, 0F, 0F), noRotation(1.0F)))
        .build();

    //TODO: Add translations to body
    public static final AnimationDefinition ATTACK = AnimationDefinition.Builder.withLength(0.4F)
        .addAnimation("body", new AnimationChannel(AnimationChannel.Targets.ROTATION, noRotation(0.0F), rotation(0.1667F, -15F, 0F, 0F), noRotation(0.4F)))
        .addAnimation("neck", new AnimationChannel(AnimationChannel.Targets.ROTATION, noRotation(0.0F), rotation(0.2083F, 15F, 0F, 0F), noRotation(0.4F)))
        .addAnimation("jaw", new AnimationChannel(AnimationChannel.Targets.ROTATION, noRotation(0.0F), rotation(0.2083F, 15F, 0F, 0F), noRotation(0.4F)))
        .addAnimation("right_front_leg", new AnimationChannel(AnimationChannel.Targets.ROTATION, noRotation(0.0F), rotation(0.0833F, -21.8848F, 23.55077F, 9.29137F), rotation(0.1667F, -58.9136F, 17.66308F, 6.96853F), noRotation(0.4F)))
        .addAnimation("left_front_leg", new AnimationChannel(AnimationChannel.Targets.ROTATION, noRotation(0.0F), rotation(0.0833F, -17.5F, 0F, -20F), rotation(0.1667F, -65.625F, 0F, -15F), noRotation(0.4F)))
        .addAnimation("tail", new AnimationChannel(AnimationChannel.Targets.ROTATION, noRotation(0.0F), rotation(0.1667F, 95F, 0F, 0F), noRotation(0.4F)))
        .addAnimation("tail1", new AnimationChannel(AnimationChannel.Targets.ROTATION, noRotation(0.0F), rotation(0.1667F, -109.17912F, 0F, 0F), rotation(0.25F, -32.51868F, 0F, 0F), noRotation(0.4F)))
        .addAnimation("left_hind_leg", new AnimationChannel(AnimationChannel.Targets.ROTATION, noRotation(0.0F), rotation(0.1667F, 15F, 0F, 0F), noRotation(0.4F)))
        .addAnimation("right_hind_leg", new AnimationChannel(AnimationChannel.Targets.ROTATION, noRotation(0.0F), rotation(0.1667F, 17.5F, 0F, 0F), noRotation(0.4F)))
        .build();

    public static final AnimationDefinition SLEEP = AnimationDefinition.Builder.withLength(2.0F).looping()
        .addAnimation("head", new AnimationChannel(AnimationChannel.Targets.ROTATION, rotation(0.0F, 15F, 0F, 0F), rotation(1.0F, 12.5F, 0F, 0F), rotation(2.0F, 15F, 0F, 0F)))
        .addAnimation("tail", new AnimationChannel(AnimationChannel.Targets.ROTATION, rotation(0.0F, -30F, 0F, 0F), rotation(2.0F, -30F, 0F, 0F)))
        .addAnimation("tail1", new AnimationChannel(AnimationChannel.Targets.ROTATION, rotation(0.0F, 2.5F, 0F, 0F), rotation(2.0F, 2.5F, 0F, 0F)))
        .addAnimation("right_front_leg", new AnimationChannel(AnimationChannel.Targets.ROTATION, rotation(0.0F, -90F, 0F, 0F), rotation(2.0F, -90F, 0F, 0F)))
        .addAnimation("left_front_leg", new AnimationChannel(AnimationChannel.Targets.ROTATION, rotation(0.0F, -90F, 0F, 0F), rotation(2.0F, -90F, 0F, 0F)))
        .addAnimation("left_hind_leg", new AnimationChannel(AnimationChannel.Targets.ROTATION, rotation(0.0F, -90F, -5F, 0F), rotation(2.0F, -90F, -5F, 0F)))
        .addAnimation("right_hind_leg", new AnimationChannel(AnimationChannel.Targets.ROTATION, rotation(0.0F, -90F, 5F, 0F), rotation(2.0F, -90F, 5F, 0F)))
        .build();

    public static final AnimationDefinition RUN = AnimationDefinition.Builder.withLength(0.5F).looping()
        .addAnimation("body", new AnimationChannel(AnimationChannel.Targets.ROTATION, noRotation(0.0F), rotation(0.1667F, -7.5F, 0F, 0F), noRotation(0.3333F), rotation(0.4167F, 4.69F, 0F, 0F), noRotation(0.5F)))
        .addAnimation("tail", new AnimationChannel(AnimationChannel.Targets.ROTATION, noRotation(0.0F), rotation(0.2083F, 60F, 0F, 0F), noRotation(0.5F)))
        .addAnimation("tail1", new AnimationChannel(AnimationChannel.Targets.ROTATION, noRotation(0.0F), rotation(0.25F, -90F, 0F, 0F), rotation(0.4167F, -2.5F, 0F, 0F), noRotation(0.5F)))
        .addAnimation("head", new AnimationChannel(AnimationChannel.Targets.ROTATION, noRotation(0.0F), rotation(0.1667F, 7.5F, 0F, 0F), noRotation(0.3333F), rotation(0.4167F, -4.69F, 0F, 0F), noRotation(0.5F)))
        .addAnimation("right_front_leg", new AnimationChannel(AnimationChannel.Targets.ROTATION, noRotation(0.0F), rotation(0.1667F, -32.5F, 0F, 0F), rotation(0.375F, 25.5F, 0F, 0F), noRotation(0.5F)))
        .addAnimation("left_front_leg", new AnimationChannel(AnimationChannel.Targets.ROTATION, noRotation(0.0F), rotation(0.125F, -32.5F, 0F, 0F), rotation(0.3333F, 25.5F, 0F, 0F), noRotation(0.5F)))
        .addAnimation("left_hind_leg", new AnimationChannel(AnimationChannel.Targets.ROTATION, noRotation(0.0F), rotation(0.125F, 40F, 0F, 0F), rotation(0.3333F, -30F, 0F, 0F), noRotation(0.5F)))
        .addAnimation("right_hind_leg", new AnimationChannel(AnimationChannel.Targets.ROTATION, noRotation(0.0F), rotation(0.1667F, 40F, 0F, 0F), rotation(0.375F, -30F, 0F, 0F), noRotation(0.5F)))
        .build();

    private final ModelPart body;
    private final ModelPart tail;
    private final ModelPart tail1;
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
    private final ModelPart maneBone;
    private final ModelPart maneHeadBone;

    public LionModel(ModelPart root)
    {
        super(root, SLEEP, WALK, RUN, ATTACK);
        this.body = root.getChild("body");
        this.neck = body.getChild("neck");
        this.tail = body.getChild("tail");
        this.tail1 = tail.getChild("tail1");
        this.head = neck.getChild("head");
        this.jaw = head.getChild("jaw");
        this.nose = head.getChild("nose");
        this.earL = head.getChild("earL");
        this.earR = head.getChild("earR");
        this.legFR = body.getChild("right_front_leg");
        this.legFL = body.getChild("left_front_leg");
        this.legBR = body.getChild("right_hind_leg");
        this.legBL = body.getChild("left_hind_leg");
        this.maneBone = body.getChild("maneBone");
        this.maneHeadBone = head.getChild("maneHeadBone");

    }

    @Override
    public void setupAnim(FelinePredator felinePredator, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch)
    {
        super.setupAnim(felinePredator, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        maneHeadBone.visible = maneBone.visible = felinePredator.isMale();
    }

    @Override
    public void setupSleeping()
    {
        body.y = 30f;

        legFR.y = -9f;
        legFR.z = -5f;
        legFL.y = -9f;
        legFL.z = -5f;

        legBL.y = -10f;
        legBL.z = 12f;
        legBR.y = -10f;
        legBR.z = 12f;
    }

    @Override
    public void setupHeadRotations(float yaw, float pitch)
    {
        head.xRot = pitch * Mth.PI / 720F;
        neck.xRot = pitch * Mth.PI / 720F;
        head.yRot = yaw * Mth.PI / 360F;
        neck.yRot = yaw * Mth.PI / 360F;
    }
}