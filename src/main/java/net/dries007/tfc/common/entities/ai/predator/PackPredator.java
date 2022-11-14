/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities.ai.predator;

import com.mojang.serialization.Dynamic;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.EntityDamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.client.TFCSounds;
import net.dries007.tfc.common.entities.EntityHelpers;
import net.dries007.tfc.common.entities.predator.Predator;

public class PackPredator extends Predator
{
    public static PackPredator createWolf(EntityType<? extends Predator> type, Level level)
    {
        return new PackPredator(type, level, false, TFCSounds.DOG);
    }

    public static final EntityDataAccessor<Integer> DATA_RESPECT = SynchedEntityData.defineId(PackPredator.class, EntityDataSerializers.INT);

    private boolean howled;

    public PackPredator(EntityType<? extends Predator> type, Level level, boolean diurnal, TFCSounds.EntitySound sounds)
    {
        super(type, level, diurnal, sounds);
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType type, @Nullable SpawnGroupData data, @Nullable CompoundTag tag)
    {
        final SpawnGroupData group = super.finalizeSpawn(level, difficulty, type, data, tag);
        setRespect(random.nextInt(10));
        return group;
    }

    @Override
    public float getVoicePitch()
    {
        return isSleeping() ? 0.5f * super.getVoicePitch() : super.getVoicePitch();
    }

    public int getRespect()
    {
        return entityData.get(DATA_RESPECT);
    }

    public void setRespect(int amount)
    {
        entityData.set(DATA_RESPECT, amount);
    }

    public void addRespect(int amount)
    {
        setRespect(getRespect() + amount);
    }

    @Override
    public void defineSynchedData()
    {
        super.defineSynchedData();
        entityData.define(DATA_RESPECT, 0);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag)
    {
        super.addAdditionalSaveData(tag);
        tag.putInt("respect", getRespect());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag)
    {
        super.readAdditionalSaveData(tag);
        setRespect(EntityHelpers.getIntOrDefault(tag, "respect", 0));
    }

    @Override
    public boolean pinPlayer(Player player)
    {
        if (random.nextFloat() < 0.2f)
        {
            if (super.pinPlayer(player))
            {
                addRespect(1);
                return true;
            }
        }
        return false;
    }

    @Override
    protected Brain.Provider<? extends Predator> brainProvider()
    {
        return Brain.provider(PackPredatorAi.MEMORY_TYPES, PackPredatorAi.SENSOR_TYPES);
    }

    @Override
    protected Brain<?> makeBrain(Dynamic<?> dynamic)
    {
        return PackPredatorAi.makeBrain(brainProvider().makeBrain(dynamic), this);
    }

    @Override
    public boolean doHurtTarget(Entity target)
    {
        if (super.doHurtTarget(target))
        {
            if (!target.isAlive())
            {
                addRespect(getRandom().nextInt(3));
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean hurt(DamageSource source, float amount)
    {
        if (!level.isClientSide && source instanceof EntityDamageSource entitySource && entitySource.getEntity() instanceof LivingEntity livingEntity && EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(livingEntity))
        {
            PackPredatorAi.alertOthers(this, livingEntity);
        }
        return super.hurt(source, amount);
    }

    @Override
    public void tick()
    {
        super.tick();
        final long time = level.getDayTime() % 24000;
        if (!howled && time > 18000 && time < 19000 && random.nextInt(10) == 0)
        {
            playSound(SoundEvents.WOLF_HOWL, getSoundVolume() * 1.2f, getVoicePitch());
            howled = true;
        }
        if (time > 19000)
        {
            howled = false;
        }
    }

}
