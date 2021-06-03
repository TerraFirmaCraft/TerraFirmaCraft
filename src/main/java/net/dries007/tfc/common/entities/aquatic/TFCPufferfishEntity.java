/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities.aquatic;

import java.util.Set;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.entity.ai.goal.PrioritizedGoal;
import net.minecraft.entity.passive.fish.PufferfishEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

import net.dries007.tfc.common.entities.ai.FluidPreferenceGoal;
import net.dries007.tfc.common.entities.ai.TFCAvoidEntityGoal;
import net.dries007.tfc.common.fluids.TFCFluids;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.mixin.entity.ai.goal.GoalSelectorAccessor;

public class TFCPufferfishEntity extends PufferfishEntity implements IBucketableOceanFish
{
    public TFCPufferfishEntity(EntityType<? extends PufferfishEntity> entityType_, World world_)
    {
        super(entityType_, world_);
    }

    @Override
    protected void registerGoals()
    {
        super.registerGoals();
        Set<PrioritizedGoal> availableGoals = ((GoalSelectorAccessor) goalSelector).getAvailableGoals();
        availableGoals.removeIf(priority -> priority.getGoal() instanceof AvoidEntityGoal);

        goalSelector.addGoal(2, new TFCAvoidEntityGoal<>(this, PlayerEntity.class, 8.0F, 5.0D, 5.4D));
        goalSelector.addGoal(3, new FluidPreferenceGoal(this, 1.0F, 8, TFCFluids.SALT_WATER.getSource()));
    }

    @Override
    protected ActionResultType mobInteract(PlayerEntity player, Hand hand)
    {
        return TFCAbstractGroupFishEntity.handleInteract(this, level, player, hand, getBucketItemStack(), getSaltyBucketItemStack()).orElseGet(() -> super.mobInteract(player, hand));
    }

    @Override
    public ItemStack getSaltyBucketItemStack()
    {
        return new ItemStack(TFCItems.PUFFERFISH_BUCKET.get());
    }

    @Override
    protected ItemStack getBucketItemStack()
    {
        return ItemStack.EMPTY;
    }
}
