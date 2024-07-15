/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities.aquatic;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.portal.DimensionTransition;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.effect.TFCEffects;
import net.dries007.tfc.common.entities.IGlow;

public class Octopoteuthis extends TFCSquid implements IGlow
{
    private static final EntityDataAccessor<Integer> DATA_DARK_TICKS_REMAINING = SynchedEntityData.defineId(Octopoteuthis.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<BlockPos> DATA_LIGHT_POS = SynchedEntityData.defineId(Octopoteuthis.class, EntityDataSerializers.BLOCK_POS);

    public Octopoteuthis(EntityType<? extends Octopoteuthis> type, Level level)
    {
        super(type, level);
    }

    @Override
    protected ParticleOptions getInkParticle()
    {
        return ParticleTypes.GLOW_SQUID_INK;
    }

    @Override
    public void defineSynchedData(SynchedEntityData.Builder builder)
    {
        super.defineSynchedData(builder);
        builder.define(DATA_DARK_TICKS_REMAINING, 0);
        builder.define(DATA_LIGHT_POS, BlockPos.ZERO);
    }

    @Override
    public SoundEvent getSquirtSound()
    {
        return SoundEvents.GLOW_SQUID_SQUIRT;
    }

    @Override
    public SoundEvent getAmbientSound()
    {
        return SoundEvents.GLOW_SQUID_AMBIENT;
    }

    @Override
    public SoundEvent getHurtSound(DamageSource source)
    {
        return SoundEvents.GLOW_SQUID_HURT;
    }

    @Override
    public SoundEvent getDeathSound()
    {
        return SoundEvents.GLOW_SQUID_DEATH;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag)
    {
        super.addAdditionalSaveData(tag);
        tag.putInt("DarkTicksRemaining", this.getDarkTicksRemaining());
        saveLight(tag);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag)
    {
        super.readAdditionalSaveData(tag);
        this.setDarkTicks(tag.getInt("DarkTicksRemaining"));
        readLight(tag);
    }

    @Override
    public void tickGlow()
    {
        super.tick();
        IGlow.super.tickGlow();
    }

    @Override
    public int getLightLevel()
    {
        return (int) Mth.clamp(getSize() * 0.234375F, 1, 15);
    }

    @Override
    public void aiStep()
    {
        super.aiStep();
        final int ticks = getDarkTicksRemaining();
        if (ticks > 0)
        {
            setDarkTicks(ticks - 1);
        }
        level().addParticle(ParticleTypes.GLOW, getRandomX(0.6D), getRandomY(), getRandomZ(0.6D), 0.0D, 0.0D, 0.0D);
    }

    @Override
    public void remove(Entity.RemovalReason reason)
    {
        tryRemoveLight();
        super.remove(reason);
    }

    @Nullable
    @Override
    public Entity changeDimension(DimensionTransition transition)
    {
        tryRemoveLight();
        return super.changeDimension(transition);
    }

    @Override
    public boolean hurt(DamageSource source, float amount)
    {
        boolean hurt = super.hurt(source, amount);
        if (hurt) setDarkTicks(100);
        return hurt;
    }

    @Override
    public Holder<MobEffect> getInkEffect()
    {
        return TFCEffects.GLOW_INK.holder();
    }

    @Override
    public Pair<Integer, Integer> getSizeRangeForSpawning()
    {
        return Pair.of(22, 100);
    }

    private void setDarkTicks(int ticks)
    {
        entityData.set(DATA_DARK_TICKS_REMAINING, ticks);
    }

    public int getDarkTicksRemaining()
    {
        return entityData.get(DATA_DARK_TICKS_REMAINING);
    }

    @Override
    public void setLightPos(BlockPos pos)
    {
        entityData.set(DATA_LIGHT_POS, pos);
    }

    @Override
    public BlockPos getLightPos()
    {
        return entityData.get(DATA_LIGHT_POS);
    }
}
