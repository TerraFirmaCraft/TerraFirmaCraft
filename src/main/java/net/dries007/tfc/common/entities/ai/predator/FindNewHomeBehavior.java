/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities.ai.predator;

import java.util.function.Predicate;
import com.google.common.collect.ImmutableMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.BlockPosTracker;
import net.minecraft.world.entity.ai.behavior.OneShot;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.phys.Vec3;

import net.dries007.tfc.common.entities.predator.Predator;

public class FindNewHomeBehavior
{
    public static OneShot<Predator> create()
    {
        return create(s -> true);
    }

    public static OneShot<Predator> create(Predicate<Predator> predicate)
    {
        return BehaviorBuilder.triggerIf(predator -> PredatorAi.getDistanceFromHomeSqr(predator) > PredatorAi.MAX_WANDER_DISTANCE && predicate.test(predator), BehaviorBuilder.create(instance -> {
            return instance.group(
                instance.present(MemoryModuleType.HOME),
                instance.registered(MemoryModuleType.WALK_TARGET)
            ).apply(instance, (homeMemory, walkMemory) -> {
                return (level, predator, time) -> {
                    Vec3 found = LandRandomPos.getPos(predator, 10, 5);
                    if (found != null)
                    {
                        homeMemory.set(GlobalPos.of(level.dimension(), BlockPos.containing(found)));
                        homeMemory.set(GlobalPos.of(level.dimension(), BlockPos.containing(found)));
                        return true;
                    }
                    return false;
                };
            });
        }));
    }
}
