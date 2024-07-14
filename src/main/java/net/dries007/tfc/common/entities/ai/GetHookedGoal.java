/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities.ai;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.component.BaitType;
import net.dries007.tfc.common.entities.misc.TFCFishingHook;
import net.dries007.tfc.util.Helpers;

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
            mob.level().playSound(null, mob.blockPosition(), SoundEvents.FISHING_BOBBER_SPLASH, SoundSource.NEUTRAL, 1.0F + mob.getRandom().nextFloat(), mob.getRandom().nextFloat() + 0.7F + 0.3F);
            // delete the bait. Large mobs always eat the bait, Small mobs only sometimes
            if (Helpers.isEntity(mob, TFCTags.Entities.NEEDS_LARGE_FISHING_BAIT) || mob.getRandom().nextInt(12) == 0)
            {
                hook.eatBait();
            }
        }
        super.tick();
    }

    @Override
    public boolean canContinueToUse()
    {
        return hook != null && !hook.isRemoved() && hook.getPlayerOwner() != null && hook.getHookedIn() == null && super.canContinueToUse();
    }

    @Override
    protected boolean isValidTarget(LevelReader level, BlockPos pos)
    {
        return Helpers.isFluid(level.getFluidState(pos), TFCTags.Fluids.WATER_LIKE);
    }

    @Override
    protected BlockPos getMoveToTarget()
    {
        return blockPos;
    }

    @Override
    protected boolean findNearestBlock()
    {
        List<TFCFishingHook> entities = mob.level().getEntitiesOfClass(TFCFishingHook.class, new AABB(mob.blockPosition()).inflate(12), hook -> !hook.isRemoved());
        if (!entities.isEmpty())
        {
            final TFCFishingHook possibleHook = entities.get(0);
            final ItemStack bait = possibleHook.getBait();
            final BaitType type = BaitType.getType(bait);
            final boolean isLarge = Helpers.isEntity(mob, TFCTags.Entities.NEEDS_LARGE_FISHING_BAIT);
            if ((type == BaitType.LARGE && isLarge) || (type == BaitType.SMALL && !isLarge))
            {
                hook = possibleHook;
                blockPos = hook.blockPosition();
                return true;
            }
        }
        return false;
    }

}
