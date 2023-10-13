/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities.ai;

import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.tags.TagKey;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.ai.behavior.EntityTracker;
import net.minecraft.world.entity.ai.behavior.SetEntityLookTargetSometimes;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;

import net.dries007.tfc.util.Helpers;

public class SetLookTarget
{
    public static BehaviorControl<LivingEntity> create(float distance, UniformInt interval)
    {
        return create(distance, interval, p -> true);
    }

    public static BehaviorControl<LivingEntity> create(EntityType<?> type, float distance, UniformInt interval)
    {
        return create(distance, interval, e -> type.equals(e.getType()));
    }

    public static BehaviorControl<LivingEntity> create(TagKey<EntityType<?>> tag, float distance, UniformInt interval)
    {
        return create(distance, interval, e -> Helpers.isEntity(e, tag));
    }

    @SuppressWarnings("deprecation")
    public static BehaviorControl<LivingEntity> create(float distance, UniformInt interval, Predicate<LivingEntity> predicate)
    {
        float f = distance * distance;
        SetEntityLookTargetSometimes.Ticker setLookTargetTicker = new SetEntityLookTargetSometimes.Ticker(interval);
        return BehaviorBuilder.create((instance) -> instance.group(
            instance.absent(MemoryModuleType.LOOK_TARGET), instance.present(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES))
            .apply(instance, (posTracker, nearVisible) -> (server, entity, time) -> {
                Optional<LivingEntity> optional = instance.get(nearVisible).findClosest(predicate.and((e) -> e.distanceToSqr(entity) <= f));
                if (optional.isEmpty())
                {
                    return false;
                }
                else if (!setLookTargetTicker.tickDownAndCheck(server.random))
                {
                    return false;
                }
                else
                {
                    posTracker.set(new EntityTracker(optional.get(), true));
                    return true;
                }
            }));
    }
}
