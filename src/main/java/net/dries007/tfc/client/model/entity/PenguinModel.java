/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.model.entity;

import java.util.Map;

import com.mojang.math.Constants;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;

import net.dries007.tfc.client.model.animation.AnimationChannel;
import net.dries007.tfc.client.model.animation.AnimationDefinition;
import net.dries007.tfc.client.model.animation.VanillaAnimations;
import net.dries007.tfc.common.entities.aquatic.Penguin;

import static net.dries007.tfc.client.model.animation.VanillaAnimations.*;

public class PenguinModel extends HierarchicalAnimatedModel<Penguin>
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

    public static final AnimationDefinition WALK = AnimationDefinition.Builder.withLength(1f).looping()
        .addAnimation("core", new AnimationChannel(AnimationChannel.Targets.ROTATION, rotation(0f, 0f, 0f, 15f), rotation(0.5f, 0f, 0f, -15f), rotation(1f, 0f, 0f, 15f)))
        .addAnimation("head", new AnimationChannel(AnimationChannel.Targets.ROTATION, noRotation(0f), rotation(0.33f, 0f, 0f, -15f), rotation(0.66f, 0f, 0f, 15f), noRotation(1f)))
        .addAnimation("leftfoot", new AnimationChannel(AnimationChannel.Targets.ROTATION, noRotation(0f), rotation(0.5f, 45f, 0f, 0f), noRotation(1f)))
        .addAnimation("rightfoot", new AnimationChannel(AnimationChannel.Targets.ROTATION, noRotation(0f), rotation(0.5f, -45f, 0f, 0f), noRotation(1f)))
        .addAnimation("leftwing", new AnimationChannel(AnimationChannel.Targets.ROTATION, noRotation(0f), rotation(0.5f, 25f, 0f, 0f), noRotation(1f)))
        .addAnimation("rightwing", new AnimationChannel(AnimationChannel.Targets.ROTATION, noRotation(0f), rotation(0.5f, 25f, 0f, 0f), noRotation(1f)))
        .build();

    public static final AnimationDefinition SWIM = AnimationDefinition.Builder.withLength(1f).looping()
        .addAnimation("leftfoot", new AnimationChannel(AnimationChannel.Targets.ROTATION, noRotation(0f), rotation(0.5f, 45f, 0f, 0f), noRotation(1f)))
        .addAnimation("rightfoot", new AnimationChannel(AnimationChannel.Targets.ROTATION, noRotation(0f), rotation(0.5f, -45f, 0f, 0f), noRotation(1f)))
        .addAnimation("leftwing", new AnimationChannel(AnimationChannel.Targets.ROTATION, noRotation(0f), rotation(0.5f, 0f, 0f, -25f), noRotation(1f)))
        .addAnimation("rightwing", new AnimationChannel(AnimationChannel.Targets.ROTATION, noRotation(0f), rotation(0.5f, 0f, 0f, 25f), noRotation(1f)))
        .build();

    public final Map<ModelPart, PartPose> defaults;

    private final ModelPart core;
    private final ModelPart head;

    public PenguinModel(ModelPart root)
    {
        super(root);
        core = root.getChild("core");
        head = core.getChild("head");

        defaults = VanillaAnimations.save(root.getAllParts());
    }

    @Override
    public void setupAnim(Penguin entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch)
    {
        super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        if (entity.isPlayingDead())
        {
            core.xRot = -90F * Constants.DEG_TO_RAD;
        }
        else
        {
            this.animate(entity.walkingAnimation, WALK, ageInTicks, getAdjustedLandSpeed(entity));
            this.animate(entity.swimmingAnimation, SWIM, ageInTicks);

            head.xRot = entity.isInWater() ? -1 : headPitch * Constants.DEG_TO_RAD;
            head.yRot = netHeadYaw * Constants.DEG_TO_RAD;
        }
    }
}
