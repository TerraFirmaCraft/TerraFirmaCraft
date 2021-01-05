/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.common.entities.animals.chicken;

import javax.annotation.Nonnull;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.world.World;

import net.dries007.tfc.common.entities.animals.ILivestock;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.calendar.Calendars;

public abstract class TFCChickenEntity extends ChickenEntity implements ILivestock
{
    private static final DataParameter<Float> FAMILIARITY_ID = EntityDataManager.defineId(TFCChickenEntity.class, DataSerializers.FLOAT);

    private float familiarity;
    private long birthTicks;

    public TFCChickenEntity(EntityType<? extends ChickenEntity> type, World worldIn)
    {
        super(type, worldIn);
    }

    @Override
    public void aiStep()
    {
        super.aiStep();
        eggTime = Integer.MAX_VALUE; // Prevents vanilla spawning eggs
    }

    @Override
    public void readAdditionalSaveData(@Nonnull CompoundNBT compound)
    {
        super.readAdditionalSaveData(compound);
        isChickenJockey = false;
        familiarity = compound.getFloat("familiarity");
        birthTicks = compound.getLong("birthTicks");
    }

    @Override
    public void addAdditionalSaveData(@Nonnull CompoundNBT compound)
    {
        super.addAdditionalSaveData(compound);
        compound.putFloat("familiarity", familiarity);
        compound.putLong("birthTicks", birthTicks);
    }

    @Override
    public float getFamiliarity()
    {
        if (level.isClientSide)
        {
            return entityData.get(FAMILIARITY_ID);
        }
        else
        {
            return familiarity;
        }
    }

    @Override
    public void setFamiliarity(float value)
    {
        familiarity = value;
        entityData.set(FAMILIARITY_ID, familiarity);
    }

    @Override
    public long getBirthTicks()
    {
        return birthTicks;
    }

    @Override
    public void setBirthTicks(long value)
    {
        birthTicks = value;
    }

    @Override
    public float getGrowth()
    {
        long deltaTicks = Calendars.get(level).getTicks() - birthTicks;
        return Math.min(1, deltaTicks / (float) TFCConfig.SERVER.animals.CHICKEN.adulthoodTicks.get());
    }

    @Override
    public float getElderly()
    {
        long elderly = TFCConfig.SERVER.animals.CHICKEN.elderlyTicks.get();
        if (elderly > 0)
        {
            long lifeTime = TFCConfig.SERVER.animals.CHICKEN.adulthoodTicks.get() + elderly;
            long deltaTicks = Calendars.get(level).getTicks() - birthTicks;
            return deltaTicks / (float) lifeTime;
        }
        else
        {
            return getGrowth();
        }
    }

    @Override
    protected void defineSynchedData()
    {
        super.defineSynchedData();
        entityData.define(FAMILIARITY_ID, 0F);
    }

    @Override
    public boolean isBaby()
    {
        return getAging() == Aging.CHILD;
    }
}
