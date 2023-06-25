package net.dries007.tfc.common.entities.ai.pet;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.behavior.MoveToTargetSink;

public class MoveToTargetSinkIfNotSleeping extends MoveToTargetSink
{
    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, Mob mob)
    {
        return super.checkExtraStartConditions(level, mob) && !mob.isSleeping();
    }
}
