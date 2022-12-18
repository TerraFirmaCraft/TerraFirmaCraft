/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities.ai;

import java.util.List;
import java.util.Map;
import java.util.Set;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.GateBehavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.mixin.accessor.BehaviorAccessor;

/**
 * Naming a class "Fast" makes it faster. However, to explain, this just chooses one ability from several to run, randomly.
 *
 * That said, truly where to begin with {@link GateBehavior}
 * * A shuffling list. Why?
 * * Multiple ticks per tick.
 * * Streams everywhere.
 */
public class FastGateBehavior<E extends LivingEntity> extends Behavior<E>
{
    public static <E extends LivingEntity> FastGateBehavior<E> runOne(List<Behavior<? super E>> behaviors)
    {
        return new FastGateBehavior<>(ImmutableMap.of(), ImmutableSet.of(), behaviors);
    }

    private final Set<MemoryModuleType<?>> exitErasedMemories;
    private final List<Behavior<? super E>> behaviors;
    @Nullable private Behavior<? super E> current = null;

    public FastGateBehavior(Map<MemoryModuleType<?>, MemoryStatus> map, Set<MemoryModuleType<?>> exitErasedMemories, List<Behavior<? super E>> behaviors)
    {
        super(map);
        this.exitErasedMemories = exitErasedMemories;
        this.behaviors = behaviors;
    }

    @Override
    public boolean canStillUse(ServerLevel level, E entity, long gameTime)
    {
        return current != null && ((BehaviorAccessor) current).invoke$canStillUse(level, entity, gameTime) && current.getStatus() == Status.RUNNING;
    }

    @Override
    protected boolean timedOut(long gameTime)
    {
        return false;
    }

    @Override
    protected void start(ServerLevel level, E entity, long gameTime)
    {
        current = behaviors.get(entity.getRandom().nextInt(behaviors.size()));
        current.tryStart(level, entity, gameTime);
    }

    @Override
    protected void tick(ServerLevel level, E entity, long gameTime)
    {
        if (current != null)
        {
            current.tickOrStop(level, entity, gameTime);
        }
    }

    @Override
    protected void stop(ServerLevel level, E entity, long gameTime)
    {
        if (current != null)
        {
            current.doStop(level, entity, gameTime);
            current = null;
        }
        this.exitErasedMemories.forEach(entity.getBrain()::eraseMemory);
    }
}
