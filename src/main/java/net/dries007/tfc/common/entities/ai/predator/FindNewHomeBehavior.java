/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities.ai.predator;

import com.google.common.collect.ImmutableMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.BlockPosTracker;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.phys.Vec3;

import net.dries007.tfc.common.entities.predator.Predator;

public class FindNewHomeBehavior extends Behavior<Predator>
{
    public FindNewHomeBehavior()
    {
        super(ImmutableMap.of(MemoryModuleType.HOME, MemoryStatus.VALUE_PRESENT, MemoryModuleType.WALK_TARGET, MemoryStatus.REGISTERED));
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, Predator predator)
    {
        return PredatorAi.getDistanceFromHomeSqr(predator) > PredatorAi.MAX_WANDER_DISTANCE;
    }

    @Override
    protected void start(ServerLevel level, Predator predator, long time)
    {
        Vec3 found = LandRandomPos.getPos(predator, 10, 5);
        if (found != null)
        {
            Brain<Predator> brain = predator.getBrain();
            BlockPos pos = new BlockPos(found);
            brain.setMemory(MemoryModuleType.HOME, GlobalPos.of(level.dimension(), pos));
            brain.setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(pos, 1.2f, 5));
        }
    }
}
