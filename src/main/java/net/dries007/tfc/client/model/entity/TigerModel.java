/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

// Made with Blockbench 4.8.3
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports

package net.dries007.tfc.client.model.entity;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;

import net.dries007.tfc.common.entities.predator.FelinePredator;

public class TigerModel extends FelinePredatorModel<FelinePredator>
{
    private final ModelPart body;
    private final ModelPart tail;
    private final ModelPart neck;
    private final ModelPart head;
    private final ModelPart right_front_leg;
    private final ModelPart right_hind_leg;
    private final ModelPart left_front_leg;
    private final ModelPart left_hind_leg;
    private final ModelPart earL;
    private final ModelPart earR;
    private final ModelPart jaw;
    private final ModelPart nose;
    private final ModelPart rear;

    public TigerModel(ModelPart root)
    {
        super(root, SabertoothModel.SLEEP, SabertoothModel.WALK, SabertoothModel.RUN, SabertoothModel.ATTACK);
        this.body = root.getChild("body");
        this.neck = body.getChild("neck");
        this.rear = body.getChild("rear");
        this.tail = rear.getChild("tail");
        this.head = neck.getChild("head");
        this.jaw = head.getChild("jaw");
        this.nose = head.getChild("nose");
        this.earL = head.getChild("earL");
        this.earR = head.getChild("earR");
        this.right_front_leg = body.getChild("right_front_leg");
        this.left_front_leg = body.getChild("left_front_leg");
        this.right_hind_leg = rear.getChild("right_hind_leg");
        this.left_hind_leg = rear.getChild("left_hind_leg");
    }

    public static LayerDefinition createBodyLayer()
    {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offset(0.0F, 21.0F, -2.0F));

        PartDefinition body_r1 = body.addOrReplaceChild("body_r1", CubeListBuilder.create().texOffs(1, 1).addBox(-4.0F, -9.0F, -12.0F, 8.0F, 11.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -6.0F, 4.0F, -0.1309F, 0.0F, 0.0F));

        PartDefinition neck = body.addOrReplaceChild("neck", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -13.75F, -6.0F, 0.1745F, 0.0F, 0.0F));

        PartDefinition neck0_r1 = neck.addOrReplaceChild("neck0_r1", CubeListBuilder.create().texOffs(27, 25).addBox(-2.5F, -2.0F, -4.0F, 5.0F, 5.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.5F, -0.866F, -0.4363F, 0.0F, 0.0F));

        PartDefinition head = neck.addOrReplaceChild("head", CubeListBuilder.create().texOffs(32, 0).addBox(-3.0F, -4.0F, -6.0F, 6.0F, 7.0F, 6.0F, new CubeDeformation(0.0F))
            .texOffs(65, 12).addBox(-2.0F, -0.9564F, -9.001F, 4.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.25F, -2.866F, -0.1745F, 0.0F, 0.0F));

        PartDefinition nose = head.addOrReplaceChild("nose", CubeListBuilder.create().texOffs(65, 19).addBox(-1.0F, -1.6173F, -3.7761F, 2.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.0F, -5.5F, 0.3491F, 0.0F, 0.0F));

        PartDefinition jaw = head.addOrReplaceChild("jaw", CubeListBuilder.create().texOffs(40, 65).addBox(-1.5F, 1.1436F, -3.501F, 3.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.75F, -5.0F));

        PartDefinition earL = head.addOrReplaceChild("earL", CubeListBuilder.create().texOffs(0, 4).addBox(-1.8237F, -1.5152F, -0.0002F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.0F, -4.0F, -3.0F, 0.0F, -0.1745F, -0.1745F));

        PartDefinition earR = head.addOrReplaceChild("earR", CubeListBuilder.create().texOffs(0, 0).addBox(0.0F, -1.5F, 0.0F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.0F, -4.0F, -3.0F, 0.0F, 0.1745F, 0.1745F));

        PartDefinition right_front_leg = body.addOrReplaceChild("right_front_leg", CubeListBuilder.create().texOffs(52, 26).addBox(-1.0F, 5.0F, -2.0F, 4.0F, 8.0F, 4.0F, new CubeDeformation(0.0F))
            .texOffs(58, 38).addBox(-1.0F, 13.0F, -3.0F, 4.0F, 2.0F, 5.0F, new CubeDeformation(0.0F))
            .texOffs(46, 13).addBox(-1.0F, -3.0F, -3.0F, 4.0F, 8.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(-4.0F, -12.0F, -4.0F));

        PartDefinition left_front_leg = body.addOrReplaceChild("left_front_leg", CubeListBuilder.create().texOffs(38, 51).addBox(-3.0F, 5.0F, -2.0F, 4.0F, 8.0F, 4.0F, new CubeDeformation(0.0F))
            .texOffs(54, 58).addBox(-3.0F, 13.0F, -3.0F, 4.0F, 2.0F, 5.0F, new CubeDeformation(0.0F))
            .texOffs(20, 45).addBox(-3.0F, -3.0F, -3.0F, 4.0F, 8.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(4.0F, -12.0F, -4.0F));

        PartDefinition rear = body.addOrReplaceChild("rear", CubeListBuilder.create().texOffs(0, 25).addBox(-3.0F, 0.0F, 0.0F, 6.0F, 9.0F, 11.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -15.0F, 4.0F));

        PartDefinition tail = rear.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(27, 59).addBox(-1.0F, -1.0F, 0.0F, 2.0F, 2.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 1.0F, 10.0F, -0.7854F, 0.0F, 0.0F));

        PartDefinition tail1 = tail.addOrReplaceChild("tail1", CubeListBuilder.create().texOffs(27, 67).addBox(-1.01F, -1.0F, 0.0F, 2.0F, 2.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 6.0F, 0.6545F, 0.0F, 0.0F));

        PartDefinition tail2 = tail1.addOrReplaceChild("tail2", CubeListBuilder.create().texOffs(27, 75).addBox(-1.0F, -1.0F, 0.0F, 2.0F, 2.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 5.75F, 0.6545F, 0.0F, 0.0F));

        PartDefinition left_hind_leg = rear.addOrReplaceChild("left_hind_leg", CubeListBuilder.create().texOffs(57, 0).addBox(-2.0F, 5.0F, -2.0F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.0F))
            .texOffs(0, 45).addBox(-2.0F, -4.0F, -4.0F, 4.0F, 9.0F, 6.0F, new CubeDeformation(0.0F))
            .texOffs(0, 60).addBox(-2.0F, 11.0F, -3.0F, 4.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(3.0F, 5.0F, 8.0F));

        PartDefinition right_hind_leg = rear.addOrReplaceChild("right_hind_leg", CubeListBuilder.create().texOffs(54, 47).addBox(-2.0F, 5.0F, -2.0F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.0F))
            .texOffs(38, 36).addBox(-2.0F, -4.0F, -4.0F, 4.0F, 9.0F, 6.0F, new CubeDeformation(0.0F))
            .texOffs(15, 58).addBox(-2.0F, 11.0F, -3.0F, 4.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(-3.0F, 5.0F, 8.0F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }

    @Override
    public void setupHeadRotations(float netHeadYaw, float headPitch)
    {
        head.xRot = headPitch * Mth.PI / 720F;
        neck.xRot = headPitch * Mth.PI / 720F;
        head.yRot = netHeadYaw * Mth.PI / 360F;
        neck.yRot = netHeadYaw * Mth.PI / 360F;
    }

    @Override
    public void setupSleeping()
    {
        body.y = 28f;

        right_front_leg.y = -7f;
        right_front_leg.z = -4f;
        left_front_leg.y = -7f;
        left_front_leg.z = -4f;

        left_hind_leg.y = 8f;
        left_hind_leg.z = 7f;
        right_hind_leg.y = 8f;
        right_hind_leg.z = 7f;
    }
}