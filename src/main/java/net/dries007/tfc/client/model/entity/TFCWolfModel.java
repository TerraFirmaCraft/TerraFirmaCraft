/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

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
import net.minecraft.util.Mth;

import net.dries007.tfc.common.entities.EntityHelpers;
import net.dries007.tfc.common.entities.ai.predator.PackPredator;

public class TFCWolfModel extends HierarchicalAnimatedModel<PackPredator>
{
    public static LayerDefinition createBodyLayer()
    {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition head = partdefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, -3.0F, -2.0F, 6.0F, 6.0F, 4.0F, new CubeDeformation(0.0F))
            .texOffs(16, 14).addBox(2.0F, -5.0F, 0.0F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(16, 14).addBox(-2.0F, -5.0F, 0.0F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(0, 10).addBox(-0.5F, -0.02F, -5.0F, 3.0F, 3.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.0F, 13.5F, -7.0F));

        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(18, 14).addBox(-3.0F, -1.0F, -3.0F, 6.0F, 9.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 14.0F, 2.0F, 1.5708F, 0.0F, 0.0F));

        PartDefinition mane = partdefinition.addOrReplaceChild("mane", CubeListBuilder.create().texOffs(21, 0).addBox(-3.0F, -3.0F, -3.0F, 8.0F, 6.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, 14.0F, -2.0F, 1.5708F, 0.0F, 0.0F));

        PartDefinition leg1 = partdefinition.addOrReplaceChild("leg1", CubeListBuilder.create().texOffs(0, 18).addBox(0.0F, 0.0F, -1.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.5F, 16.0F, 7.0F));

        PartDefinition leg2 = partdefinition.addOrReplaceChild("leg2", CubeListBuilder.create().texOffs(0, 18).addBox(0.0F, 0.0F, -1.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.5F, 16.0F, 7.0F));

        PartDefinition leg3 = partdefinition.addOrReplaceChild("leg3", CubeListBuilder.create().texOffs(0, 18).addBox(0.0F, 0.0F, -1.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.5F, 16.0F, -4.0F));

        PartDefinition leg4 = partdefinition.addOrReplaceChild("leg4", CubeListBuilder.create().texOffs(0, 18).addBox(0.0F, 0.0F, -1.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.5F, 16.0F, -4.0F));

        PartDefinition tail = partdefinition.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(9, 18).addBox(0.0F, 0.0F, -1.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, 12.0F, 9.0F, 0.48F, 0.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 32);
    }

    //public static final ResourceLocation WOLF_LOCATION = new ResourceLocation("textures/entity/wolf/wolf.png");

    private final ModelPart head;
    private final ModelPart body;
    private final ModelPart rightHindLeg;
    private final ModelPart leftHindLeg;
    private final ModelPart rightFrontLeg;
    private final ModelPart leftFrontLeg;
    private final ModelPart tail;
    private final ModelPart mane;

    public TFCWolfModel(ModelPart root)
    {
        super(root);
        this.head = root.getChild("head");
        this.body = root.getChild("body");
        this.mane = root.getChild("mane");
        this.rightHindLeg = root.getChild("leg1");
        this.leftHindLeg = root.getChild("leg2");
        this.rightFrontLeg = root.getChild("leg3");
        this.leftFrontLeg = root.getChild("leg4");
        this.tail = root.getChild("tail");
    }

    @Override
    public void setupAnim(PackPredator entity, float limbSwing, float limbSwingAmount, float ageInTicks, float yaw, float pitch)
    {
        super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, yaw, pitch);

        if (entity.isSleeping() && entity.sleepingAnimation.isStarted())
        {
            this.animate(entity.sleepingAnimation, WOLF_SLEEPING, ageInTicks);
        }
        else
        {
            if (entity.isInWaterOrBubble())
            {
                this.animateWalk(WOLF_SWIM, limbSwing, limbSwingAmount, 4f, 2.5f);
            }
            else
            {
                if (entity.isAggressive() && EntityHelpers.isMovingOnLand(entity))
                {
                    animateWalk(WOLF_RUN, limbSwing, limbSwingAmount, 1f, 2.5f);
                }
                else
                {
                    animateWalk(WOLF_WALK, limbSwing, limbSwingAmount, 2.5f, 2.5f);
                }
                this.animate(entity.attackingAnimation, WOLF_ATTACK, ageInTicks);
            }

            this.head.xRot = pitch * Constants.DEG_TO_RAD;
            this.head.yRot = yaw * Constants.DEG_TO_RAD;
        }
    }


    public static final AnimationDefinition WOLF_SLEEPING = AnimationDefinition.Builder.withLength(2.5f).looping()
        .addAnimation("body",
            new AnimationChannel(AnimationChannel.Targets.POSITION,
                new Keyframe(0f, KeyframeAnimations.posVec(-3.25f, -6.5f, -1.5f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.75f, KeyframeAnimations.posVec(-3.5f, -6f, -1.75f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(1.7916767f, KeyframeAnimations.posVec(-3.25f, -6.5f, -1.5f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(1.9167667f, KeyframeAnimations.posVec(-3.25f, -6.5f, -1.5f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(2.1676665f, KeyframeAnimations.posVec(-3.25f, -6.5f, -1.5f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("body",
            new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, KeyframeAnimations.degreeVec(-30f, 0f, -60f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.75f, KeyframeAnimations.degreeVec(-30.09f, -4.33f, -57.5f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(1.7916767f, KeyframeAnimations.degreeVec(-30f, 0f, -60f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(1.9167667f, KeyframeAnimations.degreeVec(-30f, 0f, -60f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(2.1676665f, KeyframeAnimations.degreeVec(-30f, 0f, -60f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("mane",
            new AnimationChannel(AnimationChannel.Targets.POSITION,
                new Keyframe(0f, KeyframeAnimations.posVec(-4f, -6f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.75f, KeyframeAnimations.posVec(-4f, -6.25f, 0.75f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(1.7916767f, KeyframeAnimations.posVec(-4f, -6f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(1.9167667f, KeyframeAnimations.posVec(-4f, -6f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(2.1676665f, KeyframeAnimations.posVec(-4f, -6f, 0f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("mane",
            new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.75f, KeyframeAnimations.degreeVec(7.5f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(1.7916767f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(1.9167667f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(2.1676665f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("tail",
            new AnimationChannel(AnimationChannel.Targets.POSITION,
                new Keyframe(0f, KeyframeAnimations.posVec(-1f, -10f, -3f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.75f, KeyframeAnimations.posVec(-1f, -10f, -3f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(1.7916767f, KeyframeAnimations.posVec(-1f, -10f, -3f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(2f, KeyframeAnimations.posVec(-1f, -10f, -3f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(2.5f, KeyframeAnimations.posVec(-1f, -10f, -3f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("tail",
            new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, KeyframeAnimations.degreeVec(0f, 0f, -72.5f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.75f, KeyframeAnimations.degreeVec(0f, 0f, -72.5f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(1.7916767f, KeyframeAnimations.degreeVec(0f, 0f, -72.5f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(2f, KeyframeAnimations.degreeVec(0f, 0f, -90f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(2.5f, KeyframeAnimations.degreeVec(0f, 0f, -72.5f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("head",
            new AnimationChannel(AnimationChannel.Targets.POSITION,
                new Keyframe(0f, KeyframeAnimations.posVec(-3f, -6f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.75f, KeyframeAnimations.posVec(-3f, -6f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(1.7916767f, KeyframeAnimations.posVec(-3f, -6f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(1.9167667f, KeyframeAnimations.posVec(-3f, -6f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(2.5f, KeyframeAnimations.posVec(-3f, -6f, 0f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("head",
            new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, KeyframeAnimations.degreeVec(0f, -42.5f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.75f, KeyframeAnimations.degreeVec(0f, -37.5f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(1.7916767f, KeyframeAnimations.degreeVec(0f, -42.5f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(1.9167667f, KeyframeAnimations.degreeVec(0f, -42.5f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(2.5f, KeyframeAnimations.degreeVec(0f, -42.5f, 0f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("leg1",
            new AnimationChannel(AnimationChannel.Targets.POSITION,
                new Keyframe(0f, KeyframeAnimations.posVec(1.25f, -8.5f, -2.5f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.75f, KeyframeAnimations.posVec(1.25f, -8.5f, -2.5f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(1.7916767f, KeyframeAnimations.posVec(1.25f, -8.5f, -2.5f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(1.9167667f, KeyframeAnimations.posVec(1.25f, -8.5f, -2.5f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(2.1676665f, KeyframeAnimations.posVec(1.25f, -8.5f, -2.5f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("leg1",
            new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, KeyframeAnimations.degreeVec(-69.42f, -4.74f, -89.74f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.75f, KeyframeAnimations.degreeVec(-69.5f, -0.06f, -87.98f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(1.7916767f, KeyframeAnimations.degreeVec(-69.42f, -4.74f, -89.74f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(1.9167667f, KeyframeAnimations.degreeVec(-69.42f, -4.74f, -89.74f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(2.1676665f, KeyframeAnimations.degreeVec(-69.42f, -4.74f, -89.74f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("leg2",
            new AnimationChannel(AnimationChannel.Targets.POSITION,
                new Keyframe(0f, KeyframeAnimations.posVec(1.25f, -6.5f, -2.5f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.75f, KeyframeAnimations.posVec(1.25f, -6.5f, -2.5f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(1.7916767f, KeyframeAnimations.posVec(1.25f, -6.5f, -2.5f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(1.9167667f, KeyframeAnimations.posVec(1.25f, -6.5f, -2.5f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(2.1676665f, KeyframeAnimations.posVec(1.25f, -6.5f, -2.5f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("leg2",
            new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, KeyframeAnimations.degreeVec(-52.08f, 7.92f, -66.37f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.75f, KeyframeAnimations.degreeVec(-52.26f, 5.94f, -67.92f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(1.7916767f, KeyframeAnimations.degreeVec(-52.08f, 7.92f, -66.37f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(1.9167667f, KeyframeAnimations.degreeVec(-52.08f, 7.92f, -66.37f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(2.1676665f, KeyframeAnimations.degreeVec(-52.08f, 7.92f, -66.37f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("leg3",
            new AnimationChannel(AnimationChannel.Targets.POSITION,
                new Keyframe(0f, KeyframeAnimations.posVec(-3f, -8f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.9583434f, KeyframeAnimations.posVec(-3f, -8f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(1.7916767f, KeyframeAnimations.posVec(-3f, -8f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(1.9167667f, KeyframeAnimations.posVec(-3f, -8f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(2.1676665f, KeyframeAnimations.posVec(-3f, -8f, 0f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("leg3",
            new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, KeyframeAnimations.degreeVec(-75f, 0f, -87.5f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.9583434f, KeyframeAnimations.degreeVec(-75f, 0f, -87.5f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(1.7916767f, KeyframeAnimations.degreeVec(-75f, 0f, -87.5f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(1.9167667f, KeyframeAnimations.degreeVec(-75f, 0f, -87.5f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(2.1676665f, KeyframeAnimations.degreeVec(-75f, 0f, -87.5f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("leg4",
            new AnimationChannel(AnimationChannel.Targets.POSITION,
                new Keyframe(0f, KeyframeAnimations.posVec(-3.75f, -7f, 1.5f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.9583434f, KeyframeAnimations.posVec(-3.75f, -7f, 1.5f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(1.7916767f, KeyframeAnimations.posVec(-3.75f, -7f, 1.5f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(1.9167667f, KeyframeAnimations.posVec(-3.75f, -7f, 1.5f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(2.1676665f, KeyframeAnimations.posVec(-3.75f, -7f, 1.5f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("leg4",
            new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, KeyframeAnimations.degreeVec(-86.51f, -29.37f, 0.86f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.9583434f, KeyframeAnimations.degreeVec(-86.51f, -29.37f, 0.86f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(1.7916767f, KeyframeAnimations.degreeVec(-86.51f, -29.37f, 0.86f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(1.9167667f, KeyframeAnimations.degreeVec(-86.51f, -29.37f, 0.86f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(2.1676665f, KeyframeAnimations.degreeVec(-86.51f, -29.37f, 0.86f),
                    AnimationChannel.Interpolations.LINEAR))).build();
    public static final AnimationDefinition WOLF_WALK = AnimationDefinition.Builder.withLength(1f).looping()
        .addAnimation("leg1",
            new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.25f, KeyframeAnimations.degreeVec(30f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.5f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.75f, KeyframeAnimations.degreeVec(-30f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(1f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("leg2",
            new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.25f, KeyframeAnimations.degreeVec(-30f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.5f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.75f, KeyframeAnimations.degreeVec(30f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(1f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("leg3",
            new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.25f, KeyframeAnimations.degreeVec(-30f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.5f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.75f, KeyframeAnimations.degreeVec(30f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(1f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("leg4",
            new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.25f, KeyframeAnimations.degreeVec(30f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.5f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.75f, KeyframeAnimations.degreeVec(-30f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(1f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("tail",
            new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.25f, KeyframeAnimations.degreeVec(0f, 0f, -10f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.75f, KeyframeAnimations.degreeVec(0f, 0f, 10f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(1f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR))).build();
    public static final AnimationDefinition WOLF_RUN = AnimationDefinition.Builder.withLength(0.5f).looping()
        .addAnimation("head",
            new AnimationChannel(AnimationChannel.Targets.POSITION,
                new Keyframe(0f, KeyframeAnimations.posVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.3433333f, KeyframeAnimations.posVec(0f, -0.5f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.5f, KeyframeAnimations.posVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("body",
            new AnimationChannel(AnimationChannel.Targets.POSITION,
                new Keyframe(0.3433333f, KeyframeAnimations.posVec(0f, 0f, -1f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("body",
            new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.3433333f, KeyframeAnimations.degreeVec(-10f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.5f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("mane",
            new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.3433333f, KeyframeAnimations.degreeVec(5f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.5f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("leg1",
            new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.08343333f, KeyframeAnimations.degreeVec(42.5f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.3433333f, KeyframeAnimations.degreeVec(-38f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.5f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("leg2",
            new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.125f, KeyframeAnimations.degreeVec(45f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.3433333f, KeyframeAnimations.degreeVec(-34.5f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.5f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("leg3",
            new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.125f, KeyframeAnimations.degreeVec(-35f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.3433333f, KeyframeAnimations.degreeVec(33.5f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.5f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("leg4",
            new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.08343333f, KeyframeAnimations.degreeVec(-35f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.3433333f, KeyframeAnimations.degreeVec(36f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.5f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("tail",
            new AnimationChannel(AnimationChannel.Targets.POSITION,
                new Keyframe(0f, KeyframeAnimations.posVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.3433333f, KeyframeAnimations.posVec(0f, -1.75f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.5f, KeyframeAnimations.posVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("tail",
            new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, KeyframeAnimations.degreeVec(55f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.125f, KeyframeAnimations.degreeVec(77.5f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.3433333f, KeyframeAnimations.degreeVec(51.5f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.5f, KeyframeAnimations.degreeVec(55f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR))).build();
    public static final AnimationDefinition WOLF_ATTACK = AnimationDefinition.Builder.withLength(0.375f)
        .addAnimation("head",
            new AnimationChannel(AnimationChannel.Targets.POSITION,
                new Keyframe(0f, KeyframeAnimations.posVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.25f, KeyframeAnimations.posVec(0f, -0.25f, -0.75f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.375f, KeyframeAnimations.posVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("head",
            new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.25f, KeyframeAnimations.degreeVec(-6.93f, 2.86f, 39.83f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.375f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("body",
            new AnimationChannel(AnimationChannel.Targets.POSITION,
                new Keyframe(0f, KeyframeAnimations.posVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.25f, KeyframeAnimations.posVec(0f, 0.5f, -1.75f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.375f, KeyframeAnimations.posVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("body",
            new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.25f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.375f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("mane",
            new AnimationChannel(AnimationChannel.Targets.POSITION,
                new Keyframe(0f, KeyframeAnimations.posVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.25f, KeyframeAnimations.posVec(0f, 0f, -1.25f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.375f, KeyframeAnimations.posVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("mane",
            new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.25f, KeyframeAnimations.degreeVec(10f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.375f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("leg1",
            new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.25f, KeyframeAnimations.degreeVec(12.5f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.375f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("leg2",
            new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.25f, KeyframeAnimations.degreeVec(15f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.375f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("leg3",
            new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.25f, KeyframeAnimations.degreeVec(2.5f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.375f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("leg4",
            new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.25f, KeyframeAnimations.degreeVec(5f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.375f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("tail",
            new AnimationChannel(AnimationChannel.Targets.POSITION,
                new Keyframe(0f, KeyframeAnimations.posVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.25f, KeyframeAnimations.posVec(0f, 0f, -1f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.375f, KeyframeAnimations.posVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("tail",
            new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.25f, KeyframeAnimations.degreeVec(75f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.375f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR))).build();
    public static final AnimationDefinition WOLF_SWIM = AnimationDefinition.Builder.withLength(0.5f).looping()
        .addAnimation("head",
            new AnimationChannel(AnimationChannel.Targets.POSITION,
                new Keyframe(0f, KeyframeAnimations.posVec(0f, -6f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.25f, KeyframeAnimations.posVec(0f, -6f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.5f, KeyframeAnimations.posVec(0f, -6f, 0f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("body",
            new AnimationChannel(AnimationChannel.Targets.POSITION,
                new Keyframe(0f, KeyframeAnimations.posVec(0f, -7f, -2f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.25f, KeyframeAnimations.posVec(0f, -7f, -2f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.5f, KeyframeAnimations.posVec(0f, -7f, -2f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("body",
            new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, KeyframeAnimations.degreeVec(-37.5f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.5f, KeyframeAnimations.degreeVec(-37.5f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("mane",
            new AnimationChannel(AnimationChannel.Targets.POSITION,
                new Keyframe(0f, KeyframeAnimations.posVec(0f, -8f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.25f, KeyframeAnimations.posVec(0f, -8f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.5f, KeyframeAnimations.posVec(0f, -8f, 0f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("mane",
            new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.5f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("leg1",
            new AnimationChannel(AnimationChannel.Targets.POSITION,
                new Keyframe(0f, KeyframeAnimations.posVec(0f, -10f, -3f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.25f, KeyframeAnimations.posVec(0f, -10f, -3f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.5f, KeyframeAnimations.posVec(0f, -10f, -3f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("leg1",
            new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, KeyframeAnimations.degreeVec(-32.5f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.25f, KeyframeAnimations.degreeVec(22.5f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.5f, KeyframeAnimations.degreeVec(-32.5f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("leg2",
            new AnimationChannel(AnimationChannel.Targets.POSITION,
                new Keyframe(0f, KeyframeAnimations.posVec(0f, -10f, -3f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.25f, KeyframeAnimations.posVec(0f, -10f, -3f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.5f, KeyframeAnimations.posVec(0f, -10f, -3f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("leg2",
            new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, KeyframeAnimations.degreeVec(27.5f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.25f, KeyframeAnimations.degreeVec(-30f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.5f, KeyframeAnimations.degreeVec(27.5f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("leg3",
            new AnimationChannel(AnimationChannel.Targets.POSITION,
                new Keyframe(0f, KeyframeAnimations.posVec(0f, -8f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.25f, KeyframeAnimations.posVec(0f, -8f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.5f, KeyframeAnimations.posVec(0f, -8f, 0f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("leg3",
            new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, KeyframeAnimations.degreeVec(-92.5f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.25f, KeyframeAnimations.degreeVec(-60f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.5f, KeyframeAnimations.degreeVec(-92.5f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("leg4",
            new AnimationChannel(AnimationChannel.Targets.POSITION,
                new Keyframe(0f, KeyframeAnimations.posVec(0f, -8f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.25f, KeyframeAnimations.posVec(0f, -8f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.5f, KeyframeAnimations.posVec(0f, -8f, 0f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("leg4",
            new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, KeyframeAnimations.degreeVec(-55f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.25f, KeyframeAnimations.degreeVec(-92.5f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.5f, KeyframeAnimations.degreeVec(-55f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("tail",
            new AnimationChannel(AnimationChannel.Targets.POSITION,
                new Keyframe(0f, KeyframeAnimations.posVec(0f, -12f, -2f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.25f, KeyframeAnimations.posVec(0f, -12f, -2f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.5f, KeyframeAnimations.posVec(0f, -12f, -2f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("tail",
            new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, KeyframeAnimations.degreeVec(100f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.25f, KeyframeAnimations.degreeVec(87.5f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.5f, KeyframeAnimations.degreeVec(100f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR))).build();


}
