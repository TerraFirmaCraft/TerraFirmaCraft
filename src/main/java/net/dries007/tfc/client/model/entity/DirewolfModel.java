/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.model.entity;

import com.mojang.math.Constants;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;

import net.dries007.tfc.client.model.animation.AnimationChannel;
import net.dries007.tfc.client.model.animation.AnimationDefinition;
import net.dries007.tfc.client.model.animation.Keyframe;
import net.dries007.tfc.client.model.animation.VanillaAnimations;
import net.dries007.tfc.common.entities.ai.predator.PackPredator;

public class DirewolfModel extends HierarchicalAnimatedModel<PackPredator>
{
    public static LayerDefinition createBodyLayer()
    {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-4.5F, -16.0F, -8.0F, 9.0F, 9.0F, 12.0F, new CubeDeformation(0.0F))
            .texOffs(0, 21).addBox(-4.0F, -16.0F, 4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 23.0F, -2.0F));

        PartDefinition neck = body.addOrReplaceChild("neck", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition neck0_r1 = neck.addOrReplaceChild("neck0_r1", CubeListBuilder.create().texOffs(25, 30).addBox(-4.0F, -3.5F, -3.0F, 8.0F, 7.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -14.0F, -8.0F, -0.4363F, 0.0F, 0.0F));

        PartDefinition head = neck.addOrReplaceChild("head", CubeListBuilder.create().texOffs(30, 0).addBox(-3.5F, -4.0F, -5.0F, 7.0F, 7.0F, 4.0F, new CubeDeformation(0.0F))
            .texOffs(48, 13).addBox(-3.0F, -3.5F, -7.0F, 6.0F, 6.0F, 2.0F, new CubeDeformation(0.0F))
            .texOffs(12, 56).addBox(-2.0F, -1.5F, -10.0F, 4.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -14.5F, -8.0F));

        PartDefinition jaw = head.addOrReplaceChild("jaw", CubeListBuilder.create().texOffs(48, 7).addBox(-1.5F, 0.75F, -3.9924F, 3.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.75F, -6.0F, 0.0873F, 0.0F, 0.0F));

        PartDefinition earL = head.addOrReplaceChild("earL", CubeListBuilder.create().texOffs(0, 24).addBox(-1.0F, -1.5F, 0.0F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.0F, -4.0F, -4.5F, 0.0F, -0.1745F, -0.1745F));

        PartDefinition earR = head.addOrReplaceChild("earR", CubeListBuilder.create().texOffs(0, 21).addBox(-1.0F, -1.5F, 0.0F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.0F, -4.0F, -4.5F, 0.0F, 0.1745F, 0.1745F));

        PartDefinition tail = body.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(11, 37).addBox(-2.0F, -1.5F, 0.0F, 2.0F, 2.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, -14.0F, 11.0F, -1.0472F, 0.0F, 0.0F));

        PartDefinition tail1 = tail.addOrReplaceChild("tail1", CubeListBuilder.create().texOffs(22, 56).addBox(-2.0F, -0.934F, -0.4866F, 3.0F, 3.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.5F, -1.0F, 3.0F, -0.3491F, 0.0F, 0.0F));

        PartDefinition tail2 = tail1.addOrReplaceChild("tail2", CubeListBuilder.create().texOffs(24, 21).addBox(-1.0F, -1.0F, -0.25F, 2.0F, 2.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(-0.5F, 0.566F, 5.3794F));

        PartDefinition legFR = body.addOrReplaceChild("legFR", CubeListBuilder.create().texOffs(43, 44).addBox(-1.0F, 4.0F, -1.0F, 3.0F, 7.0F, 3.0F, new CubeDeformation(0.0F))
            .texOffs(52, 40).addBox(-1.0F, 11.0F, -2.0F, 3.0F, 2.0F, 4.0F, new CubeDeformation(0.0F))
            .texOffs(37, 16).addBox(-1.0F, -3.0F, -2.0F, 3.0F, 7.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(-4.5F, -12.0F, -4.0F));

        PartDefinition legFL = body.addOrReplaceChild("legFL", CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, 4.0F, -1.0F, 3.0F, 7.0F, 3.0F, new CubeDeformation(0.0F))
            .texOffs(55, 46).addBox(-2.0F, 11.0F, -2.0F, 3.0F, 2.0F, 4.0F, new CubeDeformation(0.0F))
            .texOffs(0, 37).addBox(-2.0F, -3.0F, -2.0F, 3.0F, 7.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(4.5F, -12.0F, -4.0F));

        PartDefinition legBL = body.addOrReplaceChild("legBL", CubeListBuilder.create().texOffs(52, 52).addBox(-2.0F, 5.0F, 0.0F, 3.0F, 6.0F, 3.0F, new CubeDeformation(0.0F))
            .texOffs(27, 44).addBox(-2.0F, -2.0F, -2.0F, 3.0F, 7.0F, 5.0F, new CubeDeformation(0.0F))
            .texOffs(52, 0).addBox(-2.0F, 11.0F, -1.0F, 3.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(4.0F, -12.0F, 8.0F));

        PartDefinition legBR = body.addOrReplaceChild("legBR", CubeListBuilder.create().texOffs(50, 25).addBox(-1.0F, 5.0F, -1.0F, 3.0F, 6.0F, 3.0F, new CubeDeformation(0.0F))
            .texOffs(11, 44).addBox(-1.0F, -2.0F, -3.0F, 3.0F, 7.0F, 5.0F, new CubeDeformation(0.0F))
            .texOffs(0, 52).addBox(-1.0F, 11.0F, -2.0F, 3.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-4.0F, -12.0F, 9.0F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }


    public static final AnimationDefinition DIREWOLF_RUN = AnimationDefinition.Builder.withLength(0.5f).looping()
        .addAnimation("body",
            new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, VanillaAnimations.degreeVec(0f, 0f, 0f), AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.1667f, VanillaAnimations.degreeVec(-7.5f, 0f, 0f), AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.3333f, VanillaAnimations.degreeVec(0f, 0f, 0f), AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.4167f, VanillaAnimations.degreeVec(4.69f, 0f, 0f), AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.5f, VanillaAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("tail",
            new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, VanillaAnimations.degreeVec(30f, 0f, 0f), AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.2083f, VanillaAnimations.degreeVec(90f, 0f, 0f), AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.5f, VanillaAnimations.degreeVec(30f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("head",
            new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, VanillaAnimations.degreeVec(0f, 0f, 0f), AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.1667f, VanillaAnimations.degreeVec(7.5f, 0f, 0f), AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.3333f, VanillaAnimations.degreeVec(0f, 0f, 0f), AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.4167f, VanillaAnimations.degreeVec(-4.69f, 0f, 0f), AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.5f, VanillaAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("legFR",
            new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, VanillaAnimations.degreeVec(0f, 0f, 0f), AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.1667f, VanillaAnimations.degreeVec(-32.5f, 0f, 0f), AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.375f, VanillaAnimations.degreeVec(25.5f, 0f, 0f), AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.5f, VanillaAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("legFL",
            new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, VanillaAnimations.degreeVec(0f, 0f, 0f), AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.125f, VanillaAnimations.degreeVec(-32.5f, 0f, 0f), AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.3333f, VanillaAnimations.degreeVec(25.5f, 0f, 0f), AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.5f, VanillaAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("legBL",
            new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, VanillaAnimations.degreeVec(0f, 0f, 0f), AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.125f, VanillaAnimations.degreeVec(40f, 0f, 0f), AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.3333f, VanillaAnimations.degreeVec(-30f, 0f, 0f), AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.5f, VanillaAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("legBR",
            new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, VanillaAnimations.degreeVec(0f, 0f, 0f), AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.1667f, VanillaAnimations.degreeVec(40f, 0f, 0f), AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.375f, VanillaAnimations.degreeVec(-30f, 0f, 0f), AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.5f, VanillaAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("tail1",
            new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, VanillaAnimations.degreeVec(-10f, 0f, 0f), AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.25f, VanillaAnimations.degreeVec(17.5f, 0f, 0f), AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.5f, VanillaAnimations.degreeVec(-10f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("tail2",
            new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, VanillaAnimations.degreeVec(-10f, 0f, 0f), AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.2916767f, VanillaAnimations.degreeVec(12.5f, 0f, 0f), AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.5f, VanillaAnimations.degreeVec(-10f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR))).build();
    public static final AnimationDefinition DIREWOLF_WALK = AnimationDefinition.Builder.withLength(1f).looping()
        .addAnimation("neck",
            new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, VanillaAnimations.degreeVec(0f, 0f, 0f), AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.2917f, VanillaAnimations.degreeVec(1f, 0f, 0f), AnimationChannel.Interpolations.LINEAR),
                new Keyframe(1f, VanillaAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("head",
            new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, VanillaAnimations.degreeVec(2f, 0f, 0f), AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.3333f, VanillaAnimations.degreeVec(0f, 0f, 0f), AnimationChannel.Interpolations.LINEAR),
                new Keyframe(1f, VanillaAnimations.degreeVec(2f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("tail",
            new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, VanillaAnimations.degreeVec(0f, 0f, 0f), AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.3333f, VanillaAnimations.degreeVec(10f, 0f, 0f), AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.625f, VanillaAnimations.degreeVec(3f, 0f, 0f), AnimationChannel.Interpolations.LINEAR),
                new Keyframe(1f, VanillaAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("legBR",
            new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, VanillaAnimations.degreeVec(0f, 0f, 0f), AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.4167f, VanillaAnimations.degreeVec(22.5f, 0f, 0f), AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.75f, VanillaAnimations.degreeVec(-27.5f, 0f, 0f), AnimationChannel.Interpolations.LINEAR),
                new Keyframe(1f, VanillaAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("legBL",
            new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, VanillaAnimations.degreeVec(0f, 0f, 0f), AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.1667f, VanillaAnimations.degreeVec(-27.5f, 0f, 0f), AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.8333f, VanillaAnimations.degreeVec(22.5f, 0f, 0f), AnimationChannel.Interpolations.LINEAR),
                new Keyframe(1f, VanillaAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("legFR",
            new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, VanillaAnimations.degreeVec(0f, 0f, 0f), AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.1667f, VanillaAnimations.degreeVec(-27.5f, 0f, 0f), AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.7917f, VanillaAnimations.degreeVec(22.5f, 0f, 0f), AnimationChannel.Interpolations.LINEAR),
                new Keyframe(1f, VanillaAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("legFL",
            new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, VanillaAnimations.degreeVec(0f, 0f, 0f), AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.4167f, VanillaAnimations.degreeVec(22.5f, 0f, 0f), AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.75f, VanillaAnimations.degreeVec(-27.5f, 0f, 0f), AnimationChannel.Interpolations.LINEAR),
                new Keyframe(1f, VanillaAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR))).build();
    public static final AnimationDefinition DIREWOLF_SLEEP = AnimationDefinition.Builder.withLength(4.68f).looping()
        .addAnimation("body",
            new AnimationChannel(AnimationChannel.Targets.POSITION,
                new Keyframe(0f, VanillaAnimations.posVec(0f, -9f, 0f),
                    AnimationChannel.Interpolations.CATMULLROM)))
        .addAnimation("neck",
            new AnimationChannel(AnimationChannel.Targets.POSITION,
                new Keyframe(0f, VanillaAnimations.posVec(0f, 1f, 0f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("neck",
            new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, VanillaAnimations.degreeVec(15f, 0f, 0f), AnimationChannel.Interpolations.LINEAR),
                new Keyframe(2.32f, VanillaAnimations.degreeVec(10.35f, 0f, 0f), AnimationChannel.Interpolations.LINEAR),
                new Keyframe(2.44f, VanillaAnimations.degreeVec(7.5f, 0f, 0f), AnimationChannel.Interpolations.LINEAR),
                new Keyframe(4.68f, VanillaAnimations.degreeVec(15f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("tail",
            new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, VanillaAnimations.degreeVec(47.5f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("tail1",
            new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, VanillaAnimations.degreeVec(0f, 20f, 0f), AnimationChannel.Interpolations.LINEAR),
                new Keyframe(2.44f, VanillaAnimations.degreeVec(0f, -25f, 0f), AnimationChannel.Interpolations.LINEAR),
                new Keyframe(4.68f, VanillaAnimations.degreeVec(0f, 20f, 0f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("legFR",
            new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, VanillaAnimations.degreeVec(-77.5f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("legFL",
            new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, VanillaAnimations.degreeVec(-80f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("legBL",
            new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, VanillaAnimations.degreeVec(-82.5f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("legBR",
            new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, VanillaAnimations.degreeVec(-75f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR))).build();
    public static final AnimationDefinition DIREWOLF_ATTACK = AnimationDefinition.Builder.withLength(0.64f)
        .addAnimation("body",
            new AnimationChannel(AnimationChannel.Targets.POSITION,
                new Keyframe(0f, VanillaAnimations.posVec(0f, 0f, 0f), AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.2f, VanillaAnimations.posVec(0f, 5f, 0f), AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.6f, VanillaAnimations.posVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("body",
            new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, VanillaAnimations.degreeVec(0f, 0f, 0f), AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.2f, VanillaAnimations.degreeVec(-25f, 0f, 0f), AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.6f, VanillaAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("neck",
            new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, VanillaAnimations.degreeVec(5f, 0f, 0f), AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.2f, VanillaAnimations.degreeVec(12.5f, 0f, 0f), AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.6f, VanillaAnimations.degreeVec(5f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("jaw",
            new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, VanillaAnimations.degreeVec(0f, 0f, 0f), AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.2f, VanillaAnimations.degreeVec(17.5f, 0f, 0f), AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.64f, VanillaAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("legFR",
            new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, VanillaAnimations.degreeVec(0f, 0f, 0f), AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.2f, VanillaAnimations.degreeVec(-22.5f, 0f, 0f), AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.6f, VanillaAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("legFL",
            new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, VanillaAnimations.degreeVec(0f, 0f, 0f), AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.2f, VanillaAnimations.degreeVec(-27.5f, 0f, 0f), AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.6f, VanillaAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR))).build();

    private final ModelPart head;

    public DirewolfModel(ModelPart root)
    {
        super(root);
        this.head = root.getChild("body").getChild("neck").getChild("head");
    }

    @Override
    public void setupAnim(PackPredator predator, float limbSwing, float limbSwingAmount, float ageInTicks, float yaw, float pitch)
    {
        super.setupAnim(predator, limbSwing, limbSwingAmount, ageInTicks, yaw, pitch);

        if (predator.sleepingAnimation.isStarted())
        {
            this.animate(predator.sleepingAnimation, DIREWOLF_SLEEP, ageInTicks);
        }
        else
        {
            final float speed = getAdjustedLandSpeed(predator);
            if (predator.swimmingAnimation.isStarted())
            {
                this.animate(predator.swimmingAnimation, DIREWOLF_RUN, ageInTicks, speed);
            }
            else
            {
                this.animate(predator.walkingAnimation, DIREWOLF_WALK, ageInTicks, speed);
                this.animate(predator.runningAnimation, DIREWOLF_RUN, ageInTicks, speed);
                this.animate(predator.attackingAnimation, DIREWOLF_ATTACK, ageInTicks);
            }
            head.xRot = pitch * Mth.PI / 180F;
            head.yRot = yaw * Mth.PI / 180F;
        }
    }
}
