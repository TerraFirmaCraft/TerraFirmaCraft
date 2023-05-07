/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.model.entity;

import net.minecraft.client.model.ChestedHorseModel;
import net.minecraft.client.model.geom.ModelPart;

import net.dries007.tfc.common.entities.livestock.horse.TFCChestedHorse;

public class TFCChestedHorseModel<T extends TFCChestedHorse> extends ChestedHorseModel<T>
{
    private final ModelPart leftChest = this.body.getChild("left_chest");
    private final ModelPart rightChest = this.body.getChild("right_chest");
    private final boolean isChestVisible;

    public TFCChestedHorseModel(ModelPart root, boolean isChestVisible)
    {
        super(root);
        this.isChestVisible = isChestVisible;
    }

    @Override
    public void setupAnim(T horse, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch)
    {
        super.setupAnim(horse, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        leftChest.visible = isChestVisible;
        rightChest.visible = isChestVisible;
    }
}
