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

public class PheasantModel extends HierarchicalAnimatedModel<Prey>
{
    public static LayerDefinition createBodyLayer()
    {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-2.5F, -6.0F, -6.0F, 5.0F, 6.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 18.0F, -1.0F, -0.5236F, 0.0F, 0.0F));

        PartDefinition legR = body.addOrReplaceChild("legR", CubeListBuilder.create().texOffs(0, 0).addBox(-0.5F, 1.0F, 0.0F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(8, 15).addBox(-1.5F, 5.0F, -2.0F, 3.0F, 0.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.0F, 0.0F, 2.0F, 0.5236F, 0.0F, 0.0F));

        PartDefinition haunch_r1 = legR.addOrReplaceChild("haunch_r1", CubeListBuilder.create().texOffs(13, 29).addBox(-1.0F, -2.0F, -1.5F, 2.0F, 4.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.3054F, 0.0F, 0.0F));

        PartDefinition legL = body.addOrReplaceChild("legL", CubeListBuilder.create().texOffs(4, 0).addBox(-0.5F, 1.0F, 0.0F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(18, 15).addBox(-1.5F, 5.0F, -2.0F, 3.0F, 0.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.0F, 0.0F, 2.0F, 0.5236F, 0.0F, 0.0F));

        PartDefinition haunch_r2 = legL.addOrReplaceChild("haunch_r2", CubeListBuilder.create().texOffs(0, 31).addBox(-1.0F, -2.0F, -1.5F, 2.0F, 4.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.3054F, 0.0F, 0.0F));

        PartDefinition wingR = body.addOrReplaceChild("wingR", CubeListBuilder.create(), PartPose.offset(-2.5F, -5.0F, -1.0F));

        PartDefinition main_r1 = wingR.addOrReplaceChild("main_r1", CubeListBuilder.create().texOffs(13, 17).addBox(-1.0F, -1.0F, -2.0F, 1.0F, 4.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 1.0F, -2.0F, -0.0873F, 0.0F, 0.0F));

        PartDefinition neck = body.addOrReplaceChild("neck", CubeListBuilder.create(), PartPose.offsetAndRotation(0.5F, -4.0F, -4.5F, 0.3054F, 0.0F, 0.0F));

        PartDefinition cube1_r1 = neck.addOrReplaceChild("cube1_r1", CubeListBuilder.create().texOffs(31, 18).addBox(-2.0F, -2.0F, -3.0F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.0F, -3.0F, -1.0036F, 0.0F, 0.0F));

        PartDefinition cube_r1 = neck.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(21, 0).addBox(-2.5028F, -3.7661F, -1.1073F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 1.9166F, -0.909F, 0.6981F, 0.0044F, 0.0028F));

        PartDefinition head = neck.addOrReplaceChild("head", CubeListBuilder.create().texOffs(28, 12).addBox(-1.51F, -2.0436F, -1.001F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.5F, -3.8334F, -4.909F, 0.3054F, 0.0F, 0.0F));

        PartDefinition beak_r1 = head.addOrReplaceChild("beak_r1", CubeListBuilder.create().texOffs(0, 5).addBox(-0.5F, 2.5F, -2.25F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -3.0436F, -1.001F, 0.1745F, 0.0F, 0.0F));

        PartDefinition wingL = body.addOrReplaceChild("wingL", CubeListBuilder.create(), PartPose.offset(2.5F, -6.0F, -1.0F));

        PartDefinition main_r2 = wingL.addOrReplaceChild("main_r2", CubeListBuilder.create().texOffs(22, 21).addBox(0.0F, -1.0F, -2.0F, 1.0F, 4.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 2.0F, -2.0F, -0.0873F, 0.0F, 0.0F));

        PartDefinition tail = body.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(0, 24).addBox(-2.0F, -2.0F, -1.25F, 4.0F, 3.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -3.25F, 3.25F, 0.3927F, 0.0F, 0.0F));

        PartDefinition end_r1 = tail.addOrReplaceChild("end_r1", CubeListBuilder.create().texOffs(0, 15).addBox(-1.0F, -1.5F, 0.75F, 2.0F, 1.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 2.0F, 0.1745F, 0.0F, 0.0F));

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

    public PheasantModel(ModelPart root)
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
    }
}