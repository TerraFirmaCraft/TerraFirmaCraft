/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities.ai.prey;

import com.google.common.collect.ImmutableMap;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.entities.prey.Prey;
import net.dries007.tfc.util.Helpers;

public class AvoidPredatorBehavior extends Behavior<Prey>
{
    public AvoidPredatorBehavior()
    {
        super(ImmutableMap.of(MemoryModuleType.AVOID_TARGET, MemoryStatus.VALUE_ABSENT));
    }

    @Override
    protected void start(ServerLevel level, Prey prey, long time)
    {
        Brain<Prey> brain = prey.getBrain();
        brain.getMemory(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES)
            .flatMap(entities -> entities.findClosest(e -> Helpers.isEntity(e, TFCTags.Entities.HUNTS_LAND_PREY) && EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(e)))
            .ifPresent(entity -> PreyAi.setAvoidTarget(prey, entity)
            );
    }
}
