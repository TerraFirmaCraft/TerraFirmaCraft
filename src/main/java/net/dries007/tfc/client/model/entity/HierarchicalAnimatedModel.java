/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.model.entity;


import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.Entity;

public abstract class HierarchicalAnimatedModel<E extends Entity> extends HierarchicalModel<E>
{
    private final ModelPart root;

    public HierarchicalAnimatedModel(ModelPart root)
    {
        this.root = root;
    }

    public float getAdjustedLandSpeed(E entity)
    {
        return getAdjustedLandSpeed(entity, 80f, 8);
    }

    public float getAdjustedLandSpeed(E entity, float scale, float max)
    {
        return Math.min((float) entity.getDeltaMovement().lengthSqr() * scale, max);
    }

    @Override
    public void setupAnim(E entity, float limbSwing, float limbSwingAmount, float ageInTicks, float headYaw, float headPitch)
    {
        this.root().getAllParts().forEach(ModelPart::resetPose);
    }

    @Override
    public ModelPart root()
    {
        return root;
    }

}
