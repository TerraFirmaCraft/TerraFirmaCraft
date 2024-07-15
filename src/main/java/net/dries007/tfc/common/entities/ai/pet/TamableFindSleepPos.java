/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities.ai.pet;

import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.behavior.OneShot;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.Vec3;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.entities.ai.TFCBrain;
import net.dries007.tfc.util.Helpers;

public class TamableFindSleepPos
{
    public static OneShot<PathfinderMob> create()
    {
        return BehaviorBuilder.create(instance -> instance.group(
            instance.present(MemoryModuleType.HOME),
            instance.absent(MemoryModuleType.AVOID_TARGET),
            instance.registered(MemoryModuleType.WALK_TARGET),
            instance.registered(TFCBrain.SLEEP_POS.get())
        ).apply(instance, (home, avoid, walk, sleep) -> (level, entity, time) -> {

            final GlobalPos globalPos = instance.tryGet(sleep).orElse(GlobalPos.of(level.dimension(), entity.blockPosition()));
            final BlockPos homePos = globalPos.pos();
            if (globalPos.dimension() != level.dimension() || !wantsToSitAt(level, homePos))
            {
                final Vec3 newPos = LandRandomPos.getPos(entity, 12, 5, pos -> wantsToSitAt(level, pos) ? entity.getWalkTargetValue(pos) : -10d);
                if (newPos != null)
                {
                    sleep.set(GlobalPos.of(level.dimension(), BlockPos.containing(newPos)));
                    return true;
                }
            }
            if (level.getBlockState(globalPos.pos()).getFluidState().isEmpty())
            {
                sleep.set(globalPos);
                return true;
            }
            return false;
        }));
    }

    private static boolean wantsToSitAt(ServerLevel level, BlockPos pos)
    {
        BlockState state = level.getBlockState(pos);
        return Helpers.isBlock(state, TFCTags.Blocks.PET_SITS_ON) && state.isPathfindable(PathComputationType.LAND) && level.getBlockState(pos.above()).isAir();
    }
}
