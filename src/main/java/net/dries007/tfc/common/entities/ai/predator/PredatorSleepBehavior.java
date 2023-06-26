/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities.ai.predator;

import net.minecraft.world.entity.ai.behavior.OneShot;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;

import net.dries007.tfc.common.entities.predator.Predator;

public class PredatorSleepBehavior
{
    public static OneShot<Predator> create()
    {
        return BehaviorBuilder.triggerIf(entity -> PredatorAi.getDistanceFromHomeSqr(entity) < 25 && !entity.isSleeping() && !entity.isInWaterOrBubble(),
            BehaviorBuilder.create(instance -> {
                return instance.group(
                    instance.absent(MemoryModuleType.ATTACK_TARGET),
                    instance.registered(MemoryModuleType.WALK_TARGET),
                    instance.registered(MemoryModuleType.LOOK_TARGET)
                ).apply(instance, (attack, walk, look) -> {
                    return (level, predator, time) -> {
                        walk.erase();
                        look.erase();
                        predator.setSleeping(true);
                        return true;
                    };
                });
            })
        );
    }
}
