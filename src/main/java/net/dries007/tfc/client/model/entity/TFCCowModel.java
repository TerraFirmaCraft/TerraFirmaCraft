/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.model.entity;

import net.minecraft.client.model.CowModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

import net.dries007.tfc.common.entities.land.DairyAnimal;

public class TFCCowModel extends CowModel<DairyAnimal>
{
    public static LayerDefinition createBodyLayer()
    {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition head = root.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -4.0F, -6.0F, 8.0F, 8.0F, 6.0F).texOffs(22, 0), PartPose.offset(0.0F, 4.0F, -8.0F));
        head.addOrReplaceChild("left_horn", CubeListBuilder.create().texOffs(22, 0).addBox(4.0F, -5.0F, -4.0F, 1.0F, 3.0F, 1.0F), PartPose.ZERO);
        head.addOrReplaceChild("right_horn", CubeListBuilder.create().texOffs(22, 0).addBox(-5.0F, -5.0F, -4.0F, 1.0F, 3.0F, 1.0F).texOffs(22, 0), PartPose.ZERO);

        root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(18, 4).addBox(-6.0F, -10.0F, -7.0F, 12.0F, 18.0F, 10.0F).texOffs(52, 0).addBox(-2.0F, 2.0F, -8.0F, 4.0F, 6.0F, 1.0F), PartPose.offsetAndRotation(0.0F, 5.0F, 2.0F, ((float)Math.PI / 2F), 0.0F, 0.0F));
        CubeListBuilder builder = CubeListBuilder.create().texOffs(0, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F);
        root.addOrReplaceChild("right_hind_leg", builder, PartPose.offset(-4.0F, 12.0F, 7.0F));
        root.addOrReplaceChild("left_hind_leg", builder, PartPose.offset(4.0F, 12.0F, 7.0F));
        root.addOrReplaceChild("right_front_leg", builder, PartPose.offset(-4.0F, 12.0F, -6.0F));
        root.addOrReplaceChild("left_front_leg", builder, PartPose.offset(4.0F, 12.0F, -6.0F));
        return LayerDefinition.create(mesh, 64, 32);
    }

    private final ModelPart leftHorn;
    private final ModelPart rightHorn;

    public TFCCowModel(ModelPart part)
    {
        super(part);
        this.leftHorn = part.getChild("head").getChild("left_horn");
        this.rightHorn = part.getChild("head").getChild("right_horn");
    }

    @Override
    public void setupAnim(DairyAnimal entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch)
    {
        super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        leftHorn.visible = rightHorn.visible = entity.displayMaleCharacteristics();
    }

}
