/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities.ai;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.ai.behavior.GateBehavior;
import org.jetbrains.annotations.Nullable;

/**
 * Naming a class "Fast" makes it faster. However, to explain, this just chooses one ability from several to run, randomly.
 *
 * That said, truly where to begin with {@link GateBehavior}
 * * A shuffling list. Why?
 * * Multiple ticks per tick.
 * * Streams everywhere.
 */
public class FastGateBehavior<E extends LivingEntity> implements BehaviorControl<E>
{
    public static <E extends LivingEntity> FastGateBehavior<E> runOne(List<BehaviorControl<? super E>> behaviors)
    {
        return new FastGateBehavior<>(behaviors);
    }

    private final List<BehaviorControl<? super E>> behaviors;
    @Nullable private BehaviorControl<? super E> current = null;
    private Behavior.Status status = Behavior.Status.STOPPED;

    public FastGateBehavior(List<BehaviorControl<? super E>> behaviors)
    {
        this.behaviors = behaviors;
    }

    @Override
    public Behavior.Status getStatus()
    {
        return status;
    }

    @Override
    public boolean tryStart(ServerLevel level, E entity, long gameTime)
    {
        current = behaviors.get(entity.getRandom().nextInt(behaviors.size()));
        return current.tryStart(level, entity, gameTime);
    }

    @Override
    public void tickOrStop(ServerLevel level, E entity, long gameTime)
    {
        if (current != null && current.getStatus() == Behavior.Status.RUNNING)
        {
            current.tickOrStop(level, entity, gameTime);
        }
        else
        {
            doStop(level, entity, gameTime);
        }
    }

    @Override
    public void doStop(ServerLevel level, E entity, long gameTime)
    {
        status = Behavior.Status.STOPPED;
        if (current != null)
        {
            current.doStop(level, entity, gameTime);

        }
    }

    @Override
    public String debugString()
    {
        return getClass().getSimpleName();
    }

    @Override
    public String toString()
    {
        Set<? extends BehaviorControl<? super E>> set = this.behaviors.stream().filter((b) -> b.getStatus() == Behavior.Status.RUNNING).collect(Collectors.toSet());
        return "(" + this.getClass().getSimpleName() + "): " + set;
    }

}
