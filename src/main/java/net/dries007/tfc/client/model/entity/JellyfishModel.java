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

import net.dries007.tfc.common.entities.aquatic.Jellyfish;

public class JellyfishModel extends HierarchicalAnimatedModel<Jellyfish>
{
    public static LayerDefinition createBodyLayer()
    {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition wholebody = partdefinition.addOrReplaceChild("wholebody", CubeListBuilder.create(), PartPose.offset(0.0F, 14.0F, 0.0F));

        PartDefinition cap_0 = wholebody.addOrReplaceChild("cap_0", CubeListBuilder.create().texOffs(16, 12).addBox(-2.0F, -2.9F, -2.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.0F))
            .texOffs(0, 12).addBox(-2.0F, 1.1F, -2.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition cap_1 = cap_0.addOrReplaceChild("cap_1", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -4.0F, -4.0F, 8.0F, 4.0F, 8.0F, new CubeDeformation(0.0F))
            .texOffs(0, 20).addBox(-4.0F, 0.0F, -4.0F, 8.0F, 4.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition tentacle_1 = cap_0.addOrReplaceChild("tentacle_1", CubeListBuilder.create(), PartPose.offset(2.0F, 1.0F, -2.0F));

        PartDefinition part_r1 = tentacle_1.addOrReplaceChild("part_r1", CubeListBuilder.create().texOffs(24, 1).addBox(-2.0F, -3.9F, 0.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F))
            .texOffs(0, 0).addBox(-2.0F, 0.1F, 0.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, 4.0F, 1.0F, 0.0F, 3.1416F, 0.0F));

        PartDefinition tentacle_2 = cap_0.addOrReplaceChild("tentacle_2", CubeListBuilder.create(), PartPose.offset(-2.0F, 1.0F, -2.0F));

        PartDefinition part_r2 = tentacle_2.addOrReplaceChild("part_r2", CubeListBuilder.create().texOffs(24, 21).addBox(0.0F, -3.9F, 0.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F))
            .texOffs(0, 20).addBox(0.0F, 0.1F, 0.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, 4.0F, 1.0F, 0.0F, 3.1416F, 0.0F));

        PartDefinition tentacle_3 = cap_0.addOrReplaceChild("tentacle_3", CubeListBuilder.create(), PartPose.offset(-2.0F, 1.0F, 2.0F));

        PartDefinition part_r3 = tentacle_3.addOrReplaceChild("part_r3", CubeListBuilder.create().texOffs(24, 21).addBox(0.0F, -3.9F, -2.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F))
            .texOffs(0, 24).addBox(0.0F, 0.1F, -2.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, 4.0F, -1.0F, 0.0F, 3.1416F, 0.0F));

        PartDefinition tentacle_4 = cap_0.addOrReplaceChild("tentacle_4", CubeListBuilder.create(), PartPose.offset(2.0F, 1.0F, 2.0F));

        PartDefinition part_r4 = tentacle_4.addOrReplaceChild("part_r4", CubeListBuilder.create().texOffs(0, 4).addBox(-2.0F, -3.9F, -2.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
            .texOffs(24, 1).addBox(-2.0F, -7.9F, -2.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, 8.0F, -1.0F, 0.0F, 3.1416F, 0.0F));

        return LayerDefinition.create(meshdefinition, 32, 32);
    }

    public JellyfishModel(ModelPart root)
    {
        super(root);
    }

    @Override
    public void setupAnim(Jellyfish entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch)
    {
        super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        animate(entity.thrownAnimation, THROWN, ageInTicks);
        animate(entity.swimmingAnimation, SWIM, ageInTicks);
        animate(entity.idleAnimation, IDLE, ageInTicks);
    }

    public static final AnimationDefinition SWIM = AnimationDefinition.Builder.withLength(2.4071F).looping()
        .addAnimation("cap_0", new AnimationChannel(AnimationChannel.Targets.ROTATION,
            new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.2123F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.5662F, KeyframeAnimations.degreeVec(-1.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.7785F, KeyframeAnimations.degreeVec(0.25F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.2739F, KeyframeAnimations.degreeVec(0.88F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(2.0524F, KeyframeAnimations.degreeVec(-1.97F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(2.4062F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
        ))
        .addAnimation("cap_0", new AnimationChannel(AnimationChannel.Targets.POSITION,
            new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, -1.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.2831F, KeyframeAnimations.posVec(0.0F, -2.0F, -0.14F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.4246F, KeyframeAnimations.posVec(0.0F, -3.0F, -0.14F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.7077F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.0616F, KeyframeAnimations.posVec(0.0F, 1.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.6985F, KeyframeAnimations.posVec(0.0F, 2.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(2.4062F, KeyframeAnimations.posVec(0.0F, -1.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
        ))
        .addAnimation("cap_0", new AnimationChannel(AnimationChannel.Targets.SCALE,
            new Keyframe(0.0F, KeyframeAnimations.scaleVec(1.0F, 1.0F, 1.0F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(2.4062F, KeyframeAnimations.scaleVec(1.0F, 1.0F, 1.0F), AnimationChannel.Interpolations.LINEAR)
        ))
        .addAnimation("tentacle_4", new AnimationChannel(AnimationChannel.Targets.ROTATION,
            new Keyframe(0.0F, KeyframeAnimations.degreeVec(15.9855F, 0.6889F, -2.4033F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.0708F, KeyframeAnimations.degreeVec(16.9892F, 0.5167F, -1.8025F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.4246F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.0616F, KeyframeAnimations.degreeVec(-10.0051F, 0.8742F, 7.438F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.557F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(2.4062F, KeyframeAnimations.degreeVec(15.9855F, 0.6889F, -2.4033F), AnimationChannel.Interpolations.LINEAR)
        ))
        .addAnimation("tentacle_1", new AnimationChannel(AnimationChannel.Targets.ROTATION,
            new Keyframe(0.0F, KeyframeAnimations.degreeVec(-13.4505F, -1.1658F, -4.8625F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.0708F, KeyframeAnimations.degreeVec(-15.0879F, -0.8744F, -3.6469F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.4246F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.0616F, KeyframeAnimations.degreeVec(7.4978F, -0.8749F, 7.4475F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.557F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(2.4062F, KeyframeAnimations.degreeVec(-13.4505F, -1.1658F, -4.8625F), AnimationChannel.Interpolations.LINEAR)
        ))
        .addAnimation("tentacle_3", new AnimationChannel(AnimationChannel.Targets.ROTATION,
            new Keyframe(0.0F, KeyframeAnimations.degreeVec(15.9855F, -0.6889F, 2.4033F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.0708F, KeyframeAnimations.degreeVec(16.9892F, -0.5167F, 1.8025F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.4246F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.0616F, KeyframeAnimations.degreeVec(-7.5073F, -0.7664F, -7.4546F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.557F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(2.4062F, KeyframeAnimations.degreeVec(15.9855F, -0.6889F, 2.4033F), AnimationChannel.Interpolations.LINEAR)
        ))
        .addAnimation("tentacle_2", new AnimationChannel(AnimationChannel.Targets.ROTATION,
            new Keyframe(0.0F, KeyframeAnimations.degreeVec(-13.4505F, 1.1658F, 4.8625F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.0708F, KeyframeAnimations.degreeVec(-15.0879F, 0.8744F, 3.6469F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.4246F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.0616F, KeyframeAnimations.degreeVec(7.4978F, 0.8749F, -7.4475F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.557F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(2.3355F, KeyframeAnimations.degreeVec(-13.4505F, 1.1658F, 4.8625F), AnimationChannel.Interpolations.LINEAR)
        ))
        .addAnimation("cap_1", new AnimationChannel(AnimationChannel.Targets.ROTATION,
            new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.2831F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.4246F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.7785F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.2739F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.6985F, KeyframeAnimations.degreeVec(2.5F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(2.4062F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
        ))
        .addAnimation("cap_1", new AnimationChannel(AnimationChannel.Targets.POSITION,
            new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 0.14F, 0.0F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.2831F, KeyframeAnimations.posVec(0.0F, 0.51F, 0.0F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.4954F, KeyframeAnimations.posVec(0.0F, 0.51F, 0.0F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.7785F, KeyframeAnimations.posVec(0.0F, 1.51F, 0.0F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.2739F, KeyframeAnimations.posVec(0.0F, 0.54F, 0.0F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.6985F, KeyframeAnimations.posVec(0.0F, -0.46F, 0.0F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(2.4062F, KeyframeAnimations.posVec(0.0F, 0.14F, 0.0F), AnimationChannel.Interpolations.LINEAR)
        ))
        .addAnimation("cap_1", new AnimationChannel(AnimationChannel.Targets.SCALE,
            new Keyframe(0.0F, KeyframeAnimations.scaleVec(1.0F, 1.0F, 1.0F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.0708F, KeyframeAnimations.scaleVec(1.0F, 1.0F, 1.0F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.2831F, KeyframeAnimations.scaleVec(1.0F, 0.8F, 1.0F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.4246F, KeyframeAnimations.scaleVec(1.0F, 0.8F, 1.0F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.7785F, KeyframeAnimations.scaleVec(1.0F, 1.0F, 1.0F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.2739F, KeyframeAnimations.scaleVec(1.0F, 1.0F, 1.0F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.6985F, KeyframeAnimations.scaleVec(1.0F, 1.0F, 1.0F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(2.4062F, KeyframeAnimations.scaleVec(1.0F, 1.0F, 1.0F), AnimationChannel.Interpolations.LINEAR)
        ))
        .addAnimation("wholebody", new AnimationChannel(AnimationChannel.Targets.POSITION,
            new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(2.4062F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
        ))
        .build();

    public static final AnimationDefinition IDLE = AnimationDefinition.Builder.withLength(3.3432F).looping()
        .addAnimation("cap_0", new AnimationChannel(AnimationChannel.Targets.ROTATION,
            new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.295F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.7866F, KeyframeAnimations.degreeVec(-1.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.0816F, KeyframeAnimations.degreeVec(0.25F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.7699F, KeyframeAnimations.degreeVec(0.88F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(2.8515F, KeyframeAnimations.degreeVec(-1.97F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(3.3432F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
        ))
        .addAnimation("cap_0", new AnimationChannel(AnimationChannel.Targets.POSITION,
            new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, -1.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.3933F, KeyframeAnimations.posVec(0.0F, -2.0F, -0.14F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.59F, KeyframeAnimations.posVec(0.0F, -3.0F, -0.14F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.9833F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.4749F, KeyframeAnimations.posVec(0.0F, 1.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(2.3599F, KeyframeAnimations.posVec(0.0F, 2.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(3.3432F, KeyframeAnimations.posVec(0.0F, -1.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
        ))
        .addAnimation("cap_0", new AnimationChannel(AnimationChannel.Targets.SCALE,
            new Keyframe(0.0F, KeyframeAnimations.scaleVec(1.0F, 1.0F, 1.0F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(3.3432F, KeyframeAnimations.scaleVec(1.0F, 1.0F, 1.0F), AnimationChannel.Interpolations.LINEAR)
        ))
        .addAnimation("tentacle_4", new AnimationChannel(AnimationChannel.Targets.ROTATION,
            new Keyframe(0.0F, KeyframeAnimations.degreeVec(15.9855F, 0.6889F, -2.4033F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.0983F, KeyframeAnimations.degreeVec(16.9892F, 0.5167F, -1.8025F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.59F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.4749F, KeyframeAnimations.degreeVec(-10.0051F, 0.8742F, 7.438F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(2.1632F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(3.3432F, KeyframeAnimations.degreeVec(15.9855F, 0.6889F, -2.4033F), AnimationChannel.Interpolations.LINEAR)
        ))
        .addAnimation("tentacle_1", new AnimationChannel(AnimationChannel.Targets.ROTATION,
            new Keyframe(0.0F, KeyframeAnimations.degreeVec(-13.4505F, -1.1658F, -4.8625F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.0983F, KeyframeAnimations.degreeVec(-15.0879F, -0.8744F, -3.6469F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.59F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.4749F, KeyframeAnimations.degreeVec(7.4978F, -0.8749F, 7.4475F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(2.1632F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(3.3432F, KeyframeAnimations.degreeVec(-13.4505F, -1.1658F, -4.8625F), AnimationChannel.Interpolations.LINEAR)
        ))
        .addAnimation("tentacle_3", new AnimationChannel(AnimationChannel.Targets.ROTATION,
            new Keyframe(0.0F, KeyframeAnimations.degreeVec(15.9855F, -0.6889F, 2.4033F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.0983F, KeyframeAnimations.degreeVec(16.9892F, -0.5167F, 1.8025F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.59F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.4749F, KeyframeAnimations.degreeVec(-7.5073F, -0.7664F, -7.4546F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(2.1632F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(3.3432F, KeyframeAnimations.degreeVec(15.9855F, -0.6889F, 2.4033F), AnimationChannel.Interpolations.LINEAR)
        ))
        .addAnimation("tentacle_2", new AnimationChannel(AnimationChannel.Targets.ROTATION,
            new Keyframe(0.0F, KeyframeAnimations.degreeVec(-13.4505F, 1.1658F, 4.8625F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.0983F, KeyframeAnimations.degreeVec(-15.0879F, 0.8744F, 3.6469F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.59F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.4749F, KeyframeAnimations.degreeVec(7.4978F, 0.8749F, -7.4475F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(2.1632F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(3.2448F, KeyframeAnimations.degreeVec(-13.4505F, 1.1658F, 4.8625F), AnimationChannel.Interpolations.LINEAR)
        ))
        .addAnimation("cap_1", new AnimationChannel(AnimationChannel.Targets.ROTATION,
            new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.3933F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.59F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.0816F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.7699F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(2.3599F, KeyframeAnimations.degreeVec(2.5F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(3.3432F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
        ))
        .addAnimation("cap_1", new AnimationChannel(AnimationChannel.Targets.POSITION,
            new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 0.14F, 0.0F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.3933F, KeyframeAnimations.posVec(0.0F, 0.51F, 0.0F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.6883F, KeyframeAnimations.posVec(0.0F, 0.51F, 0.0F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.0816F, KeyframeAnimations.posVec(0.0F, 1.51F, 0.0F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.7699F, KeyframeAnimations.posVec(0.0F, 0.54F, 0.0F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(2.3599F, KeyframeAnimations.posVec(0.0F, -0.46F, 0.0F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(3.3432F, KeyframeAnimations.posVec(0.0F, 0.14F, 0.0F), AnimationChannel.Interpolations.LINEAR)
        ))
        .addAnimation("cap_1", new AnimationChannel(AnimationChannel.Targets.SCALE,
            new Keyframe(0.0F, KeyframeAnimations.scaleVec(1.0F, 1.0F, 1.0F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.0983F, KeyframeAnimations.scaleVec(1.0F, 1.0F, 1.0F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.3933F, KeyframeAnimations.scaleVec(1.0F, 0.8F, 1.0F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.59F, KeyframeAnimations.scaleVec(1.0F, 0.8F, 1.0F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.0816F, KeyframeAnimations.scaleVec(1.0F, 1.0F, 1.0F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.7699F, KeyframeAnimations.scaleVec(1.0F, 1.0F, 1.0F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(2.3599F, KeyframeAnimations.scaleVec(1.0F, 1.0F, 1.0F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(3.3432F, KeyframeAnimations.scaleVec(1.0F, 1.0F, 1.0F), AnimationChannel.Interpolations.LINEAR)
        ))
        .addAnimation("wholebody", new AnimationChannel(AnimationChannel.Targets.ROTATION,
            new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(3.3432F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
        ))
        .addAnimation("wholebody", new AnimationChannel(AnimationChannel.Targets.POSITION,
            new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(3.3432F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
        ))
        .build();

    public static final AnimationDefinition THROWN = AnimationDefinition.Builder.withLength(0.7143F).looping()
        .addAnimation("cap_0", new AnimationChannel(AnimationChannel.Targets.ROTATION,
            new Keyframe(0.0F, KeyframeAnimations.degreeVec(90.0F, 0.0F, -90.0F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.7143F, KeyframeAnimations.degreeVec(90.0F, 0.0F, -90.0F), AnimationChannel.Interpolations.LINEAR)
        ))
        .addAnimation("cap_0", new AnimationChannel(AnimationChannel.Targets.POSITION,
            new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 4.0F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.7143F, KeyframeAnimations.posVec(0.0F, 0.0F, 4.0F), AnimationChannel.Interpolations.LINEAR)
        ))
        .addAnimation("cap_1", new AnimationChannel(AnimationChannel.Targets.ROTATION,
            new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.3274F, KeyframeAnimations.degreeVec(0.0F, 0.0F, -5.0F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.7143F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
        ))
        .addAnimation("tentacle_1", new AnimationChannel(AnimationChannel.Targets.ROTATION,
            new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.4464F, KeyframeAnimations.degreeVec(0.0F, 0.0F, -20.0F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.7143F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
        ))
        .addAnimation("tentacle_2", new AnimationChannel(AnimationChannel.Targets.ROTATION,
            new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.4464F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 10.0F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.7143F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
        ))
        .addAnimation("tentacle_3", new AnimationChannel(AnimationChannel.Targets.ROTATION,
            new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.4464F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 12.5F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.7143F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
        ))
        .addAnimation("tentacle_4", new AnimationChannel(AnimationChannel.Targets.ROTATION,
            new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.4464F, KeyframeAnimations.degreeVec(0.0F, 0.0F, -17.5F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.7143F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
        ))
        .addAnimation("wholebody", new AnimationChannel(AnimationChannel.Targets.POSITION,
            new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, -6.0F, -4.0F), AnimationChannel.Interpolations.LINEAR)
        ))
        .build();
}
