/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.model.entity;

import com.mojang.math.Constants;
import net.minecraft.client.model.WolfModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.util.Mth;

import net.dries007.tfc.common.entities.livestock.pet.Dog;

public class DogModel extends HierarchicalAnimatedModel<Dog>
{
    public static LayerDefinition createBodyLayer()
    {
        // todo 1.21, someone who knows wtf we did here verify
        return LayerDefinition.create(WolfModel.createMeshDefinition(CubeDeformation.NONE), 64, 32);
    }

    private final ModelPart head;
    private final ModelPart realHead;
    private final ModelPart body;
    private final ModelPart rightHindLeg;
    private final ModelPart leftHindLeg;
    private final ModelPart rightFrontLeg;
    private final ModelPart leftFrontLeg;
    private final ModelPart tail;
    private final ModelPart upperBody;

    public DogModel(ModelPart root)
    {
        super(root);
        this.head = root.getChild("head");
        this.realHead = this.head.getChild("real_head");
        this.body = root.getChild("body");
        this.upperBody = root.getChild("upper_body");
        this.rightHindLeg = root.getChild("right_hind_leg");
        this.leftHindLeg = root.getChild("left_hind_leg");
        this.rightFrontLeg = root.getChild("right_front_leg");
        this.leftFrontLeg = root.getChild("left_front_leg");
        this.tail = root.getChild("tail");
    }

    @Override
    public void prepareMobModel(Dog entity, float limbSwing, float limbSwingAmount, float partialTick)
    {
        tail.yRot = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;

        if (entity.isSitting() || entity.isSleeping())
        {
            upperBody.setPos(-1.0F, 16.0F, -3.0F);
            upperBody.xRot = 1.2566371F;
            upperBody.yRot = 0.0F;
            body.setPos(0.0F, 18.0F, 0.0F);
            body.xRot = (Mth.PI / 4F);
            tail.setPos(-1.0F, 21.0F, 6.0F);
            rightHindLeg.setPos(-2.5F, 22.7F, 2.0F);
            rightHindLeg.xRot = (Mth.PI * 1.5F);
            leftHindLeg.setPos(0.5F, 22.7F, 2.0F);
            leftHindLeg.xRot = (Mth.PI * 1.5F);
            rightFrontLeg.xRot = 5.811947F;
            rightFrontLeg.setPos(-2.49F, 17.0F, -4.0F);
            leftFrontLeg.xRot = 5.811947F;
            leftFrontLeg.setPos(0.51F, 17.0F, -4.0F);

            // tfc
            tail.xRot = entity.isSleeping() ? 0 : Mth.PI / 5f;
        }
        else
        {
            body.setPos(0.0F, 14.0F, 2.0F);
            body.xRot = Mth.HALF_PI;
            upperBody.setPos(-1.0F, 14.0F, -3.0F);
            upperBody.xRot = body.xRot;
            tail.setPos(-1.0F, 12.0F, 8.0F);
            rightHindLeg.setPos(-2.5F, 16.0F, 7.0F);
            leftHindLeg.setPos(0.5F, 16.0F, 7.0F);
            rightFrontLeg.setPos(-2.5F, 16.0F, -4.0F);
            leftFrontLeg.setPos(0.5F, 16.0F, -4.0F);
            rightHindLeg.xRot = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
            leftHindLeg.xRot = Mth.cos(limbSwing * 0.6662F + Mth.PI) * 1.4F * limbSwingAmount;
            rightFrontLeg.xRot = Mth.cos(limbSwing * 0.6662F + Mth.PI) * 1.4F * limbSwingAmount;
            leftFrontLeg.xRot = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
        }

        realHead.zRot = entity.getHeadRollAngle(partialTick);
    }

    @Override
    public void setupAnim(Dog entity, float limbSwing, float limbSwingAmount, float ageInTicks, float yaw, float pitch)
    {
        this.head.xRot = pitch * Constants.DEG_TO_RAD;
        this.head.yRot = yaw * Constants.DEG_TO_RAD;
    }
}
