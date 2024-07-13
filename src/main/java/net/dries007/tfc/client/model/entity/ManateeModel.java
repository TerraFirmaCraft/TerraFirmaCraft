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

import net.dries007.tfc.common.entities.aquatic.Manatee;

public class ManateeModel extends EntityModel<Manatee>
{
    public static LayerDefinition createBodyLayer()
    {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition head = partdefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 69).addBox(-7.0F, -8.2468F, -5.4432F, 14.0F, 14.0F, 15.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 11.0F, -18.0F, -0.0873F, 3.1416F, 0.0F));

        PartDefinition nosefront = head.addOrReplaceChild("nosefront", CubeListBuilder.create().texOffs(0, 98).addBox(-4.0F, -5.4475F, -2.3259F, 8.0F, 8.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.7532F, 18.5568F, 0.0873F, 0.0F, 0.0F));

        PartDefinition noseback = nosefront.addOrReplaceChild("noseback", CubeListBuilder.create().texOffs(64, 0).addBox(-5.0F, -13.0152F, -15.8264F, 10.0F, 10.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 8.0F, 5.0F, -0.1745F, 0.0F, 0.0F));

        PartDefinition finright = partdefinition.addOrReplaceChild("finright", CubeListBuilder.create().texOffs(0, 6).addBox(-1.1639F, 0.0844F, -2.0F, 5.0F, 2.0F, 4.0F, new CubeDeformation(0.0F))
            .texOffs(96, 13).addBox(3.8361F, 0.0844F, -3.0F, 8.0F, 2.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(9.0F, 15.0F, -1.0F, 0.0F, 0.0F, 0.3491F));

        PartDefinition finleft = partdefinition.addOrReplaceChild("finleft", CubeListBuilder.create().texOffs(93, 0).addBox(-11.1654F, -1.3769F, -3.0F, 8.0F, 2.0F, 6.0F, new CubeDeformation(0.0F))
            .texOffs(0, 0).addBox(-3.1654F, -1.3769F, -2.0F, 5.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-9.0F, 16.0F, -2.0F, 0.0F, 0.0F, -0.3491F));

        PartDefinition mainPart = partdefinition.addOrReplaceChild("mainPart", CubeListBuilder.create().texOffs(58, 71).addBox(-9.0F, -9.0F, -4.0F, 18.0F, 17.0F, 10.0F, new CubeDeformation(0.0F))
            .texOffs(0, 0).addBox(-11.0F, -12.0F, -24.0F, 22.0F, 21.0F, 20.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 10.0F, -12.0F, 0.0F, 3.1416F, 0.0F));

        PartDefinition back = partdefinition.addOrReplaceChild("back", CubeListBuilder.create().texOffs(0, 41).addBox(-9.0F, -5.993F, -9.7425F, 18.0F, 13.0F, 15.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 8.0F, 16.0F, 0.0F, 3.1416F, 0.0F));

        PartDefinition butt = back.addOrReplaceChild("butt", CubeListBuilder.create().texOffs(69, 26).addBox(-6.0F, -5.7778F, -9.0194F, 12.0F, 9.0F, 15.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 1.9734F, -9.6101F, 0.0873F, 0.0F, 0.0F));

        PartDefinition tail = butt.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(49, 52).addBox(-9.0F, 3.4086F, -15.1552F, 18.0F, 2.0F, 17.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -4.0F, -6.0F, 0.1745F, 0.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }

    private final ModelPart head;
    private final ModelPart finright;
    private final ModelPart finleft;
    private final ModelPart mainPart;
    private final ModelPart back;

    public ManateeModel(ModelPart root)
    {
        this.head = root.getChild("head");
        this.finright = root.getChild("finright");
        this.finleft = root.getChild("finleft");
        this.mainPart = root.getChild("mainPart");
        this.back = root.getChild("back");
    }

    @Override
    public void setupAnim(Manatee entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch)
    {
        float swingRate = entity.isInWater() ? 1.0F : 1.5F;
        float oscillation = -1 * swingRate * 0.2F * Mth.sin(0.06F * ageInTicks);
        finright.zRot = oscillation * -2F;
        finleft.zRot = oscillation * 2F;
        head.xRot = oscillation * 0.5F;
        back.xRot = oscillation * 0.5F;
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, int color)
    {
        head.render(poseStack, buffer, packedLight, packedOverlay);
        finright.render(poseStack, buffer, packedLight, packedOverlay);
        finleft.render(poseStack, buffer, packedLight, packedOverlay);
        mainPart.render(poseStack, buffer, packedLight, packedOverlay);
        back.render(poseStack, buffer, packedLight, packedOverlay);
    }
}