/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities;

import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import net.dries007.tfc.client.TFCSounds;
import org.jetbrains.annotations.Nullable;

public class WildAnimal extends PathfinderMob implements GenderedRenderAnimal
{
    public static final EntityDataAccessor<Boolean> DATA_IS_MALE = SynchedEntityData.defineId(WildAnimal.class, EntityDataSerializers.BOOLEAN);

    protected final Supplier<SoundEvent> ambient;
    protected final Supplier<SoundEvent> death;
    protected final Supplier<SoundEvent> hurt;
    protected final Supplier<SoundEvent> step;

    @Nullable public Vec3 location;
    @Nullable public Vec3 prevLocation;
    public float walkProgress = 0f;
    public final int walkAnimationLength;
    protected float limbSwing = 1f;

    public WildAnimal(EntityType<? extends PathfinderMob> type, Level level, TFCSounds.EntitySound sounds, int walkLength)
    {
        super(type, level);
        getNavigation().setCanFloat(true);
        this.walkAnimationLength = walkLength;
        this.ambient = sounds.ambient();
        this.death = sounds.death();
        this.hurt = sounds.hurt();
        this.step = sounds.step();
    }

    @Override
    public void tick()
    {
        super.tick();
        //Variable for smooth walk animation
        prevLocation = location;
        location = this.position();
        if (walkProgress >= (float) walkAnimationLength)
        {
            walkProgress = 0f;
        }

        else if (this.isMoving() || walkProgress > 0f)
        {
            walkProgress = Math.min(walkProgress + limbSwing, (float) walkAnimationLength);
        }
    }

    public boolean isMoving()
    {
        return location != null && prevLocation != null && !location.equals(prevLocation);
    }

    @Override
    public boolean displayMaleCharacteristics()
    {
        return isMale();
    }

    @Override
    public boolean displayFemaleCharacteristics()
    {
        return !isMale();
    }

    @Override
    protected void defineSynchedData()
    {
        super.defineSynchedData();
        entityData.define(DATA_IS_MALE, true);
    }

    public boolean isMale()
    {
        return entityData.get(DATA_IS_MALE);
    }

    public void setIsMale(boolean male)
    {
        entityData.set(DATA_IS_MALE, male);
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType type, @Nullable SpawnGroupData data, @Nullable CompoundTag tag)
    {
        SpawnGroupData spawnData = super.finalizeSpawn(level, difficulty, type, data, tag);
        setIsMale(level.getRandom().nextBoolean());
        return spawnData;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag)
    {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("male", isMale());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag)
    {
        super.readAdditionalSaveData(tag);
        setIsMale(tag.getBoolean("male"));
    }

    @Override
    protected void playStepSound(BlockPos pPos, BlockState pBlock)
    {
        this.playSound(step.get(), 0.15F, 1.0F);
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource src)
    {
        return hurt.get();
    }

    @Override
    protected SoundEvent getDeathSound()
    {
        return death.get();
    }

    @Override
    public boolean removeWhenFarAway(double distance)
    {
        return false;
    }

    public void setLimbSwing(float swing)
    {
        limbSwing = swing;
    }
}
