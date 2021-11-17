/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities.land;

import java.util.function.Supplier;
import javax.annotation.Nullable;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;

import net.minecraftforge.common.ForgeConfigSpec;

import net.dries007.tfc.common.entities.EntityHelpers;
import net.dries007.tfc.util.calendar.Calendars;

public abstract class Mammal extends TFCAnimal
{
    private static final EntityDataAccessor<Long> PREGNANT_TIME = SynchedEntityData.defineId(Mammal.class, EntityHelpers.LONG_SERIALIZER);
    private final ForgeConfigSpec.IntValue childCount;
    private final ForgeConfigSpec.IntValue gestationDays;

    public Mammal(EntityType<? extends TFCAnimal> animal, Level level, Supplier<? extends SoundEvent> ambient, Supplier<? extends SoundEvent> hurt, Supplier<? extends SoundEvent> death, Supplier<? extends SoundEvent> step, ForgeConfigSpec.DoubleValue adultFamiliarityCap, ForgeConfigSpec.IntValue daysToAdulthood, ForgeConfigSpec.IntValue usesToElderly, ForgeConfigSpec.BooleanValue eatsRottenFood, ForgeConfigSpec.IntValue childCount, ForgeConfigSpec.IntValue gestationDays)
    {
        super(animal, level, ambient, hurt, death, step, adultFamiliarityCap, daysToAdulthood, usesToElderly, eatsRottenFood);
        this.childCount = childCount;
        this.gestationDays = gestationDays;
    }

    public long getPregnantTime()
    {
        return entityData.get(PREGNANT_TIME);
    }

    private void setPregnantTime(long day)
    {
        entityData.set(PREGNANT_TIME, day);
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData data, @Nullable CompoundTag tag)
    {
        data = super.finalizeSpawn(level, difficulty, reason, data, tag);
        setPregnantTime(-1L);
        return data;
    }

    @Override
    public void onFertilized(TFCAnimalProperties male)
    {
        //Mark the day this female became pregnant
        setPregnantTime(Calendars.get(male.getEntity().level).getTotalDays());
    }

    @Override
    protected void defineSynchedData()
    {
        super.defineSynchedData();
        entityData.define(PREGNANT_TIME, -1L);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag nbt)
    {
        super.addAdditionalSaveData(nbt);
        nbt.putLong("pregnant", getPregnantTime());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag nbt)
    {
        super.readAdditionalSaveData(nbt);
        setPregnantTime(nbt.getLong("pregnant"));
    }

    @Override
    public TFCAnimalProperties.Type getTFCAnimalType()
    {
        return TFCAnimalProperties.Type.MAMMAL;
    }

    @Override
    public void tick()
    {
        super.tick();
        if (!level.isClientSide)
        {
            if (isFertilized() && Calendars.SERVER.getTotalDays() >= getPregnantTime() + getGestationDays())
            {
                birthChildren();
                setFertilized(false);
                setPregnantTime(-1L);
                addUses(1);
            }
        }
    }

    public void birthChildren()
    {
        for (int i = 0; i < getChildCount(); i++)
        {
            AgeableMob offspring = getBreedOffspring((ServerLevel) level, this);
            if (offspring == null) continue;
            if (offspring instanceof TFCAnimal animal)
            {
                animal.setPos(position());
                animal.setFamiliarity(getFamiliarity() < 0.9F ? getFamiliarity() / 2.0F : getFamiliarity() * 0.9F);
                level.addFreshEntity(animal);
            }
        }
    }

    public int getChildCount()
    {
        return childCount.get();
    }

    /**
     * Return the number of days for a full gestation
     *
     * @return long value in days
     */
    public long getGestationDays()
    {
        return gestationDays.get();
    }
}
