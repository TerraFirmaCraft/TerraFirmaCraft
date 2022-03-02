package net.dries007.tfc.common.entities.ai.predator;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.behavior.StopAttackingIfTargetInvalid;

import net.dries007.tfc.common.entities.predator.Predator;

public class PredatorStopAttackingBehavior extends StopAttackingIfTargetInvalid<Predator>
{
    @Override
    protected void start(ServerLevel level, Predator predator, long time)
    {
        if (PredatorAi.getDistanceFromHome(predator) > PredatorAi.MAX_ATTACK_DISTANCE)
        {
            clearAttackTarget(predator);
        }
        else
        {
            super.start(level, predator, time);
        }
    }
}
