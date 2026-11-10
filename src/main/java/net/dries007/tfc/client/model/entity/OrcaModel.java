/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.model.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
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

import net.dries007.tfc.common.entities.aquatic.TFCDolphin;

public class OrcaModel extends HierarchicalAnimatedModel<TFCDolphin>
{
    public static LayerDefinition createBodyLayer()
    {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-8.0F, -9.0F, -6.0F, 16.0F, 15.0F, 27.0F, new CubeDeformation(1.0F)), PartPose.offset(0.0F, 17.0F, -7.0F));

        PartDefinition head = body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(56, 51).addBox(-9.0F, -4.0F, -11.0F, 16.0F, 12.0F, 9.0F, new CubeDeformation(1.0F)), PartPose.offset(1.0F, -5.0F, -6.0F));

        PartDefinition jaw = head.addOrReplaceChild("jaw", CubeListBuilder.create(), PartPose.offset(-1.0F, 4.0F, -11.0F));

        PartDefinition jaw1 = jaw.addOrReplaceChild("jaw1", CubeListBuilder.create().texOffs(2, 96).addBox(-5.0F, -3.0F, -7.0F, 10.0F, 1.0F, 7.0F, new CubeDeformation(0.01F)).texOffs(29, 105).addBox(-7.0F, -2.0F, -7.0F, 14.0F, 4.0F, 8.0F, new CubeDeformation(0.01F)), PartPose.offset(0.0F, 0.9501F, -1.1098F));

        PartDefinition jaw2 = jaw.addOrReplaceChild("jaw2", CubeListBuilder.create().texOffs(2, 96).addBox(-11.0F, -0.0499F, -2.1098F, 10.0F, 1.0F, 7.0F, new CubeDeformation(0.0F)).texOffs(73, 100).addBox(-13.0F, -7.0F, -2.0F, 14.0F, 7.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offset(6.0F, -1.0F, -6.0F));

        PartDefinition fins1 = body.addOrReplaceChild("fins1", CubeListBuilder.create().texOffs(84, 72).addBox(0.2066F, -1.0017F, -4.0F, 12.0F, 2.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offset(9.0F, 1.0F, -1.0F));

        PartDefinition fins4 = body.addOrReplaceChild("fins4", CubeListBuilder.create(), PartPose.offset(0.0F, -10.3075F, 9.9149F));

        PartDefinition cube_r1 = fins4.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(62, 4).addBox(-1.5F, -12.7694F, -5.3604F, 3.0F, 14.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 1.0F, 2.1701F, -0.3054F, 0.0F, 0.0F));

        PartDefinition fins2 = body.addOrReplaceChild("fins2", CubeListBuilder.create().texOffs(46, 82).addBox(-11.8192F, -1.5736F, -4.0F, 12.0F, 2.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offset(-9.0F, 2.0F, -1.0F));

        PartDefinition wholetail = body.addOrReplaceChild("wholetail", CubeListBuilder.create().texOffs(0, 42).addBox(-6.0F, -3.0F, -4.0F, 12.0F, 11.0F, 16.0F, new CubeDeformation(1.0F)), PartPose.offset(0.0F, -5.0F, 21.0F));

        PartDefinition tail = wholetail.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(0, 69).addBox(-4.0F, -2.0F, 1.0F, 8.0F, 7.0F, 15.0F, new CubeDeformation(1.0F)), PartPose.offset(0.0F, -0.5534F, 11.3256F));

        PartDefinition fins3 = tail.addOrReplaceChild("fins3", CubeListBuilder.create().texOffs(42, 42).addBox(-15.0F, -1.0F, -2.0F, 30.0F, 2.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.5534F, 15.6744F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }

    public static final AnimationDefinition ORCA_ATTACK = AnimationDefinition.Builder.withLength(0.7486F).addAnimation("body", new AnimationChannel(AnimationChannel.Targets.ROTATION, new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.0384F, KeyframeAnimations.degreeVec(-5.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.1152F, KeyframeAnimations.degreeVec(-9.9627F, -0.8672F, -4.9244F), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.1919F, KeyframeAnimations.degreeVec(-2.5F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.2495F, KeyframeAnimations.degreeVec(3.718F, -0.4891F, 7.4841F), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.3263F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.3839F, KeyframeAnimations.degreeVec(0.0F, 0.0F, -2.5F), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.5182F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.7486F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR))).addAnimation("body", new AnimationChannel(AnimationChannel.Targets.POSITION, new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.2303F, KeyframeAnimations.posVec(0.0F, 0.0F, -5.67F), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.3263F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.5182F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.7486F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR))).addAnimation("head", new AnimationChannel(AnimationChannel.Targets.ROTATION, new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.1535F, KeyframeAnimations.degreeVec(-7.5F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.2303F, KeyframeAnimations.degreeVec(1.25F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.2687F, KeyframeAnimations.degreeVec(-1.56F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.3071F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.5182F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.7486F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR))).addAnimation("head", new AnimationChannel(AnimationChannel.Targets.SCALE, new Keyframe(0.0F, KeyframeAnimations.scaleVec(1.0F, 1.0F, 1.0F), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.1727F, KeyframeAnimations.scaleVec(1.0F, 1.0F, 1.0F), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.2111F, KeyframeAnimations.scaleVec(1.0F, 1.0F, 1.1F), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.2687F, KeyframeAnimations.scaleVec(1.0F, 1.0F, 1.0F), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.3071F, KeyframeAnimations.scaleVec(1.0F, 1.0F, 1.0F), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.5182F, KeyframeAnimations.scaleVec(1.0F, 1.0F, 1.0F), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.7486F, KeyframeAnimations.scaleVec(1.0F, 1.0F, 1.0F), AnimationChannel.Interpolations.LINEAR))).addAnimation("fins1", new AnimationChannel(AnimationChannel.Targets.ROTATION, new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 22.5F), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.096F, KeyframeAnimations.degreeVec(0.0F, 0.0F, -22.5F), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.2111F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 22.5F), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.2687F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 35.5F), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.3071F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 22.5F), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.5182F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 22.5F), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.7486F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 22.5F), AnimationChannel.Interpolations.LINEAR))).addAnimation("fins2", new AnimationChannel(AnimationChannel.Targets.ROTATION, new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, -23.0F), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.096F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 25.0F), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.2111F, KeyframeAnimations.degreeVec(0.0F, 0.0F, -22.5F), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.2687F, KeyframeAnimations.degreeVec(0.0F, 0.0F, -35.5F), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.3071F, KeyframeAnimations.degreeVec(0.0F, 0.0F, -22.5F), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.5182F, KeyframeAnimations.degreeVec(0.0F, 0.0F, -23.0F), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.7486F, KeyframeAnimations.degreeVec(0.0F, 0.0F, -23.0F), AnimationChannel.Interpolations.LINEAR))).addAnimation("wholetail", new AnimationChannel(AnimationChannel.Targets.ROTATION, new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.1152F, KeyframeAnimations.degreeVec(10.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.2303F, KeyframeAnimations.degreeVec(10.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.3263F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.5182F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.7486F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR))).addAnimation("tail", new AnimationChannel(AnimationChannel.Targets.ROTATION, new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.1152F, KeyframeAnimations.degreeVec(12.5F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.2303F, KeyframeAnimations.degreeVec(15.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.3263F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.5182F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.7486F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR))).addAnimation("fins3", new AnimationChannel(AnimationChannel.Targets.ROTATION, new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.1152F, KeyframeAnimations.degreeVec(7.5F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.2303F, KeyframeAnimations.degreeVec(20.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.3263F, KeyframeAnimations.degreeVec(-17.5F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.3839F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.5182F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.7486F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR))).addAnimation("jaw1", new AnimationChannel(AnimationChannel.Targets.ROTATION, new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.1152F, KeyframeAnimations.degreeVec(47.5F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.1919F, KeyframeAnimations.degreeVec(46.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.2303F, KeyframeAnimations.degreeVec(-3.5F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.2687F, KeyframeAnimations.degreeVec(3.25F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.3071F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.5182F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR))).addAnimation("jaw1", new AnimationChannel(AnimationChannel.Targets.POSITION, new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.1152F, KeyframeAnimations.posVec(0.0F, -1.0F, 0.0F), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.1727F, KeyframeAnimations.posVec(0.0F, -2.67F, 0.0F), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.2111F, KeyframeAnimations.posVec(0.0F, -1.0F, 0.0F), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.3071F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.5182F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR))).addAnimation("fins4", new AnimationChannel(AnimationChannel.Targets.ROTATION, new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.0384F, KeyframeAnimations.degreeVec(-5.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.1152F, KeyframeAnimations.degreeVec(-9.9627F, -0.8672F, -4.9244F), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.1919F, KeyframeAnimations.degreeVec(-2.5F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.2495F, KeyframeAnimations.degreeVec(3.718F, -0.4891F, 7.4841F), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.3263F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.3839F, KeyframeAnimations.degreeVec(0.0F, 0.0F, -7.5F), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.5182F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.7486F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR))).build();

    private final ModelPart body;
    private final ModelPart head;
    private final ModelPart jaw;
    private final ModelPart jaw1;
    private final ModelPart jaw2;
    private final ModelPart fins1;
    private final ModelPart fins4;
    private final ModelPart fins2;
    private final ModelPart wholetail;
    private final ModelPart tail;
    private final ModelPart fins3;

    public OrcaModel(ModelPart root)
    {
        super(root);
        this.body = root.getChild("body");
        this.head = this.body.getChild("head");
        this.jaw = this.head.getChild("jaw");
        this.jaw1 = this.jaw.getChild("jaw1");
        this.jaw2 = this.jaw.getChild("jaw2");
        this.fins1 = this.body.getChild("fins1");
        this.fins4 = this.body.getChild("fins4");
        this.fins2 = this.body.getChild("fins2");
        this.wholetail = this.body.getChild("wholetail");
        this.tail = this.wholetail.getChild("tail");
        this.fins3 = this.tail.getChild("fins3");
    }

    @Override
    public void setupAnim(TFCDolphin entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch)
    {
        super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);

        final float time = ageInTicks / 20f;
        if (entity.isAggressive())
        {
            final float wave = cos(time, 343.8f);
            body.xRot += rad(headPitch - 2.865f - 2.865f * wave);
            body.yRot += rad(netHeadYaw);
            setFins(17.19f * cos(time, 229.2f) + 22.92f);
            wholetail.xRot += rad(-5.73f * wave);
            tail.xRot += rad(-5.73f * wave);
            fins3.xRot += rad(-11.46f * wave);
            jaw1.xRot += rad(6.635f + headPitch - 2.865f * wave);
            jaw1.yRot += rad(netHeadYaw);
        }
        else
        {
            final float wave = cos(time, 100.8f);
            body.xRot += rad(headPitch - 0.365f - 2.865f * cos(time, 110.8f));
            body.yRot += rad(netHeadYaw);
            setFins(17.19f * cos(time, 120.2f) + 22.92f);
            wholetail.xRot += rad(-5.73f * wave);
            tail.xRot += rad(-11.46f * wave);
            fins3.xRot += rad(-11.46f * wave);
            jaw1.xRot += rad(5f - 5.73f * wave);
        }

        animate(entity.attackingAnimation, ORCA_ATTACK, ageInTicks);
    }

    private void setFins(float degrees)
    {
        fins1.zRot += rad(degrees);
        fins2.zRot -= rad(degrees);
    }

    private static float cos(float time, float degreesPerSecond)
    {
        return Mth.cos(time * degreesPerSecond * Constants.DEG_TO_RAD);
    }

    private static float rad(float degrees)
    {
        return degrees * Constants.DEG_TO_RAD;
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, int color)
    {
        body.render(poseStack, buffer, packedLight, packedOverlay);
    }
}
