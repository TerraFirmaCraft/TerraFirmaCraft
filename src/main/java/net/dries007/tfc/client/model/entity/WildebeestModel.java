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
import net.minecraft.client.animation.AnimationChannel;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.animation.Keyframe;
import net.minecraft.client.animation.KeyframeAnimations;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

import net.dries007.tfc.common.entities.EntityHelpers;
import net.dries007.tfc.common.entities.prey.RammingPrey;

public class WildebeestModel extends HierarchicalAnimatedModel<RammingPrey>
{
    private final ModelPart body;
    private final ModelPart head;
    private final ModelPart neck;
    private final ModelPart rightHindLeg;
    private final ModelPart leftHindLeg;
    private final ModelPart rightFrontLeg;
    private final ModelPart leftFrontLeg;

    public WildebeestModel(ModelPart root)
    {
        super(root);
        this.body = root.getChild("body");
        this.neck = body.getChild("neck");
        this.head = neck.getChild("head");
        this.rightFrontLeg = body.getChild("legFR");
        this.leftFrontLeg = body.getChild("legFL");
        this.rightHindLeg = body.getChild("legBR");
        this.leftHindLeg = body.getChild("legBL");

    }

    public static LayerDefinition createBodyLayer()
    {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-3.0F, -19.0F, -2.0F, 6.0F, 11.0F, 11.0F, new CubeDeformation(0.0F))
            .texOffs(0, 22).addBox(-4.0F, -20.0F, -10.0F, 8.0F, 12.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition neck = body.addOrReplaceChild("neck", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -14.0F, -7.75F, 0.0436F, 0.0F, 0.0F));

        PartDefinition beard_r1 = neck.addOrReplaceChild("beard_r1", CubeListBuilder.create().texOffs(51, 0).addBox(0.0F, -8.0F, -6.0F, 0.0F, 9.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 3.0408F, 0.8682F, 0.6545F, 0.0F, 0.0F));

        PartDefinition neck0_r1 = neck.addOrReplaceChild("neck0_r1", CubeListBuilder.create().texOffs(32, 27).addBox(-1.5872F, -4.4133F, -3.2175F, 3.0F, 9.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0872F, 0.0446F, -1.1318F, 0.6545F, 0.0F, 0.0F));

        PartDefinition head = neck.addOrReplaceChild("head", CubeListBuilder.create().texOffs(25, 13).addBox(-2.0872F, -3.0038F, -7.0F, 4.0F, 5.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0872F, -3.9554F, -4.1318F, 1.0036F, 0.0F, 0.0F));

        PartDefinition hornL = head.addOrReplaceChild("hornL", CubeListBuilder.create(), PartPose.offsetAndRotation(1.8257F, -3.75F, 0.0F, -0.0873F, -0.0175F, 0.1396F));

        PartDefinition tip_r1 = hornL.addOrReplaceChild("tip_r1", CubeListBuilder.create().texOffs(7, 0).mirror().addBox(1.0F, -2.5F, 0.25F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.3927F));

        PartDefinition base_r1 = hornL.addOrReplaceChild("base_r1", CubeListBuilder.create().texOffs(0, 7).mirror().addBox(0.0F, -2.0F, 0.0F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.1781F));

        PartDefinition hornR = head.addOrReplaceChild("hornR", CubeListBuilder.create(), PartPose.offsetAndRotation(-2.0F, -3.75F, 0.0F, -0.0873F, 0.0175F, -0.1396F));

        PartDefinition tip_r2 = hornR.addOrReplaceChild("tip_r2", CubeListBuilder.create().texOffs(7, 0).addBox(-2.0F, -2.5F, 0.25F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.3927F));

        PartDefinition base_r2 = hornR.addOrReplaceChild("base_r2", CubeListBuilder.create().texOffs(0, 7).addBox(-1.0F, -2.0F, 0.0F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -1.1781F));

        PartDefinition tail = body.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(23, 0).addBox(-0.5F, -1.0F, 0.0F, 1.0F, 1.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -18.0F, 8.5F, -1.0472F, 0.0F, 0.0F));

        PartDefinition tail2 = tail.addOrReplaceChild("tail2", CubeListBuilder.create().texOffs(23, 0).addBox(0.0F, 0.0F, 0.0F, 0.0F, 2.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.0F, 4.0F, -0.1745F, 0.0F, 0.0F));

        PartDefinition legBR = body.addOrReplaceChild("legBR", CubeListBuilder.create().texOffs(48, 44).addBox(-1.0F, -2.0F, -2.0F, 2.0F, 7.0F, 4.0F, new CubeDeformation(0.0F))
            .texOffs(24, 42).addBox(-1.0F, 5.0F, -1.0F, 3.0F, 10.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(-4.0F, -15.0F, 5.0F));

        PartDefinition legBL = body.addOrReplaceChild("legBL", CubeListBuilder.create().texOffs(39, 0).addBox(-1.0F, -2.0F, -2.0F, 2.0F, 7.0F, 4.0F, new CubeDeformation(0.0F))
            .texOffs(36, 42).addBox(-2.0F, 5.0F, -1.0F, 3.0F, 10.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(4.0F, -15.0F, 5.0F));

        PartDefinition legFR = body.addOrReplaceChild("legFR", CubeListBuilder.create().texOffs(12, 42).addBox(-1.0F, 1.0F, -1.0F, 3.0F, 13.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(-4.0F, -14.0F, -6.0F));

        PartDefinition legFL = body.addOrReplaceChild("legFL", CubeListBuilder.create().texOffs(0, 42).addBox(-2.0F, 1.0F, -1.0F, 3.0F, 13.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(4.0F, -14.0F, -6.0F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    public void setupAnim(RammingPrey entity, float limbSwing, float limbSwingAmount, float ageInTicks, float headYaw, float headPitch)
    {
        super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, headYaw, headPitch);
        if (EntityHelpers.isMovingOnLand(entity))
        {
            final float speed = getAdjustedLandSpeed(entity);
            if (entity.getTelegraphAttackTick() > 0)
            {
                animateWalk(WILDEBEEST_RUN, limbSwing, limbSwingAmount, 1F, 3 * speed);
            }
            else
            {
                animateWalk(WILDEBEEST_WALK, limbSwing, limbSwingAmount, 1F, 3 * speed);
            }
        }

        if (entity.isTelegraphingAttack())
        {
            //Note for re-use: telegraph animations should be 1 second long, or the float here should be multiplied by their length
            //animate(entity.telegraphAnimation, BOAR_PREPARE_CHARGE, entity.getTelegraphAnimationProgress());
            this.head.xRot = (entity.getTelegraphAttackTick() + 50) * Constants.DEG_TO_RAD * -1;
            this.neck.xRot = entity.getTelegraphAttackTick() * Constants.DEG_TO_RAD * -1;
        }
        else
        {
            this.head.xRot = (headPitch + 50) * Constants.DEG_TO_RAD;
            this.head.yRot = headYaw * Constants.DEG_TO_RAD;
        }
        this.animate(entity.attackingAnimation, WILDEBEEST_ATTACK, ageInTicks);
    }

    public static final AnimationDefinition WILDEBEEST_RUN = AnimationDefinition.Builder.withLength(0.5f).looping()
        .addAnimation("body",
            new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, KeyframeAnimations.degreeVec(-5f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.125f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.25f, KeyframeAnimations.degreeVec(5f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.375f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.5f, KeyframeAnimations.degreeVec(-5f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("tail",
            new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, KeyframeAnimations.degreeVec(15f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.2083f, KeyframeAnimations.degreeVec(60f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.5f, KeyframeAnimations.degreeVec(22.5f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("tail1",
            new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.25f, KeyframeAnimations.degreeVec(-90f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.4167f, KeyframeAnimations.degreeVec(-2.5f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.5f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("head",
            new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, KeyframeAnimations.degreeVec(5f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.0833f, KeyframeAnimations.degreeVec(-2.5f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.25f, KeyframeAnimations.degreeVec(-5f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.3333f, KeyframeAnimations.degreeVec(7.5f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.5f, KeyframeAnimations.degreeVec(5f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("legFR",
            new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, KeyframeAnimations.degreeVec(5f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.0833f, KeyframeAnimations.degreeVec(-40f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.2083f, KeyframeAnimations.degreeVec(-40f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.375f, KeyframeAnimations.degreeVec(25.5f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.4167f, KeyframeAnimations.degreeVec(25.5f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.5f, KeyframeAnimations.degreeVec(5f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("legFL",
            new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, KeyframeAnimations.degreeVec(5f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.0833f, KeyframeAnimations.degreeVec(-40f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.2083f, KeyframeAnimations.degreeVec(-40f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.375f, KeyframeAnimations.degreeVec(25.5f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.4167f, KeyframeAnimations.degreeVec(25.5f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.5f, KeyframeAnimations.degreeVec(5f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("legBL",
            new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.0833f, KeyframeAnimations.degreeVec(45f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.2083f, KeyframeAnimations.degreeVec(45f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.25f, KeyframeAnimations.degreeVec(45f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.375f, KeyframeAnimations.degreeVec(-30f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.5f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("legBR",
            new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.0833f, KeyframeAnimations.degreeVec(45f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.2083f, KeyframeAnimations.degreeVec(45f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.25f, KeyframeAnimations.degreeVec(45f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.375f, KeyframeAnimations.degreeVec(-30f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.5f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("neck",
            new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.0833f, KeyframeAnimations.degreeVec(7.5f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.2083f, KeyframeAnimations.degreeVec(5f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.3333f, KeyframeAnimations.degreeVec(-7.5f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.5f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("tail2",
            new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.2917f, KeyframeAnimations.degreeVec(10f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.5f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR))).build();
    public static final AnimationDefinition WILDEBEEST_WALK = AnimationDefinition.Builder.withLength(1f).looping()
        .addAnimation("neck",
            new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.2917f, KeyframeAnimations.degreeVec(5f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.8333f, KeyframeAnimations.degreeVec(-1f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(1f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("head",
            new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.3333f, KeyframeAnimations.degreeVec(-5f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.8333f, KeyframeAnimations.degreeVec(1f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(1f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("tail",
            new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.3333f, KeyframeAnimations.degreeVec(10f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.625f, KeyframeAnimations.degreeVec(3f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(1f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("legBR",
            new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.4167f, KeyframeAnimations.degreeVec(22.5f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.7083f, KeyframeAnimations.degreeVec(-27.5f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(1f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("legBL",
            new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.1667f, KeyframeAnimations.degreeVec(-27.5f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.75f, KeyframeAnimations.degreeVec(22.5f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(1f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("legFR",
            new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.1667f, KeyframeAnimations.degreeVec(-27.5f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.75f, KeyframeAnimations.degreeVec(22.5f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(1f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("legFL",
            new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.4167f, KeyframeAnimations.degreeVec(22.5f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.7083f, KeyframeAnimations.degreeVec(-27.5f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(1f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR))).build();
    public static final AnimationDefinition WILDEBEEST_ATTACK = AnimationDefinition.Builder.withLength(0.5f)
        .addAnimation("body",
            new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.125f, KeyframeAnimations.degreeVec(5f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.3433333f, KeyframeAnimations.degreeVec(-1f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.5f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("neck",
            new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.125f, KeyframeAnimations.degreeVec(30f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.3433333f, KeyframeAnimations.degreeVec(-14.17f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.5f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("head",
            new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.125f, KeyframeAnimations.degreeVec(-2.5f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.3433333f, KeyframeAnimations.degreeVec(-27.22f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.5f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("legBR",
            new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.125f, KeyframeAnimations.degreeVec(-7.5f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.2916767f, KeyframeAnimations.degreeVec(24.72f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.5f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("legBL",
            new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.125f, KeyframeAnimations.degreeVec(-7.5f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.2916767f, KeyframeAnimations.degreeVec(19.72f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.5f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("legFR",
            new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.125f, KeyframeAnimations.degreeVec(-7.5f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.2916767f, KeyframeAnimations.degreeVec(-11.67f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.5f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("legFL",
            new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.125f, KeyframeAnimations.degreeVec(-5f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.2916767f, KeyframeAnimations.degreeVec(-10.28f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.5f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR))).build();

}