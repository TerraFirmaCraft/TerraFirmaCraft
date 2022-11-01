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

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.entities.ai.TFCBrain;
import net.dries007.tfc.common.entities.livestock.pet.TamableMammal;
import net.dries007.tfc.util.Helpers;
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
        return !animal.isSleeping();
    }

    @Override
    protected void afterReached(TamableMammal mob)
    {
        mob.setSleeping(true);
        mob.getBrain().setMemory(MemoryModuleType.LAST_SLEPT, Calendars.SERVER.getTicks());
    }

    @Override
    protected Optional<BlockPos> getNearestTarget(TamableMammal mob)
    {
        return mob.getBrain().getMemory(TFCBrain.SLEEP_POS.get()).map(GlobalPos::pos);
    }

    @Override
    protected boolean isTargetAt(ServerLevel level, BlockPos pos)
    {
        return Helpers.isBlock(level.getBlockState(pos), TFCTags.Blocks.CAT_SITS_ON);
    }

    @Override
    protected boolean onTarget(ServerLevel level, TamableMammal mob)
    {
        return super.onTarget(level, mob) || isTargetAt(level, mob.blockPosition().below());
    }
}
