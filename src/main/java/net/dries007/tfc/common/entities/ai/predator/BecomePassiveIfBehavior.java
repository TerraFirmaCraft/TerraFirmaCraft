/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities.ai.predator;

import java.util.function.Predicate;

import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;

import net.dries007.tfc.common.entities.predator.Predator;

public class BecomePassiveIfBehavior extends Behavior<Predator>
{
    private final Predicate<Predator> predicate;
    private final int pacifiedTicks;

    public BecomePassiveIfBehavior(Predicate<Predator> predicate, int pacifiedTicks)
    {
        super(ImmutableMap.of(MemoryModuleType.ATTACK_TARGET, MemoryStatus.REGISTERED, MemoryModuleType.PACIFIED, MemoryStatus.VALUE_ABSENT));
        this.predicate = predicate;
        this.pacifiedTicks = pacifiedTicks;
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, Predator predator)
    {
        return super.checkExtraStartConditions(level, predator) && predicate.test(predator);
    }

    @Override
    public void start(ServerLevel level, Predator predator, long time)
    {
        predator.getBrain().setMemoryWithExpiry(MemoryModuleType.PACIFIED, true, pacifiedTicks);
        predator.getBrain().eraseMemory(MemoryModuleType.ATTACK_TARGET);
    }
}
