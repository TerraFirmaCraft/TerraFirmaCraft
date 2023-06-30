/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

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
