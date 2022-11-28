/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

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
import net.dries007.tfc.common.entities.prey.Prey;

public class MooseModel extends HierarchicalAnimatedModel<Prey>
{
    public static LayerDefinition createBodyLayer()
    {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition Moose = partdefinition.addOrReplaceChild("Moose", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 31.0F, 17.0F, 0.0F, 3.1416F, 0.0F));

        PartDefinition body = Moose.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-8.0F, -4.0F, 4.0F, 15.0F, 16.0F, 23.0F, new CubeDeformation(0.0F)), PartPose.offset(1.0F, -48.0F, -2.0F));

        PartDefinition cube_r1 = body.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(0, 39).addBox(-14.0F, -47.8942F, 21.5911F, 13.0F, 13.0F, 25.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(7.0F, 46.0F, 0.0F, 0.2618F, 0.0F, 0.0F));

        PartDefinition cube_r2 = body.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(58, 21).addBox(-8.0F, -50.0F, -35.0F, 17.0F, 17.0F, 18.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, 46.0F, 26.0F, -0.0436F, 0.0F, 0.0F));

        PartDefinition cube_r3 = body.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(128, 140).addBox(-2.0F, -36.0F, -12.0F, 4.0F, 10.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, 35.0F, -9.0F, -0.3578F, 0.0F, 0.0F));

        PartDefinition cube_r4 = body.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(60, 61).addBox(-8.0F, -51.0F, 2.0F, 17.0F, 18.0F, 16.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, 46.0F, 26.0F, 0.0873F, 0.0F, 0.0F));

        PartDefinition neck = body.addOrReplaceChild("neck", CubeListBuilder.create().texOffs(96, 105).addBox(-3.5F, 0.7643F, 0.1755F, 7.0F, 12.0F, 18.0F, new CubeDeformation(0.0F)), PartPose.offset(-0.5F, -9.7643F, 33.8245F));

        PartDefinition cube_r5 = neck.addOrReplaceChild("cube_r5", CubeListBuilder.create().texOffs(100, 135).addBox(-2.0F, -50.0F, -14.0F, 5.0F, 13.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.5F, 55.7643F, -7.8245F, -0.5672F, 0.0F, 0.0F));

        PartDefinition cube_r6 = neck.addOrReplaceChild("cube_r6", CubeListBuilder.create().texOffs(0, 77).addBox(-5.0F, -62.0F, 5.0F, 11.0F, 11.0F, 17.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.5F, 51.7643F, -31.8245F, -0.4363F, 0.0F, 0.0F));

        PartDefinition head = neck.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 39).addBox(-0.4926F, 2.923F, -2.4288F, 1.0F, 10.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.0074F, 7.5199F, 17.5063F, -0.0436F, 0.0F, 0.0F));

        PartDefinition cube_r7 = head.addOrReplaceChild("cube_r7", CubeListBuilder.create().texOffs(39, 83).addBox(-1.5F, -2.5F, -4.0F, 3.0F, 5.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.8876F, -6.2647F, 4.9703F, -0.0763F, -0.0886F, -0.2467F));

        PartDefinition cube_r8 = head.addOrReplaceChild("cube_r8", CubeListBuilder.create().texOffs(0, 83).addBox(-1.5F, -2.5F, -4.0F, 3.0F, 5.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.8683F, -6.0215F, 4.8818F, -0.0326F, 0.0886F, 0.2467F));

        PartDefinition cube_r9 = head.addOrReplaceChild("cube_r9", CubeListBuilder.create().texOffs(0, 133).addBox(-3.0F, -58.0F, 51.0F, 7.0F, 8.0F, 11.0F, new CubeDeformation(0.0F))
            .texOffs(126, 67).addBox(-4.0F, -61.0F, 41.0F, 9.0F, 11.0F, 11.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.4926F, 35.923F, -60.4288F, -0.3578F, 0.0F, 0.0F));

        PartDefinition cube_r10 = head.addOrReplaceChild("cube_r10", CubeListBuilder.create().texOffs(140, 28).addBox(-2.0F, -64.0F, 45.0F, 5.0F, 4.0F, 11.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.4926F, 35.923F, -60.4288F, -0.4451F, 0.0F, 0.0F));

        PartDefinition cube_r11 = head.addOrReplaceChild("cube_r11", CubeListBuilder.create().texOffs(63, 143).addBox(-1.0F, -36.0F, 42.0F, 3.0F, 5.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.4926F, 43.923F, -40.4288F, 0.0873F, 0.0F, 0.0F));

        PartDefinition lefthorn = head.addOrReplaceChild("lefthorn", CubeListBuilder.create(), PartPose.offsetAndRotation(-13.9398F, -8.9723F, 4.6713F, 0.4349F, 0.0368F, -0.0791F));

        PartDefinition cube_r12 = lefthorn.addOrReplaceChild("cube_r12", CubeListBuilder.create().texOffs(0, 59).addBox(3.5F, 6.0F, -0.5F, 3.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(0, 54).addBox(-0.5F, 2.0F, -0.5F, 8.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.8055F, 1.0429F, 1.1749F, 0.0627F, 0.6157F, -0.5853F));

        PartDefinition cube_r13 = lefthorn.addOrReplaceChild("cube_r13", CubeListBuilder.create().texOffs(73, 7).addBox(-8.5F, -2.0F, 0.5F, 12.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.9419F, 8.7268F, 7.112F, -2.9171F, 1.465F, 2.041F));

        PartDefinition cube_r14 = lefthorn.addOrReplaceChild("cube_r14", CubeListBuilder.create().texOffs(73, 0).addBox(-3.0F, 0.5F, -2.0F, 10.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.4218F, 5.1432F, 1.1283F, -0.8226F, 0.1677F, 0.0781F));

        PartDefinition cube_r15 = lefthorn.addOrReplaceChild("cube_r15", CubeListBuilder.create().texOffs(53, 9).addBox(-6.0F, -1.0F, -0.5F, 7.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.6471F, 5.1476F, -0.4881F, -0.6757F, 1.1065F, -1.9179F));

        PartDefinition cube_r16 = lefthorn.addOrReplaceChild("cube_r16", CubeListBuilder.create().texOffs(60, 95).addBox(-1.5F, -5.0F, -0.5F, 1.0F, 9.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.8055F, 1.0429F, 1.1749F, 0.0191F, 0.6157F, -0.5853F));

        PartDefinition cube_r17 = lefthorn.addOrReplaceChild("cube_r17", CubeListBuilder.create().texOffs(51, 39).addBox(0.5F, -10.0F, -0.5F, 2.0F, 11.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.8055F, 1.0429F, 1.1749F, -0.0245F, 0.6157F, -0.5853F));

        PartDefinition cube_r18 = lefthorn.addOrReplaceChild("cube_r18", CubeListBuilder.create().texOffs(70, 95).addBox(-1.0F, -4.0F, -0.5F, 2.0F, 6.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.0766F, -6.9028F, -3.0826F, 0.0626F, 0.5558F, -0.1181F));

        PartDefinition cube_r19 = lefthorn.addOrReplaceChild("cube_r19", CubeListBuilder.create().texOffs(51, 51).addBox(-1.0F, -1.5F, -0.5F, 2.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.5834F, -6.7346F, -4.5476F, 0.1499F, 0.5558F, -0.1181F));

        PartDefinition cube_r20 = lefthorn.addOrReplaceChild("cube_r20", CubeListBuilder.create().texOffs(50, 82).addBox(-1.0F, -5.5F, -0.5F, 2.0F, 7.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.4781F, -6.1341F, -1.3821F, 0.1499F, 0.5558F, -0.1181F));

        PartDefinition cube_r21 = lefthorn.addOrReplaceChild("cube_r21", CubeListBuilder.create().texOffs(0, 9).addBox(2.0F, -9.0F, 2.0F, 8.0F, 8.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-5.4725F, 4.8411F, 0.632F, 0.2808F, 0.5558F, -0.1181F));

        PartDefinition bone = lefthorn.addOrReplaceChild("bone", CubeListBuilder.create(), PartPose.offsetAndRotation(0.9419F, 5.7268F, 7.112F, 0.2618F, -0.3491F, 0.0F));

        PartDefinition cube_r22 = bone.addOrReplaceChild("cube_r22", CubeListBuilder.create().texOffs(53, 20).addBox(-5.5F, -2.0F, 0.5F, 9.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 1.0F, 0.0F, -2.9171F, 1.465F, 2.041F));

        PartDefinition cube_r23 = bone.addOrReplaceChild("cube_r23", CubeListBuilder.create().texOffs(53, 6).addBox(-6.0F, -1.0F, -0.5F, 7.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.2949F, -2.5792F, -7.6002F, -0.6757F, 1.1065F, -1.9179F));

        PartDefinition righthorn2 = head.addOrReplaceChild("righthorn2", CubeListBuilder.create(), PartPose.offsetAndRotation(16.6547F, -8.2266F, 6.3154F, 0.4349F, -0.0368F, 0.0791F));

        PartDefinition cube_r24 = righthorn2.addOrReplaceChild("cube_r24", CubeListBuilder.create().texOffs(53, 12).addBox(-6.5F, 6.0F, -0.5F, 3.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(7, 39).addBox(-7.5F, 2.0F, -0.5F, 8.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0627F, -0.6157F, 0.5853F));

        PartDefinition cube_r25 = righthorn2.addOrReplaceChild("cube_r25", CubeListBuilder.create().texOffs(73, 4).addBox(-3.5F, -2.0F, 0.5F, 12.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-7.7474F, 7.6839F, 5.9371F, -2.9171F, -1.465F, -2.041F));

        PartDefinition cube_r26 = righthorn2.addOrReplaceChild("cube_r26", CubeListBuilder.create().texOffs(51, 56).addBox(-7.0F, 0.5F, -2.0F, 10.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-8.2273F, 4.1003F, -0.0466F, -0.8226F, -0.1677F, -0.0781F));

        PartDefinition cube_r27 = righthorn2.addOrReplaceChild("cube_r27", CubeListBuilder.create().texOffs(53, 3).addBox(-1.0F, -1.0F, -0.5F, 7.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-6.4526F, 4.1047F, -1.663F, -0.6757F, -1.1065F, 1.9179F));

        PartDefinition cube_r28 = righthorn2.addOrReplaceChild("cube_r28", CubeListBuilder.create().texOffs(18, 0).addBox(0.5F, -5.0F, -0.5F, 1.0F, 9.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0191F, -0.6157F, 0.5853F));

        PartDefinition cube_r29 = righthorn2.addOrReplaceChild("cube_r29", CubeListBuilder.create().texOffs(18, 50).addBox(-2.5F, -10.0F, -0.5F, 2.0F, 11.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.0245F, -0.6157F, 0.5853F));

        PartDefinition cube_r30 = righthorn2.addOrReplaceChild("cube_r30", CubeListBuilder.create().texOffs(64, 95).addBox(-1.0F, -4.0F, -0.5F, 2.0F, 6.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.7289F, -7.9457F, -4.2575F, 0.0626F, -0.5558F, 0.1181F));

        PartDefinition cube_r31 = righthorn2.addOrReplaceChild("cube_r31", CubeListBuilder.create().texOffs(12, 50).addBox(-1.0F, -1.5F, -0.5F, 2.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.3889F, -7.7776F, -5.7225F, 0.1499F, -0.5558F, 0.1181F));

        PartDefinition cube_r32 = righthorn2.addOrReplaceChild("cube_r32", CubeListBuilder.create().texOffs(11, 82).addBox(-1.0F, -5.5F, -0.5F, 2.0F, 7.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.6726F, -7.177F, -2.557F, 0.1499F, -0.5558F, 0.1181F));

        PartDefinition cube_r33 = righthorn2.addOrReplaceChild("cube_r33", CubeListBuilder.create().texOffs(0, 0).addBox(-10.0F, -9.0F, 2.0F, 8.0F, 8.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.667F, 3.7982F, -0.5429F, 0.2808F, -0.5558F, 0.1181F));

        PartDefinition bone2 = righthorn2.addOrReplaceChild("bone2", CubeListBuilder.create(), PartPose.offsetAndRotation(-4.7474F, 4.6839F, 5.9371F, 0.2618F, 0.3491F, 0.0F));

        PartDefinition cube_r34 = bone2.addOrReplaceChild("cube_r34", CubeListBuilder.create().texOffs(0, 18).addBox(-3.5F, -2.0F, 0.5F, 9.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 1.0F, 0.0F, -2.9171F, -1.465F, -2.041F));

        PartDefinition cube_r35 = bone2.addOrReplaceChild("cube_r35", CubeListBuilder.create().texOffs(53, 0).addBox(-1.0F, -1.0F, -0.5F, 7.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.2949F, -2.5792F, -7.6002F, -0.6757F, -1.1065F, 1.9179F));

        PartDefinition leftbackleg = Moose.addOrReplaceChild("leftbackleg", CubeListBuilder.create(), PartPose.offset(-6.0F, -38.9634F, -2.1665F));

        PartDefinition cube_r36 = leftbackleg.addOrReplaceChild("cube_r36", CubeListBuilder.create().texOffs(73, 123).addBox(-7.0F, 24.0F, -32.0F, 2.0F, 4.0F, 16.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(6.0F, 42.9634F, 26.1665F, -1.4399F, 0.0F, 0.0F));

        PartDefinition cube_r37 = leftbackleg.addOrReplaceChild("cube_r37", CubeListBuilder.create().texOffs(132, 121).addBox(-8.0F, 34.0F, -13.0F, 4.0F, 5.0F, 14.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(6.0F, 36.9634F, 26.1665F, -2.1817F, 0.0F, 0.0F));

        PartDefinition cube_r38 = leftbackleg.addOrReplaceChild("cube_r38", CubeListBuilder.create().texOffs(12, 44).addBox(-8.0F, 60.0F, -13.0F, 4.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(6.0F, 42.9634F, 61.1665F, -1.5708F, 0.0F, 0.0F));

        PartDefinition cube_r39 = leftbackleg.addOrReplaceChild("cube_r39", CubeListBuilder.create().texOffs(31, 118).addBox(-9.0F, 8.0F, -55.0F, 6.0F, 11.0F, 15.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(6.0F, 42.9634F, 26.1665F, -1.309F, 0.0F, 0.0F));

        PartDefinition leftforleg = Moose.addOrReplaceChild("leftforleg", CubeListBuilder.create(), PartPose.offset(-6.0F, -42.9128F, 30.7086F));

        PartDefinition bone3 = leftforleg.addOrReplaceChild("bone3", CubeListBuilder.create(), PartPose.offset(-3.0F, -2.0872F, -0.7086F));

        PartDefinition cube_r40 = bone3.addOrReplaceChild("cube_r40", CubeListBuilder.create().texOffs(139, 0).addBox(-8.0F, 27.0F, -30.0F, 4.0F, 6.0F, 11.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(9.0F, 43.0F, 29.0F, -1.6581F, 0.0F, 0.0F));

        PartDefinition cube_r41 = bone3.addOrReplaceChild("cube_r41", CubeListBuilder.create().texOffs(110, 0).addBox(-9.0F, 18.0F, -55.0F, 6.0F, 11.0F, 17.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(9.0F, 49.0F, 26.0F, -1.5272F, 0.0F, 0.0F));

        PartDefinition bone5 = leftforleg.addOrReplaceChild("bone5", CubeListBuilder.create(), PartPose.offset(6.0F, 46.9128F, 28.2914F));

        PartDefinition cube_r42 = bone5.addOrReplaceChild("cube_r42", CubeListBuilder.create().texOffs(73, 124).addBox(-7.0F, 26.0F, -29.0F, 2.0F, 3.0F, 16.0F, new CubeDeformation(0.0F))
            .texOffs(39, 77).addBox(-8.0F, 25.0F, -13.0F, 4.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -1.5708F, 0.0F, 0.0F));

        PartDefinition rightforleg = Moose.addOrReplaceChild("rightforleg", CubeListBuilder.create(), PartPose.offset(7.0F, -42.9128F, 30.7086F));

        PartDefinition bone4 = rightforleg.addOrReplaceChild("bone4", CubeListBuilder.create(), PartPose.offset(2.0F, -2.0872F, -0.7086F));

        PartDefinition cube_r43 = bone4.addOrReplaceChild("cube_r43", CubeListBuilder.create().texOffs(0, 105).addBox(-9.0F, 18.0F, -55.0F, 6.0F, 11.0F, 17.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.0F, 49.0F, 26.0F, -1.5272F, 0.0F, 0.0F));

        PartDefinition cube_r44 = bone4.addOrReplaceChild("cube_r44", CubeListBuilder.create().texOffs(89, 0).addBox(-8.0F, 27.0F, -30.0F, 4.0F, 6.0F, 11.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.0F, 43.0F, 29.0F, -1.6581F, 0.0F, 0.0F));

        PartDefinition bone6 = rightforleg.addOrReplaceChild("bone6", CubeListBuilder.create(), PartPose.offset(0.0F, 34.9128F, 0.2914F));

        PartDefinition cube_r45 = bone6.addOrReplaceChild("cube_r45", CubeListBuilder.create().texOffs(73, 123).addBox(-7.0F, 26.0F, -29.0F, 2.0F, 3.0F, 16.0F, new CubeDeformation(0.0F))
            .texOffs(0, 77).addBox(-8.0F, 25.0F, -13.0F, 4.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(6.0F, 12.0F, 28.0F, -1.5708F, 0.0F, 0.0F));

        PartDefinition rightbackleg = Moose.addOrReplaceChild("rightbackleg", CubeListBuilder.create(), PartPose.offset(7.0F, -38.9634F, -2.1665F));

        PartDefinition cube_r46 = rightbackleg.addOrReplaceChild("cube_r46", CubeListBuilder.create().texOffs(53, 0).addBox(-7.0F, 24.0F, -32.0F, 2.0F, 4.0F, 16.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(6.0F, 42.9634F, 26.1665F, -1.4399F, 0.0F, 0.0F));

        PartDefinition cube_r47 = rightbackleg.addOrReplaceChild("cube_r47", CubeListBuilder.create().texOffs(128, 89).addBox(-8.0F, 34.0F, -13.0F, 4.0F, 5.0F, 14.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(6.0F, 36.9634F, 26.1665F, -2.1817F, 0.0F, 0.0F));

        PartDefinition cube_r48 = rightbackleg.addOrReplaceChild("cube_r48", CubeListBuilder.create().texOffs(73, 10).addBox(5.0F, 60.0F, -13.0F, 4.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-7.0F, 42.9634F, 61.1665F, -1.5708F, 0.0F, 0.0F));

        PartDefinition cube_r49 = rightbackleg.addOrReplaceChild("cube_r49", CubeListBuilder.create().texOffs(113, 41).addBox(-9.0F, 8.0F, -55.0F, 6.0F, 11.0F, 15.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(6.0F, 42.9634F, 26.1665F, -1.309F, 0.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 256, 256);
    }

    public static final AnimationDefinition MOOSE_RUN = AnimationDefinition.Builder.withLength(0.9583434f).looping()
        .addAnimation("neck",
            new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, VanillaAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.25f, VanillaAnimations.degreeVec(-4.566151407625057f, 5.398935714646086f, 4.566151407625057f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.5f, VanillaAnimations.degreeVec(0.8909982392571145f, 2.440552142406377f, 1.5636135959662716f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.75f, VanillaAnimations.degreeVec(-3.684368349694159f, -4.6235401686931805f, -5.398590600548887f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.9583434f, VanillaAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("leftbackleg",
            new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, VanillaAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.25f, VanillaAnimations.degreeVec(-12.5f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.7083434f, VanillaAnimations.degreeVec(17.5f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.9583434f, VanillaAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("leftforleg",
            new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, VanillaAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.25f, VanillaAnimations.degreeVec(12.5f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.7083434f, VanillaAnimations.degreeVec(-12.5f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.9583434f, VanillaAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("rightforleg",
            new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, VanillaAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.25f, VanillaAnimations.degreeVec(-12.5f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.7083434f, VanillaAnimations.degreeVec(15f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.9583434f, VanillaAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("rightbackleg",
            new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, VanillaAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.25f, VanillaAnimations.degreeVec(20f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.7083434f, VanillaAnimations.degreeVec(-10f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.9583434f, VanillaAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR))).build();
    public static final AnimationDefinition MOOSE_EAT_GRASS = AnimationDefinition.Builder.withLength(12.291676f)
        .addAnimation("Moose",
            new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, VanillaAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("body",
            new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, VanillaAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(2.125f, VanillaAnimations.degreeVec(-17.5f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(11.041676f, VanillaAnimations.degreeVec(-17.5f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(12.291676f, VanillaAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("neck",
            new AnimationChannel(AnimationChannel.Targets.POSITION,
                new Keyframe(0f, VanillaAnimations.posVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(2.125f, VanillaAnimations.posVec(0f, 0f, 5f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(11.791676f, VanillaAnimations.posVec(0f, 0f, 5f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(12.291676f, VanillaAnimations.posVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("neck",
            new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, VanillaAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(2.125f, VanillaAnimations.degreeVec(-31.97f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(11.791676f, VanillaAnimations.degreeVec(-31.97f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(12.291676f, VanillaAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("head",
            new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, VanillaAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(3.7916765f, VanillaAnimations.degreeVec(12.5f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(4.5f, VanillaAnimations.degreeVec(2.5f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(5.291677f, VanillaAnimations.degreeVec(10f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(6.583433f, VanillaAnimations.degreeVec(2.5f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(7.834333f, VanillaAnimations.degreeVec(12.5f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(9.541676f, VanillaAnimations.degreeVec(20f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(11.25f, VanillaAnimations.degreeVec(5f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("bone3",
            new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, VanillaAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(2.125f, VanillaAnimations.degreeVec(0f, 0f, 2.5f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("bone4",
            new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, VanillaAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(2.125f, VanillaAnimations.degreeVec(0f, 0f, -2.5f),
                    AnimationChannel.Interpolations.LINEAR))).build();

    public static final AnimationDefinition MOOSE_WALK = AnimationDefinition.Builder.withLength(1.5834333f).looping()
        .addAnimation("neck",
            new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, VanillaAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.375f, VanillaAnimations.degreeVec(-4.566151407625057f, 5.398935714646086f, 4.566151407625057f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.8343334f, VanillaAnimations.degreeVec(2.8063274466114123f, 0.2742341597003364f, -0.5754860573808216f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(1.2083433f, VanillaAnimations.degreeVec(-4.680252810036091f, -4.254785989860138f, -4.722188855115492f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(1.5834333f, VanillaAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("leftbackleg",
            new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, VanillaAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.4167667f, VanillaAnimations.degreeVec(-12.5f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(1.2083433f, VanillaAnimations.degreeVec(17.5f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(1.5834333f, VanillaAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("leftforleg",
            new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, VanillaAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.4167667f, VanillaAnimations.degreeVec(12.5f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(1.2083433f, VanillaAnimations.degreeVec(-12.5f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(1.5834333f, VanillaAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("rightforleg",
            new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, VanillaAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.4167667f, VanillaAnimations.degreeVec(-12.5f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(1.2083433f, VanillaAnimations.degreeVec(15f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(1.5834333f, VanillaAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("rightbackleg",
            new AnimationChannel(AnimationChannel.Targets.ROTATION,
                new Keyframe(0f, VanillaAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(0.4167667f, VanillaAnimations.degreeVec(20f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(1.2083433f, VanillaAnimations.degreeVec(-10f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR),
                new Keyframe(1.5834333f, VanillaAnimations.degreeVec(0f, 0f, 0f),
                    AnimationChannel.Interpolations.LINEAR))).build();

    private final ModelPart neck;
    private final ModelPart antler1;
    private final ModelPart antler2;

    public MooseModel(ModelPart root)
    {
        super(root);
        this.neck = root.getChild("Moose").getChild("body").getChild("neck");
        this.antler1 = neck.getChild("head").getChild("lefthorn");
        this.antler2 = neck.getChild("head").getChild("righthorn2");
    }

    @Override
    public void setupAnim(Prey entity, float limbSwing, float limbSwingAmount, float ageInTicks, float headYaw, float headPitch)
    {
        super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, headYaw, headPitch);
        final boolean antlers = entity.displayMaleCharacteristics();
        Stream.concat(antler1.getAllParts(), antler2.getAllParts()).forEach(p -> p.visible = antlers);
        final float speed = getAdjustedLandSpeed(entity);
        this.animate(entity.walkingAnimation, speed > 1f ? MOOSE_RUN : MOOSE_WALK, ageInTicks, speed);

        this.neck.xRot = headPitch * Constants.DEG_TO_RAD;
        this.neck.yRot = headYaw * Constants.DEG_TO_RAD;
    }
}
