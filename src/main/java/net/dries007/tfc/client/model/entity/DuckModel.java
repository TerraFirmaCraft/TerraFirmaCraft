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

public class DuckModel extends AgeableListModel<OviparousAnimal>
{

    public static LayerDefinition createBodyLayer()
    {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-3.0F, -9.0F, -5.0F, 6.0F, 6.0F, 9.0F, new CubeDeformation(0.0F))
            .texOffs(9, 15).addBox(-2.0F, -9.0F, 4.0F, 4.0F, 5.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition legR = body.addOrReplaceChild("legR", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, 0.0F, 0.0F, 1.0F, 3.0F, 0.0F, new CubeDeformation(0.0F))
            .texOffs(0, 0).addBox(-2.0F, 3.0F, -3.0F, 3.0F, 0.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.0F, -3.0F, 0.0F));

        PartDefinition legL = body.addOrReplaceChild("legL", CubeListBuilder.create().texOffs(0, 3).addBox(0.0F, 0.0F, 0.0F, 1.0F, 3.0F, 0.0F, new CubeDeformation(0.0F))
            .texOffs(0, 3).addBox(-1.0F, 3.0F, -3.0F, 3.0F, 0.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(1.0F, -3.0F, 0.0F));

        PartDefinition wingR = body.addOrReplaceChild("wingR", CubeListBuilder.create().texOffs(0, 15).addBox(-1.0F, 0.0F, -2.0F, 1.0F, 5.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offset(-3.0F, -9.0F, -2.0F));

        PartDefinition neck = body.addOrReplaceChild("neck", CubeListBuilder.create().texOffs(0, 27).addBox(-1.0F, -5.0F, -2.0F, 3.0F, 6.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.5F, -7.0F, -4.0F, 0.0873F, 0.0F, 0.0F));

        PartDefinition head = neck.addOrReplaceChild("head", CubeListBuilder.create().texOffs(21, 0).addBox(-1.5F, -4.0F, -2.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.0F))
            .texOffs(25, 15).addBox(-1.0F, -2.0F, -5.0F, 3.0F, 2.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -4.75F, -1.0F, -0.0873F, 0.0F, 0.0F));

        PartDefinition wingL = body.addOrReplaceChild("wingL", CubeListBuilder.create().texOffs(16, 15).addBox(0.0F, 0.0F, -2.0F, 1.0F, 5.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offset(3.0F, -9.0F, -2.0F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    private final ModelPart body;
    private final ModelPart legR;
    private final ModelPart legL;
    private final ModelPart head;
    private final ModelPart neck;
    private final ModelPart wingR;
    private final ModelPart wingL;

    public DuckModel(ModelPart root)
    {
        super(false, 0F, 0F, 1.8F, 1.8F, 18F);
        body = root.getChild("body");
        neck = body.getChild("neck");
        head = neck.getChild("head");
        legL = body.getChild("legL");
        legR = body.getChild("legR");
        wingL = body.getChild("wingL");
        wingR = body.getChild("wingR");
    }

    @Override
    public void setupAnim(OviparousAnimal duck, float limbSwing, float limbSwingAmount, float ageInTicks, float headYaw, float headPitch)
    {
        head.xRot = headPitch * ((float) Math.PI / 240F);
        neck.xRot = headPitch * ((float) Math.PI / 720F);
        head.yRot = headYaw * ((float) Math.PI / 360F);
        neck.yRot = headYaw * ((float) Math.PI / 360F);
        legR.xRot = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
        legL.xRot = Mth.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.4F * limbSwingAmount;

        wingR.zRot = 0F;
        wingL.zRot = 0F;

        //Body Sway
        if (!duck.isInWater())
        {
            body.zRot = Mth.cos(limbSwing * 0.6662F + ((float) Math.PI / 2F)) * 0.3F * limbSwingAmount;
            neck.zRot = Mth.cos(limbSwing * 0.6662F + ((float) Math.PI / 2F)) * -0.25F * limbSwingAmount;
            //Flapping in air
            if (!duck.isOnGround())
            {
                wingR.zRot = ageInTicks;
                wingL.zRot = -ageInTicks;
            }
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