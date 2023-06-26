/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities.ai.predator;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.EntityTracker;
import net.minecraft.world.entity.ai.behavior.OneShot;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.WalkTarget;

import net.dries007.tfc.common.entities.predator.Predator;

public class ListenToAlphaBehavior
{
    public static OneShot<Predator> create()
    {
        return BehaviorBuilder.triggerIf(PackPredatorAi::isNotAlpha, BehaviorBuilder.create(instance -> instance.group(
            instance.registered(MemoryModuleType.WALK_TARGET),
            instance.registered(MemoryModuleType.HOME),
            instance.absent(MemoryModuleType.ATTACK_TARGET)
        ).apply(instance, (walk, home, attackTarget) -> (level, predator, time) -> {
            final LivingEntity alpha = PackPredatorAi.getAlpha(predator);
            final var alphaBrain = alpha.getBrain();

            alphaBrain.getMemory(MemoryModuleType.HOME).ifPresent(home::set);

            if (!alpha.equals(predator))
            {
                walk.set(new WalkTarget(new EntityTracker(alpha,  false), 1.1f, 6));
            }
            return true;
            })
        ));
    }

}
