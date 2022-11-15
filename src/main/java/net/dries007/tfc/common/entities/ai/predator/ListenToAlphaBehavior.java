/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities.ai.predator;

import java.util.Map;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.EntityTracker;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;

import net.dries007.tfc.common.entities.predator.Predator;

public class ListenToAlphaBehavior extends Behavior<Predator>
{
    public ListenToAlphaBehavior()
    {
        super(Map.of(MemoryModuleType.WALK_TARGET, MemoryStatus.REGISTERED, MemoryModuleType.HOME, MemoryStatus.REGISTERED));
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, Predator predator)
    {
        return PackPredatorAi.isNotAlpha(predator);
    }

    @Override
    protected void start(ServerLevel level, Predator entity, long gameTime)
    {
        final var brain = entity.getBrain();
        final LivingEntity alpha = PackPredatorAi.getAlpha(entity);
        final var alphaBrain = alpha.getBrain();

        brain.setMemory(MemoryModuleType.HOME, alphaBrain.getMemory(MemoryModuleType.HOME));

        if (!alpha.equals(entity) && !brain.hasMemoryValue(MemoryModuleType.ATTACK_TARGET) && !brain.hasMemoryValue(MemoryModuleType.WALK_TARGET))
        {
            brain.setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(new EntityTracker(alpha,  false), 1.1f, 6));
        }
    }
}
