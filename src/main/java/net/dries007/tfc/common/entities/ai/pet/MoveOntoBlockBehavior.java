/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities.ai.pet;

import java.util.Optional;
import com.google.common.collect.ImmutableMap;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import org.jetbrains.annotations.Nullable;

public abstract class MoveOntoBlockBehavior<T extends PathfinderMob> extends Behavior<T>
{
    @Nullable
    protected BlockPos targetPos;
    private int remainingTimeToReach;
    private final MemoryModuleType<?> memory;
    private final boolean erase;

    public MoveOntoBlockBehavior(MemoryModuleType<?> memory, boolean erase)
    {
        super(ImmutableMap.of(memory, MemoryStatus.VALUE_PRESENT));
        this.memory = memory;
        this.erase = erase;
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, T animal)
    {
        return !animal.isPassenger() && !animal.isSleeping();
    }

    @Override
    protected void start(ServerLevel level, T mob, long time)
    {
        getNearestTarget(mob).ifPresent(pos -> {
            targetPos = pos;
            remainingTimeToReach = 100;
            startWalkingTowards(mob, pos);
        });
    }

    @Override
    protected void stop(ServerLevel level, T mob, long time)
    {
        targetPos = null;
        remainingTimeToReach = 0;
    }

    @Override
    protected boolean canStillUse(ServerLevel level, T animal, long time)
    {
        return targetPos != null && checkExtraStartConditions(level, animal) && isTargetAt(level, targetPos) && !tiredOfWalking(level, animal);
    }

    @Override
    protected boolean timedOut(long time)
    {
        return false; // we implement our own stopping function
    }

    @Override
    protected void tick(ServerLevel level, T mob, long time)
    {
        if (!onTarget(level, mob))
        {
            --remainingTimeToReach;
        }
        else
        {
            afterReached(mob);
            mob.getBrain().eraseMemory(MemoryModuleType.WALK_TARGET);
            targetPos = null;
            if (erase) mob.getBrain().eraseMemory(memory);
        }
    }

    protected abstract void afterReached(T mob);

    protected abstract Optional<BlockPos> getNearestTarget(T mob);

    protected abstract boolean isTargetAt(ServerLevel level, BlockPos pos);

    protected boolean onTarget(ServerLevel level, T mob)
    {
        return isTargetAt(level, mob.blockPosition());
    }

    private void startWalkingTowards(T mob, BlockPos pos)
    {
        BehaviorUtils.setWalkAndLookTargetMemories(mob, pos, 1f, 1);
    }

    private boolean tiredOfWalking(ServerLevel level, T mob)
    {
        if (!onTarget(level, mob) && remainingTimeToReach <= 0)
        {
            if (erase) mob.getBrain().eraseMemory(memory);
            return true;
        }
        else
        {
            return false;
        }
    }
}
