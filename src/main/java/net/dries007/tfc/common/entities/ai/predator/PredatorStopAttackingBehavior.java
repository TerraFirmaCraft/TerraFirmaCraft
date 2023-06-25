/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities.ai.predator;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.ai.behavior.StopAttackingIfTargetInvalid;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;

public class PredatorStopAttackingBehavior
{
    public static BehaviorControl<Mob> create()
    {
        return StopAttackingIfTargetInvalid.create(
            predator -> PredatorAi.getDistanceFromHomeSqr(predator) > PredatorAi.MAX_ATTACK_DISTANCE,
            (predator, target) -> {
                if (target.isDeadOrDying())
                {
                    final Brain<?> brain = predator.getBrain();
                    brain.setMemoryWithExpiry(MemoryModuleType.HUNTED_RECENTLY, true, 12000);
                    if (brain.hasMemoryValue(MemoryModuleType.HURT_BY_ENTITY))
                    {
                        brain.eraseMemory(MemoryModuleType.HURT_BY_ENTITY);
                    }
                }
            },
            true
        );
    }

}
