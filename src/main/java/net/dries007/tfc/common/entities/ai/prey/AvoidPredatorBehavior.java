/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities.ai.prey;

import java.util.function.Predicate;

import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.player.Player;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.util.Helpers;

public class AvoidPredatorBehavior extends Behavior<LivingEntity>
{
    private final Predicate<Entity> extraConditions;

    public AvoidPredatorBehavior(boolean exemptPlayers)
    {
        super(ImmutableMap.of(MemoryModuleType.AVOID_TARGET, MemoryStatus.VALUE_ABSENT));
        extraConditions = exemptPlayers ? entity -> !(entity instanceof Player) : EntitySelector.NO_CREATIVE_OR_SPECTATOR;
    }

    @Override
    protected void start(ServerLevel level, LivingEntity prey, long time)
    {
        prey.getBrain().getMemory(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES)
            .flatMap(entities -> entities.findClosest(e -> Helpers.isEntity(e, TFCTags.Entities.HUNTS_LAND_PREY) && extraConditions.test(e)))
            .ifPresent(entity -> PreyAi.setAvoidTarget(prey, entity)
        );
    }
}
