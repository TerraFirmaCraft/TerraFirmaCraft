/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.model.entity;

import net.minecraft.client.model.GoatModel;
import net.minecraft.client.model.QuadrupedModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;

import net.dries007.tfc.common.entities.livestock.DairyAnimal;

public class TFCGoatModel extends QuadrupedModel<DairyAnimal>
{
    public static LayerDefinition createBodyLayer()
    {
        return GoatModel.createBodyLayer();
    }

    public TFCGoatModel(ModelPart root)
    {
        super(root, true, 19.0F, 1.0F, 2.5F, 2.0F, 24);
    }

    public void setupAnim(DairyAnimal goat, float limbSwing, float limbSwingAmount, float ageInTicks, float headYaw, float headPitch)
    {
        this.head.getChild("left_horn").visible = !goat.isBaby();
        this.head.getChild("right_horn").visible = !goat.isBaby();
        if (goat.displayFemaleCharacteristics())
        {
            this.head.getChild("left_horn").y = 2;
            this.head.getChild("right_horn").y = 2;
        }
        else
        {
            this.head.getChild("left_horn").y = 0;
            this.head.getChild("right_horn").y = 0;
        }
        super.setupAnim(goat, limbSwing, limbSwingAmount, ageInTicks, headYaw / 3, headPitch);
    }
}

