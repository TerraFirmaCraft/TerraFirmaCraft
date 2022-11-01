/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities.ai.pet;

import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.EntityTracker;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;

import net.dries007.tfc.common.entities.livestock.pet.TamableMammal;

public class FollowOwnerBehavior extends Behavior<TamableMammal>
{
    public FollowOwnerBehavior()
    {
        super(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT));
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, TamableMammal entity)
    {
        return entity.getOwner() != null;
    }

    @Override
    protected void start(ServerLevel level, TamableMammal entity, long gameTime)
    {
        if (entity.getOwner() != null)
        {
            entity.getBrain().setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(new EntityTracker(entity.getOwner(), false), 1.1f, 5));
        }
    }
}
