/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities.land;

import java.util.function.Supplier;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeConfigSpec;

import net.dries007.tfc.common.entities.EntityHelpers;
import net.dries007.tfc.util.calendar.Calendars;

public abstract class ProducingAnimal extends TFCAnimal
{
    public static final EntityDataAccessor<Long> DATA_PRODUCED = SynchedEntityData.defineId(ProducingAnimal.class, EntityHelpers.LONG_SERIALIZER);

    protected final ForgeConfigSpec.IntValue produceTicks;
    protected final ForgeConfigSpec.DoubleValue produceFamiliarity;

    public ProducingAnimal(EntityType<? extends TFCAnimal> type, Level level, Supplier<? extends SoundEvent> ambient, Supplier<? extends SoundEvent> hurt, Supplier<? extends SoundEvent> death, Supplier<? extends SoundEvent> step, ForgeConfigSpec.DoubleValue adultFamiliarityCap, ForgeConfigSpec.IntValue daysToAdulthood, ForgeConfigSpec.IntValue usesToElderly, ForgeConfigSpec.BooleanValue eatsRottenFood, ForgeConfigSpec.IntValue childCount, ForgeConfigSpec.IntValue gestationDays, ForgeConfigSpec.IntValue produceTicks, ForgeConfigSpec.DoubleValue produceFamiliarity)
    {
        super(type, level, ambient, hurt, death, step, adultFamiliarityCap, daysToAdulthood, usesToElderly, eatsRottenFood);
        this.produceTicks = produceTicks;
        this.produceFamiliarity = produceFamiliarity;
    }

    @Override
    public boolean isReadyForAnimalProduct()
    {
        return getFamiliarity() > produceFamiliarity.get() && hasProduct();
    }

    @Override
    public void setProductsCooldown()
    {
        setProducedTick(Calendars.get(level).getTicks());
    }

    @Override
    public long getProductsCooldown()
    {
        return Math.max(0, produceTicks.get() + getProducedTick() - Calendars.get(level).getTicks());
    }

    @Override
    public void addAdditionalSaveData(CompoundTag nbt)
    {
        super.addAdditionalSaveData(nbt);
        nbt.putLong("produced", getProducedTick());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag nbt)
    {
        super.readAdditionalSaveData(nbt);
        setProducedTick(nbt.getLong("produced"));
    }

    @Override
    public void defineSynchedData()
    {
        super.defineSynchedData();
        entityData.define(DATA_PRODUCED, 0L);
    }

    public long getProducedTick()
    {
        return entityData.get(DATA_PRODUCED);
    }

    public void setProducedTick(long producedTick)
    {
        entityData.set(DATA_PRODUCED, producedTick);
    }
}
