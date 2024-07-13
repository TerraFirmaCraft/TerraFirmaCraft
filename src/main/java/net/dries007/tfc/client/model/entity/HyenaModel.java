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

import net.dries007.tfc.common.entities.EntityHelpers;
import net.dries007.tfc.common.entities.ai.predator.PackPredator;

public class HyenaModel extends HierarchicalAnimatedModel<PackPredator>
{
    private final ModelPart head;
    private final ModelPart neck;

    public HyenaModel(ModelPart root)
    {
        super(root);
        this.head = root.getChild("body").getChild("neck").getChild("head");
        this.neck = root.getChild("body").getChild("neck");
    }

    public static LayerDefinition createBodyLayer()
    {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 15).addBox(-3.0F, -14.0F, 2.0F, 6.0F, 6.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 26.0F, -2.0F));

        PartDefinition frillC_r1 = body.addOrReplaceChild("frillC_r1", CubeListBuilder.create().texOffs(0, 35).addBox(-0.01F, -17.25F, -6.75F, 0.0F, 3.0F, 5.0F, new CubeDeformation(0.0F))
            .texOffs(10, 35).addBox(0.49F, -16.25F, -6.75F, 0.0F, 2.0F, 5.0F, new CubeDeformation(0.0F))
            .texOffs(36, 7).addBox(-0.49F, -16.25F, -6.75F, 0.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.1309F, 0.0F, 0.0F));

        PartDefinition body_r1 = body.addOrReplaceChild("body_r1", CubeListBuilder.create().texOffs(0, 0).addBox(-3.5F, -0.25F, -7.0F, 7.0F, 7.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -14.0F, 2.0F, -0.1309F, 0.0F, 0.0F));

        PartDefinition neck = body.addOrReplaceChild("neck", CubeListBuilder.create().texOffs(36, 21).addBox(-1.5F, -1.0086F, -2.8695F, 3.0F, 4.0F, 3.0F, new CubeDeformation(0.0F))
            .texOffs(36, 36).addBox(-0.5F, -3.0F, -4.0F, 0.0F, 2.0F, 5.0F, new CubeDeformation(0.0F))
            .texOffs(9, 22).addBox(0.0F, -4.0F, -5.0F, 0.0F, 3.0F, 6.0F, new CubeDeformation(0.0F))
            .texOffs(36, 34).addBox(0.5F, -3.0F, -4.0F, 0.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -13.75F, -5.0F));

        PartDefinition head = neck.addOrReplaceChild("head", CubeListBuilder.create().texOffs(22, 24).addBox(-2.0F, -3.0F, -3.0F, 4.0F, 5.0F, 4.0F, new CubeDeformation(0.0F))
            .texOffs(38, 0).addBox(-1.5F, -1.0F, -5.0F, 3.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 1.2414F, -3.3695F));

        PartDefinition jaw = head.addOrReplaceChild("jaw", CubeListBuilder.create().texOffs(22, 0).addBox(-1.5F, -0.2462F, -1.9052F, 2.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.5F, 1.0F, -3.0F, 0.0873F, 0.0F, 0.0F));

        PartDefinition earL = head.addOrReplaceChild("earL", CubeListBuilder.create().texOffs(0, 3).addBox(-1.0F, -1.75F, 0.0F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.5F, -2.5F, -0.5F, 0.0F, -0.1745F, -0.1745F));

        PartDefinition earR = head.addOrReplaceChild("earR", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, -2.25F, 0.0F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.5F, -2.0F, -0.5F, 0.0F, 0.1745F, 0.1745F));

        PartDefinition tail = body.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(39, 14).addBox(-1.0F, -0.6005F, 0.058F, 2.0F, 2.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -13.5F, 8.5F, -0.7854F, 0.0F, 0.0F));

        PartDefinition tail1 = tail.addOrReplaceChild("tail1", CubeListBuilder.create().texOffs(38, 5).addBox(-1.01F, 0.0358F, -0.3156F, 2.02F, 3.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.6005F, 3.058F, -0.3491F, 0.0F, 0.0F));

        PartDefinition legFR = body.addOrReplaceChild("legFR", CubeListBuilder.create().texOffs(0, 28).addBox(0.0F, 1.0F, -1.0F, 3.0F, 9.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(-4.0F, -12.0F, -3.0F));

        PartDefinition legFL = body.addOrReplaceChild("legFL", CubeListBuilder.create().texOffs(27, 12).addBox(-3.0F, 1.0F, -1.0F, 3.0F, 9.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(4.0F, -12.0F, -3.0F));

        PartDefinition legBL = body.addOrReplaceChild("legBL", CubeListBuilder.create().texOffs(36, 30).addBox(-2.0F, 7.0F, -1.0F, 3.0F, 6.0F, 3.0F, new CubeDeformation(0.0F))
            .texOffs(24, 33).addBox(-1.0F, 2.0F, -2.0F, 2.0F, 5.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(3.0F, -15.0F, 6.0F));

        PartDefinition legBR = body.addOrReplaceChild("legBR", CubeListBuilder.create().texOffs(12, 31).addBox(0.0F, 7.0F, -2.0F, 3.0F, 6.0F, 3.0F, new CubeDeformation(0.0F))
            .texOffs(30, 0).addBox(0.0F, 2.0F, -3.0F, 2.0F, 5.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-4.0F, -15.0F, 7.0F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    public void setupAnim(PackPredator predator, float limbSwing, float limbSwingAmount, float ageInTicks, float yaw, float pitch)
    {
        super.setupAnim(predator, limbSwing, limbSwingAmount, ageInTicks, yaw, pitch);

        if (predator.sleepingAnimation.isStarted())
        {
            this.animate(predator.sleepingAnimation, HYENA_SLEEP, ageInTicks);
        }
        else
        {
            if (predator.isInWaterOrBubble())
            {
                this.animateWalk(DirewolfModel.DIREWOLF_RUN, limbSwing, limbSwingAmount, 1f, 2.5f);
            }
            else
            {
                if (predator.isAggressive() && EntityHelpers.isMovingOnLand(predator))
                {
                    animateWalk(DirewolfModel.DIREWOLF_RUN, limbSwing, limbSwingAmount, 1f, 2.5f);
                }
                else
                {
                    animateWalk(DirewolfModel.DIREWOLF_WALK, limbSwing, limbSwingAmount, 2.5f, 2.5f);
                }
                this.animate(predator.attackingAnimation, DirewolfModel.DIREWOLF_ATTACK, ageInTicks);
            }
            head.xRot = pitch * Mth.PI / 180F;
            head.yRot = yaw * Mth.PI / 360F;
            neck.yRot = yaw * Mth.PI / 360F;
        }
    }

    public static final AnimationDefinition HYENA_SLEEP = AnimationDefinition.Builder.withLength(4.68f).looping()
        .addAnimation("body",
            new AnimationChannel(AnimationChannel.Targets.POSITION,
                new Keyframe(0f, KeyframeAnimations.posVec(0f, -6.25f, 0f),
                    AnimationChannel.Interpolations.CATMULLROM)))
        .addAnimation("neck",
            new AnimationChannel(AnimationChannel.Targets.POSITION,
                new Keyframe(0f, KeyframeAnimations.posVec(0f, 1f, 0f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("neck",
            new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, KeyframeAnimations.degreeVec(15f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(2.32f, KeyframeAnimations.degreeVec(10.35f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(2.44f, KeyframeAnimations.degreeVec(7.5f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(4.68f, KeyframeAnimations.degreeVec(15f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("tail",
            new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, KeyframeAnimations.degreeVec(9.97f, 2.05f, -1.43f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(2.44f, KeyframeAnimations.degreeVec(9.97f, -2.05f, 1.43f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("tail1",
            new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, KeyframeAnimations.degreeVec(0f, 20f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(2.44f, KeyframeAnimations.degreeVec(0f, -25f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(4.68f, KeyframeAnimations.degreeVec(0f, 20f, 0f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("legFR",
            new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, KeyframeAnimations.degreeVec(-77.5f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("legFL",
            new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, KeyframeAnimations.degreeVec(-80f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("legBL",
            new AnimationChannel(AnimationChannel.Targets.POSITION,
                new Keyframe(0f, KeyframeAnimations.posVec(0f, -5.25f, 6f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("legBL",
            new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, KeyframeAnimations.degreeVec(-90f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("legBR",
            new AnimationChannel(AnimationChannel.Targets.POSITION,
                new Keyframe(0f, KeyframeAnimations.posVec(0f, -6.25f, 5f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("legBR",
            new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, KeyframeAnimations.degreeVec(-90f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR))).build();
}