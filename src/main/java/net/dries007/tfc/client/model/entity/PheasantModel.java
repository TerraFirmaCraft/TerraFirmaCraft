/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

// Made with Blockbench 4.5.2
// Exported for Minecraft version 1.17 - 1.18 with Mojang mappings
// Paste this class into your mod and generate all required imports

package net.dries007.tfc.client.model.entity;

import java.util.stream.Stream;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;

import com.mojang.math.Constants;
import net.dries007.tfc.client.model.animation.AnimationChannel;
import net.dries007.tfc.client.model.animation.AnimationDefinition;
import net.dries007.tfc.client.model.animation.Keyframe;
import net.dries007.tfc.client.model.animation.VanillaAnimations;
import net.dries007.tfc.common.entities.prey.WingedPrey;

public class PheasantModel extends HierarchicalAnimatedModel<WingedPrey>
{
    public static LayerDefinition createBodyLayer()
    {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-2.5F, -6.0F, -6.0F, 5.0F, 6.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 18.0F, -1.0F, -0.5236F, 0.0F, 0.0F));

        PartDefinition legR = body.addOrReplaceChild("legR", CubeListBuilder.create().texOffs(0, 0).addBox(-0.5F, 1.0F, 0.0F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(8, 15).addBox(-1.5F, 5.0F, -2.0F, 3.0F, 0.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.0F, 0.0F, 2.0F, 0.5236F, 0.0F, 0.0F));

        PartDefinition haunch_r1 = legR.addOrReplaceChild("haunch_r1", CubeListBuilder.create().texOffs(13, 29).addBox(-1.0F, -2.0F, -1.5F, 2.0F, 4.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.3054F, 0.0F, 0.0F));

        PartDefinition legL = body.addOrReplaceChild("legL", CubeListBuilder.create().texOffs(4, 0).addBox(-0.5F, 1.0F, 0.0F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(18, 15).addBox(-1.5F, 5.0F, -2.0F, 3.0F, 0.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.0F, 0.0F, 2.0F, 0.5236F, 0.0F, 0.0F));

        PartDefinition haunch_r2 = legL.addOrReplaceChild("haunch_r2", CubeListBuilder.create().texOffs(0, 31).addBox(-1.0F, -2.0F, -1.5F, 2.0F, 4.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.3054F, 0.0F, 0.0F));

        PartDefinition wingR = body.addOrReplaceChild("wingR", CubeListBuilder.create(), PartPose.offset(-2.5F, -5.0F, -1.0F));

        PartDefinition main_r1 = wingR.addOrReplaceChild("main_r1", CubeListBuilder.create().texOffs(13, 17).addBox(-1.0F, -1.0F, -2.0F, 1.0F, 4.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 1.0F, -2.0F, -0.0873F, 0.0F, 0.0F));

        PartDefinition neck = body.addOrReplaceChild("neck", CubeListBuilder.create(), PartPose.offsetAndRotation(0.5F, -4.0F, -4.5F, 0.3054F, 0.0F, 0.0F));

        PartDefinition cube1_r1 = neck.addOrReplaceChild("cube1_r1", CubeListBuilder.create().texOffs(31, 18).addBox(-2.0F, -2.0F, -3.0F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.0F, -3.0F, -1.0036F, 0.0F, 0.0F));

        PartDefinition cube_r1 = neck.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(21, 0).addBox(-2.5028F, -3.7661F, -1.1073F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 1.9166F, -0.909F, 0.6981F, 0.0044F, 0.0028F));

        PartDefinition head = neck.addOrReplaceChild("head", CubeListBuilder.create().texOffs(28, 12).addBox(-1.51F, -2.0436F, -1.001F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.5F, -3.8334F, -4.909F, 0.3054F, 0.0F, 0.0F));

        PartDefinition beak_r1 = head.addOrReplaceChild("beak_r1", CubeListBuilder.create().texOffs(0, 5).addBox(-0.5F, 2.5F, -2.25F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -3.0436F, -1.001F, 0.1745F, 0.0F, 0.0F));

        PartDefinition wingL = body.addOrReplaceChild("wingL", CubeListBuilder.create(), PartPose.offset(2.5F, -6.0F, -1.0F));

        PartDefinition main_r2 = wingL.addOrReplaceChild("main_r2", CubeListBuilder.create().texOffs(22, 21).addBox(0.0F, -1.0F, -2.0F, 1.0F, 4.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 2.0F, -2.0F, -0.0873F, 0.0F, 0.0F));

        PartDefinition tail = body.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(0, 24).addBox(-2.0F, -2.0F, -1.25F, 4.0F, 3.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -3.25F, 3.25F, 0.3927F, 0.0F, 0.0F));

        PartDefinition end_r1 = tail.addOrReplaceChild("end_r1", CubeListBuilder.create().texOffs(0, 15).addBox(-1.0F, -1.5F, 0.75F, 2.0F, 1.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 2.0F, 0.1745F, 0.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }


    public static final AnimationDefinition PHEASANT_WALK = AnimationDefinition.Builder.withLength(1.0417f).looping()
        .addAnimation("body",
            new AnimationChannel(AnimationChannel.Targets.POSITION,
                new Keyframe(0f, VanillaAnimations.posVec(0f, -0.30000000000000004f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(1.0416767f, VanillaAnimations.posVec(0f, -0.3f, 0f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("body",
            new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, VanillaAnimations.degreeVec(15f, 0f, -1.5f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.25f, VanillaAnimations.degreeVec(25f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.5f, VanillaAnimations.degreeVec(20f, 0f, 1.5f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.75f, VanillaAnimations.degreeVec(25f, 0f, 0.11547f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(1.0417f, VanillaAnimations.degreeVec(15f, 0f, -1.5f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("legR",
            new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, VanillaAnimations.degreeVec(-15f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.25f, VanillaAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.75f, VanillaAnimations.degreeVec(-60f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(1.0417f, VanillaAnimations.degreeVec(-15f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("legL",
            new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, VanillaAnimations.degreeVec(-15f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.25f, VanillaAnimations.degreeVec(-60f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.75f, VanillaAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(1.0417f, VanillaAnimations.degreeVec(-15f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("neck",
            new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, VanillaAnimations.degreeVec(0f, 0f, 1f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.25f, VanillaAnimations.degreeVec(12.476380948833594f, -1.2497024866634092f, 2.1654069746787172f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.5f, VanillaAnimations.degreeVec(0f, 0f, -1f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.75f, VanillaAnimations.degreeVec(9.910628628178983f, 2.3064366979988336f, -4.437453245269808f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(1.0417f, VanillaAnimations.degreeVec(0f, 0f, 1f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("head",
            new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, VanillaAnimations.degreeVec(-15f, 0f, 0.5f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.25f, VanillaAnimations.degreeVec(-40f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.5f, VanillaAnimations.degreeVec(-17.5f, 0f, -0.5f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.75f, VanillaAnimations.degreeVec(-32.5f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(1.0416767f, VanillaAnimations.degreeVec(-15f, 0f, 0.5f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("tail",
            new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, VanillaAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.25f, VanillaAnimations.degreeVec(-10f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.75f, VanillaAnimations.degreeVec(-5f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(1.0416767f, VanillaAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR))).build();

    private final ModelPart neck;
    private final ModelPart body;
    private final ModelPart head;
    private final ModelPart legR;
    private final ModelPart legL;
    private final ModelPart wingR;
    private final ModelPart wingL;
    private final ModelPart tail;

    public PheasantModel(ModelPart root)
    {
        super(root);
        this.body = root.getChild("body");
        this.neck = body.getChild("neck");
        this.head = neck.getChild("head");
        this.legR = body.getChild("legR");
        this.legL = body.getChild("legL");
        this.wingR = body.getChild("wingR");
        this.wingL = body.getChild("wingL");
        this.tail = body.getChild("tail");
    }

    @Override
    public void setupAnim(WingedPrey entity, float limbSwing, float limbSwingAmount, float ageInTicks, float headYaw, float headPitch)
    {
        super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, headYaw, headPitch);
        final float speed = getAdjustedLandSpeed(entity);
        this.animate(entity.walkingAnimation, PHEASANT_WALK, ageInTicks, speed);
        if (!entity.isOnGround())
        {
            wingR.zRot = ageInTicks;
            wingL.zRot = -ageInTicks;
        }

        this.neck.xRot = headPitch * Constants.DEG_TO_RAD;
        this.neck.yRot = headYaw * Constants.DEG_TO_RAD;
    }
}