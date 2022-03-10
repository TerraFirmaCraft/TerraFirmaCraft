/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities.ai.predator;

import java.util.Optional;

import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.schedule.Activity;

import net.dries007.tfc.common.entities.predator.Predator;

public class TickScheduleAndWakeBehavior extends Behavior<Predator>
{
    public TickScheduleAndWakeBehavior()
    {
        super(ImmutableMap.of());
    }

    @Override
    protected void start(ServerLevel level, Predator predator, long time)
    {
        Optional<Activity> before = predator.getBrain().getActiveNonCoreActivity();
        predator.getBrain().updateActivityFromSchedule(level.getDayTime(), level.getGameTime());
        Optional<Activity> after = predator.getBrain().getActiveNonCoreActivity();
        if (before.isPresent() && after.isPresent() && before.get() == Activity.REST && after.get() != Activity.REST)
        {
            predator.setSleeping(false);
        }
    }
}
