/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities.aquatic;

import java.util.Objects;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.Squid;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;

import com.mojang.datafixers.util.Pair;
import net.dries007.tfc.common.TFCEffects;
import net.dries007.tfc.util.Helpers;
import org.jetbrains.annotations.Nullable;


public class TFCSquid extends Squid implements AquaticMob
{
    public static final EntityDataAccessor<Integer> ID_SIZE = SynchedEntityData.defineId(TFCSquid.class, EntityDataSerializers.INT);

    public static final int MAX_SIZE = 127;
    public static final int MIN_SIZE = 1;

    public TFCSquid(EntityType<? extends Squid> type, Level level)
    {
        super(type, level);
    }

    @Override
    public boolean canSpawnIn(Fluid fluid)
    {
        return Helpers.isFluid(fluid, FluidTags.WATER);
    }

    @Override
    public void registerGoals()
    {
        goalSelector.addGoal(1, new RandomMovementGoal(this));
        goalSelector.addGoal(2, new FleeGoal(this));
    }

    @Override
    public void defineSynchedData()
    {
        super.defineSynchedData();
        entityData.define(ID_SIZE, MIN_SIZE);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag)
    {
        super.addAdditionalSaveData(tag);
        tag.putInt("size", getSize() - 1);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag)
    {
        setSize(tag.getInt("size") + 1, false);
        super.readAdditionalSaveData(tag);
    }

    @Override
    public void refreshDimensions()
    {
        final double x = getX();
        final double y = getY();
        final double z = getZ();
        super.refreshDimensions();
        setPos(x, y, z);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> data)
    {
        super.onSyncedDataUpdated(data);
        if (ID_SIZE.equals(data)) refreshDimensions();
    }

    @Override
    public boolean hurt(DamageSource source, float amount)
    {
        if (super.hurt(source, amount))
        {
            if (!level.isClientSide && getLastHurtByMob() instanceof Player player && random.nextInt(3) == 0 && player.distanceToSqr(this) < 64)
            {
                player.addEffect(new MobEffectInstance(getInkEffect(), 100));
            }
            return true;
        }
        return false;
    }

    public MobEffect getInkEffect()
    {
        return TFCEffects.INK.get();
    }

    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData data, @Nullable CompoundTag tag)
    {
        var pair = getSizeRangeForSpawning();
        setSize(Mth.nextInt(random, pair.getFirst(), pair.getSecond()), true);
        return super.finalizeSpawn(level, difficulty, spawnType, data, tag);
    }

    @Override
    public EntityDimensions getDimensions(Pose pose)
    {
        return super.getDimensions(pose).scale(getVisualScale());
    }

    public float getVisualScale()
    {
        return 0.05F * getSize();
    }

    public Pair<Integer, Integer> getSizeRangeForSpawning()
    {
        return Pair.of(22, 65);
    }

    public int getSize()
    {
        return entityData.get(ID_SIZE);
    }

    public void setSize(int size, boolean heal)
    {
        size = Mth.clamp(size, MIN_SIZE, MAX_SIZE);
        reapplyPosition();
        refreshDimensions();
        Objects.requireNonNull(getAttribute(Attributes.MAX_HEALTH)).setBaseValue(size);
        if (heal) setHealth(getMaxHealth());
        entityData.set(ID_SIZE, size);
    }

    public static class RandomMovementGoal extends Goal
    {
        private final TFCSquid squid;

        public RandomMovementGoal(TFCSquid squid)
        {
            this.squid = squid;
        }

        @Override
        public boolean canUse()
        {
            return true;
        }

        @Override
        public void tick()
        {
            int ticks = squid.getNoActionTime();
            if (ticks > 100)
            {
                squid.setMovementVector(0.0F, 0.0F, 0.0F);
            }
            else if (squid.getRandom().nextInt(reducedTickDelay(50)) == 0 || !squid.wasTouchingWater || !squid.hasMovementVector())
            {
                float f = this.squid.getRandom().nextFloat() * ((float)Math.PI * 2F);
                float f1 = Mth.cos(f) * 0.2F;
                float f2 = -0.1F + this.squid.getRandom().nextFloat() * 0.2F;
                float f3 = Mth.sin(f) * 0.2F;
                this.squid.setMovementVector(f1, f2, f3);
            }

        }
    }

    public static class FleeGoal extends Goal
    {
        private static final float SQUID_FLEE_SPEED = 3.0F;
        private static final float SQUID_FLEE_MIN_DISTANCE = 5.0F;
        private static final float SQUID_FLEE_MAX_DISTANCE = 10.0F;

        private int fleeTicks;
        private final TFCSquid squid;

        public FleeGoal(TFCSquid squid)
        {
            this.squid = squid;
        }

        @Override
        public boolean canUse()
        {
            LivingEntity livingentity = squid.getLastHurtByMob();
            if (squid.isInWater() && livingentity != null)
            {
                return squid.distanceToSqr(livingentity) < SQUID_FLEE_MAX_DISTANCE * SQUID_FLEE_MAX_DISTANCE;
            }
            return false;
        }

        @Override
        public void start()
        {
            this.fleeTicks = 0;
        }

        @Override
        public boolean requiresUpdateEveryTick()
        {
            return true;
        }

        @Override
        public void tick()
        {
            ++this.fleeTicks;
            LivingEntity hurtBy = squid.getLastHurtByMob();
            if (hurtBy != null)
            {
                Vec3 movement = new Vec3(squid.getX() - hurtBy.getX(), squid.getY() - hurtBy.getY(), squid.getZ() - hurtBy.getZ());
                final BlockPos nextPos = new BlockPos(squid.getX() + movement.x, squid.getY() + movement.y, squid.getZ() + movement.z);
                BlockState stateAt = squid.level.getBlockState(nextPos);
                FluidState fluidAt = squid.level.getFluidState(nextPos);
                if (fluidAt.is(FluidTags.WATER) || stateAt.isAir())
                {
                    double length = movement.length();
                    if (length > 0.0D)
                    {
                        movement.normalize();
                        double scale = SQUID_FLEE_SPEED;
                        if (length > SQUID_FLEE_MIN_DISTANCE)
                        {
                            scale -= (length - SQUID_FLEE_MIN_DISTANCE) / SQUID_FLEE_MIN_DISTANCE;
                        }

                        if (scale > 0.0D)
                        {
                            movement = movement.scale(scale);
                        }
                    }

                    if (stateAt.isAir())
                    {
                        movement = movement.subtract(0.0D, movement.y, 0.0D);
                    }

                    squid.setMovementVector((float) movement.x / 20.0F, (float) movement.y / 20.0F, (float) movement.z / 20.0F);
                }

                if (this.fleeTicks % 10 == 5)
                {
                    squid.level.addParticle(ParticleTypes.BUBBLE, squid.getX(), squid.getY(), squid.getZ(), 0.0D, 0.0D, 0.0D);
                }

            }
        }
    }


}
