/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.model.entity;

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
import org.joml.Vector3f;

import net.dries007.tfc.common.entities.EntityHelpers;
import net.dries007.tfc.common.entities.predator.Predator;

public class BearModel extends HierarchicalAnimatedModel<Predator>
{
    public static LayerDefinition createBodyLayer()
    {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 24).addBox(-7.5F, -9.0F, -18.0F, 15.0F, 11.0F, 10.0F, new CubeDeformation(0.0F))
            .texOffs(0, 0).addBox(-7.5F, -10.0F, -8.0F, 15.0F, 12.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 15.0F, 8.0F));

        PartDefinition right_front_leg = body.addOrReplaceChild("right_front_leg", CubeListBuilder.create().texOffs(50, 54).addBox(-2.0F, -1.0F, -2.0F, 4.0F, 11.0F, 4.0F, new CubeDeformation(0.0F))
            .texOffs(64, 27).addBox(-2.5F, 8.0F, -4.0F, 5.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(-6.0F, -1.0F, -15.0F));

        PartDefinition left_front_leg = body.addOrReplaceChild("left_front_leg", CubeListBuilder.create().texOffs(34, 54).addBox(-2.0F, -1.0F, -2.0F, 4.0F, 11.0F, 4.0F, new CubeDeformation(0.0F))
            .texOffs(15, 64).addBox(-2.5F, 8.0F, -4.0F, 5.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(6.0F, -1.0F, -15.0F));

        PartDefinition left_hind_leg = body.addOrReplaceChild("left_hind_leg", CubeListBuilder.create().texOffs(66, 54).addBox(-2.0F, 2.0F, 0.0F, 4.0F, 7.0F, 4.0F, new CubeDeformation(0.0F))
            .texOffs(64, 13).addBox(-2.5F, 7.0F, -2.0F, 5.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(6.0F, 0.0F, 0.0F));

        PartDefinition left_thigh_r1 = left_hind_leg.addOrReplaceChild("left_thigh_r1", CubeListBuilder.create().texOffs(49, 19).addBox(-2.5F, -2.4645F, -0.2929F, 5.0F, 8.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 1.0F, -2.0F, 0.7854F, 0.0F, 0.0F));

        PartDefinition right_hind_leg = body.addOrReplaceChild("right_hind_leg", CubeListBuilder.create().texOffs(62, 65).addBox(-2.0F, 2.0F, 0.0F, 4.0F, 7.0F, 4.0F, new CubeDeformation(0.0F))
            .texOffs(0, 62).addBox(-2.5F, 7.0F, -2.0F, 5.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(-6.0F, 0.0F, 0.0F));

        PartDefinition left_thigh_r2 = right_hind_leg.addOrReplaceChild("left_thigh_r2", CubeListBuilder.create().texOffs(54, 0).addBox(-2.5F, -3.1716F, -1.0F, 5.0F, 8.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 1.0F, -1.0F, 0.7854F, 0.0F, 0.0F));

        PartDefinition neck = body.addOrReplaceChild("neck", CubeListBuilder.create(), PartPose.offset(1.0F, -5.0F, -15.0F));

        PartDefinition neck_r1 = neck.addOrReplaceChild("neck_r1", CubeListBuilder.create().texOffs(41, 36).addBox(-6.0F, -6.0761F, -6.6173F, 12.0F, 9.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, 1.0F, -1.0F, -0.3927F, 0.0F, 0.0F));

        PartDefinition head = neck.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 45).addBox(-5.0F, -5.0F, -8.0F, 8.0F, 8.0F, 9.0F, new CubeDeformation(0.0F))
            .texOffs(25, 45).addBox(-3.0F, -1.0F, -11.0F, 4.0F, 4.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -2.0F, -4.0F));

        PartDefinition right_ear = head.addOrReplaceChild("right_ear", CubeListBuilder.create().texOffs(0, 3).addBox(-1.0F, -2.0F, 0.0F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(-3.0F, -5.0F, -7.0F));

        PartDefinition left_ear = head.addOrReplaceChild("left_ear", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, -2.0F, 0.0F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(1.0F, -5.0F, -7.0F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }

    public static final AnimationDefinition WALK = AnimationDefinition.Builder.withLength(1.0F).looping()
        .addAnimation("right_front_leg", new AnimationChannel(AnimationChannel.Targets.ROTATION, new Keyframe(0.25F, KeyframeAnimations.degreeVec(-30F, 0F, 0F), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.75F, KeyframeAnimations.degreeVec(25F, 0F, 0F), AnimationChannel.Interpolations.LINEAR), new Keyframe(1.0F, KeyframeAnimations.degreeVec(0f, 0f, 0f), AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("left_front_leg", new AnimationChannel(AnimationChannel.Targets.ROTATION, new Keyframe(0.25F, KeyframeAnimations.degreeVec(20F, 0F, 0F), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.5F, KeyframeAnimations.degreeVec(0f, 0f, 0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.75F, KeyframeAnimations.degreeVec(-30F, 0F, 0F), AnimationChannel.Interpolations.LINEAR), new Keyframe(1.0F, KeyframeAnimations.degreeVec(0f, 0f, 0f), AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("left_hind_leg", new AnimationChannel(AnimationChannel.Targets.ROTATION, new Keyframe(0.375F, KeyframeAnimations.degreeVec(-30F, 0F, 0F), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.75F, KeyframeAnimations.degreeVec(25F, 0F, 0F), AnimationChannel.Interpolations.LINEAR), new Keyframe(1.0F, KeyframeAnimations.degreeVec(0f, 0f, 0f), AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("right_hind_leg", new AnimationChannel(AnimationChannel.Targets.ROTATION, new Keyframe(0.2917F, KeyframeAnimations.degreeVec(20F, 0F, 0F), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.5833F, KeyframeAnimations.degreeVec(0f, 0f, 0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.7917F, KeyframeAnimations.degreeVec(-30F, 0F, 0F), AnimationChannel.Interpolations.LINEAR), new Keyframe(1.0F, KeyframeAnimations.degreeVec(0f, 0f, 0f), AnimationChannel.Interpolations.LINEAR)))
        .build();

    public static final AnimationDefinition RUN = AnimationDefinition.Builder.withLength(0.7F).looping()
        .addAnimation("right_front_leg", new AnimationChannel(AnimationChannel.Targets.ROTATION, new Keyframe(0.0F, KeyframeAnimations.degreeVec(17.5F, 0F, 0F), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.32F, KeyframeAnimations.degreeVec(-20F, 0F, 0F), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.7F, KeyframeAnimations.degreeVec(17.5F, 0F, 0F), AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("left_front_leg", new AnimationChannel(AnimationChannel.Targets.ROTATION, new Keyframe(0.04F, KeyframeAnimations.degreeVec(17.5F, 0F, 0F), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.36F, KeyframeAnimations.degreeVec(-20F, 0F, 0F), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.7F, KeyframeAnimations.degreeVec(17.5F, 0F, 0F), AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("left_hind_leg", new AnimationChannel(AnimationChannel.Targets.ROTATION, new Keyframe(0.04F, KeyframeAnimations.degreeVec(-15F, 0F, 0F), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.36F, KeyframeAnimations.degreeVec(20F, 0F, 0F), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.7F, KeyframeAnimations.degreeVec(-15F, 0F, 0F), AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("right_hind_leg", new AnimationChannel(AnimationChannel.Targets.ROTATION, new Keyframe(0.0F, KeyframeAnimations.degreeVec(-15F, 0F, 0F), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.32F, KeyframeAnimations.degreeVec(20F, 0F, 0F), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.7F, KeyframeAnimations.degreeVec(-15F, 0F, 0F), AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("body", new AnimationChannel(AnimationChannel.Targets.ROTATION, new Keyframe(0.0F, KeyframeAnimations.degreeVec(0f, 0f, 0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.32F, KeyframeAnimations.degreeVec(-5F, 0F, 0F), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.7F, KeyframeAnimations.degreeVec(0f, 0f, 0f), AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("head", new AnimationChannel(AnimationChannel.Targets.ROTATION, new Keyframe(0.0F, KeyframeAnimations.degreeVec(-10F, 0F, 0F), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.32F, KeyframeAnimations.degreeVec(10F, 0F, 0F), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.7F, KeyframeAnimations.degreeVec(-10F, 0F, 0F), AnimationChannel.Interpolations.LINEAR)))
        .build();

    public static final AnimationDefinition SLEEP = AnimationDefinition.Builder.withLength(2.5F).looping()
        .addAnimation("right_front_leg", new AnimationChannel(AnimationChannel.Targets.ROTATION, new Keyframe(0.0F, KeyframeAnimations.degreeVec(-100F, 0F, 0F), AnimationChannel.Interpolations.LINEAR), new Keyframe(2.5F, KeyframeAnimations.degreeVec(-100F, 0F, 0F), AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("left_front_leg", new AnimationChannel(AnimationChannel.Targets.ROTATION, new Keyframe(0.0F, KeyframeAnimations.degreeVec(-100F, 0F, 0F), AnimationChannel.Interpolations.LINEAR), new Keyframe(2.5F, KeyframeAnimations.degreeVec(-100F, 0F, 0F), AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("left_hind_leg", new AnimationChannel(AnimationChannel.Targets.ROTATION, new Keyframe(0.0F, KeyframeAnimations.degreeVec(-100F, 0F, 0F), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.9167F, KeyframeAnimations.degreeVec(-102.5F, 0F, 0F), AnimationChannel.Interpolations.LINEAR), new Keyframe(2.5F, KeyframeAnimations.degreeVec(-100F, 0F, 0F), AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("right_hind_leg", new AnimationChannel(AnimationChannel.Targets.ROTATION, new Keyframe(0.0F, KeyframeAnimations.degreeVec(-100F, 0F, 0F), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.9167F, KeyframeAnimations.degreeVec(-102.5F, 0F, 0F), AnimationChannel.Interpolations.LINEAR), new Keyframe(2.5F, KeyframeAnimations.degreeVec(-100F, 0F, 0F), AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("body", new AnimationChannel(AnimationChannel.Targets.ROTATION, new Keyframe(0.0F, KeyframeAnimations.degreeVec(10F, 0F, 0F), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.9167F, KeyframeAnimations.degreeVec(12.5F, 0F, 0F), AnimationChannel.Interpolations.LINEAR), new Keyframe(2.5F, KeyframeAnimations.degreeVec(10F, 0F, 0F), AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("head", new AnimationChannel(AnimationChannel.Targets.ROTATION, new Keyframe(0.0F, KeyframeAnimations.degreeVec(34.70381F, 17.89439F, 28.3117F), AnimationChannel.Interpolations.LINEAR), new Keyframe(2.5F, KeyframeAnimations.degreeVec(34.70381F, 17.89439F, 28.3117F), AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("neck", new AnimationChannel(AnimationChannel.Targets.ROTATION, new Keyframe(0.0F, KeyframeAnimations.degreeVec(-355.004F, 18.9931F, -348.65664F), AnimationChannel.Interpolations.LINEAR), new Keyframe(2.5F, KeyframeAnimations.degreeVec(-355.004F, 18.9931F, -348.65664F), AnimationChannel.Interpolations.LINEAR)))
        .build();

    public static final AnimationDefinition ATTACK = AnimationDefinition.Builder.withLength(0.65F)
        .addAnimation("right_front_leg", new AnimationChannel(AnimationChannel.Targets.ROTATION, new Keyframe(0.0F, KeyframeAnimations.degreeVec(0f, 0f, 0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.25F, KeyframeAnimations.degreeVec(-77.58077F, 50.04097F, -37.61043F), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.3333F, KeyframeAnimations.degreeVec(-5.35402F, 39.14934F, -15.61544F), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.4167F, KeyframeAnimations.degreeVec(24.98346F, 39.58329F, -35.66042F), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.65F, KeyframeAnimations.degreeVec(0f, 0f, 0f), AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("left_front_leg", new AnimationChannel(AnimationChannel.Targets.ROTATION, new Keyframe(0.0F, KeyframeAnimations.degreeVec(0f, 0f, 0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.375F, KeyframeAnimations.degreeVec(-88.26784F, -47.13536F, 48.49352F), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.4583F, KeyframeAnimations.degreeVec(-12.45074F, -49.52961F, 14.56411F), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.5417F, KeyframeAnimations.degreeVec(35.22909F, -29.67177F, 26.85242F), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.65F, KeyframeAnimations.degreeVec(0f, 0f, 0f), AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("left_hind_leg", new AnimationChannel(AnimationChannel.Targets.ROTATION, new Keyframe(0.0F, KeyframeAnimations.degreeVec(0f, 0f, 0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.4167F, KeyframeAnimations.degreeVec(40F, 0F, 0F), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.65F, KeyframeAnimations.degreeVec(0f, 0f, 0f), AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("right_hind_leg", new AnimationChannel(AnimationChannel.Targets.ROTATION, new Keyframe(0.0F, KeyframeAnimations.degreeVec(0f, 0f, 0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.4167F, KeyframeAnimations.degreeVec(40F, 0F, 0F), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.65F, KeyframeAnimations.degreeVec(0f, 0f, 0f), AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("body", new AnimationChannel(AnimationChannel.Targets.ROTATION, new Keyframe(0.0F, KeyframeAnimations.degreeVec(0f, 0f, 0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.4167F, KeyframeAnimations.degreeVec(-37.5F, 0F, 0F), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.65F, KeyframeAnimations.degreeVec(0f, 0f, 0f), AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("head", new AnimationChannel(AnimationChannel.Targets.ROTATION, new Keyframe(0.0F, KeyframeAnimations.degreeVec(0f, 0f, 0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.4167F, KeyframeAnimations.degreeVec(10F, 0F, 0F), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.65F, KeyframeAnimations.degreeVec(0f, 0f, 0f), AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("neck", new AnimationChannel(AnimationChannel.Targets.ROTATION, new Keyframe(0.0F, KeyframeAnimations.degreeVec(10F, 0F, 0F), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.4167F, KeyframeAnimations.degreeVec(50F, 0F, 0F), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.65F, KeyframeAnimations.degreeVec(10F, 0F, 0F), AnimationChannel.Interpolations.LINEAR)))
        .build();

    public static final AnimationDefinition SWIM = AnimationDefinition.Builder.withLength(1.0F).looping()
        .addAnimation("right_front_leg", new AnimationChannel(AnimationChannel.Targets.ROTATION, new Keyframe(0.0F, KeyframeAnimations.degreeVec(0F, 0F, 82.5F), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.25F, KeyframeAnimations.degreeVec(-44.58543F, -6.80829F, 75.18027F), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.5833F, KeyframeAnimations.degreeVec(35.22818F, 3.59874F, 79.76447F), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.7917F, KeyframeAnimations.degreeVec(15.47767F, 10.66721F, 94.49245F), AnimationChannel.Interpolations.LINEAR), new Keyframe(1.0F, KeyframeAnimations.degreeVec(0F, 0F, 82.5F), AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("left_front_leg", new AnimationChannel(AnimationChannel.Targets.ROTATION, new Keyframe(0.0F, KeyframeAnimations.degreeVec(0F, 0F, -82.5F), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.2917F, KeyframeAnimations.degreeVec(-44.56145F, 7.05302F, -75.39292F), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.625F, KeyframeAnimations.degreeVec(35.26063F, -4.25476F, -84.31852F), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.8333F, KeyframeAnimations.degreeVec(17.64114F, -6.03989F, -95.48139F), AnimationChannel.Interpolations.LINEAR), new Keyframe(1.0F, KeyframeAnimations.degreeVec(0F, 0F, -82.5F), AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("left_hind_leg", new AnimationChannel(AnimationChannel.Targets.ROTATION, new Keyframe(0.0F, KeyframeAnimations.degreeVec(32.5F, 0F, -82.5F), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.5F, KeyframeAnimations.degreeVec(42.5F, 0F, -82.5F), AnimationChannel.Interpolations.LINEAR), new Keyframe(1.0F, KeyframeAnimations.degreeVec(32.5F, 0F, -82.5F), AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("right_hind_leg", new AnimationChannel(AnimationChannel.Targets.ROTATION, new Keyframe(0.0F, KeyframeAnimations.degreeVec(32.5F, 0F, 82.5F), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.5F, KeyframeAnimations.degreeVec(42.5F, 0F, 82.5F), AnimationChannel.Interpolations.LINEAR), new Keyframe(1.0F, KeyframeAnimations.degreeVec(32.5F, 0F, 82.5F), AnimationChannel.Interpolations.LINEAR)))
        .build();

    private final ModelPart body;
    private final ModelPart neck;
    private final ModelPart head;
    private final ModelPart right_front_leg;
    private final ModelPart left_front_leg;
    private final ModelPart right_hind_leg;
    private final ModelPart left_hind_leg;
    private final ModelPart right_ear;
    private final ModelPart left_ear;

    public BearModel(ModelPart root)
    {
        super(root);
        this.body = root.getChild("body");
        this.neck = body.getChild("neck");
        this.head = neck.getChild("head");
        this.right_ear = head.getChild("right_ear");
        this.left_ear = head.getChild("left_ear");
        this.right_front_leg = body.getChild("right_front_leg");
        this.left_front_leg = body.getChild("left_front_leg");
        this.left_hind_leg = body.getChild("left_hind_leg");
        this.right_hind_leg = body.getChild("right_hind_leg");
    }

    @Override
    public void setupAnim(Predator predator, float limbSwing, float limbSwingAmount, float ageInTicks, float headYaw, float headPitch)
    {
        super.setupAnim(predator, limbSwing, limbSwingAmount, ageInTicks, headYaw, headPitch);

        if (predator.sleepingAnimation.isStarted())
        {
            setupSleeping();
            this.animate(predator.sleepingAnimation, SLEEP, ageInTicks);
        }
        else
        {
            if (predator.isInWaterOrBubble())
            {
                setupSwimming();
                this.animateWalk(SWIM, limbSwing, limbSwingAmount, 1f, 2.5f);
            }
            else
            {
                if (predator.isAggressive() && EntityHelpers.isMovingOnLand(predator))
                {
                    animateWalk(RUN, limbSwing, limbSwingAmount, 1f, 2.5f);
                }
                else
                {
                    animateWalk(WALK, limbSwing, limbSwingAmount, 1f, 2.5f);
                }
                this.animate(predator.attackingAnimation, ATTACK, ageInTicks);
            }
            head.xRot = headPitch * Mth.PI / 180F;
            head.yRot = headYaw * Mth.PI / 180F;
        }
    }

    private void setupSwimming()
    {
        body.y = 22f;
    }

    private void setupSleeping()
    {
        right_front_leg.z = -17.6f;
        body.y = 22f;
    }
}
