/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.model.entity;

import java.util.stream.Stream;
import com.mojang.math.Constants;
import net.minecraft.client.animation.AnimationChannel;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.animation.Keyframe;
import net.minecraft.client.animation.KeyframeAnimations;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;

import net.dries007.tfc.common.entities.prey.RammingPrey;
import net.dries007.tfc.common.recipes.ChiselRecipe;

public class MooseModel extends HierarchicalAnimatedModel<RammingPrey>
{
    public static LayerDefinition createBodyLayer()
    {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition moose = partdefinition.addOrReplaceChild("moose", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 41.0F, 23.0F, 0.0F, 3.1416F, 0.0F));

        PartDefinition body = moose.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-8.0F, -5.04F, -0.168F, 14.0F, 17.0F, 26.0F, new CubeDeformation(0.0F)), PartPose.offset(1.0F, -48.0F, -2.0F));

        PartDefinition cube_r1 = body.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(50, 43).addBox(-1.0F, -38.4515F, -5.4433F, 2.0F, 7.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, 35.0F, -9.0F, -0.3578F, 0.0F, 0.0F));

        PartDefinition cube_r2 = body.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(0, 43).addBox(-8.0F, -54.0F, 1.0F, 16.0F, 21.0F, 18.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, 46.0F, 26.0F, 0.0873F, 0.0F, 0.0F));

        PartDefinition neck = body.addOrReplaceChild("neck", CubeListBuilder.create().texOffs(62, 25).addBox(-3.5F, 1.7643F, 0.1755F, 6.0F, 10.0F, 18.0F, new CubeDeformation(0.0F)), PartPose.offset(-0.5F, -9.7643F, 31.8245F));

        PartDefinition head = neck.addOrReplaceChild("head", CubeListBuilder.create().texOffs(67, 86).addBox(0.0074F, 2.923F, -8.4288F, 0.0F, 9.0F, 17.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.0074F, 6.5199F, 17.5063F, -0.0436F, 0.0F, 0.0F));

        PartDefinition cube_r3 = head.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(6, 82).addBox(-1.6981F, -2.2641F, -5.9761F, 3.0F, 5.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.8876F, -5.2647F, 4.9703F, -0.0763F, -0.0886F, -0.2467F));

        PartDefinition cube_r4 = head.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(16, 12).addBox(-2.3019F, -2.3506F, -5.9845F, 3.0F, 5.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.8683F, -5.0215F, 4.8818F, -0.0326F, 0.0886F, 0.2467F));

        PartDefinition cube_r5 = head.addOrReplaceChild("cube_r5", CubeListBuilder.create().texOffs(83, 0).addBox(-3.0F, -57.0F, 50.0F, 6.0F, 7.0F, 8.0F, new CubeDeformation(0.0F))
            .texOffs(92, 16).addBox(-4.0F, -60.0F, 41.0F, 8.0F, 10.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.4926F, 35.923F, -60.4288F, -0.3578F, 0.0F, 0.0F));

        PartDefinition left_antler = head.addOrReplaceChild("left_antler", CubeListBuilder.create(), PartPose.offsetAndRotation(-13.9398F, -8.9723F, 4.6713F, 0.4349F, 0.0368F, -0.0791F));

        PartDefinition cube_r6 = left_antler.addOrReplaceChild("cube_r6", CubeListBuilder.create().texOffs(54, 12).addBox(4.9564F, 5.3252F, -1.6931F, 3.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(92, 35).addBox(0.9564F, 1.3252F, -1.6931F, 8.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.8055F, 1.0429F, 1.1749F, 0.0627F, 0.6157F, -0.5853F));

        PartDefinition cube_r7 = left_antler.addOrReplaceChild("cube_r7", CubeListBuilder.create().texOffs(68, 60).addBox(-6.7375F, -2.0783F, 1.4421F, 12.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.9419F, 8.7268F, 7.112F, -2.9171F, 1.465F, 2.041F));

        PartDefinition cube_r8 = left_antler.addOrReplaceChild("cube_r8", CubeListBuilder.create().texOffs(50, 53).addBox(-2.6847F, 1.3137F, -3.7996F, 10.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.4218F, 5.1432F, 1.1283F, -0.8226F, 0.1677F, 0.0781F));

        PartDefinition cube_r9 = left_antler.addOrReplaceChild("cube_r9", CubeListBuilder.create().texOffs(54, 9).addBox(-4.037F, -0.6194F, -0.4597F, 7.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.6471F, 5.1476F, -0.4881F, -0.6757F, 1.1065F, -1.9179F));

        PartDefinition cube_r10 = left_antler.addOrReplaceChild("cube_r10", CubeListBuilder.create().texOffs(58, 43).addBox(-0.0436F, -5.6221F, -1.7214F, 1.0F, 9.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.8055F, 1.0429F, 1.1749F, 0.0191F, 0.6157F, -0.5853F));

        PartDefinition cube_r11 = left_antler.addOrReplaceChild("cube_r11", CubeListBuilder.create().texOffs(0, 82).addBox(1.9564F, -10.5683F, -1.7474F, 2.0F, 11.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.8055F, 1.0429F, 1.1749F, -0.0245F, 0.6157F, -0.5853F));

        PartDefinition cube_r12 = left_antler.addOrReplaceChild("cube_r12", CubeListBuilder.create().texOffs(6, 89).addBox(0.1075F, -4.8409F, -1.9375F, 2.0F, 6.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.0766F, -6.9028F, -3.0826F, 0.0626F, 0.5558F, -0.1181F));

        PartDefinition cube_r13 = left_antler.addOrReplaceChild("cube_r13", CubeListBuilder.create().texOffs(80, 63).addBox(0.1075F, -2.463F, -1.8588F, 2.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.5834F, -6.7346F, -4.5476F, 0.1499F, 0.5558F, -0.1181F));

        PartDefinition cube_r14 = left_antler.addOrReplaceChild("cube_r14", CubeListBuilder.create().texOffs(31, 82).addBox(0.1075F, -6.463F, -1.8588F, 2.0F, 7.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.4781F, -6.1341F, -1.3821F, 0.1499F, 0.5558F, -0.1181F));

        PartDefinition cube_r15 = left_antler.addOrReplaceChild("cube_r15", CubeListBuilder.create().texOffs(0, 52).addBox(3.1075F, -10.1321F, 0.7786F, 8.0F, 8.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-5.4725F, 4.8411F, 0.632F, 0.2808F, 0.5558F, -0.1181F));

        PartDefinition bone = left_antler.addOrReplaceChild("bone", CubeListBuilder.create(), PartPose.offsetAndRotation(0.9419F, 5.7268F, 7.112F, 0.2618F, -0.3491F, 0.0F));

        PartDefinition cube_r16 = bone.addOrReplaceChild("cube_r16", CubeListBuilder.create().texOffs(76, 56).addBox(-4.0915F, -2.8079F, 1.6677F, 9.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 1.0F, 0.0F, -2.9171F, 1.465F, 2.041F));

        PartDefinition cube_r17 = bone.addOrReplaceChild("cube_r17", CubeListBuilder.create().texOffs(54, 6).addBox(-4.0686F, -1.4158F, -0.189F, 7.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.2949F, -2.5792F, -7.6002F, -0.6757F, 1.1065F, -1.9179F));

        PartDefinition right_antler = head.addOrReplaceChild("right_antler", CubeListBuilder.create(), PartPose.offsetAndRotation(16.6547F, -8.2266F, 6.3154F, 0.4349F, -0.0368F, 0.0791F));

        PartDefinition cube_r18 = right_antler.addOrReplaceChild("cube_r18", CubeListBuilder.create().texOffs(16, 19).addBox(-7.9564F, 5.3252F, -1.6931F, 3.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(50, 90).addBox(-8.9564F, 1.3252F, -1.6931F, 8.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0627F, -0.6157F, 0.5853F));

        PartDefinition cube_r19 = right_antler.addOrReplaceChild("cube_r19", CubeListBuilder.create().texOffs(50, 57).addBox(-5.2625F, -2.0783F, 1.4421F, 12.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-7.7474F, 7.6839F, 5.9371F, -2.9171F, -1.465F, -2.041F));

        PartDefinition cube_r20 = right_antler.addOrReplaceChild("cube_r20", CubeListBuilder.create().texOffs(0, 22).addBox(-7.3153F, 1.3137F, -3.7996F, 10.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-8.2273F, 4.1003F, -0.0466F, -0.8226F, -0.1677F, -0.0781F));

        PartDefinition cube_r21 = right_antler.addOrReplaceChild("cube_r21", CubeListBuilder.create().texOffs(54, 3).addBox(-2.963F, -0.6194F, -0.4597F, 7.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-6.4526F, 4.1047F, -1.663F, -0.6757F, -1.1065F, 1.9179F));

        PartDefinition cube_r22 = right_antler.addOrReplaceChild("cube_r22", CubeListBuilder.create().texOffs(22, 0).addBox(-0.9564F, -5.6221F, -1.7214F, 1.0F, 9.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0191F, -0.6157F, 0.5853F));

        PartDefinition cube_r23 = right_antler.addOrReplaceChild("cube_r23", CubeListBuilder.create().texOffs(16, 0).addBox(-3.9564F, -10.5683F, -1.7474F, 2.0F, 11.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.0245F, -0.6157F, 0.5853F));

        PartDefinition cube_r24 = right_antler.addOrReplaceChild("cube_r24", CubeListBuilder.create().texOffs(83, 0).addBox(-2.1075F, -4.8409F, -1.9375F, 2.0F, 6.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.7289F, -7.9457F, -4.2575F, 0.0626F, -0.5558F, 0.1181F));

        PartDefinition cube_r25 = right_antler.addOrReplaceChild("cube_r25", CubeListBuilder.create().texOffs(62, 12).addBox(-2.1075F, -2.463F, -1.8588F, 2.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.3889F, -7.7776F, -5.7225F, 0.1499F, -0.5558F, 0.1181F));

        PartDefinition cube_r26 = right_antler.addOrReplaceChild("cube_r26", CubeListBuilder.create().texOffs(25, 82).addBox(-2.1075F, -6.463F, -1.8588F, 2.0F, 7.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.6726F, -7.177F, -2.557F, 0.1499F, -0.5558F, 0.1181F));

        PartDefinition cube_r27 = right_antler.addOrReplaceChild("cube_r27", CubeListBuilder.create().texOffs(0, 43).addBox(-11.1075F, -10.1321F, 0.7786F, 8.0F, 8.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.667F, 3.7982F, -0.5429F, 0.2808F, -0.5558F, 0.1181F));

        PartDefinition bone2 = right_antler.addOrReplaceChild("bone2", CubeListBuilder.create(), PartPose.offsetAndRotation(-4.7474F, 4.6839F, 5.9371F, 0.2618F, 0.3491F, 0.0F));

        PartDefinition cube_r28 = bone2.addOrReplaceChild("cube_r28", CubeListBuilder.create().texOffs(73, 53).addBox(-4.9085F, -2.8079F, 1.6677F, 9.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 1.0F, 0.0F, -2.9171F, -1.465F, -2.041F));

        PartDefinition cube_r29 = bone2.addOrReplaceChild("cube_r29", CubeListBuilder.create().texOffs(54, 0).addBox(-2.9314F, -1.4158F, -0.189F, 7.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.2949F, -2.5792F, -7.6002F, -0.6757F, -1.1065F, 1.9179F));

        PartDefinition left_hind_leg = moose.addOrReplaceChild("left_hind_leg", CubeListBuilder.create(), PartPose.offset(-7.0F, -38.9634F, -2.1665F));

        PartDefinition cube_r30 = left_hind_leg.addOrReplaceChild("cube_r30", CubeListBuilder.create().texOffs(0, 103).addBox(-8.0F, -39.0F, -21.5F, 4.0F, 18.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(7.0F, 42.9634F, 26.1665F, 0.0F, 0.0F, 0.0F));

        PartDefinition cube_r31 = left_hind_leg.addOrReplaceChild("cube_r31", CubeListBuilder.create().texOffs(81, 78).addBox(-9.0F, -8.9699F, -10.5488F, 6.0F, 9.0F, 16.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(7.0F, -1.0366F, 2.1665F, -1.8326F, 0.0F, 0.0F));

        PartDefinition left_front_leg = moose.addOrReplaceChild("left_front_leg", CubeListBuilder.create(), PartPose.offset(-6.0F, -42.9128F, 30.7086F));

        PartDefinition bone3 = left_front_leg.addOrReplaceChild("bone3", CubeListBuilder.create(), PartPose.offset(-3.0F, -2.0872F, -0.7086F));

        PartDefinition cube_r32 = bone3.addOrReplaceChild("cube_r32", CubeListBuilder.create().texOffs(51, 65).addBox(-9.0F, 19.0F, -55.0F, 6.0F, 8.0F, 17.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(9.0F, 49.0F, 26.0F, -1.5272F, 0.0F, 0.0F));

        PartDefinition bone5 = left_front_leg.addOrReplaceChild("bone5", CubeListBuilder.create(), PartPose.offset(6.0F, 46.9128F, 28.2914F));

        PartDefinition cube_r33 = bone5.addOrReplaceChild("cube_r33", CubeListBuilder.create().texOffs(25, 86).addBox(-8.0F, 25.0F, -38.0F, 4.0F, 4.0F, 17.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -1.5708F, 0.0F, 0.0F));

        PartDefinition right_front_leg = moose.addOrReplaceChild("right_front_leg", CubeListBuilder.create(), PartPose.offset(7.0F, -42.9128F, 30.7086F));

        PartDefinition bone4 = right_front_leg.addOrReplaceChild("bone4", CubeListBuilder.create(), PartPose.offset(2.0F, -2.0872F, -0.7086F));

        PartDefinition cube_r34 = bone4.addOrReplaceChild("cube_r34", CubeListBuilder.create().texOffs(54, 0).addBox(-10.0F, 19.0F, -55.0F, 6.0F, 8.0F, 17.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.0F, 49.0F, 26.0F, -1.5272F, 0.0F, 0.0F));

        PartDefinition bone6 = right_front_leg.addOrReplaceChild("bone6", CubeListBuilder.create(), PartPose.offset(0.0F, 34.9128F, 0.2914F));

        PartDefinition cube_r35 = bone6.addOrReplaceChild("cube_r35", CubeListBuilder.create().texOffs(0, 82).addBox(4.0F, 25.0F, -38.0F, 4.0F, 4.0F, 17.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-7.0F, 12.0F, 28.0F, -1.5708F, 0.0F, 0.0F));

        PartDefinition right_hind_leg = moose.addOrReplaceChild("right_hind_leg", CubeListBuilder.create(), PartPose.offset(7.0F, -38.9634F, -2.1665F));

        PartDefinition cube_r36 = right_hind_leg.addOrReplaceChild("cube_r36", CubeListBuilder.create().texOffs(0, 0).addBox(-8.0F, -39.0F, -21.5F, 4.0F, 18.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(5.0F, 42.9634F, 26.1665F, 0.0F, 0.0F, 0.0F));

        PartDefinition cube_r37 = right_hind_leg.addOrReplaceChild("cube_r37", CubeListBuilder.create().texOffs(80, 53).addBox(4.0F, -8.9699F, -10.5488F, 6.0F, 9.0F, 16.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-8.0F, -1.0366F, 2.1665F, -1.8326F, 0.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }

    public static final AnimationDefinition MOOSE_RUN = AnimationDefinition.Builder.withLength(0.9583434f).looping()
        .addAnimation("neck",
            new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.25f, KeyframeAnimations.degreeVec(-4.566151407625057f, 5.398935714646086f, 4.566151407625057f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.5f, KeyframeAnimations.degreeVec(0.8909982392571145f, 2.440552142406377f, 1.5636135959662716f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.75f, KeyframeAnimations.degreeVec(-3.684368349694159f, -4.6235401686931805f, -5.398590600548887f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.9583434f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("left_hind_leg",
            new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.25f, KeyframeAnimations.degreeVec(-12.5f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.7083434f, KeyframeAnimations.degreeVec(17.5f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.9583434f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("left_front_leg",
            new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.25f, KeyframeAnimations.degreeVec(12.5f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.7083434f, KeyframeAnimations.degreeVec(-12.5f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.9583434f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("right_front_leg",
            new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.25f, KeyframeAnimations.degreeVec(-12.5f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.7083434f, KeyframeAnimations.degreeVec(15f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.9583434f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("right_hind_leg",
            new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.25f, KeyframeAnimations.degreeVec(20f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.7083434f, KeyframeAnimations.degreeVec(-10f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.9583434f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR))).build();
    public static final AnimationDefinition MOOSE_EAT_GRASS = AnimationDefinition.Builder.withLength(12.291676f)
        .addAnimation("moose",
            new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("body",
            new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(2.125f, KeyframeAnimations.degreeVec(-17.5f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(11.041676f, KeyframeAnimations.degreeVec(-17.5f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(12.291676f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("neck",
            new AnimationChannel(AnimationChannel.Targets.POSITION,
                new Keyframe(0f, KeyframeAnimations.posVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(2.125f, KeyframeAnimations.posVec(0f, 0f, 5f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(11.791676f, KeyframeAnimations.posVec(0f, 0f, 5f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(12.291676f, KeyframeAnimations.posVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("neck",
            new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(2.125f, KeyframeAnimations.degreeVec(-31.97f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(11.791676f, KeyframeAnimations.degreeVec(-31.97f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(12.291676f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("head",
            new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(3.7916765f, KeyframeAnimations.degreeVec(12.5f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(4.5f, KeyframeAnimations.degreeVec(2.5f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(5.291677f, KeyframeAnimations.degreeVec(10f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(6.583433f, KeyframeAnimations.degreeVec(2.5f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(7.834333f, KeyframeAnimations.degreeVec(12.5f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(9.541676f, KeyframeAnimations.degreeVec(20f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(11.25f, KeyframeAnimations.degreeVec(5f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("bone3",
            new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(2.125f, KeyframeAnimations.degreeVec(0f, 0f, 2.5f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("bone4",
            new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(2.125f, KeyframeAnimations.degreeVec(0f, 0f, -2.5f),
                    AnimationChannel.Interpolations.LINEAR))).build();

    public static final AnimationDefinition MOOSE_WALK = AnimationDefinition.Builder.withLength(1.5834333f).looping()
        .addAnimation("neck",
            new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.375f, KeyframeAnimations.degreeVec(-4.566151407625057f, 5.398935714646086f, 4.566151407625057f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.8343334f, KeyframeAnimations.degreeVec(2.8063274466114123f, 0.2742341597003364f, -0.5754860573808216f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(1.2083433f, KeyframeAnimations.degreeVec(-4.680252810036091f, -4.254785989860138f, -4.722188855115492f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(1.5834333f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("left_hind_leg",
            new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.4167667f, KeyframeAnimations.degreeVec(-12.5f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(1.2083433f, KeyframeAnimations.degreeVec(17.5f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(1.5834333f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("left_front_leg",
            new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.4167667f, KeyframeAnimations.degreeVec(12.5f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(1.2083433f, KeyframeAnimations.degreeVec(-12.5f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(1.5834333f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("right_front_leg",
            new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.4167667f, KeyframeAnimations.degreeVec(-12.5f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(1.2083433f, KeyframeAnimations.degreeVec(15f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(1.5834333f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("right_hind_leg",
            new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.4167667f, KeyframeAnimations.degreeVec(20f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(1.2083433f, KeyframeAnimations.degreeVec(-10f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(1.5834333f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR))).build();

    private final ModelPart moose;
    private final ModelPart neck;
    private final ModelPart antler1;
    private final ModelPart antler2;
    private final ModelPart rightHindLeg;
    private final ModelPart leftHindLeg;
    private final ModelPart rightFrontLeg;
    private final ModelPart leftFrontLeg;
    private final ModelPart head;

    public MooseModel(ModelPart root)
    {
        super(root);
        this.moose = root.getChild("moose");
        this.neck = moose.getChild("body").getChild("neck");
        this.head = neck.getChild("head");
        this.antler1 = head.getChild("left_antler");
        this.antler2 = head.getChild("right_antler");
        this.rightHindLeg = moose.getChild("right_hind_leg");
        this.leftHindLeg = moose.getChild("left_hind_leg");
        this.rightFrontLeg = moose.getChild("right_front_leg");
        this.leftFrontLeg = moose.getChild("left_front_leg");

    }

    @Override
    public void setupAnim(RammingPrey entity, float limbSwing, float limbSwingAmount, float ageInTicks, float headYaw, float headPitch)
    {
        super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, headYaw, headPitch);
        final boolean antlers = entity.displayMaleCharacteristics();
        Stream.concat(antler1.getAllParts(), antler2.getAllParts()).forEach(p -> p.visible = antlers);
        final float speed = getAdjustedLandSpeed(entity);
        if (speed > 1f)
        {
            this.animateWalk(MOOSE_RUN, limbSwing, limbSwingAmount, 1f, 2.5f);
        }
        else
        {
            rightHindLeg.xRot = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
            leftHindLeg.xRot = Mth.cos(limbSwing * 0.6662F + Mth.PI) * 1.4F * limbSwingAmount;
            rightFrontLeg.xRot = Mth.cos(limbSwing * 0.6662F + Mth.PI) * 1.4F * limbSwingAmount;
            leftFrontLeg.xRot = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
        }

        if (entity.isTelegraphingAttack())
        {
            //Note for re-use: telegraph animations should be 1 second long, or the float here should be multiplied by their length
            //animate(entity.telegraphAnimation, BOAR_PREPARE_CHARGE, entity.getTelegraphAnimationProgress());
            this.head.xRot = entity.getTelegraphAttackTick() * Constants.DEG_TO_RAD * -1;
            this.neck.xRot = entity.getTelegraphAttackTick() * Constants.DEG_TO_RAD * -1;
        }
        else
        {
            this.head.xRot = headPitch * Constants.DEG_TO_RAD * 0.6f;
            this.neck.xRot = headPitch * Constants.DEG_TO_RAD * 0.4f;
            this.head.yRot = headYaw * Constants.DEG_TO_RAD * 0.6f;
            this.neck.yRot = headYaw * Constants.DEG_TO_RAD * 0.4f;
        }
    }
}
