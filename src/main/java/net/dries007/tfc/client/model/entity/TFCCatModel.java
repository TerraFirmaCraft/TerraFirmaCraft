/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.model.entity;

import net.dries007.tfc.client.model.animation.AnimationChannel;
import net.dries007.tfc.client.model.animation.AnimationDefinition;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;

import com.mojang.math.Constants;
import net.dries007.tfc.client.model.animation.Keyframe;
import net.dries007.tfc.client.model.animation.VanillaAnimations;
import net.dries007.tfc.common.entities.livestock.pet.TFCCat;

public class TFCCatModel extends HierarchicalAnimatedModel<TFCCat>
{
    public static LayerDefinition createBodyLayer(CubeDeformation deform)
    {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition main = partdefinition.addOrReplaceChild("main", CubeListBuilder.create(), PartPose.offset(0.0F, 18.25F, 0.0F));

        PartDefinition body_r1 = main.addOrReplaceChild("body_r1", CubeListBuilder.create().texOffs(20, 0).addBox(-2.0F, -8.0F, 4.0F, 4.0F, 16.0F, 6.0F, deform), PartPose.offsetAndRotation(0.0F, 6.0F, 0.0F, 1.5708F, 0.0F, 0.0F));

        PartDefinition head = main.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-2.5F, -1.0F, -5.0F, 5.0F, 4.0F, 5.0F, deform)
            .texOffs(0, 24).addBox(-1.5F, 1.0F, -6.0F, 3.0F, 2.0F, 2.0F, deform)
            .texOffs(0, 10).addBox(-1.5F, -2.0F, -2.0F, 1.0F, 1.0F, 2.0F, deform)
            .texOffs(6, 10).addBox(0.5F, -2.0F, -2.0F, 1.0F, 1.0F, 2.0F, deform), PartPose.offset(0.0F, -4.0F, -8.0F));

        PartDefinition legFR = main.addOrReplaceChild("legFR", CubeListBuilder.create().texOffs(40, 0).addBox(-1.0F, -2.0F, -1.0F, 2.0F, 10.0F, 2.0F, deform), PartPose.offset(-1.25F, -2.25F, -6.0F));

        PartDefinition legFL = main.addOrReplaceChild("legFL", CubeListBuilder.create().texOffs(40, 0).addBox(-1.0F, -2.0F, -1.0F, 2.0F, 10.0F, 2.0F, deform), PartPose.offset(1.25F, -2.25F, -6.0F));

        PartDefinition legRR = main.addOrReplaceChild("legRR", CubeListBuilder.create().texOffs(8, 13).addBox(-1.0F, -1.0F, -1.0F, 2.0F, 6.0F, 2.0F, deform), PartPose.offset(-1.25F, 0.75F, 6.0F));

        PartDefinition legRL = main.addOrReplaceChild("legRL", CubeListBuilder.create().texOffs(8, 13).addBox(-1.0F, -1.0F, -1.0F, 2.0F, 6.0F, 2.0F, deform), PartPose.offset(1.25F, 0.75F, 6.0F));

        PartDefinition tail1 = main.addOrReplaceChild("tail1", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -3.75F, 8.0F, -0.3927F, 0.0F, 0.0F));

        PartDefinition tail1_r1 = tail1.addOrReplaceChild("tail1_r1", CubeListBuilder.create().texOffs(0, 15).addBox(-0.5F, -8.0F, 0.0F, 1.0F, 8.0F, 1.0F, deform), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -1.5708F, 0.0F, 0.0F));

        PartDefinition tail2 = tail1.addOrReplaceChild("tail2", CubeListBuilder.create(), PartPose.offset(-0.5F, 0.0F, 8.0F));

        PartDefinition tail2_r1 = tail2.addOrReplaceChild("tail2_r1", CubeListBuilder.create().texOffs(4, 15).addBox(0.01F, -0.5F, -1.25F, 1.0F, 10.0F, 1.0F, deform), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 2.3562F, 0.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 32);
    }

    public static final AnimationDefinition CAT_SLEEP = AnimationDefinition.Builder.withLength(4f).looping()
        .addAnimation("main", new AnimationChannel(AnimationChannel.Targets.POSITION, new Keyframe(0f, VanillaAnimations.posVec(0f, -4f, 0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(3.96f, VanillaAnimations.posVec(0f, -4f, 0f), AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("main", new AnimationChannel(AnimationChannel.Targets.ROTATION, new Keyframe(0f, VanillaAnimations.degreeVec(0f, 0f, -90f), AnimationChannel.Interpolations.LINEAR), new Keyframe(3.96f, VanillaAnimations.degreeVec(0f, 0f, -90f), AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("head", new AnimationChannel(AnimationChannel.Targets.ROTATION, new Keyframe(0f, VanillaAnimations.degreeVec(0f, 0f, 5f), AnimationChannel.Interpolations.LINEAR), new Keyframe(2.04f, VanillaAnimations.degreeVec(-2.5f, 0f, 5f), AnimationChannel.Interpolations.LINEAR), new Keyframe(3.96f, VanillaAnimations.degreeVec(0f, 0f, 5f), AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("legFL", new AnimationChannel(AnimationChannel.Targets.ROTATION, new Keyframe(0f, VanillaAnimations.degreeVec(0f, 0f, 5f), AnimationChannel.Interpolations.LINEAR), new Keyframe(3.96f, VanillaAnimations.degreeVec(0f, 0f, 5f), AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("legRL", new AnimationChannel(AnimationChannel.Targets.ROTATION, new Keyframe(0f, VanillaAnimations.degreeVec(0f, 0f, 5f), AnimationChannel.Interpolations.LINEAR), new Keyframe(3.96f, VanillaAnimations.degreeVec(0f, 0f, 5f), AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("tail1", new AnimationChannel(AnimationChannel.Targets.ROTATION, new Keyframe(0f, VanillaAnimations.degreeVec(0f, -5f, 0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(2.04f, VanillaAnimations.degreeVec(0f, 0f, 0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(3.96f, VanillaAnimations.degreeVec(0f, -5f, 0f), AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("tail2", new AnimationChannel(AnimationChannel.Targets.ROTATION, new Keyframe(0f, VanillaAnimations.degreeVec(0f, -2.5f, 0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(2.04f, VanillaAnimations.degreeVec(0f, -5f, 0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(3.96f, VanillaAnimations.degreeVec(0f, -2.5f, 0f), AnimationChannel.Interpolations.LINEAR))).build();

    public static final AnimationDefinition CAT_WALK = AnimationDefinition.Builder.withLength(0.96f).looping()
        .addAnimation("legFR", new AnimationChannel(AnimationChannel.Targets.ROTATION, new Keyframe(0f, VanillaAnimations.degreeVec(-7.5f, 0f, 0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.48f, VanillaAnimations.degreeVec(7.5f, 0f, 0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.96f, VanillaAnimations.degreeVec(-7.5f, 0f, 0f), AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("legFL", new AnimationChannel(AnimationChannel.Targets.ROTATION, new Keyframe(0f, VanillaAnimations.degreeVec(7.5f, 0f, 0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.48f, VanillaAnimations.degreeVec(-7.5f, 0f, 0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.96f, VanillaAnimations.degreeVec(7.5f, 0f, 0f), AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("legRR", new AnimationChannel(AnimationChannel.Targets.ROTATION, new Keyframe(0f, VanillaAnimations.degreeVec(12.5f, 0f, 0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.48f, VanillaAnimations.degreeVec(-12.5f, 0f, 0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.96f, VanillaAnimations.degreeVec(12.5f, 0f, 0f), AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("legRL", new AnimationChannel(AnimationChannel.Targets.ROTATION, new Keyframe(0f, VanillaAnimations.degreeVec(-12.5f, 0f, 0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.48f, VanillaAnimations.degreeVec(12.5f, 0f, 0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.96f, VanillaAnimations.degreeVec(-12.5f, 0f, 0f), AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("tail1", new AnimationChannel(AnimationChannel.Targets.ROTATION, new Keyframe(0f, VanillaAnimations.degreeVec(0f, -7.5f, 0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.48f, VanillaAnimations.degreeVec(0f, 5f, 0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.96f, VanillaAnimations.degreeVec(0f, -7.5f, 0f), AnimationChannel.Interpolations.LINEAR))).build();

    public static final AnimationDefinition CAT_SIT = AnimationDefinition.Builder.withLength(2.08f).looping()
        .addAnimation("main", new AnimationChannel(AnimationChannel.Targets.POSITION, new Keyframe(0f, VanillaAnimations.posVec(0f, -2f, 0f), AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("main", new AnimationChannel(AnimationChannel.Targets.ROTATION, new Keyframe(0f, VanillaAnimations.degreeVec(-20f, 0f, 0f), AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("head", new AnimationChannel(AnimationChannel.Targets.ROTATION, new Keyframe(0f, VanillaAnimations.degreeVec(17.5f, 0f, 0f), AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("legFR", new AnimationChannel(AnimationChannel.Targets.ROTATION, new Keyframe(0f, VanillaAnimations.degreeVec(12.5f, 0f, 0f), AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("legFL", new AnimationChannel(AnimationChannel.Targets.ROTATION, new Keyframe(0f, VanillaAnimations.degreeVec(17.5f, 0f, 0f), AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("legRR", new AnimationChannel(AnimationChannel.Targets.ROTATION, new Keyframe(0f, VanillaAnimations.degreeVec(-70f, 0f, 0f), AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("legRL", new AnimationChannel(AnimationChannel.Targets.ROTATION, new Keyframe(0f, VanillaAnimations.degreeVec(-67.5f, 0f, 0f), AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("tail1", new AnimationChannel(AnimationChannel.Targets.ROTATION, new Keyframe(0f, VanillaAnimations.degreeVec(19.93938104910376f, -12.48791063281351f, 0.5540435390526f), AnimationChannel.Interpolations.LINEAR), new Keyframe(1.04f, VanillaAnimations.degreeVec(19.99046259165056f, 4.995229008024362f, -0.21865120146203476f), AnimationChannel.Interpolations.LINEAR), new Keyframe(2.08f, VanillaAnimations.degreeVec(19.93938104910376f, -12.48791063281351f, 0.5540435390526f), AnimationChannel.Interpolations.LINEAR))).build();

    private final ModelPart head;

    public TFCCatModel(ModelPart root)
    {
        super(root);
        this.head = root.getChild("main").getChild("head");
    }

    @Override
    public void setupAnim(TFCCat cat, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch)
    {
        super.setupAnim(cat, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);

        if (cat.sittingAnimation.isStarted())
        {
            this.animate(cat.sittingAnimation, CAT_SIT, ageInTicks);
        }
        else if (cat.sleepingAnimation.isStarted())
        {
            this.animate(cat.sleepingAnimation, CAT_SLEEP, ageInTicks);
        }
        else
        {
            final float speed = getAdjustedLandSpeed(cat);
            this.animate(cat.walkingAnimation, CAT_WALK, ageInTicks, speed);
        }
        if (!cat.isSleeping())
        {
            this.head.xRot = headPitch * Constants.DEG_TO_RAD;
            this.head.yRot = netHeadYaw * Constants.DEG_TO_RAD;
        }
    }

}
