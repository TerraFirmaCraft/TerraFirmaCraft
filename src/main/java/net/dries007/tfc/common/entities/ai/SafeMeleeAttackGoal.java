/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities.ai;

import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;

public class SafeMeleeAttackGoal extends MeleeAttackGoal
{
    public SafeMeleeAttackGoal(CreatureEntity creature, double speedIn, boolean useLongMemory)
    {
        super(creature, speedIn, useLongMemory);
    }

    /**
     * The Target is regularly set to null, mainly when the task using it ends.
     * MeleeAttackGoal, as a condition of starting / continuing to use it, requires target to be nonnull.
     *
     * When combined with a goal that sets a target, like NearestAttackableTargetGoal, it can become null.
     * This regularly crashes the game, so we just stop it.
     */
    @Override
    public void tick()
    {
        if (canContinueToUse())
        {
            super.tick();
        }
    }
}
