package net.dries007.tfc.common.entities.aquatic;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.fish.AbstractGroupFishEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;

import net.dries007.tfc.common.entities.ai.FluidPreferenceGoal;
import net.dries007.tfc.common.entities.ai.UnderwaterHideGoal;
import net.dries007.tfc.common.fluids.TFCFluids;

public class TFCCodEntity extends TFCAbstractGroupFishEntity
{
    public TFCCodEntity(EntityType<? extends AbstractGroupFishEntity> type, World worldIn)
    {
        super(type, worldIn);
    }

    @Override
    public int getMaxSchoolSize()
    {
        return 5;
    }

    @Override
    protected void registerGoals()
    {
        super.registerGoals();
        goalSelector.addGoal(5, new FluidPreferenceGoal(this, 1.0F, 16, TFCFluids.SALT_WATER.getSource()));
        goalSelector.addGoal(6, new UnderwaterHideGoal(this, 1.0F, 8));
    }

    @Override
    protected ItemStack getBucketItemStack()
    {
        return new ItemStack(Items.COD_BUCKET);
    }

    @Override
    protected SoundEvent getAmbientSound()
    {
        return SoundEvents.COD_AMBIENT;
    }

    @Override
    protected SoundEvent getDeathSound()
    {
        return SoundEvents.COD_DEATH;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn)
    {
        return SoundEvents.COD_HURT;
    }

    @Override
    protected SoundEvent getFlopSound()
    {
        return SoundEvents.COD_FLOP;
    }
}
