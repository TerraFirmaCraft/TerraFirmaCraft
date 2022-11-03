/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.model.entity;

import net.minecraft.client.model.geom.ModelPart;

import net.dries007.tfc.client.model.animation.AnimationDefinition;
import net.dries007.tfc.common.entities.predator.FelinePredator;

public class FelinePredatorModel<E extends FelinePredator> extends HierarchicalAnimatedModel<E>
{
    private final AnimationDefinition sleep;
    private final AnimationDefinition walk;
    private final AnimationDefinition run;
    private final AnimationDefinition attack;

    public FelinePredatorModel(ModelPart root, AnimationDefinition sleep, AnimationDefinition walk, AnimationDefinition run, AnimationDefinition attack)
    {
        super(root);
        this.sleep = sleep;
        this.walk = walk;
        this.run = run;
        this.attack = attack;
    }

    @Override
    public void setupAnim(E predator, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch)
    {
        super.setupAnim(predator, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);

        if (predator.sleepingAnimation.isStarted())
        {
            setupSleeping();
            this.animate(predator.sleepingAnimation, sleep, ageInTicks);
        }
        else
        {
            final float speed = getAdjustedLandSpeed(predator);
            // swimming is animated as walking. animations can be swapped with no consequences!
            this.animate(predator.swimmingAnimation, walk, ageInTicks, speed);
            this.animate(predator.walkingAnimation, walk, ageInTicks, speed);
            this.animate(predator.runningAnimation, run, ageInTicks, speed);
            this.animate(predator.attackingAnimation, attack, ageInTicks, speed);
            setupHeadRotations(netHeadYaw, headPitch);
        }
    }

    public void setupHeadRotations(float yaw, float pitch)
    {

    }

    public void setupSleeping()
    {

    }
}
