package net.dries007.tfc.common.entities.aquatic;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.fish.AbstractGroupFishEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;

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
    protected ItemStack getBucketItemStack()
    {
        return new ItemStack(Items.SALMON_BUCKET);
    }

    @Override
    protected SoundEvent getAmbientSound()
    {
        return SoundEvents.SALMON_AMBIENT;
    }

    @Override
    protected SoundEvent getDeathSound()
    {
        return SoundEvents.SALMON_DEATH;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn)
    {
        return SoundEvents.SALMON_HURT;
    }

    @Override
    protected SoundEvent getFlopSound()
    {
        return SoundEvents.SALMON_FLOP;
    }
}
