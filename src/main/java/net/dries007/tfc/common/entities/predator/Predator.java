/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities.predator;

import java.util.function.Supplier;
import com.mojang.serialization.Dynamic;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.EntityDamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.client.TFCSounds;
import net.dries007.tfc.client.particle.TFCParticles;
import net.dries007.tfc.common.TFCEffects;
import net.dries007.tfc.common.entities.AnimationState;
import net.dries007.tfc.common.entities.EntityHelpers;
import net.dries007.tfc.common.entities.WildAnimal;
import net.dries007.tfc.common.entities.ai.TFCBrain;
import net.dries007.tfc.common.entities.ai.predator.PredatorAi;

public class Predator extends WildAnimal
{
    public static AttributeSupplier.Builder createAttributes()
    {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 40).add(Attributes.MOVEMENT_SPEED, 0.3F).add(Attributes.ATTACK_KNOCKBACK, 1).add(Attributes.ATTACK_DAMAGE, 6);
    }

    public static final EntityDataAccessor<Boolean> DATA_SLEEPING = SynchedEntityData.defineId(Predator.class, EntityDataSerializers.BOOLEAN);

    public final AnimationState walkingAnimation = new AnimationState();
    public final AnimationState swimmingAnimation = new AnimationState();
    public final AnimationState sleepingAnimation = new AnimationState();
    public final AnimationState attackingAnimation = new AnimationState();
    public final AnimationState runningAnimation = new AnimationState();

    private final Supplier<SoundEvent> attack;
    private final Supplier<SoundEvent> sleeping;

    public final boolean diurnal;

    public static Predator createBear(EntityType<? extends Predator> type, Level level)
    {
        return new Predator(type, level, true, TFCSounds.BEAR);
    }

    public Predator(EntityType<? extends Predator> type, Level level, boolean diurnal, TFCSounds.EntitySound sounds)
    {
        super(type, level, sounds);
        this.diurnal = diurnal;
        getBrain().setSchedule(diurnal ? TFCBrain.DIURNAL.get() : TFCBrain.NOCTURNAL.get());
        this.attack = sounds.attack().orElseThrow();
        this.sleeping = sounds.sleep().orElseThrow();
    }

    public boolean isDiurnal()
    {
        return diurnal;
    }

    @Override
    protected Brain.Provider<? extends Predator> brainProvider()
    {
        return Brain.provider(PredatorAi.MEMORY_TYPES, PredatorAi.SENSOR_TYPES);
    }

    @Override
    protected Brain<?> makeBrain(Dynamic<?> dynamic)
    {
        return PredatorAi.makeBrain(brainProvider().makeBrain(dynamic), this);
    }

    @Override
    protected void customServerAiStep()
    {
        getBrain().tick((ServerLevel) level, this);
        PredatorAi.updateActivity(this);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Brain<Predator> getBrain()
    {
        return (Brain<Predator>) super.getBrain();
    }

    @Override
    public void tick()
    {
        if (level.isClientSide)
        {
            tickAnimationStates();
        }
        super.tick();
    }

    public void tickAnimationStates()
    {
        if (isSleeping())
        {
            if (getRandom().nextInt(10) == 0)
            {
                level.addParticle(TFCParticles.SLEEP.get(), getX(), getY() + getEyeHeight(), getZ(), 0.01, 0.05, 0.01);
            }
            sleepingAnimation.startIfStopped(tickCount);
        }
        else
        {
            sleepingAnimation.stop();

            BlockPos blockPosBelow = getBlockPosBelowThatAffectsMyMovement();
            BlockState blockStateBelow = level.getBlockState(blockPosBelow);

            EntityHelpers.startOrStop(swimmingAnimation, isInWater() || (blockStateBelow.getFriction(level, blockPosBelow, null) > 0.7), tickCount);
            final boolean moving = EntityHelpers.isMovingOnLand(this);
            EntityHelpers.startOrStop(runningAnimation, isAggressive() && moving, tickCount);
            EntityHelpers.startOrStop(walkingAnimation, !isAggressive() && moving, tickCount);
        }

    }

    @Override
    public boolean hurt(DamageSource source, float amount)
    {
        boolean hurt = super.hurt(source, amount);
        if (!level.isClientSide && source instanceof EntityDamageSource entitySource && entitySource.getEntity() instanceof LivingEntity livingEntity && getHealth() > 0 && EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(livingEntity))
        {
            brain.setMemory(MemoryModuleType.ATTACK_TARGET, livingEntity);
            brain.setActiveActivityIfPossible(Activity.FIGHT);
            setSleeping(false);
        }
        return hurt;
    }

    @Override
    public boolean doHurtTarget(Entity target)
    {
        boolean hurt = super.doHurtTarget(target);
        level.broadcastEntityEvent(this, (byte) 4);
        playSound(getAttackSound(), 1.0f, getVoicePitch());

        if (hurt && target instanceof Player player && random.nextInt(5) == 0 && player.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE) <= 0)
        {
            pinPlayer(player);
        }

        return hurt;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag)
    {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("sleeping", isSleeping());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag)
    {
        super.readAdditionalSaveData(tag);
        setSleeping(tag.getBoolean("sleeping"));
    }

    @Override
    public void handleEntityEvent(byte id)
    {
        if (id == 4)
        {
            attackingAnimation.start(tickCount);
            playSound(getAttackSound(), 1.0F, getVoicePitch());
        }
        super.handleEntityEvent(id);
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType type, @Nullable SpawnGroupData data, @Nullable CompoundTag tag)
    {
        SpawnGroupData spawnData = super.finalizeSpawn(level, difficulty, type, data, tag);
        getBrain().setMemory(MemoryModuleType.HOME, GlobalPos.of(level.getLevel().dimension(), blockPosition()));
        return spawnData;
    }

    @Override
    public void defineSynchedData()
    {
        super.defineSynchedData();
        entityData.define(DATA_SLEEPING, false);
    }

    @Override
    public boolean isSleeping()
    {
        return entityData.get(DATA_SLEEPING);
    }

    public void setSleeping(boolean asleep)
    {
        entityData.set(DATA_SLEEPING, asleep);
    }

    public SoundEvent getAttackSound()
    {
        return attack.get();
    }

    @Override
    protected SoundEvent getAmbientSound()
    {
        return isSleeping() ? sleeping.get() : super.getAmbientSound();
    }

    @Override
    protected void onOffspringSpawnedFromEgg(Player player, Mob offspring)
    {
        super.onOffspringSpawnedFromEgg(player, offspring);
        if (offspring instanceof Predator predator)
        {
            predator.getBrain().setMemory(MemoryModuleType.HOME, GlobalPos.of(level.dimension(), PredatorAi.getHomePos(this)));
        }
    }

    public boolean pinPlayer(Player player)
    {
        if (distanceToSqr(player) < 6D)
        {
            if (!player.level.isClientSide)
            {
                player.addEffect(new MobEffectInstance(TFCEffects.PINNED.get(), 35, 0, false, false));
            }
            return true;
        }
        return false;
    }
}
