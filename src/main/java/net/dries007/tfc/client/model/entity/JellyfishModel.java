/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.model.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;

import net.dries007.tfc.common.entities.aquatic.Jellyfish;

public class JellyfishModel extends EntityModel<Jellyfish>
{
    public static LayerDefinition createBodyLayer()
    {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition head = partdefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(12, 7).addBox(-1.0F, 3.0F, -1.0F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
            .texOffs(0, 0).addBox(-3.0F, 2.0F, -3.0F, 6.0F, 1.0F, 6.0F, new CubeDeformation(0.0F))
            .texOffs(0, 7).addBox(-2.0F, 1.0F, -2.0F, 4.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 15.0F, 0.0F));

        PartDefinition tail1 = partdefinition.addOrReplaceChild("tail1", CubeListBuilder.create().texOffs(0, 12).addBox(-0.6476F, 2.1436F, -0.6413F, 1.0F, 7.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, 15.0F, -1.0F, -0.0873F, 0.0F, 0.0873F));

        PartDefinition tail2 = partdefinition.addOrReplaceChild("tail2", CubeListBuilder.create().texOffs(4, 12).addBox(-0.2615F, 0.9772F, 0.2605F, 1.0F, 7.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, 16.0F, -2.0F, -0.0873F, 0.0F, -0.0873F));

        PartDefinition tail3 = partdefinition.addOrReplaceChild("tail3", CubeListBuilder.create().texOffs(8, 12).addBox(-0.2615F, 0.9772F, -0.2605F, 1.0F, 7.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, 16.0F, 1.0F, 0.0873F, 0.0F, -0.0873F));

        PartDefinition tail4 = partdefinition.addOrReplaceChild("tail4", CubeListBuilder.create().texOffs(12, 12).addBox(0.2615F, 0.9772F, -0.2605F, 1.0F, 7.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.0F, 16.0F, 1.0F, 0.0873F, 0.0F, 0.0873F));

        return LayerDefinition.create(meshdefinition, 32, 32);
    }

    private final ModelPart head;
    private final ModelPart tail1;
    private final ModelPart tail2;
    private final ModelPart tail3;
    private final ModelPart tail4;

    public JellyfishModel(ModelPart root)
    {
        this.head = root.getChild("head");
        this.tail1 = root.getChild("tail1");
        this.tail2 = root.getChild("tail2");
        this.tail3 = root.getChild("tail3");
        this.tail4 = root.getChild("tail4");
    }

    @Override
    public void setupAnim(Jellyfish entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch)
    {
        float oscillation = -0.2F * Mth.sin(0.06F * ageInTicks);
        tail1.xRot = -1 * oscillation;
        tail1.zRot = oscillation;
        tail2.xRot = -1 * oscillation;
        tail2.zRot = -1 * oscillation;
        tail3.xRot = oscillation;
        tail3.zRot = -1 * oscillation;
        tail4.xRot = oscillation;
        tail4.zRot = oscillation;
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, int color)
    {
        head.render(poseStack, buffer, packedLight, packedOverlay);
        tail1.render(poseStack, buffer, packedLight, packedOverlay);
        tail2.render(poseStack, buffer, packedLight, packedOverlay);
        tail3.render(poseStack, buffer, packedLight, packedOverlay);
        tail4.render(poseStack, buffer, packedLight, packedOverlay);
    }
}
