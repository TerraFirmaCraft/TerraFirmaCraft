/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities.ai.predator;

import java.util.Optional;
import net.minecraft.world.entity.ai.behavior.OneShot;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.schedule.Activity;

import net.dries007.tfc.common.entities.predator.Predator;

public class TickScheduleAndWakeBehavior
{
    public static OneShot<Predator> create()
    {
        return BehaviorBuilder.create(instance -> {
            return instance.group(
                instance.absent(MemoryModuleType.ATTACK_TARGET)
            ).apply(instance, attack -> {
                return (level, predator, time) -> {
                    Optional<Activity> before = predator.getBrain().getActiveNonCoreActivity();
                    predator.getBrain().updateActivityFromSchedule(level.getDayTime(), level.getGameTime());
                    Optional<Activity> after = predator.getBrain().getActiveNonCoreActivity();
                    if (before.isPresent() && after.isPresent() && before.get() == Activity.REST && after.get() != Activity.REST)
                    {
                        predator.setSleeping(false);
                        return true;
                    }
                    return false;
                };
            });
        });
    }
}
