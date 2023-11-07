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
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.schedule.Activity;

import net.dries007.tfc.common.entities.EntityHelpers;
import net.dries007.tfc.common.entities.prey.RammingPrey;

public class BoarModel extends HierarchicalAnimatedModel<RammingPrey>
{

    public static final AnimationDefinition BOAR_WALK = AnimationDefinition.Builder.withLength(2f).looping()
        .addAnimation("bone",
            new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.5f, KeyframeAnimations.degreeVec(22.5f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(1f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(1.5f, KeyframeAnimations.degreeVec(-22.5f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(2f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("head",
            new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.5f, KeyframeAnimations.degreeVec(5.01900181748988f, 4.98092532192868f, 0.43687984177449835f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(1f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(1.5f, KeyframeAnimations.degreeVec(5.01900181748988f, -4.98092532192868f, -0.43687984177449835f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(2f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("bone2",
            new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.5f, KeyframeAnimations.degreeVec(-22.5f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(1f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(1.5f, KeyframeAnimations.degreeVec(22.5f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(2f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("bone3",
            new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.5f, KeyframeAnimations.degreeVec(20f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(1f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(1.5f, KeyframeAnimations.degreeVec(-20f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(2f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("bone4",
            new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.5f, KeyframeAnimations.degreeVec(-20f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(1f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(1.5f, KeyframeAnimations.degreeVec(20f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(2f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR))).build();
    public static final AnimationDefinition BOAR_HEADBUTT = AnimationDefinition.Builder.withLength(0.375f)
        .addAnimation("boar",
            new AnimationChannel(AnimationChannel.Targets.POSITION,
                new Keyframe(0.08343333f, KeyframeAnimations.posVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.25f, KeyframeAnimations.posVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("body",
            new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0.08343333f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.25f, KeyframeAnimations.degreeVec(-5f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.375f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("head",
            new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, KeyframeAnimations.degreeVec(30f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.08343333f, KeyframeAnimations.degreeVec(17.44f, -1.5f, 4.77f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.25f, KeyframeAnimations.degreeVec(-14.89f, 9.3f, 31.28f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.375f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("bone3",
            new AnimationChannel(AnimationChannel.Targets.POSITION,
                new Keyframe(0f, KeyframeAnimations.posVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.25f, KeyframeAnimations.posVec(0f, 1f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.375f, KeyframeAnimations.posVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("bone4",
            new AnimationChannel(AnimationChannel.Targets.POSITION,
                new Keyframe(0f, KeyframeAnimations.posVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.25f, KeyframeAnimations.posVec(0f, 1f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.375f, KeyframeAnimations.posVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR))).build();
    public static final AnimationDefinition BOAR_RUN = AnimationDefinition.Builder.withLength(0.6766666f).looping()
        .addAnimation("boar",
            new AnimationChannel(AnimationChannel.Targets.POSITION,
                new Keyframe(0f, KeyframeAnimations.posVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.20834334f, KeyframeAnimations.posVec(0f, 1f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.4167667f, KeyframeAnimations.posVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("body",
            new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.20834334f, KeyframeAnimations.degreeVec(-7.5f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.5416766f, KeyframeAnimations.degreeVec(7.5f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.6766666f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("bone",
            new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.20834334f, KeyframeAnimations.degreeVec(37.5f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.4167667f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.5416766f, KeyframeAnimations.degreeVec(-17.5f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.6766666f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("bone2",
            new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.20834334f, KeyframeAnimations.degreeVec(35f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.4167667f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.5416766f, KeyframeAnimations.degreeVec(-17.5f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.6766666f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("bone3",
            new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.20834334f, KeyframeAnimations.degreeVec(-40f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.4167667f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.5416766f, KeyframeAnimations.degreeVec(20f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.6766666f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("bone4",
            new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.20834334f, KeyframeAnimations.degreeVec(-37.5f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.4167667f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.5416766f, KeyframeAnimations.degreeVec(20f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.6766666f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("head",
            new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, KeyframeAnimations.degreeVec(30f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.20834334f, KeyframeAnimations.degreeVec(40f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.5f, KeyframeAnimations.degreeVec(23.64f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.5416766f, KeyframeAnimations.degreeVec(22.73f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.6766666f, KeyframeAnimations.degreeVec(30f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR))).build();
    public static final AnimationDefinition BOAR_PREPARE_CHARGE = AnimationDefinition.Builder.withLength(1f)
        .addAnimation("bone3",
            new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.16766666f, KeyframeAnimations.degreeVec(20f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.3433333f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.4583433f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.5834334f, KeyframeAnimations.degreeVec(20f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.7916766f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(1f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("body",
            new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.5f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(1f, KeyframeAnimations.degreeVec(2.5f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("head",
            new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.4167667f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(1f, KeyframeAnimations.degreeVec(30f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR))).build();

    private final ModelPart boar;
    private final ModelPart head;
    private final ModelPart rightHindLeg;
    private final ModelPart leftHindLeg;
    private final ModelPart rightFrontLeg;
    private final ModelPart leftFrontLeg;

    public BoarModel(ModelPart root)
    {
        super(root);
        this.boar = root.getChild("boar");
        this.head = boar.getChild("body").getChild("head");
        this.rightHindLeg = boar.getChild("bone2");
        this.leftHindLeg = boar.getChild("bone");
        this.rightFrontLeg = boar.getChild("bone3");
        this.leftFrontLeg = boar.getChild("bone4");
    }

    public static LayerDefinition createBodyLayer()
    {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition boar = partdefinition.addOrReplaceChild("boar", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition bone = boar.addOrReplaceChild("bone", CubeListBuilder.create().texOffs(52, 14).addBox(-1.5F, -1.5F, -1.5F, 3.0F, 9.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(2.5F, -7.5473F, 5.7495F));

        PartDefinition bone3 = boar.addOrReplaceChild("bone3", CubeListBuilder.create().texOffs(52, 0).addBox(-1.5F, -2.5F, -1.5F, 3.0F, 11.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.5F, -8.5473F, -5.2505F));

        PartDefinition bone4 = boar.addOrReplaceChild("bone4", CubeListBuilder.create().texOffs(52, 0).addBox(-1.5F, -2.5F, -1.5F, 3.0F, 11.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(2.5F, -8.5473F, -5.2505F));

        PartDefinition bone2 = boar.addOrReplaceChild("bone2", CubeListBuilder.create().texOffs(52, 14).addBox(-1.5F, -1.5F, -1.5F, 3.0F, 9.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.5F, -7.5473F, 5.7495F));

        PartDefinition body = boar.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 24).addBox(-2.0F, -2.9527F, -12.2495F, 4.0F, 7.0F, 15.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -9.0473F, 5.2495F));

        PartDefinition body_r1 = body.addOrReplaceChild("body_r1", CubeListBuilder.create().texOffs(0, 0).addBox(-3.0F, -12.0F, -10.0F, 6.0F, 7.0F, 16.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 8.0473F, -3.2495F, -0.0873F, 0.0F, 0.0F));

        PartDefinition head = body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(24, 24).addBox(-2.0F, -3.0F, -6.0F, 4.0F, 7.0F, 6.0F, new CubeDeformation(0.0F))
            .texOffs(39, 38).addBox(-2.0F, 0.0F, -11.0F, 4.0F, 4.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -0.9527F, -11.2495F));

        PartDefinition cube_r1 = head.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(24, 24).addBox(-0.5F, -3.0F, -0.5F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(35, 47).addBox(-5.5F, -3.0F, -0.5F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.5F, 2.0F, -9.5F, 0.6981F, 0.0F, 0.0F));

        PartDefinition cube_r2 = head.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(46, 52).addBox(-4.0F, -1.0F, -0.5F, 2.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(5.5262F, -2.0662F, -5.5F, 0.3129F, -0.1116F, 0.5496F));

        PartDefinition cube_r3 = head.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(43, 55).mirror().addBox(2.0F, -1.0F, -0.5F, 2.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-5.5262F, -2.0662F, -5.5F, 0.3129F, 0.1116F, -0.5496F));

        PartDefinition cube_r4 = head.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(29, 0).addBox(-3.0F, 0.0F, -4.0F, 6.0F, 5.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, -3.0F, -0.7854F, 0.0F, 0.0F));

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
                animateWalk(BOAR_RUN, limbSwing, limbSwingAmount, 1F, 3 * speed);
            }
            else
            {
                rightHindLeg.xRot = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
                leftHindLeg.xRot = Mth.cos(limbSwing * 0.6662F + Mth.PI) * 1.4F * limbSwingAmount;
                rightFrontLeg.xRot = Mth.cos(limbSwing * 0.6662F + Mth.PI) * 1.4F * limbSwingAmount;
                leftFrontLeg.xRot = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
            }
        }

        if (entity.isTelegraphingAttack())
        {
            //Note for re-use: telegraph animations should be 1 second long, or the float here should be multiplied by their length
            //animate(entity.telegraphAnimation, BOAR_PREPARE_CHARGE, entity.getTelegraphAnimationProgress());
            this.head.xRot = entity.getTelegraphAttackTick() * 2 * Constants.DEG_TO_RAD;
        }
        else
        {
            this.head.xRot = headPitch * Constants.DEG_TO_RAD;
            this.head.yRot = headYaw * Constants.DEG_TO_RAD;
        }
        this.animate(entity.attackingAnimation, BOAR_HEADBUTT, ageInTicks);
    }
}
