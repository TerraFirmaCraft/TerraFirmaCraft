/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities.aquatic;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

import net.dries007.tfc.common.entities.AnimationState;
import net.dries007.tfc.common.entities.EntityHelpers;

public class Penguin extends AmphibiousAnimal
{
    public final AnimationState walkingAnimation = new AnimationState();
    public final AnimationState swimmingAnimation = new AnimationState();

    public Penguin(EntityType<? extends AmphibiousAnimal> type, Level level)
    {
        super(type, level);
    }

    @Override
    public boolean isPlayingDeadEffective()
    {
        return false;
    }

    @Override
    public void tick()
    {
        if (level.isClientSide)
        {
            if (EntityHelpers.isMovingOnLand(this))
            {
                walkingAnimation.startIfStopped(tickCount);
            }
            else
            {
                walkingAnimation.stop();
            }
            if (EntityHelpers.isMovingInWater(this))
            {
                swimmingAnimation.startIfStopped(tickCount);
            }
            else
            {
                swimmingAnimation.stop();
            }
        }
        super.tick();
    }


}
