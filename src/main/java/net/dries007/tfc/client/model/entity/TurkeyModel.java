/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

// Made with Blockbench 4.5.2
// Exported for Minecraft version 1.17 - 1.18 with Mojang mappings
// Paste this class into your mod and generate all required imports

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

public class TurkeyModel extends HierarchicalAnimatedModel<Prey>
{
    public static LayerDefinition createBodyLayer()
    {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(28, 45).addBox(-4.0F, -9.0F, -4.0F, 8.0F, 9.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 20.0F, -1.0F, -0.0873F, 0.0F, 0.0F));

        PartDefinition front_r1 = body.addOrReplaceChild("front_r1", CubeListBuilder.create().texOffs(31, 31).addBox(-3.5F, -4.25F, -3.75F, 7.0F, 7.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -4.0F, -4.0F, -0.2618F, 0.0F, 0.0F));

        PartDefinition legR = body.addOrReplaceChild("legR", CubeListBuilder.create().texOffs(2, 37).addBox(-1.5F, 1.9166F, -1.409F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(-1, 32).addBox(-2.5F, 3.9166F, -3.409F, 3.0F, 0.0F, 4.0F, new CubeDeformation(0.0F))
            .texOffs(0, 40).addBox(-2.0F, -1.0834F, -1.909F, 2.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, 0.0F, 1.0F, 0.0873F, 0.0F, 0.0F));

        PartDefinition legL = body.addOrReplaceChild("legL", CubeListBuilder.create().texOffs(8, 40).addBox(0.0F, -1.0834F, -1.909F, 2.0F, 3.0F, 2.0F, new CubeDeformation(0.0F))
            .texOffs(10, 37).addBox(0.5F, 1.9166F, -1.409F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(7, 32).addBox(-0.5F, 3.9166F, -3.409F, 3.0F, 0.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, 0.0F, 1.0F, 0.0873F, 0.0F, 0.0F));

        PartDefinition neck = body.addOrReplaceChild("neck", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -5.0F, -6.5F, 0.0873F, 0.0F, 0.0F));

        PartDefinition base_r1 = neck.addOrReplaceChild("base_r1", CubeListBuilder.create().texOffs(37, 22).addBox(-2.0028F, -5.0161F, 0.1427F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 1.0908F, -1.8251F, 0.0873F, 0.0F, 0.0F));

        PartDefinition neck1 = neck.addOrReplaceChild("neck1", CubeListBuilder.create().texOffs(38, 14).addBox(-1.5F, -9.0F, 0.0F, 3.0F, 4.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 1.0908F, -1.8251F));

        PartDefinition head = neck1.addOrReplaceChild("head", CubeListBuilder.create().texOffs(1, 20).addBox(-1.5F, -3.9782F, -2.4995F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.0F))
            .texOffs(4, 12).addBox(-0.5F, -2.2282F, -5.2495F, 2.0F, 2.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.5F, -8.75F, 1.75F, -0.0436F, 0.0F, 0.0F));

        PartDefinition snood = head.addOrReplaceChild("snood", CubeListBuilder.create().texOffs(8, 8).addBox(0.0F, 0.0F, -0.25F, 0.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.5F, -2.2282F, -3.9995F, 0.0F, 0.0F, 0.0873F));

        PartDefinition wingL = body.addOrReplaceChild("wingL", CubeListBuilder.create(), PartPose.offset(4.0F, -7.0F, -2.0F));

        PartDefinition main_r1 = wingL.addOrReplaceChild("main_r1", CubeListBuilder.create().texOffs(18, 25).addBox(0.0F, -4.0F, 0.0F, 1.0F, 5.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 3.0F, -1.0F, -0.0873F, 0.0F, 0.0F));

        PartDefinition tail = body.addOrReplaceChild("tail", CubeListBuilder.create(), PartPose.offset(-0.5F, -2.25F, 2.25F));

        PartDefinition base_r2 = tail.addOrReplaceChild("base_r2", CubeListBuilder.create().texOffs(2, 53).addBox(-3.0F, -6.5844F, 0.9082F, 7.0F, 6.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.1745F, 0.0F, 0.0F));

        PartDefinition feathers = tail.addOrReplaceChild("feathers", CubeListBuilder.create(), PartPose.offsetAndRotation(0.5F, -4.75F, 6.5F, 1.4835F, 0.0F, 0.0F));

        PartDefinition feather_0 = feathers.addOrReplaceChild("feather_0", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -1.5999F, -1.2117F, -0.0295F));

        PartDefinition feather0_r1 = feather_0.addOrReplaceChild("feather0_r1", CubeListBuilder.create().texOffs(0, 0).addBox(-1.5F, -0.25F, 0.0F, 3.0F, 0.0F, 13.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.25F, 0.0F, 0.0F, -0.0436F, 0.0F));

        PartDefinition feather_1 = feathers.addOrReplaceChild("feather_1", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -1.8111F, -0.9903F, 0.2021F));

        PartDefinition feather0_r2 = feather_1.addOrReplaceChild("feather0_r2", CubeListBuilder.create().texOffs(0, 0).addBox(-1.5F, -0.25F, 0.0F, 3.0F, 0.0F, 13.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.25F, 0.0F, 0.0F, -0.0436F, 0.0F));

        PartDefinition feather_2 = feathers.addOrReplaceChild("feather_2", CubeListBuilder.create().texOffs(0, 0).addBox(-1.5F, 0.0F, 0.0F, 3.0F, 0.0F, 13.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -1.7216F, -0.774F, 0.1452F));

        PartDefinition feather_3 = feathers.addOrReplaceChild("feather_3", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -1.5471F, -0.5187F, 0.0752F));

        PartDefinition feather0_r3 = feather_3.addOrReplaceChild("feather0_r3", CubeListBuilder.create().texOffs(0, 0).addBox(-1.5F, -0.25F, 0.0F, 3.0F, 0.0F, 13.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.25F, 0.0F, -0.0873F, 0.0F, 0.0F));

        PartDefinition feather_4 = feathers.addOrReplaceChild("feather_4", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -1.5317F, -0.2595F, 0.035F));

        PartDefinition feather0_r4 = feather_4.addOrReplaceChild("feather0_r4", CubeListBuilder.create().texOffs(0, 0).addBox(-1.5F, -0.25F, 0.0F, 3.0F, 0.0F, 13.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.25F, 0.0F, -0.0436F, 0.0F, 0.0F));

        PartDefinition feather_5 = feathers.addOrReplaceChild("feather_5", CubeListBuilder.create().texOffs(0, 0).addBox(-1.5F, 0.0F, 0.0F, 3.0F, 0.0F, 13.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -1.5272F, 0.0F, 0.0F));

        PartDefinition feather_6 = feathers.addOrReplaceChild("feather_6", CubeListBuilder.create().texOffs(0, 0).addBox(-1.5F, 0.0F, 0.0F, 3.0F, 0.0F, 13.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -1.5754F, 0.2595F, -0.035F));

        PartDefinition feather_7 = feathers.addOrReplaceChild("feather_7", CubeListBuilder.create().texOffs(0, 0).addBox(-1.5F, 0.0F, 0.0F, 3.0F, 0.0F, 13.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -1.6632F, 0.4891F, -0.195F));

        PartDefinition feather_8 = feathers.addOrReplaceChild("feather_8", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -1.6602F, 0.734F, -0.119F));

        PartDefinition feather0_r5 = feather_8.addOrReplaceChild("feather0_r5", CubeListBuilder.create().texOffs(0, 0).addBox(-1.5F, -0.25F, 0.0F, 3.0F, 0.0F, 13.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.25F, 0.0F, 0.0F, 0.0436F, 0.0F));

        PartDefinition feather_9 = feathers.addOrReplaceChild("feather_9", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -1.7238F, 1.0339F, -0.2021F));

        PartDefinition feather0_r6 = feather_9.addOrReplaceChild("feather0_r6", CubeListBuilder.create().texOffs(0, 0).addBox(-1.5F, -0.25F, 0.0F, 3.0F, 0.0F, 13.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.25F, 0.0F, 0.0F, 0.0873F, 0.0F));

        PartDefinition feather_10 = feathers.addOrReplaceChild("feather_10", CubeListBuilder.create().texOffs(0, 0).addBox(-1.5F, 0.0F, 0.0F, 3.0F, 0.0F, 13.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -1.8103F, 1.2535F, -0.2697F));

        PartDefinition wingR = body.addOrReplaceChild("wingR", CubeListBuilder.create(), PartPose.offset(-4.0F, -7.0F, -2.0F));

        PartDefinition main_r2 = wingR.addOrReplaceChild("main_r2", CubeListBuilder.create().texOffs(18, 13).mirror().addBox(-1.0F, -4.0F, 0.0F, 1.0F, 5.0F, 7.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(0.0F, 3.0F, -1.0F, -0.0873F, 0.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    private final ModelPart neck;
    private final ModelPart body;
    private final ModelPart head;
    private final ModelPart legR;
    private final ModelPart legL;
    private final ModelPart wingR;
    private final ModelPart wingL;
    private final ModelPart tail;
    private final ModelPart feathers;
    private final ModelPart feather_0;
    private final ModelPart feather_1;
    private final ModelPart feather_2;
    private final ModelPart feather_3;
    private final ModelPart feather_4;
    private final ModelPart feather_5;
    private final ModelPart feather_6;
    private final ModelPart feather_7;
    private final ModelPart feather_8;
    private final ModelPart feather_9;
    private final ModelPart feather_10;

    public TurkeyModel(ModelPart root)
    {
        super(root);
        this.body = root.getChild("body");
        this.neck = body.getChild("neck");
        this.head = neck.getChild("head");
        this.legR = body.getChild("legR");
        this.legL = body.getChild("legL");
        this.wingR = body.getChild("wingR");
        this.wingL = body.getChild("wingL");
        this.tail = body.getChild("tail");
        this.feathers = tail.getChild("feathers");
        this.feather_0 = feathers.getChild("feather_0");
        this.feather_1 = feathers.getChild("feather_1");
        this.feather_2 = feathers.getChild("feather_2");
        this.feather_3 = feathers.getChild("feather_3");
        this.feather_4 = feathers.getChild("feather_4");
        this.feather_5 = feathers.getChild("feather_5");
        this.feather_6 = feathers.getChild("feather_6");
        this.feather_7 = feathers.getChild("feather_7");
        this.feather_8 = feathers.getChild("feather_8");
        this.feather_9 = feathers.getChild("feather_9");
        this.feather_10 = feathers.getChild("feather_10");
    }
}