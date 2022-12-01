/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.model.entity;

import net.minecraft.client.model.ModelUtils;
import net.minecraft.client.model.OcelotModel;
import net.minecraft.client.model.geom.ModelPart;

import net.minecraft.util.Mth;

import net.dries007.tfc.common.entities.livestock.pet.TFCCat;

public class TFCCatModel extends OcelotModel<TFCCat>
{
    public TFCCatModel(ModelPart root)
    {
        super(root);
    }

    @Override
    public void prepareMobModel(TFCCat cat, float limbSwing, float limbSwingAmount, float ageInTicks)
    {
        if (cat.isSleeping())
        {
            this.head.xRot = 0.0F;
            this.head.zRot = 0.0F;
            this.leftFrontLeg.xRot = 0.0F;
            this.leftFrontLeg.zRot = 0.0F;
            this.rightFrontLeg.xRot = 0.0F;
            this.rightFrontLeg.zRot = 0.0F;
            this.rightFrontLeg.x = -1.2F;
            this.leftHindLeg.xRot = 0.0F;
            this.rightHindLeg.xRot = 0.0F;
            this.rightHindLeg.zRot = 0.0F;
            this.rightHindLeg.x = -1.1F;
            this.rightHindLeg.y = 18.0F;
        }
        super.prepareMobModel(cat, limbSwing, limbSwingAmount, ageInTicks);
        if (cat.isSitting())
        {
            this.body.xRot = (Mth.PI / 4F);
            this.body.y += -4.0F;
            this.body.z += 5.0F;
            this.head.y += -3.3F;
            ++this.head.z;
            this.tail1.y += 8.0F;
            this.tail1.z += -2.0F;
            this.tail2.y += 2.0F;
            this.tail2.z += -0.8F;
            this.tail1.xRot = 1.7278761F;
            this.tail2.xRot = 2.670354F;
            this.leftFrontLeg.xRot = -0.15707964F;
            this.leftFrontLeg.y = 16.1F;
            this.leftFrontLeg.z = -7.0F;
            this.rightFrontLeg.xRot = -0.15707964F;
            this.rightFrontLeg.y = 16.1F;
            this.rightFrontLeg.z = -7.0F;
            this.leftHindLeg.xRot = (-(float)Math.PI / 2F);
            this.leftHindLeg.y = 21.0F;
            this.leftHindLeg.z = 1.0F;
            this.rightHindLeg.xRot = (-(float)Math.PI / 2F);
            this.rightHindLeg.y = 21.0F;
            this.rightHindLeg.z = 1.0F;
            this.state = 3;
        }
    }

    @Override
    public void setupAnim(TFCCat cat, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch)
    {
        super.setupAnim(cat, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);

        if (cat.isSleeping())
        {
            this.head.zRot = ModelUtils.rotlerpRad(this.head.zRot, -1.2707963F, 1f);
            this.head.yRot = ModelUtils.rotlerpRad(this.head.yRot, 1.2707963F, 1f);
            this.leftFrontLeg.xRot = -1.2707963F;
            this.rightFrontLeg.xRot = -0.47079635F;
            this.rightFrontLeg.zRot = -0.2F;
            this.rightFrontLeg.x = -0.2F;
            this.leftHindLeg.xRot = -0.4F;
            this.rightHindLeg.xRot = 0.5F;
            this.rightHindLeg.zRot = -0.5F;
            this.rightHindLeg.x = -0.3F;
            this.rightHindLeg.y = 20.0F;
            this.tail1.xRot = ModelUtils.rotlerpRad(this.tail1.xRot, 0.8F, 1f - 0.13f);
            this.tail2.xRot = ModelUtils.rotlerpRad(this.tail2.xRot, -0.4F, 1f - 0.13f);

        }
    }
}
