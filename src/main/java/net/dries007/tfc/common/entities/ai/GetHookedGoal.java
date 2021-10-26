/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities.ai;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.phys.AABB;

import net.dries007.tfc.common.entities.TFCFishingHook;

public class GetHookedGoal extends MoveToBlockGoal
{
    @Nullable
    private TFCFishingHook hook;

    public GetHookedGoal(PathfinderMob mob)
    {
        super(mob, 0.9, 16);
        hook = null;
    }

    @Override
    public void tick()
    {
        if (isReachedTarget() && hook != null)
        {
            hook.setHookedEntity(mob);
            mob.level.playSound(null, mob.blockPosition(), SoundEvents.FISHING_BOBBER_SPLASH, SoundSource.NEUTRAL, 1.0F + mob.getRandom().nextFloat(), mob.getRandom().nextFloat() + 0.7F + 0.3F);
        }
        super.tick();
    }

    @Override
    public boolean canContinueToUse()
    {
        return hook != null && !hook.isRemoved() && hook.getPlayerOwner() != null && hook.currentState != FishingHook.FishHookState.HOOKED_IN_ENTITY && super.canContinueToUse();
    }

    @Override
    protected boolean isValidTarget(LevelReader level, BlockPos pos)
    {
        return level.getFluidState(pos).is(FluidTags.WATER);
    }

    @Override
    protected BlockPos getMoveToTarget()
    {
        return blockPos;
    }

    @Override
    protected boolean findNearestBlock()
    {
        List<TFCFishingHook> entities = mob.level.getEntitiesOfClass(TFCFishingHook.class, new AABB(mob.blockPosition().offset(-16, -16, -16), mob.blockPosition().offset(16, 16, 16)), hook -> !hook.isRemoved());
        if (!entities.isEmpty())
        {
            hook = entities.get(0);
            blockPos = hook.blockPosition();
            return true;
        }
        return false;
    }

}
