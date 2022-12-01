/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities.ai.pet;

import com.google.common.collect.ImmutableMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.Vec3;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.entities.ai.TFCBrain;
import net.dries007.tfc.util.Helpers;

public class TamableFindSleepPos extends Behavior<PathfinderMob>
{

    public TamableFindSleepPos()
    {
        super(ImmutableMap.of(MemoryModuleType.HOME, MemoryStatus.VALUE_PRESENT, MemoryModuleType.AVOID_TARGET, MemoryStatus.VALUE_ABSENT, MemoryModuleType.WALK_TARGET, MemoryStatus.REGISTERED, TFCBrain.SLEEP_POS.get(), MemoryStatus.REGISTERED));
    }

    @Override
    protected void start(ServerLevel level, PathfinderMob entity, long gameTime)
    {
        final Brain<?> brain = entity.getBrain();
        final GlobalPos globalPos = brain.getMemory(TFCBrain.SLEEP_POS.get()).orElse(GlobalPos.of(level.dimension(), entity.blockPosition()));
        final BlockPos homePos = globalPos.pos();
        if (globalPos.dimension() != level.dimension() || !wantsToSitAt(level, homePos))
        {
            final Vec3 newPos = LandRandomPos.getPos(entity, 12, 5, pos -> wantsToSitAt(level, pos) ? entity.getWalkTargetValue(pos) : -10d);
            if (newPos != null)
            {
                brain.setMemory(TFCBrain.SLEEP_POS.get(), GlobalPos.of(level.dimension(), new BlockPos(newPos)));
            }
        }
        if (level.getBlockState(globalPos.pos()).getFluidState().isEmpty())
        {
            brain.setMemory(TFCBrain.SLEEP_POS.get(), globalPos);
        }
    }

    private boolean wantsToSitAt(ServerLevel level, BlockPos pos)
    {
        BlockState state = level.getBlockState(pos);
        return Helpers.isBlock(state, TFCTags.Blocks.PET_SITS_ON) && state.isPathfindable(level, pos, PathComputationType.LAND) && level.getBlockState(pos.above()).isAir();
    }
}
