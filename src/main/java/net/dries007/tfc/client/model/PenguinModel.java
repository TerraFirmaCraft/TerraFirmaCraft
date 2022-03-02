/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.model;

import java.util.Map;

import com.google.common.collect.ImmutableMap;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.dries007.tfc.common.entities.aquatic.AmphibiousAnimal;

public class PenguinModel extends EntityModel<AmphibiousAnimal>
{
    public static LayerDefinition createBodyLayer()
    {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition core = partdefinition.addOrReplaceChild("core", CubeListBuilder.create(), PartPose.offset(0.0F, 23.0F, 0.0F));
        PartDefinition core_r1 = core.addOrReplaceChild("core_r1", CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, -9.0F, -2.0F, 4.0F, 8.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 1.0F, 1.0F, -3.1416F, 0.0F, 3.1416F));
        PartDefinition head = core.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.offset(0.0F, -8.0F, 0.0F));
        PartDefinition head_r1 = head.addOrReplaceChild("head_r1", CubeListBuilder.create().texOffs(16, 4).addBox(-1.0F, -10.0F, 1.5F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 9.0F, 1.0F, -3.1416F, 0.0F, 3.1416F));
        PartDefinition head_r2 = head.addOrReplaceChild("head_r2", CubeListBuilder.create().texOffs(13, 9).addBox(-1.0F, -2.0F, -1.0F, 2.0F, 2.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 1.0F, -3.1416F, 0.0F, 3.1416F));
        PartDefinition leftfoot = core.addOrReplaceChild("leftfoot", CubeListBuilder.create().texOffs(12, 0).addBox(1.3264F, 0.0F, -0.9848F, 2.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, 0.0F, -1.0F, 0.0F, -0.1745F, 0.0F));
        PartDefinition rightfoot = core.addOrReplaceChild("rightfoot", CubeListBuilder.create().texOffs(16, 16).addBox(-4.3264F, 0.0F, -0.9848F, 2.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.0F, 0.0F, -1.0F, 0.0F, 0.1745F, 0.0F));
        PartDefinition leftwing = core.addOrReplaceChild("leftwing", CubeListBuilder.create().texOffs(0, 12).mirror().addBox(0.0F, 0.0F, 0.0F, 1.0F, 5.0F, 3.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(2.0F, -7.0F, -1.0F, 0.0F, 0.0F, -0.1745F));
        PartDefinition rightwing = core.addOrReplaceChild("rightwing", CubeListBuilder.create().texOffs(8, 12).mirror().addBox(-1.0F, 0.0F, 0.0F, 1.0F, 5.0F, 3.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-2.0F, -7.0F, -1.0F, 0.0F, 0.0F, 0.1745F));

        return LayerDefinition.create(meshdefinition, 32, 32);
    }

    public static final Animation WALK = new Animation.Builder(1.0F)
        .bone("core", new Animation.Bone.Builder(Easing.EASE_IN_OUT_CUBIC).rotation(0F, 0F, 0F, 15F).rotation(0.5F, 0F, 0F, -15F).rotation(1.0F, 0F, 0F, 15F).build())
        .bone("head", new Animation.Bone.Builder(Easing.LINEAR).noRotation(0F).rotation(0.33F, 0F, 0F, -15F).rotation(0.66F, 0F, 0F, 15F).noRotation(1.0F).build())
        .bone("leftFoot", new Animation.Bone.Builder(Easing.LINEAR).noRotation(0F).rotation(0.5F, 45F, 0F, 0F).noRotation(1.0F).build())
        .bone("rightFoot", new Animation.Bone.Builder(Easing.LINEAR).noRotation(0F).rotation(0.5F, -45F, 0F, 0F).noRotation(1.0F).build())
        .bone("leftWing", new Animation.Bone.Builder(Easing.LINEAR).noRotation(0F).rotation(0.5F, 25F, 0F, 0F).noRotation(1.0F).build())
        .bone("rightWing", new Animation.Bone.Builder(Easing.LINEAR).noRotation(0F).rotation(0.5F, 25F, 0F, 0F).noRotation(1.0F).build())
        .build();

    public static final Animation SWIM = new Animation.Builder(0.5F)
        .bone("leftFoot", new Animation.Bone.Builder(Easing.LINEAR).noRotation(0F).rotation(0.5F, 45F, 0F, 0F).noRotation(1.0F).build())
        .bone("rightFoot", new Animation.Bone.Builder(Easing.LINEAR).noRotation(0F).rotation(0.5F, -45F, 0F, 0F).noRotation(1.0F).build())
        .bone("leftWing", new Animation.Bone.Builder(Easing.LINEAR).noRotation(0F).rotation(0.5F, 0F, 0F, -25F).noRotation(1.0F).build())
        .bone("rightWing", new Animation.Bone.Builder(Easing.LINEAR).noRotation(0F).rotation(0.5F, 0F, 0F, 25F).noRotation(1.0F).build())
        .build();

    public final Map<String, ModelPart> parts;
    public final Map<ModelPart, PartPose> defaults;

    private final ModelPart core;
    private final ModelPart head;
    private final ModelPart rightWing;
    private final ModelPart leftWing;
    private final ModelPart leftFoot;
    private final ModelPart rightFoot;

    public PenguinModel(ModelPart root)
    {
        core = root.getChild("core");
        head = core.getChild("head");
        leftFoot = core.getChild("leftfoot");
        rightFoot = core.getChild("rightfoot");
        leftWing = core.getChild("leftwing");
        rightWing = core.getChild("rightwing");

        parts = new ImmutableMap.Builder<String, ModelPart>().put("core", core).put("head", head).put("leftFoot", leftFoot).put("rightFoot", rightFoot).put("leftWing", leftWing).put("rightWing", rightWing).build();
        defaults = Animation.initDefaults(parts);
    }

    @Override
    public void setupAnim(AmphibiousAnimal entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch)
    {
        defaults.forEach(ModelPart::loadPose);
        if (entity.isPlayingDead())
        {
            core.xRot = -90F / 180F * Mth.PI;
        }
        else
        {
            if (entity.getDeltaMovement().lengthSqr() > 0f)
            {
                final float adjustedAgeInTicks = ageInTicks + limbSwingAmount * 2F;
                if (entity.isInWater())
                {
                    SWIM.tick(parts, adjustedAgeInTicks);
                }
                else
                {
                    WALK.tick(parts, adjustedAgeInTicks);
                }
            }

            head.xRot = entity.isInWater() ? -1 : headPitch * Mth.PI / 180F;
            head.yRot = netHeadYaw * Mth.PI / 180F;
        }

    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha)
    {
        core.render(poseStack, buffer, packedLight, packedOverlay);
    }
}
