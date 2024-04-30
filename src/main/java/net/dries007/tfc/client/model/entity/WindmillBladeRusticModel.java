/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.model.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.dries007.tfc.common.blockentities.rotation.WindmillBlockEntity;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;

public class WindmillBladeRusticModel extends WindmillBladeModel
{

    private final ModelPart main;
    private final ModelPart blade;
    private final ModelPart extras;

    public WindmillBladeRusticModel(ModelPart root)
    {
        super(root);
        this.main = root.getChild("main");
        this.blade = root.getChild("blade");
        this.extras = root.getChild("extras");
    }


    public static LayerDefinition createBodyLayer()
    {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition main = partdefinition.addOrReplaceChild("main", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition cube_r1 = main.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(-2, -2).addBox(-0.5F, -1.0F, -97.0F, 1.0F, 2.0F, 98.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.3142F, 0.0F, 0.0F));

        PartDefinition cube_r2 = main.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(-2, -2).addBox(-0.5F, -1.0F, -97.0F, 1.0F, 2.0F, 98.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.3142F, 0.0F, 0.0F));

        PartDefinition blade = partdefinition.addOrReplaceChild("blade", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition blade1 = blade.addOrReplaceChild("blade1", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 0.0F, 1.0F, -0.3142F, 0.0F, 0.0F));

        PartDefinition cube_r3 = blade1.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(0, 170).addBox(-1.0F, -9.0F, -97.0F, 1.0F, 15.0F, 71.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.096F, 0.0F, 0.3491F));

        PartDefinition cube_r4 = blade1.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(0, 155).addBox(-1.0F, -5.0F, -97.0F, 1.0F, 15.0F, 71.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.096F, 0.0F, 0.3491F));

        PartDefinition blade2 = blade.addOrReplaceChild("blade2", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.3142F, 0.0F, 0.0F));

        PartDefinition cube_r5 = blade2.addOrReplaceChild("cube_r5", CubeListBuilder.create().texOffs(0, 170).addBox(-1.0F, -9.0F, -97.0F, 1.0F, 15.0F, 71.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 1.0F, 0.096F, 0.0F, 0.3491F));

        PartDefinition cube_r6 = blade2.addOrReplaceChild("cube_r6", CubeListBuilder.create().texOffs(0, 155).addBox(-1.0F, -5.0F, -97.0F, 1.0F, 15.0F, 71.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 1.0F, -0.096F, 0.0F, 0.3491F));

        PartDefinition extras = partdefinition.addOrReplaceChild("extras", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition extras1 = extras.addOrReplaceChild("extras1", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.3142F, 0.0F, 0.0F));

        PartDefinition cube_r7 = extras1.addOrReplaceChild("cube_r7", CubeListBuilder.create().texOffs(245, 134).addBox(-0.468F, -33.0F, -14.5F, 1.0F, 15.0F, 2.0F, new CubeDeformation(0.0F))
            .texOffs(245, 134).addBox(-0.5F, -49.0F, -64.0F, 1.0F, 47.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.5F, 21.5F, -17.0F, -0.3142F, 0.0F, 0.0F));

        PartDefinition extras2 = extras.addOrReplaceChild("extras2", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.3142F, 0.0F, 0.0F));

        PartDefinition cube_r8 = extras2.addOrReplaceChild("cube_r8", CubeListBuilder.create().texOffs(245, 134).addBox(-0.468F, -33.0F, -14.5F, 1.0F, 15.0F, 2.0F, new CubeDeformation(0.0F))
            .texOffs(245, 134).addBox(-0.5F, -49.0F, -64.0F, 1.0F, 47.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.5F, 21.5F, -17.0F, -0.3142F, 0.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 256, 256);
    }

    public void setupAnim(WindmillBlockEntity windmill, float partialTick, float offsetAngle)
    {
        main.xRot = -(windmill.getRotationAngle(partialTick) + offsetAngle);
        blade.xRot = -(windmill.getRotationAngle(partialTick) + offsetAngle);
        extras.xRot = -(windmill.getRotationAngle(partialTick) + offsetAngle);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha)
    {
        main.render(poseStack, vertexConsumer, packedLight, packedOverlay, 1f, 1f, 1f, alpha);
        blade.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }

    @Override
    public void renderWindmillExtras(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha){
        extras.render(poseStack, vertexConsumer, packedLight, packedOverlay, 1f, 1f, 1f, alpha);
    }
}
