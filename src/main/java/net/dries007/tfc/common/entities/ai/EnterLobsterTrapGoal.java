/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities.ai;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.common.TFCPoiTypes;
import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blockentities.LobsterTrapBlockEntity;
import net.dries007.tfc.common.blockentities.TFCBlockEntities;
import net.dries007.tfc.common.blocks.LobsterTrapBlock;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.util.Helpers;

/**
 * Seeks out nearby baited lobster traps, via {@link TFCPoiTypes#BAITED_LOBSTER_TRAP}, and is caught upon getting close enough to one.
 */
public class EnterLobsterTrapGoal extends MoveToBlockGoal
{
    private static final int SEARCH_RANGE = 16;

    public EnterLobsterTrapGoal(PathfinderMob mob)
    {
        super(mob, 1.0, SEARCH_RANGE);
    }

    @Override
    public boolean canUse()
    {
        return Helpers.isEntity(mob, TFCTags.Entities.CAUGHT_IN_LOBSTER_TRAPS) && super.canUse();
    }

    @Override
    public void tick()
    {
        super.tick();
        if (isReachedTarget() && mob.level() instanceof ServerLevel level)
        {
            level.getBlockEntity(blockPos, TFCBlockEntities.LOBSTER_TRAP.get())
                .filter(LobsterTrapBlockEntity::canCatch)
                .ifPresent(trap -> {
                    Helpers.playSound(level, blockPos, SoundEvents.IRON_DOOR_CLOSE);
                    trap.catchEntity(level, mob);
                });
        }
    }

    @Override
    protected boolean findNearestBlock()
    {
        if (mob.level() instanceof ServerLevel level)
        {
            return level.getPoiManager()
                .findClosest(type -> type.is(TFCPoiTypes.BAITED_LOBSTER_TRAP.getKey()), mob.blockPosition(), SEARCH_RANGE, PoiManager.Occupancy.ANY)
                .map(pos -> {
                    blockPos = pos;
                    return true;
                })
                .orElse(false);
        }
        return false;
    }

    @Override
    protected boolean isValidTarget(LevelReader level, BlockPos pos)
    {
        final BlockState state = level.getBlockState(pos);
        return Helpers.isBlock(state, TFCBlocks.LOBSTER_TRAP.get()) && state.getValue(LobsterTrapBlock.TRAP) == LobsterTrapBlock.TrapState.BAITED;
    }

    @Override
    protected BlockPos getMoveToTarget()
    {
        return blockPos;
    }

    @Override
    public double acceptedDistance()
    {
        return 1.8;
    }
}
