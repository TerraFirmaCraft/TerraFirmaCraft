/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities.livestock;

import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;

import net.dries007.tfc.client.TFCSounds;
import net.dries007.tfc.config.animals.MammalConfig;

public abstract class Mammal extends TFCAnimal implements MammalProperties
{
    private static final EntityDataAccessor<Long> PREGNANT_TIME = SynchedEntityData.defineId(Mammal.class, EntityDataSerializers.LONG);

    private final MammalConfig config;
    @Nullable private CompoundTag genes = null;

    public Mammal(EntityType<? extends TFCAnimal> animal, Level level, TFCSounds.EntityId sounds, MammalConfig config)
    {
        super(animal, level, sounds, config.inner());
        this.config = config;
    }

    @Override
    public MammalConfig getMammalConfig()
    {
        return config;
    }

    @Override
    public long getPregnantTime()
    {
        return entityData.get(PREGNANT_TIME);
    }

    @Override
    public void setPregnantTime(long day)
    {
        entityData.set(PREGNANT_TIME, day);
    }

    @Override
    public void setGenes(@Nullable CompoundTag tag)
    {
        genes = tag;
    }

    @Override
    @Nullable
    public CompoundTag getGenes()
    {
        return genes;
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData spawnData, @Nullable CompoundTag tag)
    {
        spawnData = super.finalizeSpawn(level, difficulty, reason, spawnData, tag);
        setPregnantTime(-1L);
        return spawnData;
    }

    @Override
    protected void defineSynchedData()
    {
        super.defineSynchedData();
        entityData.define(PREGNANT_TIME, -1L);
    }
}
