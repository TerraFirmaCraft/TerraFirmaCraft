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

import net.dries007.tfc.common.blockentities.VaneBlockEntity;
import net.dries007.tfc.common.blockentities.rotation.WaterWheelBlockEntity;

public class VaneModel extends Model
{
    public VaneModel(ModelPart root)
    {
        super(RenderType::entityCutoutNoCull);
        this.base = root.getChild("base");
        this.static_parts = root.getChild("static");
        this.spinny = root.getChild("spinny");
    }

    public static LayerDefinition createBodyLayer()
    {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition base = partdefinition.addOrReplaceChild("base", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition cube_r1 = base.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(48, 58).addBox(-2.0F, -2.0F, -2.0F, 4.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 1.5708F, 0.0F));

        PartDefinition static_parts = partdefinition.addOrReplaceChild("static", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition cube_r2 = static_parts.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(0, 1).addBox(0.0F, -7.0F, -8.0F, 1.0F, 7.0F, 16.0F, new CubeDeformation(0.0F))
            .texOffs(0, 33).addBox(-8.0F, -7.0F, 0.0F, 16.0F, 7.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 1.5708F, 0.0F));

        PartDefinition spinny = partdefinition.addOrReplaceChild("spinny", CubeListBuilder.create(), PartPose.offset(0.0F, 21.0F, 0.0F));

        PartDefinition cube_r3 = spinny.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(0, 48).addBox(-8.0F, -18.0F, 0.0F, 16.0F, 15.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(1, 0).addBox(0.0F, -18.0F, -1.0F, 1.0F, 15.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 1.5708F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    private final ModelPart base;
    private final ModelPart static_parts;
    private final ModelPart spinny;

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, int color)
    {
        static_parts.render(poseStack, buffer, packedLight, packedOverlay, color);
        spinny.render(poseStack, buffer, packedLight, packedOverlay, color);
    }

    public void renderBase(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, int color)
    {
        base.render(poseStack, buffer, packedLight, packedOverlay, color);
    }

    public void setupAnim(VaneBlockEntity vane, float partialTick)
    {
        spinny.yRot = (float) (vane.getAngle(partialTick) + Math.PI / 2);
    }
}
