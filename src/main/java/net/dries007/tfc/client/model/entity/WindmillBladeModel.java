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
    private final ModelPart main;

    public WindmillBladeModel(ModelPart root)
    {
        super(RenderType::entityCutout);
        this.main = root.getChild("bb_main");
    }

    @SuppressWarnings("unused")
    public static LayerDefinition createBodyLayer()
    {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition bb_main = partdefinition.addOrReplaceChild("bb_main", CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, -3.0F, -96.0F, 4.0F, 3.0F, 96.0F, new CubeDeformation(0.0F))
            .texOffs(0, 99).addBox(-1.0F, -16.0F, -96.0F, 2.0F, 13.0F, 80.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 256, 256);
    }

    public void setupAnim(WindmillBlockEntity windmill, float partialTick, float offsetAngle)
    {
        main.xRot = -(windmill.getRotationAngle(partialTick) + offsetAngle);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha)
    {
        main.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
