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
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.RenderType;

import net.dries007.tfc.common.blockentities.rotation.WindmillBlockEntity;

public class WindmillBladeModel extends Model
{
    private final ModelPart blade;
    private final ModelPart main;

    public WindmillBladeModel(ModelPart root)
    {
        super(RenderType::entityCutout);
        this.blade = root.getChild("blade");
        this.main = root.getChild("main");
    }


    public static LayerDefinition createBodyLayer()
    {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition blade = partdefinition.addOrReplaceChild("blade", CubeListBuilder.create().texOffs(0, 99).addBox(-1.0F, 1.5F, -94.5F, 2.0F, 13.0F, 80.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition main = partdefinition.addOrReplaceChild("main", CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, -1.5F, -94.5F, 4.0F, 3.0F, 96.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 256, 256);
    }

    public void setupAnim(WindmillBlockEntity windmill, float partialTick, float offsetAngle)
    {
        main.xRot = -(windmill.getRotationAngle(partialTick) + offsetAngle);
        blade.xRot = -(windmill.getRotationAngle(partialTick) + offsetAngle);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha)
    {
        main.render(poseStack, vertexConsumer, packedLight, packedOverlay, 1f, 1f, 1f, alpha);
        blade.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }

    public void renderWindmillExtras(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha){}
    
}
