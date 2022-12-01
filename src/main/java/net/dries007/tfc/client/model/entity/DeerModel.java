/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.model.entity;

import java.util.stream.Stream;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;

import com.mojang.math.Constants;
import net.dries007.tfc.client.model.animation.AnimationChannel;
import net.dries007.tfc.client.model.animation.AnimationDefinition;
import net.dries007.tfc.client.model.animation.Keyframe;
import net.dries007.tfc.client.model.animation.VanillaAnimations;
import net.dries007.tfc.common.entities.prey.Prey;

public class DeerModel extends HierarchicalAnimatedModel<Prey>
{
    public static LayerDefinition createBodyLayer()
    {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-3.0F, -18.0F, -9.0F, 6.0F, 8.0F, 17.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 25.0F, 0.0F));

        PartDefinition neck = body.addOrReplaceChild("neck", CubeListBuilder.create(), PartPose.offset(0.0F, -15.0F, -6.0F));

        PartDefinition neck0_r1 = neck.addOrReplaceChild("neck0_r1", CubeListBuilder.create().texOffs(0, 0).addBox(-1.5872F, -9.4021F, -1.1846F, 3.0F, 11.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0872F, 0.0038F, -1.0F, 0.6545F, 0.0F, 0.0F));

        PartDefinition head = neck.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 25).addBox(-2.0872F, -4.0038F, -4.0F, 4.0F, 5.0F, 5.0F, new CubeDeformation(0.0F))
            .texOffs(29, 11).addBox(-1.5872F, -2.0038F, -7.0F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0872F, -6.9962F, -5.0F));

        PartDefinition earL = head.addOrReplaceChild("earL", CubeListBuilder.create().texOffs(41, 4).addBox(-1.0F, -2.0F, 0.0F, 2.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.0F, -4.0F, 0.0F, -0.0873F, 0.0175F, 0.1396F));

        PartDefinition earR = head.addOrReplaceChild("earR", CubeListBuilder.create().texOffs(10, 0).addBox(-1.0F, -2.0F, 0.0F, 2.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.0F, -4.0F, 0.0F, -0.0873F, 0.0175F, -0.1396F));

        PartDefinition antlerL = head.addOrReplaceChild("antlerL", CubeListBuilder.create(), PartPose.offsetAndRotation(-1.0F, -4.0F, -1.5F, 0.0F, 0.1745F, 0.0F));

        PartDefinition antler3_r1 = antlerL.addOrReplaceChild("antler3_r1", CubeListBuilder.create().texOffs(13, 14).addBox(-0.75F, -1.5F, -0.25F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.5F, -1.5F, -2.5F, 0.2835F, 0.1153F, -0.3762F));

        PartDefinition antler2_r1 = antlerL.addOrReplaceChild("antler2_r1", CubeListBuilder.create().texOffs(37, 0).addBox(-0.3645F, -0.3459F, -2.5216F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.0F, -1.0F, -2.0F, 0.0303F, -0.1698F, -0.3406F));

        PartDefinition antler1_r1 = antlerL.addOrReplaceChild("antler1_r1", CubeListBuilder.create().texOffs(36, 25).addBox(-4.5F, -2.25F, 0.0F, 4.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0458F, -0.6063F, -0.241F));

        PartDefinition antler0_r1 = antlerL.addOrReplaceChild("antler0_r1", CubeListBuilder.create().texOffs(34, 41).addBox(-1.0F, -4.5F, 0.0F, 1.0F, 5.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.3155F, 0.1624F, -0.4068F));

        PartDefinition antlerL2 = head.addOrReplaceChild("antlerL2", CubeListBuilder.create(), PartPose.offsetAndRotation(0.8257F, -4.0F, -1.5F, 0.0F, -0.1745F, 0.0F));

        PartDefinition antler3_r2 = antlerL2.addOrReplaceChild("antler3_r2", CubeListBuilder.create().texOffs(0, 0).addBox(-0.25F, -1.5F, -0.25F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.5F, -1.5F, -2.5F, 0.2835F, -0.1153F, 0.3762F));

        PartDefinition antler2_r2 = antlerL2.addOrReplaceChild("antler2_r2", CubeListBuilder.create().texOffs(13, 25).addBox(-0.6355F, -0.3459F, -2.5216F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.0F, -1.0F, -2.0F, 0.0303F, 0.1698F, 0.3406F));

        PartDefinition antler1_r2 = antlerL2.addOrReplaceChild("antler1_r2", CubeListBuilder.create().texOffs(0, 15).addBox(0.5F, -2.25F, 0.0F, 4.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0458F, 0.6063F, 0.241F));

        PartDefinition antler0_r2 = antlerL2.addOrReplaceChild("antler0_r2", CubeListBuilder.create().texOffs(38, 27).addBox(0.0F, -4.5F, 0.0F, 1.0F, 5.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.3155F, -0.1624F, 0.4068F));

        PartDefinition tail = body.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(38, 8).addBox(-1.0F, -1.0F, 0.0F, 2.0F, 2.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -17.0F, 8.0F, -0.8727F, 0.0F, 0.0F));

        PartDefinition legBR = body.addOrReplaceChild("legBR", CubeListBuilder.create().texOffs(29, 0).addBox(-1.0F, -2.0F, -2.0F, 2.0F, 7.0F, 4.0F, new CubeDeformation(0.0F))
            .texOffs(16, 36).addBox(-1.0F, 5.0F, 0.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-3.0F, -14.0F, 5.0F));

        PartDefinition legBL = body.addOrReplaceChild("legBL", CubeListBuilder.create().texOffs(18, 25).addBox(-1.0F, -2.0F, -2.0F, 2.0F, 7.0F, 4.0F, new CubeDeformation(0.0F))
            .texOffs(8, 35).addBox(-1.0F, 5.0F, 0.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(3.0F, -14.0F, 5.0F));

        PartDefinition legFR = body.addOrReplaceChild("legFR", CubeListBuilder.create().texOffs(35, 34).addBox(-1.0F, -1.0F, -2.0F, 2.0F, 4.0F, 3.0F, new CubeDeformation(0.0F))
            .texOffs(30, 25).addBox(-1.0F, 3.0F, -1.0F, 2.0F, 10.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-3.0F, -14.0F, -6.0F));

        PartDefinition legFL = body.addOrReplaceChild("legFL", CubeListBuilder.create().texOffs(0, 35).addBox(-1.0F, 3.0F, -1.0F, 2.0F, 10.0F, 2.0F, new CubeDeformation(0.0F))
            .texOffs(24, 37).addBox(-1.0F, -1.0F, -2.0F, 2.0F, 4.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(3.0F, -14.0F, -6.0F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    public static final AnimationDefinition DEER_RUN = AnimationDefinition.Builder.withLength(0.5f).looping()
        .addAnimation("body", new AnimationChannel(AnimationChannel.Targets.POSITION, new Keyframe(0f, VanillaAnimations.posVec(0f, 0f, 0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.125f, VanillaAnimations.posVec(0f, 1f, 0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.25f, VanillaAnimations.posVec(0f, 1.5f, 0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.375f, VanillaAnimations.posVec(0f, 1f, 0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.5f, VanillaAnimations.posVec(0f, 0f, 0f), AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("body", new AnimationChannel(AnimationChannel.Targets.ROTATION, new Keyframe(0f, VanillaAnimations.degreeVec(-5f, 0f, 0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.125f, VanillaAnimations.degreeVec(0f, 0f, 0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.25f, VanillaAnimations.degreeVec(5f, 0f, 0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.375f, VanillaAnimations.degreeVec(0f, 0f, 0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.5f, VanillaAnimations.degreeVec(-5f, 0f, 0f), AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("tail", new AnimationChannel(AnimationChannel.Targets.ROTATION, new Keyframe(0f, VanillaAnimations.degreeVec(22.5f, 0f, 0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.2083f, VanillaAnimations.degreeVec(60f, 0f, 0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.5f, VanillaAnimations.degreeVec(22.5f, 0f, 0f), AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("tail1", new AnimationChannel(AnimationChannel.Targets.ROTATION, new Keyframe(0f, VanillaAnimations.degreeVec(0f, 0f, 0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.25f, VanillaAnimations.degreeVec(-90f, 0f, 0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.4167f, VanillaAnimations.degreeVec(-2.5f, 0f, 0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.5f, VanillaAnimations.degreeVec(0f, 0f, 0f), AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("head", new AnimationChannel(AnimationChannel.Targets.ROTATION, new Keyframe(0f, VanillaAnimations.degreeVec(5f, 0f, 0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.0833f, VanillaAnimations.degreeVec(-7.5f, 0f, 0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.25f, VanillaAnimations.degreeVec(-5f, 0f, 0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.3333f, VanillaAnimations.degreeVec(7.5f, 0f, 0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.5f, VanillaAnimations.degreeVec(5f, 0f, 0f), AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("legFR", new AnimationChannel(AnimationChannel.Targets.ROTATION, new Keyframe(0f, VanillaAnimations.degreeVec(5f, 0f, 0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.0833f, VanillaAnimations.degreeVec(-40f, 0f, 0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.2083f, VanillaAnimations.degreeVec(-40f, 0f, 0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.375f, VanillaAnimations.degreeVec(25.5f, 0f, 0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.4167f, VanillaAnimations.degreeVec(25.5f, 0f, 0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.5f, VanillaAnimations.degreeVec(5f, 0f, 0f), AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("legFL", new AnimationChannel(AnimationChannel.Targets.ROTATION, new Keyframe(0f, VanillaAnimations.degreeVec(5f, 0f, 0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.0833f, VanillaAnimations.degreeVec(-40f, 0f, 0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.2083f, VanillaAnimations.degreeVec(-40f, 0f, 0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.375f, VanillaAnimations.degreeVec(25.5f, 0f, 0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.4167f, VanillaAnimations.degreeVec(25.5f, 0f, 0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.5f, VanillaAnimations.degreeVec(5f, 0f, 0f), AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("legBL", new AnimationChannel(AnimationChannel.Targets.ROTATION, new Keyframe(0f, VanillaAnimations.degreeVec(0f, 0f, 0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.0833f, VanillaAnimations.degreeVec(45f, 0f, 0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.2083f, VanillaAnimations.degreeVec(45f, 0f, 0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.25f, VanillaAnimations.degreeVec(45f, 0f, 0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.375f, VanillaAnimations.degreeVec(-30f, 0f, 0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.5f, VanillaAnimations.degreeVec(0f, 0f, 0f), AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("legBR", new AnimationChannel(AnimationChannel.Targets.ROTATION, new Keyframe(0f, VanillaAnimations.degreeVec(0f, 0f, 0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.0833f, VanillaAnimations.degreeVec(45f, 0f, 0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.2083f, VanillaAnimations.degreeVec(45f, 0f, 0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.25f, VanillaAnimations.degreeVec(45f, 0f, 0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.375f, VanillaAnimations.degreeVec(-30f, 0f, 0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.5f, VanillaAnimations.degreeVec(0f, 0f, 0f), AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("neck", new AnimationChannel(AnimationChannel.Targets.ROTATION, new Keyframe(0f, VanillaAnimations.degreeVec(0f, 0f, 0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.0833f, VanillaAnimations.degreeVec(7.5f, 0f, 0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.25f, VanillaAnimations.degreeVec(5f, 0f, 0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.3333f, VanillaAnimations.degreeVec(-7.5f, 0f, 0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.5f, VanillaAnimations.degreeVec(0f, 0f, 0f), AnimationChannel.Interpolations.LINEAR))).build();
    public static final AnimationDefinition DEER_WALK = AnimationDefinition.Builder.withLength(1f).looping()
        .addAnimation("neck", new AnimationChannel(AnimationChannel.Targets.ROTATION, new Keyframe(0f, VanillaAnimations.degreeVec(0f, 0f, 0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.2917f, VanillaAnimations.degreeVec(5f, 0f, 0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.8333f, VanillaAnimations.degreeVec(-1f, 0f, 0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(1f, VanillaAnimations.degreeVec(0f, 0f, 0f), AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("head", new AnimationChannel(AnimationChannel.Targets.ROTATION, new Keyframe(0f, VanillaAnimations.degreeVec(0f, 0f, 0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.3333f, VanillaAnimations.degreeVec(-5f, 0f, 0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.8333f, VanillaAnimations.degreeVec(1f, 0f, 0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(1f, VanillaAnimations.degreeVec(0f, 0f, 0f), AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("tail", new AnimationChannel(AnimationChannel.Targets.ROTATION, new Keyframe(0f, VanillaAnimations.degreeVec(0f, 0f, 0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.3333f, VanillaAnimations.degreeVec(10f, 0f, 0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.625f, VanillaAnimations.degreeVec(3f, 0f, 0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(1f, VanillaAnimations.degreeVec(0f, 0f, 0f), AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("legBR", new AnimationChannel(AnimationChannel.Targets.ROTATION, new Keyframe(0f, VanillaAnimations.degreeVec(0f, 0f, 0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.4167f, VanillaAnimations.degreeVec(22.5f, 0f, 0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.75f, VanillaAnimations.degreeVec(-27.5f, 0f, 0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(1f, VanillaAnimations.degreeVec(0f, 0f, 0f), AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("legBL", new AnimationChannel(AnimationChannel.Targets.ROTATION, new Keyframe(0f, VanillaAnimations.degreeVec(0f, 0f, 0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.1667f, VanillaAnimations.degreeVec(-27.5f, 0f, 0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.8333f, VanillaAnimations.degreeVec(22.5f, 0f, 0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(1f, VanillaAnimations.degreeVec(0f, 0f, 0f), AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("legFR", new AnimationChannel(AnimationChannel.Targets.ROTATION, new Keyframe(0f, VanillaAnimations.degreeVec(0f, 0f, 0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.1667f, VanillaAnimations.degreeVec(-27.5f, 0f, 0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.7917f, VanillaAnimations.degreeVec(22.5f, 0f, 0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(1f, VanillaAnimations.degreeVec(0f, 0f, 0f), AnimationChannel.Interpolations.LINEAR)))
        .addAnimation("legFL", new AnimationChannel(AnimationChannel.Targets.ROTATION, new Keyframe(0f, VanillaAnimations.degreeVec(0f, 0f, 0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.4167f, VanillaAnimations.degreeVec(22.5f, 0f, 0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(0.75f, VanillaAnimations.degreeVec(-27.5f, 0f, 0f), AnimationChannel.Interpolations.LINEAR), new Keyframe(1f, VanillaAnimations.degreeVec(0f, 0f, 0f), AnimationChannel.Interpolations.LINEAR))).build();

    private final ModelPart head;
    private final ModelPart antler1;
    private final ModelPart antler2;

    public DeerModel(ModelPart root)
    {
        super(root);
        this.head = root.getChild("body").getChild("neck").getChild("head");
        this.antler1 = head.getChild("antlerL");
        this.antler2 = head.getChild("antlerL2");
    }

    @Override
    public void setupAnim(Prey entity, float limbSwing, float limbSwingAmount, float ageInTicks, float headYaw, float headPitch)
    {
        super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, headYaw, headPitch);
        final boolean antlers = entity.displayMaleCharacteristics();
        Stream.concat(antler1.getAllParts(), antler2.getAllParts()).forEach(p -> p.visible = antlers);
        final float speed = getAdjustedLandSpeed(entity);
        this.animate(entity.walkingAnimation, speed > 1f ? DEER_RUN : DEER_WALK, ageInTicks, speed);

        this.head.xRot = headPitch * Constants.DEG_TO_RAD;
        this.head.yRot = headYaw * Constants.DEG_TO_RAD;
    }
}
