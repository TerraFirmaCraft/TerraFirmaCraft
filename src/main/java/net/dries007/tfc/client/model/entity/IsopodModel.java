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

import net.dries007.tfc.common.entities.aquatic.AquaticCritter;

public class IsopodModel extends EntityModel<AquaticCritter>
{
    public static LayerDefinition createBodyLayer()
    {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(12, 7).addBox(-1.0F, -1.5F, -2.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
            .texOffs(0, 7).addBox(-1.5F, -2.0F, 0.0F, 3.0F, 1.0F, 6.0F, new CubeDeformation(0.0F))
            .texOffs(0, 7).addBox(-1.0F, -1.0F, 6.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(0, 0).addBox(-2.0F, -1.0F, 0.0F, 4.0F, 1.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 23.0F, -2.0F));

        PartDefinition rightantenna = body.addOrReplaceChild("rightantenna", CubeListBuilder.create().texOffs(0, 3).addBox(5.0F, 0.5F, -8.0F, 1.0F, 0.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 6.0F, 0.0F, 0.7854F, 0.0F));

        PartDefinition leftantenna = body.addOrReplaceChild("leftantenna", CubeListBuilder.create().texOffs(0, 0).addBox(-5.2929F, 0.5F, -9.2929F, 1.0F, 0.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, 0.0F, 6.0F, 0.0F, -0.7854F, 0.0F));

        PartDefinition leftfeet = partdefinition.addOrReplaceChild("leftfeet", CubeListBuilder.create().texOffs(8, 14).addBox(0.5F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(4, 14).addBox(0.5F, -1.0F, 2.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(0, 14).addBox(0.5F, -1.0F, 4.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(-3.0F, 24.0F, -2.0F));

        PartDefinition leftfeet2 = partdefinition.addOrReplaceChild("leftfeet2", CubeListBuilder.create().texOffs(12, 11).addBox(0.5F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(0, 11).addBox(0.5F, -1.0F, 2.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(0, 9).addBox(0.5F, -1.0F, 4.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(1.0F, 24.0F, -2.0F));

        return LayerDefinition.create(meshdefinition, 32, 32);
    }

    private final ModelPart body;
    private final ModelPart leftfeet;
    private final ModelPart leftfeet2;

    public IsopodModel(ModelPart root)
    {
        this.body = root.getChild("body");
        this.leftfeet = root.getChild("leftfeet");
        this.leftfeet2 = root.getChild("leftfeet2");
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, int color)
    {
        body.render(poseStack, buffer, packedLight, packedOverlay);
        leftfeet.render(poseStack, buffer, packedLight, packedOverlay);
        leftfeet2.render(poseStack, buffer, packedLight, packedOverlay);
    }

    @Override
    public void setupAnim(AquaticCritter entity, float limbSwing, float limbSwingAmount, float age, float headYaw, float headPitch)
    {

    }
}
