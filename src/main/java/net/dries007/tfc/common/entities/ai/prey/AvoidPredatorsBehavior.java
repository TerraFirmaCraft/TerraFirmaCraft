/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities.ai.prey;

import java.util.function.Predicate;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.behavior.OneShot;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.player.Player;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.util.Helpers;

public class AvoidPredatorsBehavior
{
    public static OneShot<Mob> create(boolean playersExempt)
    {
        final Predicate<Entity> isAvoided = isAvoided(playersExempt);
        return BehaviorBuilder.create(instance -> {
            return instance.group(
                instance.present(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES),
                instance.absent(MemoryModuleType.AVOID_TARGET)
            ).apply(instance, (visible, avoiding) -> {
                return (level, mob, time) -> instance.get(visible).findClosest(
                    isAvoided::test
                ).map(closest -> {
                    avoiding.set(closest);
                    return true;
                }).orElse(false);
            });
        });
    }

    static Predicate<Entity> isAvoided(boolean playersExempt)
    {
        return playersExempt
            ? entity -> !(entity instanceof Player) && Helpers.isEntity(entity, TFCTags.Entities.LAND_PREDATORS)
            : entity -> EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(entity) && (entity instanceof Player || Helpers.isEntity(entity, TFCTags.Entities.LAND_PREDATORS));
    }
}
