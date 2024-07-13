/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

// Made with Blockbench 4.8.3
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports

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

import net.dries007.tfc.common.entities.predator.AmphibiousPredator;

public class CrocodileModel extends HierarchicalAnimatedModel<AmphibiousPredator>
{
    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-6.0F, -3.25F, -8.0F, 12.0F, 8.0F, 18.0F, new CubeDeformation(0.0F))
            .texOffs(0, 32).addBox(-3.0F, -4.25F, -8.0F, 0.0F, 1.0F, 18.0F, new CubeDeformation(0.0F))
            .texOffs(0, 31).addBox(3.0F, -4.25F, -8.0F, 0.0F, 1.0F, 18.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 18.25F, 0.0F));

        PartDefinition neck = body.addOrReplaceChild("neck", CubeListBuilder.create().texOffs(42, 0).addBox(-5.5F, -3.0F, -7.0F, 11.0F, 8.0F, 7.0F, new CubeDeformation(0.0F))
            .texOffs(0, 7).addBox(3.0F, -4.0F, -7.0F, 0.0F, 1.0F, 7.0F, new CubeDeformation(0.0F))
            .texOffs(0, 0).addBox(-3.0F, -4.0F, -7.0F, 0.0F, 1.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.75F, -6.75F, -0.1309F, 0.0F, 0.0F));

        PartDefinition head = neck.addOrReplaceChild("head", CubeListBuilder.create().texOffs(60, 58).addBox(-5.0F, -4.0F, -5.0F, 10.0F, 6.0F, 7.0F, new CubeDeformation(0.0F))
            .texOffs(42, 43).addBox(-3.0F, -1.0F, -17.0F, 6.0F, 3.0F, 12.0F, new CubeDeformation(0.0F))
            .texOffs(0, 51).addBox(-3.0F, -3.0F, -17.0F, 6.0F, 2.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 3.0F, -7.25F, 0.1309F, 0.0F, 0.0F));

        PartDefinition jaw_top = head.addOrReplaceChild("jaw_top", CubeListBuilder.create().texOffs(0, 26).addBox(-4.0F, -5.0F, -18.0F, 8.0F, 4.0F, 19.0F, new CubeDeformation(0.0F))
            .texOffs(66, 28).addBox(-4.0F, -6.0F, -4.0F, 8.0F, 1.0F, 5.0F, new CubeDeformation(0.0F))
            .texOffs(35, 26).addBox(-4.0F, -1.0F, -18.0F, 8.0F, 2.0F, 15.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition tail0 = body.addOrReplaceChild("tail0", CubeListBuilder.create().texOffs(28, 58).addBox(-4.0F, -4.05F, 0.0F, 8.0F, 7.0F, 8.0F, new CubeDeformation(0.0F))
            .texOffs(0, 4).addBox(3.0F, -5.0F, 0.0F, 0.0F, 1.0F, 8.0F, new CubeDeformation(0.0F))
            .texOffs(0, 5).addBox(-3.0F, -5.0F, 0.0F, 0.0F, 1.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 1.75F, 10.0F));

        PartDefinition tail1 = tail0.addOrReplaceChild("tail1", CubeListBuilder.create().texOffs(0, 65).addBox(-3.0F, -3.1F, 0.0F, 6.0F, 6.0F, 9.0F, new CubeDeformation(0.0F))
            .texOffs(0, 0).addBox(2.0F, -4.0F, 0.0F, 0.0F, 1.0F, 9.0F, new CubeDeformation(0.0F))
            .texOffs(0, 1).addBox(-2.0F, -4.0F, 0.0F, 0.0F, 1.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 8.0F));

        PartDefinition bone = tail1.addOrReplaceChild("bone", CubeListBuilder.create().texOffs(66, 15).addBox(-2.0F, -2.15F, 0.0F, 4.0F, 5.0F, 8.0F, new CubeDeformation(0.0F))
            .texOffs(0, 0).addBox(1.0F, -3.0F, 0.0F, 0.0F, 1.0F, 8.0F, new CubeDeformation(0.0F))
            .texOffs(0, 3).addBox(-1.0F, -3.0F, 0.0F, 0.0F, 1.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 9.0F));

        PartDefinition right_hind_leg = body.addOrReplaceChild("right_hind_leg", CubeListBuilder.create().texOffs(0, 26).addBox(-1.0F, -2.0F, -4.0F, 4.0F, 5.0F, 4.0F, new CubeDeformation(0.0F))
            .texOffs(0, 0).addBox(-1.0F, 3.0F, -5.0F, 4.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(-7.0F, 0.75F, 12.0F));

        PartDefinition right_front_leg = body.addOrReplaceChild("right_front_leg", CubeListBuilder.create().texOffs(66, 43).addBox(-1.0F, -2.0F, -4.0F, 4.0F, 5.0F, 4.0F, new CubeDeformation(0.0F))
            .texOffs(0, 35).addBox(-1.0F, 3.0F, -5.0F, 4.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(-7.0F, 0.75F, -5.0F));

        PartDefinition left_front_leg = body.addOrReplaceChild("left_front_leg", CubeListBuilder.create().texOffs(72, 71).addBox(-1.0F, -2.0F, -4.0F, 4.0F, 5.0F, 4.0F, new CubeDeformation(0.0F))
            .texOffs(66, 34).addBox(-1.0F, 3.0F, -5.0F, 4.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(5.0F, 0.75F, -5.0F));

        PartDefinition left_hind_leg = body.addOrReplaceChild("left_hind_leg", CubeListBuilder.create().texOffs(56, 71).addBox(-1.0F, -2.0F, -4.0F, 4.0F, 5.0F, 4.0F, new CubeDeformation(0.0F))
            .texOffs(24, 51).addBox(-1.0F, 3.0F, -5.0F, 4.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(5.0F, 0.75F, 12.0F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }

    private final ModelPart body;
    private final ModelPart neck;
    private final ModelPart head;
    private final ModelPart right_front_leg;
    private final ModelPart left_front_leg;
    private final ModelPart right_hind_leg;
    private final ModelPart left_hind_leg;
    private final ModelPart jaw_top;

    public CrocodileModel(ModelPart root)
    {
        super(root);
        this.body = root.getChild("body");
        this.neck = body.getChild("neck");
        this.head = neck.getChild("head");
        this.jaw_top = head.getChild("jaw_top");
        this.right_front_leg = body.getChild("right_front_leg");
        this.left_front_leg = body.getChild("left_front_leg");
        this.left_hind_leg = body.getChild("left_hind_leg");
        this.right_hind_leg = body.getChild("right_hind_leg");
    }

    @Override
    public void setupAnim(AmphibiousPredator predator, float limbSwing, float limbSwingAmount, float ageInTicks, float headYaw, float headPitch)
    {
        super.setupAnim(predator, limbSwing, limbSwingAmount, ageInTicks, headYaw, headPitch);

        if (predator.sleepingAnimation.isStarted())
        {
            this.animate(predator.sleepingAnimation, SLEEP, ageInTicks);
        }
        else
        {
            if (predator.isInWaterOrBubble())
            {
                this.animateWalk(SWIM, limbSwing, limbSwingAmount, 1f, 2.5f);
                this.animate(predator.attackingAnimation, BITE_ROLL, ageInTicks);
            }
            else
            {
                this.animateWalk(WALK, limbSwing, limbSwingAmount, 1f, 2.5f);
                this.animate(predator.attackingAnimation, BITE, ageInTicks);
            }
            head.yRot = headYaw * Mth.PI / 180F;
        }
    }

    public static final AnimationDefinition SWIM = AnimationDefinition.Builder.withLength(0.625f).looping()
        .addAnimation("body",
            new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, KeyframeAnimations.degreeVec(-5f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.20834334f, KeyframeAnimations.degreeVec(-2.5f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.625f, KeyframeAnimations.degreeVec(-5f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("neck",
            new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, KeyframeAnimations.degreeVec(-5f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.20834334f, KeyframeAnimations.degreeVec(-7.5f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.625f, KeyframeAnimations.degreeVec(-5f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("head",
            new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, KeyframeAnimations.degreeVec(10f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.625f, KeyframeAnimations.degreeVec(10f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("tail0",
            new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, KeyframeAnimations.degreeVec(5.32f, -9.92f, -1.82f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.3433333f, KeyframeAnimations.degreeVec(5.32f, 9.92f, 1.82f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.625f, KeyframeAnimations.degreeVec(5.32f, -9.92f, -1.82f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("tail1",
            new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, KeyframeAnimations.degreeVec(0f, 5f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.08343333f, KeyframeAnimations.degreeVec(0f, -10f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.4167667f, KeyframeAnimations.degreeVec(0f, 10f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.625f, KeyframeAnimations.degreeVec(0f, 5f, 0f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("bone",
            new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, KeyframeAnimations.degreeVec(0f, 8f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.20834334f, KeyframeAnimations.degreeVec(0f, -16f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.5416766f, KeyframeAnimations.degreeVec(0f, 16f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.625f, KeyframeAnimations.degreeVec(0f, 8f, 0f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("right_hind_leg",
            new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, KeyframeAnimations.degreeVec(0f, 0f, 50f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.16766666f, KeyframeAnimations.degreeVec(40f, 0f, 50f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.5416766f, KeyframeAnimations.degreeVec(0f, 0f, 50f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("right_front_leg",
            new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, KeyframeAnimations.degreeVec(0f, 0f, 50f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.16766666f, KeyframeAnimations.degreeVec(40f, 0f, 50f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.5416766f, KeyframeAnimations.degreeVec(0f, 0f, 50f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("left_front_leg",
            new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, KeyframeAnimations.degreeVec(0f, 0f, -50f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.16766666f, KeyframeAnimations.degreeVec(40f, 0f, -50f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.5416766f, KeyframeAnimations.degreeVec(0f, 0f, -50f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("left_hind_leg",
            new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, KeyframeAnimations.degreeVec(0f, 0f, -50f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.16766666f, KeyframeAnimations.degreeVec(40f, 0f, -50f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.5416766f, KeyframeAnimations.degreeVec(0f, 0f, -50f),
                    AnimationChannel.Interpolations.LINEAR))).build();
    public static final AnimationDefinition WALK = AnimationDefinition.Builder.withLength(0.625f).looping()
        .addAnimation("tail0",
            new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, KeyframeAnimations.degreeVec(5.32f, -4.92f, -1.82f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.3433333f, KeyframeAnimations.degreeVec(5.32f, 4.92f, 1.82f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.625f, KeyframeAnimations.degreeVec(5.32f, -4.92f, -1.82f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("tail1",
            new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, KeyframeAnimations.degreeVec(0f, 3f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.08343333f, KeyframeAnimations.degreeVec(0f, -6f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.4167667f, KeyframeAnimations.degreeVec(0f, 6f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.625f, KeyframeAnimations.degreeVec(0f, 3f, 0f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("bone",
            new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, KeyframeAnimations.degreeVec(0f, 4f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.20834334f, KeyframeAnimations.degreeVec(0f, -8f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.5416766f, KeyframeAnimations.degreeVec(0f, 8f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.625f, KeyframeAnimations.degreeVec(0f, 4f, 0f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("right_hind_leg",
            new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.125f, KeyframeAnimations.degreeVec(17.5f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.2916767f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.4583433f, KeyframeAnimations.degreeVec(-22.5f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.625f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("right_front_leg",
            new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.125f, KeyframeAnimations.degreeVec(-20f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.2916767f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.4583433f, KeyframeAnimations.degreeVec(20f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.625f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("left_front_leg",
            new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.125f, KeyframeAnimations.degreeVec(22.5f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.2916767f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.4583433f, KeyframeAnimations.degreeVec(-20f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.625f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("left_hind_leg",
            new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.125f, KeyframeAnimations.degreeVec(-22.5f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.2916767f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.4583433f, KeyframeAnimations.degreeVec(35f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.625f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR))).build();
    public static final AnimationDefinition BITE = AnimationDefinition.Builder.withLength(0.25f)
        .addAnimation("neck",
            new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.16766666f, KeyframeAnimations.degreeVec(-10f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.25f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("head",
            new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.16766666f, KeyframeAnimations.degreeVec(27.5f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.25f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("jaw_top",
            new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.16766666f, KeyframeAnimations.degreeVec(-50f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.25f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR))).build();
    public static final AnimationDefinition BITE_ROLL = AnimationDefinition.Builder.withLength(0.7083434f)
        .addAnimation("body",
            new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.125f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.4167667f, KeyframeAnimations.degreeVec(0f, 0f, -180f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.7083434f, KeyframeAnimations.degreeVec(0f, 0f, -360f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("neck",
            new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.125f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.20834334f, KeyframeAnimations.degreeVec(0f, 0f, 20f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.7083434f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("head",
            new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.125f, KeyframeAnimations.degreeVec(17.5f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.20834334f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.4167667f, KeyframeAnimations.degreeVec(7.5f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.5f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.7083434f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("jaw_top",
            new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.125f, KeyframeAnimations.degreeVec(-42.5f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.20834334f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.4167667f, KeyframeAnimations.degreeVec(-20.5f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.5f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.7083434f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("tail0",
            new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, KeyframeAnimations.degreeVec(0f, -10f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.16766666f, KeyframeAnimations.degreeVec(0f, 12.5f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.7083434f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("tail1",
            new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, KeyframeAnimations.degreeVec(0f, -10f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.16766666f, KeyframeAnimations.degreeVec(0f, 10f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.7083434f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("bone",
            new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, KeyframeAnimations.degreeVec(0f, -10f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.16766666f, KeyframeAnimations.degreeVec(0f, 20f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.7083434f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("right_hind_leg",
            new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.08343333f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.16766666f, KeyframeAnimations.degreeVec(0f, 0f, -17.5f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.7083434f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("right_front_leg",
            new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.08343333f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.16766666f, KeyframeAnimations.degreeVec(0f, 0f, -12.5f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.7083434f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("left_front_leg",
            new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.08343333f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.16766666f, KeyframeAnimations.degreeVec(0f, 0f, 22.5f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.7083434f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("left_hind_leg",
            new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.08343333f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.16766666f, KeyframeAnimations.degreeVec(0f, 0f, 17.5f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.7083434f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR))).build();
    public static final AnimationDefinition SLEEP = AnimationDefinition.Builder.withLength(1f)
        .addAnimation("body",
            new AnimationChannel(AnimationChannel.Targets.POSITION,
                new Keyframe(0f, KeyframeAnimations.posVec(0f, -1f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.5f, KeyframeAnimations.posVec(0f, -1f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(1f, KeyframeAnimations.posVec(0f, -1f, 0f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("neck",
            new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, KeyframeAnimations.degreeVec(0f, 30f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.5f, KeyframeAnimations.degreeVec(0f, 30f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(1f, KeyframeAnimations.degreeVec(0f, 30f, 0f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("head",
            new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, KeyframeAnimations.degreeVec(10.4f, 17.26f, 1.57f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.5f, KeyframeAnimations.degreeVec(7.9f, 17.26f, 1.57f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(1f, KeyframeAnimations.degreeVec(10.4f, 17.26f, 1.57f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("jaw_top",
            new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, KeyframeAnimations.degreeVec(5f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.5f, KeyframeAnimations.degreeVec(5f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(1f, KeyframeAnimations.degreeVec(5f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("tail0",
            new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, KeyframeAnimations.degreeVec(0f, -40f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.5f, KeyframeAnimations.degreeVec(0f, -40f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(1f, KeyframeAnimations.degreeVec(0f, -40f, 0f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("tail1",
            new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, KeyframeAnimations.degreeVec(0f, -37.5f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.5f, KeyframeAnimations.degreeVec(0f, -37.5f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(1f, KeyframeAnimations.degreeVec(0f, -37.5f, 0f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("bone",
            new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, KeyframeAnimations.degreeVec(0f, -30f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.5f, KeyframeAnimations.degreeVec(0f, -30f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(1f, KeyframeAnimations.degreeVec(0f, -30f, 0f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("right_hind_leg",
            new AnimationChannel(AnimationChannel.Targets.POSITION,
                new Keyframe(0f, KeyframeAnimations.posVec(0f, 1f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.5f, KeyframeAnimations.posVec(0f, 1f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(1f, KeyframeAnimations.posVec(0f, 1f, 0f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("right_front_leg",
            new AnimationChannel(AnimationChannel.Targets.POSITION,
                new Keyframe(0f, KeyframeAnimations.posVec(0f, 1f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.5f, KeyframeAnimations.posVec(0f, 1f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(1f, KeyframeAnimations.posVec(0f, 1f, 0f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("left_front_leg",
            new AnimationChannel(AnimationChannel.Targets.POSITION,
                new Keyframe(0f, KeyframeAnimations.posVec(0f, 1f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.5f, KeyframeAnimations.posVec(0f, 1f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(1f, KeyframeAnimations.posVec(0f, 1f, 0f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("left_hind_leg",
            new AnimationChannel(AnimationChannel.Targets.POSITION,
                new Keyframe(0f, KeyframeAnimations.posVec(0f, 1f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.5f, KeyframeAnimations.posVec(0f, 1f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(1f, KeyframeAnimations.posVec(0f, 1f, 0f),
                    AnimationChannel.Interpolations.LINEAR))).build();
    public static final AnimationDefinition GAPE = AnimationDefinition.Builder.withLength(1f)
        .addAnimation("neck",
            new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, KeyframeAnimations.degreeVec(-7.5f, 17.5f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.5f, KeyframeAnimations.degreeVec(-7.5f, 17.5f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(1f, KeyframeAnimations.degreeVec(-7.5f, 17.5f, 0f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("head",
            new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, KeyframeAnimations.degreeVec(20f, 17.5f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(1f, KeyframeAnimations.degreeVec(20f, 17.5f, 0f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("jaw_top",
            new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, KeyframeAnimations.degreeVec(-32.5f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.5f, KeyframeAnimations.degreeVec(-30f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(1f, KeyframeAnimations.degreeVec(-32.5f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("tail0",
            new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, KeyframeAnimations.degreeVec(0f, -22.5f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.5f, KeyframeAnimations.degreeVec(0f, -25f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(1f, KeyframeAnimations.degreeVec(0f, -22.5f, 0f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("tail1",
            new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, KeyframeAnimations.degreeVec(0f, -22.5f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.5f, KeyframeAnimations.degreeVec(0f, -25f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(1f, KeyframeAnimations.degreeVec(0f, -22.5f, 0f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("bone",
            new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, KeyframeAnimations.degreeVec(0f, -22.5f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.5f, KeyframeAnimations.degreeVec(0f, -25f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(1f, KeyframeAnimations.degreeVec(0f, -22.5f, 0f),
                    AnimationChannel.Interpolations.LINEAR))).build();
}