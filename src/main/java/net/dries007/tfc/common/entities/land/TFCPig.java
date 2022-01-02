/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities.land;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.Tag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.entities.EntityHelpers;

public class TFCPig extends Mammal
{
    public TFCPig(EntityType<? extends Animal> animal, Level level)
    {
        super(animal, level);
    }

    @Override
    public int childCount()
    {
        return 10;
    }

    @Override
    public void registerGoals()
    {
        super.registerGoals();
        EntityHelpers.addCommonPreyGoals(this, goalSelector);
    }

    @Override
    public float getAdultFamiliarityCap()
    {
        return 0.35F;
    }

    @Override
    public long gestationDays()
    {
        return 19;
    }

    @Override
    public int getDaysToAdulthood()
    {
        return 80;
    }

    @Override
    public int getUsesToElderly()
    {
        return 5;
    }

    @Override
    public Tag.Named<Item> getFoodTag()
    {
        return TFCTags.Items.PIG_FOOD;
    }

    @Override
    public boolean eatsRottenFood()
    {
        return true;
    }

    @Override
    protected SoundEvent getAmbientSound()
    {
        return SoundEvents.PIG_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource src)
    {
        return SoundEvents.PIG_HURT;
    }

    @Override
    protected SoundEvent getDeathSound()
    {
        return SoundEvents.PIG_DEATH;
    }

    @Override
    protected void playStepSound(BlockPos pPos, BlockState pBlock)
    {
        this.playSound(SoundEvents.PIG_STEP, 0.15F, 1.0F);
    }
}
