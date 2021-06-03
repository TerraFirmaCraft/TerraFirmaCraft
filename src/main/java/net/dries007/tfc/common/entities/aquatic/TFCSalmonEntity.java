/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities.aquatic;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.fish.AbstractGroupFishEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;

import net.dries007.tfc.common.entities.ai.FluidPreferenceGoal;
import net.dries007.tfc.common.items.TFCItems;

public class TFCSalmonEntity extends TFCAbstractGroupFishEntity
{
    public TFCSalmonEntity(EntityType<? extends AbstractGroupFishEntity> type, World worldIn)
    {
        super(type, worldIn);
    }

    @Override
    public int getMaxSchoolSize()
    {
        return 5;
    }

    @Override
    public ItemStack getSaltyBucketItemStack()
    {
        return ItemStack.EMPTY;
    }

    @Override
    protected void registerGoals()
    {
        super.registerGoals();
        goalSelector.addGoal(2, new FluidPreferenceGoal(this, 1.0F, 16, Fluids.WATER.getSource()));
    }

    @Override
    public ItemStack getBucketItemStack()
    {
        return new ItemStack(TFCItems.SALMON_BUCKET.get());
    }

    @Override
    protected SoundEvent getFlopSound()
    {
        return SoundEvents.SALMON_FLOP;
    }

    @Override
    protected SoundEvent getAmbientSound()
    {
        return SoundEvents.SALMON_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn)
    {
        return SoundEvents.SALMON_HURT;
    }

    @Override
    protected SoundEvent getDeathSound()
    {
        return SoundEvents.SALMON_DEATH;
    }
}
