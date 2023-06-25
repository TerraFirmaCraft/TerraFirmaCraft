/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities.ai.predator;

import java.util.function.Predicate;

import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.OneShot;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;

import net.dries007.tfc.common.entities.predator.Predator;

public class BecomePassiveIfBehavior
{
    public static OneShot<Predator> create(Predicate<Predator> predicate, int ticks)
    {
        return BehaviorBuilder.triggerIf(predicate, BehaviorBuilder.create(instance -> {
            return instance.group(
                instance.registered(MemoryModuleType.ATTACK_TARGET),
                instance.absent(MemoryModuleType.PACIFIED)
            ).apply(instance, (attackMemory, passiveMemory) -> {
                return (level, predator, time) -> {
                    attackMemory.erase();
                    passiveMemory.setWithExpiry(true, ticks);
                    return true;
                };
            });
        }));
    }
}
