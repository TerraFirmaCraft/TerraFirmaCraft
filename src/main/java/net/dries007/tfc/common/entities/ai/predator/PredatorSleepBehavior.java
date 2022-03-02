package net.dries007.tfc.common.entities.ai.predator;

import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;

import net.dries007.tfc.common.entities.predator.Predator;

public class PredatorSleepBehavior extends Behavior<Predator>
{
    public PredatorSleepBehavior()
    {
        super(ImmutableMap.of(MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_ABSENT));
    }

    @Override
    protected void start(ServerLevel level, Predator entity, long time)
    {
        entity.getBrain().eraseMemory(MemoryModuleType.WALK_TARGET);
        entity.getBrain().eraseMemory(MemoryModuleType.LOOK_TARGET);
        entity.setSleeping(true);
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, Predator entity)
    {
        return PredatorAi.getDistanceFromHome(entity) < 36 && super.checkExtraStartConditions(level, entity);
    }
}
