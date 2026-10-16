/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.model.entity;

import net.minecraft.client.animation.KeyframeAnimations;
import net.minecraft.client.model.ParrotModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.animal.Parrot;
import org.joml.Vector3f;

import net.dries007.tfc.client.animation.ParrotAnimation;
import net.dries007.tfc.common.entities.prey.TFCParrot;

public class TFCParrotModel extends ParrotModel
{
    private static final Vector3f ANIMATION_VECTOR_CACHE = new Vector3f();

    private float partialTick;

    public TFCParrotModel(ModelPart root)
    {
        super(root);
    }

    @Override
    public void prepareMobModel(Parrot entity, float limbSwing, float limbSwingAmount, float partialTick)
    {
        // Vanilla only sets the parts it animates, so reset everything the idle animations may have offset last frame
        root().getAllParts().forEach(ModelPart::resetPose);
        super.prepareMobModel(entity, limbSwing, limbSwingAmount, partialTick);
        this.partialTick = partialTick;
    }

    @Override
    public void setupAnim(Parrot entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch)
    {
        super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        if (entity instanceof TFCParrot parrot)
        {
            // The renderer passes the wing flap as ageInTicks, so animations are timed from tickCount instead
            parrot.idleAnimationState.updateTime(parrot.tickCount + partialTick, 1f);
            final float weight = parrot.getIdleAnimationWeight(partialTick);
            if (weight > 0f)
            {
                parrot.idleAnimationState.ifStarted(state -> KeyframeAnimations.animate(this, ParrotAnimation.get(parrot.getIdleAnimation()), state.getAccumulatedTime(), weight, ANIMATION_VECTOR_CACHE));
            }
        }
    }
}