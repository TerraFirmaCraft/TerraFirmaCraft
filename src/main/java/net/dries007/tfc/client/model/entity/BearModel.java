/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.model.entity;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;

import net.dries007.tfc.client.model.animation.AnimationChannel;
import net.dries007.tfc.client.model.animation.AnimationDefinition;
import net.dries007.tfc.common.entities.predator.Predator;

import static net.dries007.tfc.client.model.animation.VanillaAnimations.*;

public class BearModel extends HierarchicalAnimatedModel<Predator>
{
    public static LayerDefinition createBodyLayer()
    {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 24).addBox(-7.5F, -9.0F, -18.0F, 15.0F, 11.0F, 10.0F, new CubeDeformation(0.0F))
            .texOffs(0, 0).addBox(-7.5F, -10.0F, -8.0F, 15.0F, 12.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 15.0F, 8.0F));

        PartDefinition right_front_leg = body.addOrReplaceChild("right_front_leg", CubeListBuilder.create().texOffs(50, 54).addBox(-2.0F, -1.0F, -2.0F, 4.0F, 11.0F, 4.0F, new CubeDeformation(0.0F))
            .texOffs(64, 27).addBox(-2.5F, 8.0F, -4.0F, 5.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(-6.0F, -1.0F, -15.0F));

        PartDefinition left_front_leg = body.addOrReplaceChild("left_front_leg", CubeListBuilder.create().texOffs(34, 54).addBox(-2.0F, -1.0F, -2.0F, 4.0F, 11.0F, 4.0F, new CubeDeformation(0.0F))
            .texOffs(15, 64).addBox(-2.5F, 8.0F, -4.0F, 5.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(6.0F, -1.0F, -15.0F));

        PartDefinition left_hind_leg = body.addOrReplaceChild("left_hind_leg", CubeListBuilder.create().texOffs(66, 54).addBox(-2.0F, 2.0F, 0.0F, 4.0F, 7.0F, 4.0F, new CubeDeformation(0.0F))
            .texOffs(64, 13).addBox(-2.5F, 7.0F, -2.0F, 5.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(6.0F, 0.0F, 0.0F));

        PartDefinition left_thigh_r1 = left_hind_leg.addOrReplaceChild("left_thigh_r1", CubeListBuilder.create().texOffs(49, 19).addBox(-2.5F, -2.4645F, -0.2929F, 5.0F, 8.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 1.0F, -2.0F, 0.7854F, 0.0F, 0.0F));

        PartDefinition right_hind_leg = body.addOrReplaceChild("right_hind_leg", CubeListBuilder.create().texOffs(62, 65).addBox(-2.0F, 2.0F, 0.0F, 4.0F, 7.0F, 4.0F, new CubeDeformation(0.0F))
            .texOffs(0, 62).addBox(-2.5F, 7.0F, -2.0F, 5.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(-6.0F, 0.0F, 0.0F));

        PartDefinition left_thigh_r2 = right_hind_leg.addOrReplaceChild("left_thigh_r2", CubeListBuilder.create().texOffs(54, 0).addBox(-2.5F, -3.1716F, -1.0F, 5.0F, 8.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 1.0F, -1.0F, 0.7854F, 0.0F, 0.0F));

        PartDefinition neck = body.addOrReplaceChild("neck", CubeListBuilder.create(), PartPose.offset(1.0F, -5.0F, -15.0F));

        PartDefinition neck_r1 = neck.addOrReplaceChild("neck_r1", CubeListBuilder.create().texOffs(41, 36).addBox(-6.0F, -6.0761F, -6.6173F, 12.0F, 9.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, 1.0F, -1.0F, -0.3927F, 0.0F, 0.0F));

        PartDefinition head = neck.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 45).addBox(-5.0F, -5.0F, -8.0F, 8.0F, 8.0F, 9.0F, new CubeDeformation(0.0F))
            .texOffs(25, 45).addBox(-3.0F, -1.0F, -11.0F, 4.0F, 4.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -2.0F, -4.0F));

        PartDefinition right_ear = head.addOrReplaceChild("right_ear", CubeListBuilder.create().texOffs(0, 3).addBox(-1.0F, -2.0F, 0.0F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(-3.0F, -5.0F, -7.0F));

        PartDefinition left_ear = head.addOrReplaceChild("left_ear", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, -2.0F, 0.0F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(1.0F, -5.0F, -7.0F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }

    public static final AnimationDefinition WALK = AnimationDefinition.Builder.withLength(1.0F).looping()
        .addAnimation("right_front_leg", new AnimationChannel(AnimationChannel.Targets.ROTATION, rotation(0.25F, -30F, 0F, 0F), rotation(0.75F, 25F, 0F, 0F), noRotation(1.0F)))
        .addAnimation("left_front_leg", new AnimationChannel(AnimationChannel.Targets.ROTATION, rotation(0.25F, 20F, 0F, 0F), noRotation(0.5F), rotation(0.75F, -30F, 0F, 0F), noRotation(1.0F)))
        .addAnimation("left_hind_leg", new AnimationChannel(AnimationChannel.Targets.ROTATION, rotation(0.375F, -30F, 0F, 0F), rotation(0.75F, 25F, 0F, 0F), noRotation(1.0F)))
        .addAnimation("right_hind_leg", new AnimationChannel(AnimationChannel.Targets.ROTATION, rotation(0.2917F, 20F, 0F, 0F), noRotation(0.5833F), rotation(0.7917F, -30F, 0F, 0F), noRotation(1.0F)))
        .build();

    public static final AnimationDefinition RUN = AnimationDefinition.Builder.withLength(0.7F).looping()
        .addAnimation("right_front_leg", new AnimationChannel(AnimationChannel.Targets.ROTATION, rotation(0.0F, 17.5F, 0F, 0F), rotation(0.32F, -20F, 0F, 0F), rotation(0.7F, 17.5F, 0F, 0F)))
        .addAnimation("left_front_leg", new AnimationChannel(AnimationChannel.Targets.ROTATION, rotation(0.04F, 17.5F, 0F, 0F), rotation(0.36F, -20F, 0F, 0F), rotation(0.7F, 17.5F, 0F, 0F)))
        .addAnimation("left_hind_leg", new AnimationChannel(AnimationChannel.Targets.ROTATION, rotation(0.04F, -15F, 0F, 0F), rotation(0.36F, 20F, 0F, 0F), rotation(0.7F, -15F, 0F, 0F)))
        .addAnimation("right_hind_leg", new AnimationChannel(AnimationChannel.Targets.ROTATION, rotation(0.0F, -15F, 0F, 0F), rotation(0.32F, 20F, 0F, 0F), rotation(0.7F, -15F, 0F, 0F)))
        .addAnimation("body", new AnimationChannel(AnimationChannel.Targets.ROTATION, noRotation(0.0F), rotation(0.32F, -5F, 0F, 0F), noRotation(0.7F)))
        .addAnimation("head", new AnimationChannel(AnimationChannel.Targets.ROTATION, rotation(0.0F, -10F, 0F, 0F), rotation(0.32F, 10F, 0F, 0F), rotation(0.7F, -10F, 0F, 0F)))
        .build();

    public static final AnimationDefinition SLEEP = AnimationDefinition.Builder.withLength(2.5F).looping()
        .addAnimation("right_front_leg", new AnimationChannel(AnimationChannel.Targets.ROTATION, rotation(0.0F, -100F, 0F, 0F), rotation(2.5F, -100F, 0F, 0F)))
        .addAnimation("left_front_leg", new AnimationChannel(AnimationChannel.Targets.ROTATION, rotation(0.0F, -100F, 0F, 0F), rotation(2.5F, -100F, 0F, 0F)))
        .addAnimation("left_hind_leg", new AnimationChannel(AnimationChannel.Targets.ROTATION, rotation(0.0F, -100F, 0F, 0F), rotation(0.9167F, -102.5F, 0F, 0F), rotation(2.5F, -100F, 0F, 0F)))
        .addAnimation("right_hind_leg", new AnimationChannel(AnimationChannel.Targets.ROTATION, rotation(0.0F, -100F, 0F, 0F), rotation(0.9167F, -102.5F, 0F, 0F), rotation(2.5F, -100F, 0F, 0F)))
        .addAnimation("body", new AnimationChannel(AnimationChannel.Targets.ROTATION, rotation(0.0F, 10F, 0F, 0F), rotation(0.9167F, 12.5F, 0F, 0F), rotation(2.5F, 10F, 0F, 0F)))
        .addAnimation("head", new AnimationChannel(AnimationChannel.Targets.ROTATION, rotation(0.0F, 34.70381F, 17.89439F, 28.3117F), rotation(2.5F, 34.70381F, 17.89439F, 28.3117F)))
        .addAnimation("neck", new AnimationChannel(AnimationChannel.Targets.ROTATION, rotation(0.0F, -355.004F, 18.9931F, -348.65664F), rotation(2.5F, -355.004F, 18.9931F, -348.65664F)))
        .build();

    public static final AnimationDefinition ATTACK = AnimationDefinition.Builder.withLength(0.65F)
        .addAnimation("right_front_leg", new AnimationChannel(AnimationChannel.Targets.ROTATION, noRotation(0.0F), rotation(0.25F, -77.58077F, 50.04097F, -37.61043F), rotation(0.3333F, -5.35402F, 39.14934F, -15.61544F), rotation(0.4167F, 24.98346F, 39.58329F, -35.66042F), noRotation(0.65F)))
        .addAnimation("left_front_leg", new AnimationChannel(AnimationChannel.Targets.ROTATION, noRotation(0.0F), rotation(0.375F, -88.26784F, -47.13536F, 48.49352F), rotation(0.4583F, -12.45074F, -49.52961F, 14.56411F), rotation(0.5417F, 35.22909F, -29.67177F, 26.85242F), noRotation(0.65F)))
        .addAnimation("left_hind_leg", new AnimationChannel(AnimationChannel.Targets.ROTATION, noRotation(0.0F), rotation(0.4167F, 40F, 0F, 0F), noRotation(0.65F)))
        .addAnimation("right_hind_leg", new AnimationChannel(AnimationChannel.Targets.ROTATION, noRotation(0.0F), rotation(0.4167F, 40F, 0F, 0F), noRotation(0.65F)))
        .addAnimation("body", new AnimationChannel(AnimationChannel.Targets.ROTATION, noRotation(0.0F), rotation(0.4167F, -37.5F, 0F, 0F), noRotation(0.65F)))
        .addAnimation("head", new AnimationChannel(AnimationChannel.Targets.ROTATION, noRotation(0.0F), rotation(0.4167F, 10F, 0F, 0F), noRotation(0.65F)))
        .addAnimation("neck", new AnimationChannel(AnimationChannel.Targets.ROTATION, rotation(0.0F, 10F, 0F, 0F), rotation(0.4167F, 50F, 0F, 0F), rotation(0.65F, 10F, 0F, 0F)))
        .build();

    public static final AnimationDefinition SWIM = AnimationDefinition.Builder.withLength(1.0F).looping()
        .addAnimation("right_front_leg", new AnimationChannel(AnimationChannel.Targets.ROTATION, rotation(0.0F, 0F, 0F, 82.5F), rotation(0.25F, -44.58543F, -6.80829F, 75.18027F), rotation(0.5833F, 35.22818F, 3.59874F, 79.76447F), rotation(0.7917F, 15.47767F, 10.66721F, 94.49245F), rotation(1.0F, 0F, 0F, 82.5F)))
        .addAnimation("left_front_leg", new AnimationChannel(AnimationChannel.Targets.ROTATION, rotation(0.0F, 0F, 0F, -82.5F), rotation(0.2917F, -44.56145F, 7.05302F, -75.39292F), rotation(0.625F, 35.26063F, -4.25476F, -84.31852F), rotation(0.8333F, 17.64114F, -6.03989F, -95.48139F), rotation(1.0F, 0F, 0F, -82.5F)))
        .addAnimation("left_hind_leg", new AnimationChannel(AnimationChannel.Targets.ROTATION, rotation(0.0F, 32.5F, 0F, -82.5F), rotation(0.5F, 42.5F, 0F, -82.5F), rotation(1.0F, 32.5F, 0F, -82.5F)))
        .addAnimation("right_hind_leg", new AnimationChannel(AnimationChannel.Targets.ROTATION, rotation(0.0F, 32.5F, 0F, 82.5F), rotation(0.5F, 42.5F, 0F, 82.5F), rotation(1.0F, 32.5F, 0F, 82.5F)))
        .build();

    private final ModelPart body;
    private final ModelPart neck;
    private final ModelPart head;
    private final ModelPart right_front_leg;
    private final ModelPart left_front_leg;
    private final ModelPart right_hind_leg;
    private final ModelPart left_hind_leg;
    private final ModelPart right_ear;
    private final ModelPart left_ear;

    public BearModel(ModelPart root)
    {
        super(root);
        this.body = root.getChild("body");
        this.neck = body.getChild("neck");
        this.head = neck.getChild("head");
        this.right_ear = head.getChild("right_ear");
        this.left_ear = head.getChild("left_ear");
        this.right_front_leg = body.getChild("right_front_leg");
        this.left_front_leg = body.getChild("left_front_leg");
        this.left_hind_leg = body.getChild("left_hind_leg");
        this.right_hind_leg = body.getChild("right_hind_leg");
    }

    @Override
    public void setupAnim(Predator predator, float limbSwing, float limbSwingAmount, float ageInTicks, float headYaw, float headPitch)
    {
        super.setupAnim(predator, limbSwing, limbSwingAmount, ageInTicks, headYaw, headPitch);

        if (predator.sleepingAnimation.isStarted())
        {
            setupSleeping();
            this.animate(predator.sleepingAnimation, SLEEP, ageInTicks);
        }
        else
        {
            final float speed = getAdjustedLandSpeed(predator);
            if (predator.swimmingAnimation.isStarted())
            {
                setupSwimming();
                this.animate(predator.swimmingAnimation, SWIM, ageInTicks, speed);
            }
            else
            {
                this.animate(predator.walkingAnimation, WALK, ageInTicks, speed);
                this.animate(predator.runningAnimation, RUN, ageInTicks, speed);
                this.animate(predator.attackingAnimation, ATTACK, ageInTicks);
            }
            head.xRot = headPitch * Mth.PI / 180F;
            head.yRot = headYaw * Mth.PI / 180F;
        }
    }

    private void setupSwimming()
    {
        body.y = 22f;
    }

    private void setupSleeping()
    {
        right_front_leg.z = -17.6f;
        body.y = 22f;
    }
}
