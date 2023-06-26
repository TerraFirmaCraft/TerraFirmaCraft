/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities.ai.pet;

import net.minecraft.world.entity.ai.behavior.EntityTracker;
import net.minecraft.world.entity.ai.behavior.OneShot;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.WalkTarget;

import net.dries007.tfc.common.entities.livestock.pet.TamableMammal;

public class FollowOwnerBehavior
{
    public static OneShot<TamableMammal> create()
    {
        return BehaviorBuilder.triggerIf(e -> e.getOwner() != null, BehaviorBuilder.create(instance -> instance.group(
            instance.absent(MemoryModuleType.WALK_TARGET),
            instance.absent(MemoryModuleType.ATTACK_TARGET)
        ).apply(instance, (walk, attack) -> (level, pet, time) -> {
            if (pet.getOwner() != null)
            {
                walk.set(new WalkTarget(new EntityTracker(pet.getOwner(), false), 1.1f, 5));
                return true;
            }
            return false;
        })));
    }
}
