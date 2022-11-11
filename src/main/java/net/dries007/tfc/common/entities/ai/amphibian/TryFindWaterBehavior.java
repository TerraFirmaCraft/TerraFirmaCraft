/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities.ai.amphibian;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.behavior.TryFindWater;
import net.minecraft.world.level.block.state.BlockState;

import net.dries007.tfc.util.Helpers;

public class TryFindWaterBehavior extends TryFindWater
{
    private final int range;
    private final float speedModifier;
    private long nextStartTick;

    public TryFindWaterBehavior(int range, float speedModifier)
    {
        super(range, speedModifier);
        this.range = range;
        this.speedModifier = speedModifier;
    }

    @Override
    protected void stop(ServerLevel level, PathfinderMob entity, long gameTime)
    {
        this.nextStartTick = gameTime + 20L + 2L;
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, PathfinderMob entity)
    {
        return !entity.level.getFluidState(entity.blockPosition()).is(FluidTags.WATER);
    }

    @Override
    protected void start(ServerLevel level, PathfinderMob entity, long gameTime)
    {
        if (gameTime >= this.nextStartTick)
        {
            BlockPos waterPos = null;
            BlockPos notPreferredWaterPos = null;
            BlockPos entityPos = entity.blockPosition();

            for (BlockPos pos : BlockPos.withinManhattan(entityPos, this.range, this.range, this.range))
            {
                if (pos.getX() != entityPos.getX() || pos.getZ() != entityPos.getZ())
                {
                    final BlockState aboveState = entity.level.getBlockState(pos.above());
                    final BlockState state = entity.level.getBlockState(pos);
                    if (Helpers.isFluid(state.getFluidState(), FluidTags.WATER))
                    {
                        if (aboveState.isAir())
                        {
                            waterPos = pos.immutable();
                            break;
                        }

                        if (notPreferredWaterPos == null && !pos.closerToCenterThan(entity.position(), 1.5D))
                        {
                            notPreferredWaterPos = pos.immutable();
                        }
                    }
                }
            }

            if (waterPos == null)
            {
                waterPos = notPreferredWaterPos;
            }
            if (waterPos != null)
            {
                this.nextStartTick = gameTime + 40L;
                BehaviorUtils.setWalkAndLookTargetMemories(entity, waterPos, this.speedModifier, 0);
            }
        }
    }
}
