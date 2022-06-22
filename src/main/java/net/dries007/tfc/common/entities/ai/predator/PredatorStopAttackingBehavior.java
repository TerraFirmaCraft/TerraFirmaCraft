/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities.ai.predator;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.StopAttackingIfTargetInvalid;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;

import net.dries007.tfc.common.entities.predator.Predator;

public class PredatorStopAttackingBehavior extends StopAttackingIfTargetInvalid<Predator>
{
    @Override
    protected void start(ServerLevel level, Predator predator, long time)
    {
        if (getAttackTarget(predator).isDeadOrDying())
        {
            Brain<Predator> brain = predator.getBrain();
            brain.setMemoryWithExpiry(MemoryModuleType.HUNTED_RECENTLY, true, 12000);
            if (brain.hasMemoryValue(MemoryModuleType.HURT_BY_ENTITY))
            {
                brain.eraseMemory(MemoryModuleType.HURT_BY_ENTITY);
            }
            clearAttackTarget(predator);
        }
        else if (PredatorAi.getDistanceFromHomeSqr(predator) > PredatorAi.MAX_ATTACK_DISTANCE)
        {
            clearAttackTarget(predator);
        }
        else
        {
            super.start(level, predator, time);
        }
    }

    private LivingEntity getAttackTarget(Predator predator)
    {
        return predator.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).get();
    }
}
