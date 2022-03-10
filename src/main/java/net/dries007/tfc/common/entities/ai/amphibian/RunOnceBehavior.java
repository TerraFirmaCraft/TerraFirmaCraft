/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities.ai.amphibian;

import java.util.function.Consumer;

import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;

public class RunOnceBehavior<E extends LivingEntity> extends Behavior<E>
{
    private final Consumer<E> onStart;

    public RunOnceBehavior(Consumer<E> onStart)
    {
        super(ImmutableMap.of());
        this.onStart = onStart;
    }

    public RunOnceBehavior(MemoryModuleType<?> requiredMemory, Consumer<E> onStart)
    {
        super(ImmutableMap.of(requiredMemory, MemoryStatus.VALUE_PRESENT));
        this.onStart = onStart;
    }

    @Override
    protected void start(ServerLevel level, E entity, long time)
    {
        onStart.accept(entity);
    }
}
