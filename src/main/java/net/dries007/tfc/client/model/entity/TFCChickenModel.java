/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.model.entity;

// Made with Blockbench 4.2.4
// Exported for Minecraft version 1.17 - 1.18 with Mojang mappings
// Paste this class into your mod and generate all required imports


import com.google.common.collect.ImmutableList;
import net.minecraft.client.model.AgeableListModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;

import net.dries007.tfc.common.entities.livestock.OviparousAnimal;

public class TFCChickenModel extends AgeableListModel<OviparousAnimal>
{
    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-3.0F, -6.0F, -6.0F, 6.0F, 6.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 21.0F, 0.0F));

        PartDefinition legR = body.addOrReplaceChild("legR", CubeListBuilder.create().texOffs(6, 0).addBox(-1.0F, 0.0F, 0.0F, 1.0F, 3.0F, 0.0F, new CubeDeformation(0.0F))
            .texOffs(17, 4).addBox(-2.0F, 3.0F, -3.0F, 3.0F, 0.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.0F, 0.0F, 0.0F));

        PartDefinition legL = body.addOrReplaceChild("legL", CubeListBuilder.create().texOffs(0, 14).addBox(0.0F, 0.0F, 0.0F, 1.0F, 3.0F, 0.0F, new CubeDeformation(0.0F))
            .texOffs(23, 4).addBox(-1.0F, 3.0F, -3.0F, 3.0F, 0.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(1.0F, 0.0F, 0.0F));

        PartDefinition wingR = body.addOrReplaceChild("wingR", CubeListBuilder.create().texOffs(0, 23).addBox(-1.0F, 0.0F, -3.0F, 1.0F, 6.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-3.0F, -6.0F, -2.0F));

        PartDefinition neck = body.addOrReplaceChild("neck", CubeListBuilder.create(), PartPose.offset(0.0F, -5.0F, -5.0F));

        PartDefinition head = neck.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 14).addBox(-1.5F, -5.9924F, -2.1743F, 4.0F, 6.0F, 3.0F, new CubeDeformation(0.0F))
            .texOffs(20, 0).addBox(-1.5F, -3.9924F, -4.1743F, 4.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
            .texOffs(0, 0).addBox(0.5F, -9.0F, -2.0F, 0.0F, 4.0F, 4.0F, new CubeDeformation(0.0F))
            .texOffs(0, 0).addBox(-0.5F, -2.0F, -3.25F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.5F, 0.5F, 0.5F, 0.0F, 0.0F, 0.0F));

        PartDefinition wingL = body.addOrReplaceChild("wingL", CubeListBuilder.create().texOffs(10, 23).addBox(0.0F, 0.0F, -3.0F, 1.0F, 6.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(3.0F, -6.0F, -2.0F));

        PartDefinition tail = body.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(20, 13).addBox(0.0F, -11.0F, 0.0F, 0.0F, 12.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -5.0F, 1.25F));

        PartDefinition left_r1 = tail.addOrReplaceChild("left_r1", CubeListBuilder.create().texOffs(33, 16).addBox(-0.5F, -9.0F, 0.0F, 0.0F, 10.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, 0.0F, 0.0F, 0.0F, 0.0873F, 0.0F));

        PartDefinition right_r1 = tail.addOrReplaceChild("right_r1", CubeListBuilder.create().texOffs(33, 3).addBox(0.5F, -9.0F, 0.0F, 0.0F, 10.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, 0.0F, 0.0F, 0.0F, -0.0873F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    private final ModelPart body;
    private final ModelPart legR;
    private final ModelPart legL;
    private final ModelPart head;
    private final ModelPart wingR;
    private final ModelPart wingL;
    private final ModelPart neck;
    private final ModelPart tail;

    public TFCChickenModel(ModelPart root)
    {
        super(false, 0F, 0F, 1.8F, 1.8F, 18F);
        body = root.getChild("body");
        neck = body.getChild("neck");
        head = neck.getChild("head");
        legL = body.getChild("legL");
        legR = body.getChild("legR");
        wingL = body.getChild("wingL");
        wingR = body.getChild("wingR");
        tail = body.getChild("tail");
    }

    @Override
    public void setupAnim(OviparousAnimal chicken, float limbSwing, float limbSwingAmount, float ageInTicks, float headYaw, float headPitch)
    {
        float xPose = 0F;
        //Rooster stands upright
        if (chicken.displayMaleCharacteristics())
        {
            xPose = -0.71F;
        }

        head.xRot = headPitch * ((float) Math.PI / 180F);
        head.yRot = headYaw * ((float) Math.PI / 180F);
        legR.xRot = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount - xPose;
        legL.xRot = Mth.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.4F * limbSwingAmount - xPose;

        wingR.zRot = 0F;
        wingL.zRot = 0F;

        //Body Sway
        if (!chicken.isInWater())
        {
            body.xRot = Mth.cos(limbSwing * 0.6662F + ((float) Math.PI / 2F)) * 0.25F * limbSwingAmount + xPose;
            neck.xRot = Mth.cos(limbSwing * 0.6662F + ((float) Math.PI / 2F)) * -0.23F * limbSwingAmount - xPose;
        }
        else
        {
            body.xRot = xPose;
            neck.xRot = -xPose;
        }
        //Flapping in air
        if (!chicken.isOnGround())
        {
            wingR.zRot = ageInTicks;
            wingL.zRot = -ageInTicks;
        }
    }

    @Override
    protected Iterable<ModelPart> headParts()
    {
        return ImmutableList.of();
    }

    @Override
    protected Iterable<ModelPart> bodyParts()
    {
        return ImmutableList.of(this.body);
    }
}