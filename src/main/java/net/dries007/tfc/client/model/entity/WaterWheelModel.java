/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.model.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.RenderType;

import net.dries007.tfc.common.blockentities.rotation.WaterWheelBlockEntity;

public class WaterWheelModel extends Model
{
    public static LayerDefinition createBodyLayer()
    {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition main = partdefinition.addOrReplaceChild("main", CubeListBuilder.create().texOffs(0, 24).addBox(-6.0F, -2.0F, -2.0F, 12.0F, 4.0F, 4.0F, new CubeDeformation(0.0F))
            .texOffs(48, 19).addBox(-11.0F, -4.0F, -7.0F, 5.0F, 8.0F, 14.0F, new CubeDeformation(0.0F))
            .texOffs(92, 76).addBox(-11.0F, -7.0F, -4.0F, 5.0F, 3.0F, 8.0F, new CubeDeformation(0.0F))
            .texOffs(0, 70).addBox(6.0F, 4.0F, -4.0F, 5.0F, 3.0F, 8.0F, new CubeDeformation(0.0F))
            .texOffs(72, 19).addBox(-11.0F, 4.0F, -4.0F, 5.0F, 3.0F, 8.0F, new CubeDeformation(0.0F))
            .texOffs(0, 48).addBox(6.0F, -4.0F, -7.0F, 5.0F, 8.0F, 14.0F, new CubeDeformation(0.0F))
            .texOffs(90, 22).addBox(6.0F, -7.0F, -4.0F, 5.0F, 3.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 16.0F, 0.0F));

        PartDefinition armbone1 = main.addOrReplaceChild("armbone1", CubeListBuilder.create(), PartPose.offset(-8.0F, 0.0F, 0.0F));

        PartDefinition divider_r1 = armbone1.addOrReplaceChild("divider_r1", CubeListBuilder.create().texOffs(48, 0).addBox(1.0F, -1.0F, -43.0F, 14.0F, 2.0F, 17.0F, new CubeDeformation(0.0F))
            .texOffs(0, 96).addBox(17.0F, -8.0F, -38.0F, 2.0F, 16.0F, 6.0F, new CubeDeformation(0.0F))
            .texOffs(16, 96).addBox(-3.0F, -8.0F, -38.0F, 2.0F, 16.0F, 6.0F, new CubeDeformation(0.0F))
            .texOffs(0, 0).addBox(15.0F, -3.0F, -44.0F, 3.0F, 6.0F, 42.0F, new CubeDeformation(0.0F))
            .texOffs(0, 48).addBox(-2.0F, -3.0F, -44.0F, 3.0F, 6.0F, 42.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.7854F, 0.0F, 0.0F));

        PartDefinition edge_r1 = armbone1.addOrReplaceChild("edge_r1", CubeListBuilder.create().texOffs(0, 0).addBox(-9.0F, -11.0F, -25.0F, 16.0F, 22.0F, 2.0F, new CubeDeformation(0.0F))
            .texOffs(88, 40).addBox(-10.0F, -14.0F, -41.0F, 1.0F, 28.0F, 8.0F, new CubeDeformation(0.0F))
            .texOffs(66, 48).addBox(7.0F, -11.0F, -33.0F, 1.0F, 22.0F, 10.0F, new CubeDeformation(0.0F))
            .texOffs(48, 48).addBox(7.0F, -14.0F, -41.0F, 1.0F, 28.0F, 8.0F, new CubeDeformation(0.0F))
            .texOffs(80, 86).addBox(-10.0F, -11.0F, -33.0F, 1.0F, 22.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(9.0F, 0.0F, 0.0F, -0.3927F, 0.0F, 0.0F));

        PartDefinition armbone2 = main.addOrReplaceChild("armbone2", CubeListBuilder.create(), PartPose.offsetAndRotation(-8.0F, 0.0F, 0.0F, 0.7854F, 0.0F, 0.0F));

        PartDefinition divider_r2 = armbone2.addOrReplaceChild("divider_r2", CubeListBuilder.create().texOffs(48, 0).addBox(1.0F, -1.0F, -43.0F, 14.0F, 2.0F, 17.0F, new CubeDeformation(0.0F))
            .texOffs(0, 96).addBox(17.0F, -8.0F, -38.0F, 2.0F, 16.0F, 6.0F, new CubeDeformation(0.0F))
            .texOffs(16, 96).addBox(-3.0F, -8.0F, -38.0F, 2.0F, 16.0F, 6.0F, new CubeDeformation(0.0F))
            .texOffs(0, 0).addBox(15.0F, -3.0F, -44.0F, 3.0F, 6.0F, 42.0F, new CubeDeformation(0.0F))
            .texOffs(0, 48).addBox(-2.0F, -3.0F, -44.0F, 3.0F, 6.0F, 42.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.7854F, 0.0F, 0.0F));

        PartDefinition edge_r2 = armbone2.addOrReplaceChild("edge_r2", CubeListBuilder.create().texOffs(0, 0).addBox(-9.0F, -11.0F, -25.0F, 16.0F, 22.0F, 2.0F, new CubeDeformation(0.0F))
            .texOffs(88, 40).addBox(-10.0F, -14.0F, -41.0F, 1.0F, 28.0F, 8.0F, new CubeDeformation(0.0F))
            .texOffs(66, 48).addBox(7.0F, -11.0F, -33.0F, 1.0F, 22.0F, 10.0F, new CubeDeformation(0.0F))
            .texOffs(48, 48).addBox(7.0F, -14.0F, -41.0F, 1.0F, 28.0F, 8.0F, new CubeDeformation(0.0F))
            .texOffs(80, 86).addBox(-10.0F, -11.0F, -33.0F, 1.0F, 22.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(9.0F, 0.0F, 0.0F, -0.3927F, 0.0F, 0.0F));

        PartDefinition armbone3 = main.addOrReplaceChild("armbone3", CubeListBuilder.create(), PartPose.offsetAndRotation(-8.0F, 0.0F, 0.0F, 1.5708F, 0.0F, 0.0F));

        PartDefinition divider_r3 = armbone3.addOrReplaceChild("divider_r3", CubeListBuilder.create().texOffs(48, 0).addBox(1.0F, -1.0F, -43.0F, 14.0F, 2.0F, 17.0F, new CubeDeformation(0.0F))
            .texOffs(0, 96).addBox(17.0F, -8.0F, -38.0F, 2.0F, 16.0F, 6.0F, new CubeDeformation(0.0F))
            .texOffs(16, 96).addBox(-3.0F, -8.0F, -38.0F, 2.0F, 16.0F, 6.0F, new CubeDeformation(0.0F))
            .texOffs(0, 0).addBox(15.0F, -3.0F, -44.0F, 3.0F, 6.0F, 42.0F, new CubeDeformation(0.0F))
            .texOffs(0, 48).addBox(-2.0F, -3.0F, -44.0F, 3.0F, 6.0F, 42.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.7854F, 0.0F, 0.0F));

        PartDefinition edge_r3 = armbone3.addOrReplaceChild("edge_r3", CubeListBuilder.create().texOffs(0, 0).addBox(-9.0F, -11.0F, -25.0F, 16.0F, 22.0F, 2.0F, new CubeDeformation(0.0F))
            .texOffs(88, 40).addBox(-10.0F, -14.0F, -41.0F, 1.0F, 28.0F, 8.0F, new CubeDeformation(0.0F))
            .texOffs(66, 48).addBox(7.0F, -11.0F, -33.0F, 1.0F, 22.0F, 10.0F, new CubeDeformation(0.0F))
            .texOffs(48, 48).addBox(7.0F, -14.0F, -41.0F, 1.0F, 28.0F, 8.0F, new CubeDeformation(0.0F))
            .texOffs(80, 86).addBox(-10.0F, -11.0F, -33.0F, 1.0F, 22.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(9.0F, 0.0F, 0.0F, -0.3927F, 0.0F, 0.0F));

        PartDefinition armbone4 = main.addOrReplaceChild("armbone4", CubeListBuilder.create(), PartPose.offsetAndRotation(-8.0F, 0.0F, 0.0F, 2.3562F, 0.0F, 0.0F));

        PartDefinition divider_r4 = armbone4.addOrReplaceChild("divider_r4", CubeListBuilder.create().texOffs(48, 0).addBox(1.0F, -1.0F, -43.0F, 14.0F, 2.0F, 17.0F, new CubeDeformation(0.0F))
            .texOffs(0, 96).addBox(17.0F, -8.0F, -38.0F, 2.0F, 16.0F, 6.0F, new CubeDeformation(0.0F))
            .texOffs(16, 96).addBox(-3.0F, -8.0F, -38.0F, 2.0F, 16.0F, 6.0F, new CubeDeformation(0.0F))
            .texOffs(0, 0).addBox(15.0F, -3.0F, -44.0F, 3.0F, 6.0F, 42.0F, new CubeDeformation(0.0F))
            .texOffs(0, 48).addBox(-2.0F, -3.0F, -44.0F, 3.0F, 6.0F, 42.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.7854F, 0.0F, 0.0F));

        PartDefinition edge_r4 = armbone4.addOrReplaceChild("edge_r4", CubeListBuilder.create().texOffs(0, 0).addBox(-9.0F, -11.0F, -25.0F, 16.0F, 22.0F, 2.0F, new CubeDeformation(0.0F))
            .texOffs(88, 40).addBox(-10.0F, -14.0F, -41.0F, 1.0F, 28.0F, 8.0F, new CubeDeformation(0.0F))
            .texOffs(66, 48).addBox(7.0F, -11.0F, -33.0F, 1.0F, 22.0F, 10.0F, new CubeDeformation(0.0F))
            .texOffs(48, 48).addBox(7.0F, -14.0F, -41.0F, 1.0F, 28.0F, 8.0F, new CubeDeformation(0.0F))
            .texOffs(80, 86).addBox(-10.0F, -11.0F, -33.0F, 1.0F, 22.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(9.0F, 0.0F, 0.0F, -0.3927F, 0.0F, 0.0F));

        PartDefinition armbone5 = main.addOrReplaceChild("armbone5", CubeListBuilder.create(), PartPose.offsetAndRotation(-8.0F, 0.0F, 0.0F, 3.1416F, 0.0F, 0.0F));

        PartDefinition divider_r5 = armbone5.addOrReplaceChild("divider_r5", CubeListBuilder.create().texOffs(48, 0).addBox(1.0F, -1.0F, -43.0F, 14.0F, 2.0F, 17.0F, new CubeDeformation(0.0F))
            .texOffs(0, 96).addBox(17.0F, -8.0F, -38.0F, 2.0F, 16.0F, 6.0F, new CubeDeformation(0.0F))
            .texOffs(16, 96).addBox(-3.0F, -8.0F, -38.0F, 2.0F, 16.0F, 6.0F, new CubeDeformation(0.0F))
            .texOffs(0, 0).addBox(15.0F, -3.0F, -44.0F, 3.0F, 6.0F, 42.0F, new CubeDeformation(0.0F))
            .texOffs(0, 48).addBox(-2.0F, -3.0F, -44.0F, 3.0F, 6.0F, 42.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.7854F, 0.0F, 0.0F));

        PartDefinition edge_r5 = armbone5.addOrReplaceChild("edge_r5", CubeListBuilder.create().texOffs(0, 0).addBox(-9.0F, -11.0F, -25.0F, 16.0F, 22.0F, 2.0F, new CubeDeformation(0.0F))
            .texOffs(88, 40).addBox(-10.0F, -14.0F, -41.0F, 1.0F, 28.0F, 8.0F, new CubeDeformation(0.0F))
            .texOffs(66, 48).addBox(7.0F, -11.0F, -33.0F, 1.0F, 22.0F, 10.0F, new CubeDeformation(0.0F))
            .texOffs(48, 48).addBox(7.0F, -14.0F, -41.0F, 1.0F, 28.0F, 8.0F, new CubeDeformation(0.0F))
            .texOffs(80, 86).addBox(-10.0F, -11.0F, -33.0F, 1.0F, 22.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(9.0F, 0.0F, 0.0F, -0.3927F, 0.0F, 0.0F));

        PartDefinition armbone6 = main.addOrReplaceChild("armbone6", CubeListBuilder.create(), PartPose.offsetAndRotation(-8.0F, 0.0F, 0.0F, -2.3562F, 0.0F, 0.0F));

        PartDefinition divider_r6 = armbone6.addOrReplaceChild("divider_r6", CubeListBuilder.create().texOffs(48, 0).addBox(1.0F, -1.0F, -43.0F, 14.0F, 2.0F, 17.0F, new CubeDeformation(0.0F))
            .texOffs(0, 96).addBox(17.0F, -8.0F, -38.0F, 2.0F, 16.0F, 6.0F, new CubeDeformation(0.0F))
            .texOffs(16, 96).addBox(-3.0F, -8.0F, -38.0F, 2.0F, 16.0F, 6.0F, new CubeDeformation(0.0F))
            .texOffs(0, 0).addBox(15.0F, -3.0F, -44.0F, 3.0F, 6.0F, 42.0F, new CubeDeformation(0.0F))
            .texOffs(0, 48).addBox(-2.0F, -3.0F, -44.0F, 3.0F, 6.0F, 42.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.7854F, 0.0F, 0.0F));

        PartDefinition edge_r6 = armbone6.addOrReplaceChild("edge_r6", CubeListBuilder.create().texOffs(0, 0).addBox(-9.0F, -11.0F, -25.0F, 16.0F, 22.0F, 2.0F, new CubeDeformation(0.0F))
            .texOffs(88, 40).addBox(-10.0F, -14.0F, -41.0F, 1.0F, 28.0F, 8.0F, new CubeDeformation(0.0F))
            .texOffs(66, 48).addBox(7.0F, -11.0F, -33.0F, 1.0F, 22.0F, 10.0F, new CubeDeformation(0.0F))
            .texOffs(48, 48).addBox(7.0F, -14.0F, -41.0F, 1.0F, 28.0F, 8.0F, new CubeDeformation(0.0F))
            .texOffs(80, 86).addBox(-10.0F, -11.0F, -33.0F, 1.0F, 22.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(9.0F, 0.0F, 0.0F, -0.3927F, 0.0F, 0.0F));

        PartDefinition armbone7 = main.addOrReplaceChild("armbone7", CubeListBuilder.create(), PartPose.offsetAndRotation(-8.0F, 0.0F, 0.0F, -1.5708F, 0.0F, 0.0F));

        PartDefinition divider_r7 = armbone7.addOrReplaceChild("divider_r7", CubeListBuilder.create().texOffs(48, 0).addBox(1.0F, -1.0F, -43.0F, 14.0F, 2.0F, 17.0F, new CubeDeformation(0.0F))
            .texOffs(0, 96).addBox(17.0F, -8.0F, -38.0F, 2.0F, 16.0F, 6.0F, new CubeDeformation(0.0F))
            .texOffs(16, 96).addBox(-3.0F, -8.0F, -38.0F, 2.0F, 16.0F, 6.0F, new CubeDeformation(0.0F))
            .texOffs(0, 0).addBox(15.0F, -3.0F, -44.0F, 3.0F, 6.0F, 42.0F, new CubeDeformation(0.0F))
            .texOffs(0, 48).addBox(-2.0F, -3.0F, -44.0F, 3.0F, 6.0F, 42.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.7854F, 0.0F, 0.0F));

        PartDefinition edge_r7 = armbone7.addOrReplaceChild("edge_r7", CubeListBuilder.create().texOffs(0, 0).addBox(-9.0F, -11.0F, -25.0F, 16.0F, 22.0F, 2.0F, new CubeDeformation(0.0F))
            .texOffs(88, 40).addBox(-10.0F, -14.0F, -41.0F, 1.0F, 28.0F, 8.0F, new CubeDeformation(0.0F))
            .texOffs(66, 48).addBox(7.0F, -11.0F, -33.0F, 1.0F, 22.0F, 10.0F, new CubeDeformation(0.0F))
            .texOffs(48, 48).addBox(7.0F, -14.0F, -41.0F, 1.0F, 28.0F, 8.0F, new CubeDeformation(0.0F))
            .texOffs(80, 86).addBox(-10.0F, -11.0F, -33.0F, 1.0F, 22.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(9.0F, 0.0F, 0.0F, -0.3927F, 0.0F, 0.0F));

        PartDefinition armbone8 = main.addOrReplaceChild("armbone8", CubeListBuilder.create(), PartPose.offsetAndRotation(-8.0F, 0.0F, 0.0F, -0.7854F, 0.0F, 0.0F));

        PartDefinition divider_r8 = armbone8.addOrReplaceChild("divider_r8", CubeListBuilder.create().texOffs(48, 0).addBox(1.0F, -1.0F, -43.0F, 14.0F, 2.0F, 17.0F, new CubeDeformation(0.0F))
            .texOffs(0, 96).addBox(17.0F, -8.0F, -38.0F, 2.0F, 16.0F, 6.0F, new CubeDeformation(0.0F))
            .texOffs(16, 96).addBox(-3.0F, -8.0F, -38.0F, 2.0F, 16.0F, 6.0F, new CubeDeformation(0.0F))
            .texOffs(0, 0).addBox(15.0F, -3.0F, -44.0F, 3.0F, 6.0F, 42.0F, new CubeDeformation(0.0F))
            .texOffs(0, 48).addBox(-2.0F, -3.0F, -44.0F, 3.0F, 6.0F, 42.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.7854F, 0.0F, 0.0F));

        PartDefinition edge_r8 = armbone8.addOrReplaceChild("edge_r8", CubeListBuilder.create().texOffs(0, 0).addBox(-9.0F, -11.0F, -25.0F, 16.0F, 22.0F, 2.0F, new CubeDeformation(0.0F))
            .texOffs(88, 40).addBox(-10.0F, -14.0F, -41.0F, 1.0F, 28.0F, 8.0F, new CubeDeformation(0.0F))
            .texOffs(66, 48).addBox(7.0F, -11.0F, -33.0F, 1.0F, 22.0F, 10.0F, new CubeDeformation(0.0F))
            .texOffs(48, 48).addBox(7.0F, -14.0F, -41.0F, 1.0F, 28.0F, 8.0F, new CubeDeformation(0.0F))
            .texOffs(80, 86).addBox(-10.0F, -11.0F, -33.0F, 1.0F, 22.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(9.0F, 0.0F, 0.0F, -0.3927F, 0.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }

    private final ModelPart main;

    public WaterWheelModel(ModelPart root)
    {
        super(RenderType::entityCutout);
        this.main = root.getChild("main");
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, int color)
    {
        main.render(poseStack, buffer, packedLight, packedOverlay, color);
    }

    public void setupAnim(WaterWheelBlockEntity wheel, float partialTick)
    {
        main.xRot = -wheel.getRotationAngle(partialTick);
    }
}
