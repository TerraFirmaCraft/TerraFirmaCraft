/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities.prey;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;

import net.dries007.tfc.client.TFCSounds;
import net.dries007.tfc.common.entities.AnimationState;
import net.dries007.tfc.common.entities.EntityHelpers;
import net.dries007.tfc.common.entities.ai.TFCClimberNavigation;
import net.dries007.tfc.util.Helpers;
import org.jetbrains.annotations.Nullable;

public class Pest extends Prey
{
    public static AttributeSupplier.Builder createAttributes()
    {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 8.0D).add(Attributes.MOVEMENT_SPEED, 0.6F);
    }

    public static final EntityDataAccessor<Boolean> DATA_CLIMBING = SynchedEntityData.defineId(Pest.class, EntityDataSerializers.BOOLEAN);

    public final AnimationState walkingAnimation = new AnimationState();
    public final AnimationState sniffingAnimation = new AnimationState();
    public final AnimationState searchingAnimation = new AnimationState();
    public final AnimationState eatingAnimation = new AnimationState();
    public final AnimationState draggingAnimation = new AnimationState();

    public Pest(EntityType<? extends Prey> type, Level level, TFCSounds.EntitySound sounds)
    {
        super(type, level, sounds);
    }


    @Override
    protected void defineSynchedData()
    {
        super.defineSynchedData();
        entityData.define(DATA_CLIMBING, false);
    }

    public void setClimbing(boolean climbing)
    {
        entityData.set(DATA_CLIMBING, climbing);
    }

    public boolean isClimbing()
    {
        return entityData.get(DATA_CLIMBING);
    }

    @Override
    public boolean onClimbable()
    {
        return isClimbing();
    }

    @Override
    public void tick()
    {
        if (level.isClientSide)
        {
            final boolean moving = EntityHelpers.isMovingOnLand(this);
            EntityHelpers.startOrStop(walkingAnimation, moving, tickCount);
            if (!moving && random.nextInt(220) == 0)
            {
                if (random.nextBoolean())
                {
                    sniffingAnimation.startIfStopped(tickCount);
                    playSound(SoundEvents.FOX_SNIFF, getSoundVolume(), getVoicePitch());
                }
                else
                {
                    searchingAnimation.startIfStopped(tickCount);
                    playSound(SoundEvents.FOX_SNIFF, getSoundVolume(), getVoicePitch());
                }
            }
        }
        super.tick();
        if (!level.isClientSide)
        {
            setClimbing(horizontalCollision);
        }
    }

    @Override
    protected PathNavigation createNavigation(Level level)
    {
        return new TFCClimberNavigation(this, level);
    }

    @Override
    public boolean removeWhenFarAway(double distance)
    {
        return true;
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType type, @Nullable SpawnGroupData data, @Nullable CompoundTag tag)
    {
        final SpawnGroupData spawnData = super.finalizeSpawn(level, difficulty, type, data, tag);
        if (random.nextInt(1000) == 0)
        {
            setCustomName(Helpers.literal("Pak"));
        }
        return spawnData;
    }
}
