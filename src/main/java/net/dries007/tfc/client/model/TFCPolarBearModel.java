package net.dries007.tfc.client.model;

import java.util.Map;

import com.google.common.collect.ImmutableMap;
import net.minecraft.client.model.QuadrupedModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;

import net.dries007.tfc.client.ClientHelpers;
import net.dries007.tfc.common.entities.predator.Predator;

public class TFCPolarBearModel extends QuadrupedModel<Predator>
{
    public static LayerDefinition createBodyLayer()
    {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        PartDefinition right_front_leg = partdefinition.addOrReplaceChild("right_front_leg", CubeListBuilder.create().texOffs(50, 54).addBox(-2.0F, -1.0F, -2.0F, 4.0F, 11.0F, 4.0F, new CubeDeformation(0.0F))
            .texOffs(64, 27).addBox(-2.5F, 8.0F, -4.0F, 5.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(-6.0F, 14.0F, -7.0F));
        PartDefinition left_front_leg = partdefinition.addOrReplaceChild("left_front_leg", CubeListBuilder.create().texOffs(34, 54).addBox(-2.0F, -1.0F, -2.0F, 4.0F, 11.0F, 4.0F, new CubeDeformation(0.0F))
            .texOffs(15, 64).addBox(-2.5F, 8.0F, -4.0F, 5.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(6.0F, 14.0F, -7.0F));
        PartDefinition left_hind_leg = partdefinition.addOrReplaceChild("left_hind_leg", CubeListBuilder.create().texOffs(66, 54).addBox(-2.0F, 1.0F, 0.0F, 4.0F, 7.0F, 4.0F, new CubeDeformation(0.0F))
            .texOffs(64, 13).addBox(-2.5F, 6.0F, -2.0F, 5.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(6.0F, 16.0F, 8.0F));
        PartDefinition left_thigh_r1 = left_hind_leg.addOrReplaceChild("left_thigh_r1", CubeListBuilder.create().texOffs(54, 0).addBox(-2.5F, -3.1716F, -1.0F, 5.0F, 8.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-12.0F, 0.0F, -1.0F, 0.7854F, 0.0F, 0.0F));
        PartDefinition right_hind_leg = partdefinition.addOrReplaceChild("right_hind_leg", CubeListBuilder.create().texOffs(62, 65).addBox(-2.0F, 1.0F, 0.0F, 4.0F, 7.0F, 4.0F, new CubeDeformation(0.0F))
            .texOffs(0, 62).addBox(-2.5F, 6.0F, -2.0F, 5.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(-6.0F, 16.0F, 8.0F));
        PartDefinition right_thigh_r1 = right_hind_leg.addOrReplaceChild("right_thigh_r1", CubeListBuilder.create().texOffs(49, 19).addBox(-2.5F, -2.4645F, -0.2929F, 5.0F, 8.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(12.0F, 0.0F, -2.0F, 0.7854F, 0.0F, 0.0F));
        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 24).addBox(-7.5F, -7.0F, -10.0F, 15.0F, 11.0F, 10.0F, new CubeDeformation(0.0F))
            .texOffs(0, 0).addBox(-7.5F, -8.0F, 0.0F, 15.0F, 12.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 13.0F, 0.0F));
        PartDefinition head = partdefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 45).addBox(-5.0F, -5.0F, -8.0F, 8.0F, 8.0F, 9.0F, new CubeDeformation(0.0F))
            .texOffs(25, 45).addBox(-3.0F, -1.0F, -11.0F, 4.0F, 4.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(1.0F, 8.0F, -11.0F));
        PartDefinition neck_r1 = head.addOrReplaceChild("neck_r1", CubeListBuilder.create().texOffs(41, 36).addBox(-6.0F, -6.0761F, -6.6173F, 12.0F, 9.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, 3.0F, 3.0F, -0.3927F, 0.0F, 0.0F));
        PartDefinition right_ear = head.addOrReplaceChild("right_ear", CubeListBuilder.create().texOffs(0, 3).addBox(-1.0F, -2.0F, 0.0F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(-3.0F, -5.0F, -7.0F));
        PartDefinition left_ear = head.addOrReplaceChild("left_ear", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, -2.0F, 0.0F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(1.0F, -5.0F, -7.0F));
        return LayerDefinition.create(meshdefinition, 128, 128);
    }

    public static final Animation SPRINT = new Animation.Builder(0.7f)
        .bone("right_front_leg", ClientHelpers.newBone().rotation(0F, 17.5F, 0F, 0F).rotation(0.30F, -20F, 0F, 0F).rotation(0.7F, 17.5F, 0F, 0F).build())
        .bone("left_front_leg", ClientHelpers.newBone().rotation(0F, 17.5F, 0F, 0F).rotation(0.30F, -20F, 0F, 0F).rotation(0.7F, 17.5F, 0F, 0F).build())
        .bone("right_hind_leg", ClientHelpers.newBone().rotation(0F, -15F, 0F, 0F).rotation(0.05F, -15F, 0F, 0F).rotation(0.35F, 20F, 0F, 0F).rotation(0.7F, -15F, 0F, 0F).build())
        .bone("left_hind_leg", ClientHelpers.newBone().rotation(0F, -15F, 0F, 0F).rotation(0.05F, -15F, 0F, 0F).rotation(0.35F, 20F, 0F, 0F).rotation(0.7F, -15F, 0F, 0F).build())
        .bone("body", new Animation.Bone.Builder(Easing.EASE_IN_OUT_CUBIC).rotation(0F, -5F, 0F, 0F).noRotation(0.30F).rotation(0.7F, -5F, 0F, 0F).build())
        .bone("head", new Animation.Bone.Builder(Easing.EASE_IN_OUT_CUBIC).rotation(0F, -10F, 0F, 0F).rotation(0.30F, 10F, 0F, 0F).rotation(0.7F, -10F, 0F, 0F).build())
        .build();

    public static final Animation ATTACK = new Animation.Builder(0.5F)
        .bone("head", ClientHelpers.newBone().noRotation(0f).rotation(0.15f, -20f, 0f, 0f).noRotation(0.3f).build())
        .build();

    public final Map<String, ModelPart> parts;
    public final Map<ModelPart, PartPose> defaults;

    public TFCPolarBearModel(ModelPart part)
    {
        super(part, true, 16.0F, 4.0F, 2.25F, 2.0F, 24);

        parts = new ImmutableMap.Builder<String, ModelPart>().put("right_front_leg", rightFrontLeg).put("left_front_leg", leftFrontLeg).put("right_hind_leg", rightHindLeg).put("left_hind_leg", leftHindLeg).put("head", head).put("body", body).build();
        defaults = Animation.initDefaults(parts);
    }

    @Override
    public void setupAnim(Predator predator, float limbSwing, float limbSwingAmount, float ageInTicks, float headYaw, float headPitch)
    {
        defaults.forEach(ModelPart::loadPose);
        if (predator.isSleeping())
        {
            setupSleeping();
        }
        else
        {
            if (predator.getAttackTicks() > 0)
            {
                ATTACK.tick(parts, ageInTicks);
            }
            if (predator.getDeltaMovement().lengthSqr() > 0)
            {
                SPRINT.tick(parts, ageInTicks);
            }
            else
            {
                super.setupAnim(predator, limbSwing, limbSwingAmount, ageInTicks, headYaw, headPitch);
            }
            head.xRot = headYaw * Mth.PI / 180F;
            head.yRot = headPitch * Mth.PI / 180F;
        }

    }

    private void setupSleeping()
    {
        rightFrontLeg.xRot = -82.5f * Mth.PI / 180F;
        rightFrontLeg.y = 20f;
        leftFrontLeg.xRot = -82.5f * Mth.PI / 180F;
        leftFrontLeg.y = 20f;
        body.y = 16f;
        head.y = 19f;
        head.xRot = 20f * Mth.PI / 180F;
    }
}
