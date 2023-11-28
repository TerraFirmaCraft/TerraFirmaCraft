/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

// Made with Blockbench 4.8.3
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports

package net.dries007.tfc.client.model.entity;

import com.mojang.math.Constants;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

import net.dries007.tfc.common.entities.prey.Prey;

public class CaribouModel extends HierarchicalAnimatedModel<Prey>
{

    private final ModelPart head;

    public CaribouModel(ModelPart root)
    {
        super(root);
        this.head = root.getChild("caribou").getChild("body").getChild("neck").getChild("head");
    }

    public static LayerDefinition createBodyLayer()
    {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition caribou = partdefinition.addOrReplaceChild("caribou", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 47.0F, 17.0F, 0.0F, 3.1416F, 0.0F));

        PartDefinition body = caribou.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-5.0F, -1.04F, 5.832F, 8.0F, 13.0F, 18.0F, new CubeDeformation(0.0F)), PartPose.offset(1.0F, -48.0F, -2.0F));

        PartDefinition cube_r1 = body.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(21, 56).addBox(-1.0F, -38.4515F, 0.5567F, 2.0F, 7.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, 35.0F, -9.0F, -0.3578F, 0.0F, 0.0F));

        PartDefinition cube_r2 = body.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(0, 31).addBox(-5.0F, -48.0F, -1.0F, 10.0F, 15.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, 46.0F, 26.0F, 0.0873F, 0.0F, 0.0F));

        PartDefinition neck = body.addOrReplaceChild("neck", CubeListBuilder.create().texOffs(34, 60).addBox(-3.0F, 2.7643F, 8.1755F, 5.0F, 9.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offset(-0.5F, -4.7643F, 21.8245F));

        PartDefinition head = neck.addOrReplaceChild("head", CubeListBuilder.create().texOffs(30, 16).addBox(-0.4926F, 2.923F, -8.4288F, 0.0F, 9.0F, 15.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.0074F, 6.5199F, 17.5063F, -0.0436F, 0.0F, 0.0F));

        PartDefinition cube_r3 = head.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(10, 12).addBox(-2.6981F, -0.3506F, -5.9845F, 3.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.8535F, -5.0215F, 4.8818F, 0.0243F, -0.0913F, -0.8601F));

        PartDefinition cube_r4 = head.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(29, 62).addBox(-0.3019F, -0.3506F, -5.9845F, 3.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.8683F, -5.0215F, 4.8818F, 0.0243F, 0.0913F, 0.8601F));

        PartDefinition cube_r5 = head.addOrReplaceChild("cube_r5", CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, -57.0F, 49.0F, 4.0F, 7.0F, 5.0F, new CubeDeformation(0.0F))
            .texOffs(64, 60).addBox(-3.0F, -59.0F, 41.0F, 6.0F, 9.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.4926F, 35.923F, -60.4288F, -0.3578F, 0.0F, 0.0F));

        PartDefinition rightAntler = head.addOrReplaceChild("rightAntler", CubeListBuilder.create(), PartPose.offsetAndRotation(1.0148F, -4.0F, 3.0F, 0.1309F, 0.0F, 0.0F));

        PartDefinition cube_r6 = rightAntler.addOrReplaceChild("cube_r6", CubeListBuilder.create().texOffs(62, 60).addBox(-0.25F, -9.0F, 1.5F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
            .texOffs(0, 63).addBox(-0.25F, -8.0F, 0.5F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
            .texOffs(5, 64).addBox(-0.25F, -7.0F, 2.5F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
            .texOffs(64, 44).addBox(-0.25F, -5.0F, 2.5F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
            .texOffs(0, 31).addBox(-0.25F, -7.0F, -1.5F, 1.0F, 3.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.3982F, -0.2517F, 0.1407F));

        PartDefinition cube_r7 = rightAntler.addOrReplaceChild("cube_r7", CubeListBuilder.create().texOffs(59, 64).addBox(4.75F, -5.0F, -20.75F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
            .texOffs(21, 65).addBox(4.75F, -8.0F, -20.75F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
            .texOffs(0, 0).addBox(4.75F, -12.0F, -18.75F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(0, 12).addBox(4.75F, -13.0F, -16.75F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(37, 60).addBox(4.75F, -10.0F, -17.75F, 1.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, -1.0F, -1.75F, -0.7091F, -0.1449F, -0.1313F));

        PartDefinition cube_r8 = rightAntler.addOrReplaceChild("cube_r8", CubeListBuilder.create().texOffs(52, 20).addBox(-0.25F, -16.25F, -11.0F, 2.0F, 9.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, -1.0F, -1.75F, -0.0651F, -0.1608F, 0.1889F));

        PartDefinition cube_r9 = rightAntler.addOrReplaceChild("cube_r9", CubeListBuilder.create().texOffs(40, 40).addBox(-2.0F, -15.25F, 1.0F, 2.0F, 11.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.9873F, -0.19F, 0.4085F));

        PartDefinition cube_r10 = rightAntler.addOrReplaceChild("cube_r10", CubeListBuilder.create().texOffs(73, 0).addBox(0.0F, -5.0F, 3.0F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
            .texOffs(42, 13).addBox(0.0F, -4.0F, 1.0F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
            .texOffs(34, 15).addBox(0.0F, -3.0F, 4.0F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
            .texOffs(0, 38).addBox(0.0F, -1.0F, 4.0F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
            .texOffs(66, 25).addBox(0.0F, -3.0F, 2.0F, 1.0F, 3.0F, 2.0F, new CubeDeformation(0.0F))
            .texOffs(0, 12).addBox(0.0F, -3.0F, -2.0F, 1.0F, 2.0F, 4.0F, new CubeDeformation(0.0F))
            .texOffs(60, 20).addBox(-1.0F, -5.0F, -4.0F, 2.0F, 5.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.0426F, -0.0094F, 0.3056F));

        PartDefinition leftAntler = head.addOrReplaceChild("leftAntler", CubeListBuilder.create(), PartPose.offsetAndRotation(-2.0F, -4.0F, 3.0F, 0.1309F, 0.0F, 0.0F));

        PartDefinition cube_r11 = leftAntler.addOrReplaceChild("cube_r11", CubeListBuilder.create().texOffs(0, 75).addBox(-0.75F, -9.0F, 1.5F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
            .texOffs(17, 75).addBox(-0.75F, -8.0F, 0.5F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
            .texOffs(25, 75).addBox(-0.75F, -7.0F, 2.5F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
            .texOffs(73, 77).addBox(-0.75F, -5.0F, 2.5F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
            .texOffs(0, 56).addBox(-0.75F, -7.0F, -1.5F, 1.0F, 3.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.3982F, 0.2517F, -0.1407F));

        PartDefinition cube_r12 = leftAntler.addOrReplaceChild("cube_r12", CubeListBuilder.create().texOffs(74, 53).addBox(-5.75F, -5.0F, -20.75F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
            .texOffs(73, 8).addBox(-5.75F, -8.0F, -20.75F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
            .texOffs(6, 12).addBox(-5.75F, -12.0F, -18.75F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(13, 0).addBox(-5.75F, -13.0F, -16.75F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(61, 39).addBox(-5.75F, -10.0F, -17.75F, 1.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, -1.0F, -1.75F, -0.7091F, 0.1449F, 0.1313F));

        PartDefinition cube_r13 = leftAntler.addOrReplaceChild("cube_r13", CubeListBuilder.create().texOffs(54, 0).addBox(-1.75F, -16.25F, -11.0F, 2.0F, 9.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, -1.0F, -1.75F, -0.0651F, 0.1608F, -0.1889F));

        PartDefinition cube_r14 = leftAntler.addOrReplaceChild("cube_r14", CubeListBuilder.create().texOffs(46, 0).addBox(0.0F, -15.25F, 1.0F, 2.0F, 11.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.9873F, 0.19F, -0.4085F));

        PartDefinition cube_r15 = leftAntler.addOrReplaceChild("cube_r15", CubeListBuilder.create().texOffs(73, 4).addBox(-1.0F, -5.0F, 3.0F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
            .texOffs(60, 27).addBox(-1.0F, -4.0F, 1.0F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
            .texOffs(6, 56).addBox(-1.0F, -3.0F, 4.0F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
            .texOffs(64, 64).addBox(-1.0F, -1.0F, 4.0F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
            .texOffs(78, 39).addBox(-1.0F, -3.0F, 2.0F, 1.0F, 3.0F, 2.0F, new CubeDeformation(0.0F))
            .texOffs(29, 56).addBox(-1.0F, -3.0F, -2.0F, 1.0F, 2.0F, 4.0F, new CubeDeformation(0.0F))
            .texOffs(54, 60).addBox(-1.0F, -5.0F, -4.0F, 2.0F, 5.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.0426F, 0.0094F, -0.3056F));

        PartDefinition left_hind_leg = caribou.addOrReplaceChild("left_hind_leg", CubeListBuilder.create(), PartPose.offset(-7.0F, -42.9634F, 9.8335F));

        PartDefinition cube_r16 = left_hind_leg.addOrReplaceChild("cube_r16", CubeListBuilder.create().texOffs(61, 77).addBox(-5.25F, -39.0F, -20.5F, 3.0F, 12.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(7.0F, 46.9634F, 17.1665F, 0.0F, 0.0F, 0.0F));

        PartDefinition cube_r17 = left_hind_leg.addOrReplaceChild("cube_r17", CubeListBuilder.create().texOffs(52, 0).addBox(-6.0F, -8.9699F, -7.5488F, 4.0F, 7.0F, 13.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(7.0F, 2.9634F, -6.8335F, -1.8326F, 0.0F, 0.0F));

        PartDefinition left_front_leg = caribou.addOrReplaceChild("left_front_leg", CubeListBuilder.create(), PartPose.offset(-6.0F, -42.9128F, 24.7086F));

        PartDefinition bone3 = left_front_leg.addOrReplaceChild("bone3", CubeListBuilder.create(), PartPose.offset(-3.0F, -2.0872F, 6.2914F));

        PartDefinition cube_r18 = bone3.addOrReplaceChild("cube_r18", CubeListBuilder.create().texOffs(60, 20).addBox(-6.0F, 26.9933F, -52.3053F, 4.0F, 6.0F, 13.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(9.0F, 49.0F, 26.0F, -1.5272F, 0.0F, 0.0F));

        PartDefinition bone5 = left_front_leg.addOrReplaceChild("bone5", CubeListBuilder.create(), PartPose.offset(6.0F, 46.9128F, 34.2914F));

        PartDefinition cube_r19 = bone5.addOrReplaceChild("cube_r19", CubeListBuilder.create().texOffs(0, 75).addBox(-5.0F, 32.0F, -38.0F, 3.0F, 3.0F, 11.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -1.5708F, 0.0F, 0.0F));

        PartDefinition right_front_leg = caribou.addOrReplaceChild("right_front_leg", CubeListBuilder.create(), PartPose.offset(4.0F, -42.9128F, 24.7086F));

        PartDefinition bone4 = right_front_leg.addOrReplaceChild("bone4", CubeListBuilder.create(), PartPose.offset(5.0F, -2.0872F, 5.2914F));

        PartDefinition cube_r20 = bone4.addOrReplaceChild("cube_r20", CubeListBuilder.create().texOffs(0, 56).addBox(-11.0F, 25.9933F, -52.3053F, 4.0F, 6.0F, 13.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.0F, 49.0F, 26.0F, -1.5272F, 0.0F, 0.0F));

        PartDefinition bone6 = right_front_leg.addOrReplaceChild("bone6", CubeListBuilder.create(), PartPose.offset(3.0F, 34.9128F, 6.2914F));

        PartDefinition cube_r21 = bone6.addOrReplaceChild("cube_r21", CubeListBuilder.create().texOffs(61, 39).addBox(2.0F, 32.0F, -38.0F, 3.0F, 3.0F, 11.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-7.0F, 12.0F, 28.0F, -1.5708F, 0.0F, 0.0F));

        PartDefinition right_back_leg = caribou.addOrReplaceChild("right_back_leg", CubeListBuilder.create(), PartPose.offset(7.0F, -42.9634F, 9.8335F));

        PartDefinition cube_r22 = right_back_leg.addOrReplaceChild("cube_r22", CubeListBuilder.create().texOffs(34, 0).addBox(-9.75F, -39.0F, -20.5F, 3.0F, 12.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(5.0F, 46.9634F, 17.1665F, 0.0F, 0.0F, 0.0F));

        PartDefinition cube_r23 = right_back_leg.addOrReplaceChild("cube_r23", CubeListBuilder.create().texOffs(40, 40).addBox(3.0F, -8.9699F, -7.5488F, 4.0F, 7.0F, 13.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-8.0F, 2.9634F, -6.8335F, -1.8326F, 0.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }

    @Override
    public void setupAnim(Prey entity, float limbSwing, float limbSwingAmount, float ageInTicks, float headYaw, float headPitch)
    {
        super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, headYaw, headPitch);
        final float speed = getAdjustedLandSpeed(entity);
        if (speed > 1.1f)
        {
            this.animateWalk(MooseModel.MOOSE_RUN, limbSwing, limbSwingAmount, 1f, 2.5f);
        }
        else
        {
            this.animateWalk(MooseModel.MOOSE_WALK, limbSwing, limbSwingAmount, 2.5f, 2.5f);
        }


        this.head.xRot = headPitch * Constants.DEG_TO_RAD;
        this.head.yRot = headYaw * Constants.DEG_TO_RAD;
    }
}