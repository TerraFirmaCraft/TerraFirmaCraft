/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.model.entity;

import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;

import net.dries007.tfc.common.entities.aquatic.Bluegill;

public class BluegillModel extends HierarchicalModel<Bluegill>
{
    public static LayerDefinition createBodyLayer()
    {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition backFins = partdefinition.addOrReplaceChild("backFins", CubeListBuilder.create().texOffs(4, 7).addBox(-0.5F, -1.5F, 1.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 22.0F, 0.0F));

        PartDefinition backFin2 = backFins.addOrReplaceChild("backFin2", CubeListBuilder.create().texOffs(8, 0).addBox(0.0F, -8.658F, 3.0603F, 0.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 1.0F, -5.0F, -0.7854F, 0.0F, 0.0F));

        PartDefinition backFin1 = backFins.addOrReplaceChild("backFin1", CubeListBuilder.create().texOffs(0, 9).addBox(0.0F, 1.342F, 3.0603F, 0.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 1.0F, -2.0F, 0.7854F, 0.0F, 0.0F));

        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, -1.0F, -3.0F, 2.0F, 3.0F, 4.0F, new CubeDeformation(0.0F))
            .texOffs(0, 4).addBox(0.0F, -2.0F, -2.0F, 0.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
            .texOffs(0, 2).addBox(0.0F, 2.0F, 0.0F, 0.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(2, 2).addBox(0.0F, 2.0F, -2.0F, 0.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 21.0F, 0.0F));

        PartDefinition front = partdefinition.addOrReplaceChild("front", CubeListBuilder.create().texOffs(0, 0).addBox(-0.5F, -1.5F, -6.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(0, 8).addBox(-0.5F, -1.0F, -7.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 22.0F, 2.0F));

        return LayerDefinition.create(meshdefinition, 16, 16);
    }

    private final ModelPart root;
    private final ModelPart backFins;

    public BluegillModel(ModelPart root)
    {
        this.root = root;
        this.backFins = root.getChild("backFins");
    }

    @Override
    public void setupAnim(Bluegill entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch)
    {
        float speed = entity.isInWater() ? 1.0F : 1.5F;
        backFins.yRot = -speed * 0.45F * Mth.sin(0.6F * ageInTicks);
    }

    @Override
    public ModelPart root()
    {
        return root;
    }
}
