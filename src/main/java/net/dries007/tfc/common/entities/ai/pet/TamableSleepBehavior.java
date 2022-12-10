/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities.ai.pet;

import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;

import net.dries007.tfc.common.entities.ai.TFCBrain;
import net.dries007.tfc.common.entities.livestock.pet.TamableMammal;
import net.dries007.tfc.util.calendar.Calendars;

public class TamableSleepBehavior extends MoveOntoBlockBehavior<TamableMammal>
{
    public TamableSleepBehavior()
    {
        super(TFCBrain.SLEEP_POS.get(), false);
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, TamableMammal animal)
    {
        return !animal.isSleeping() && !animal.isInWaterOrBubble();
    }

    @Override
    protected void afterReached(TamableMammal mob)
    {
        mob.setSleeping(true);
        mob.getBrain().setMemory(MemoryModuleType.LAST_SLEPT, Calendars.SERVER.getTicks());
        mob.getBrain().eraseMemory(MemoryModuleType.LOOK_TARGET);
        mob.getBrain().eraseMemory(MemoryModuleType.WALK_TARGET);
    }

    @Override
    protected Optional<BlockPos> getNearestTarget(TamableMammal mob)
    {
        return mob.getBrain().getMemory(TFCBrain.SLEEP_POS.get()).map(GlobalPos::pos);
    }

    @Override
    protected boolean isTargetAt(ServerLevel level, BlockPos pos)
    {
        return true;
    }

    @Override
    protected boolean onTarget(ServerLevel level, TamableMammal mob)
    {
        return targetPos != null && targetPos.closerThan(mob.blockPosition(), 1);
    }
}
