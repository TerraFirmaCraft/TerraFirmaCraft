/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.model.entity;

import java.util.Map;

import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.world.entity.Entity;

import net.dries007.tfc.client.model.animation.VanillaAnimations;

public abstract class HierarchicalAnimatedModel<E extends Entity> extends HierarchicalModel<E>
{
    public final Map<ModelPart, PartPose> defaults;
    private final ModelPart root;

    public HierarchicalAnimatedModel(ModelPart root)
    {
        this.root = root;
        this.defaults = VanillaAnimations.save(root.getAllParts());
    }

    @Override
    public void setupAnim(E entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch)
    {
        defaults.forEach(ModelPart::loadPose);
    }

    public float getAdjustedLandSpeed(E entity)
    {
        return getAdjustedLandSpeed(entity, 80f, 8);
    }

    public float getAdjustedLandSpeed(E entity, float scale, float min)
    {
        return Math.min((float) entity.getDeltaMovement().lengthSqr() * scale, min);
    }

    @Override
    public ModelPart root()
    {
        return root;
    }

}
