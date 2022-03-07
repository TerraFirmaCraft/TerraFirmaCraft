package net.dries007.tfc.common.entities.predator;

import javax.annotation.Nullable;

import net.minecraft.core.GlobalPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.EntityDamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;

import com.mojang.serialization.Dynamic;
import net.dries007.tfc.client.particle.TFCParticles;
import net.dries007.tfc.common.TFCEffects;
import net.dries007.tfc.common.entities.ai.predator.PredatorAi;

public class Predator extends PathfinderMob
{
    public static AttributeSupplier.Builder createAttributes()
    {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 40).add(Attributes.MOVEMENT_SPEED, 0.3F).add(Attributes.ATTACK_KNOCKBACK, 1).add(Attributes.ATTACK_DAMAGE, 6);
    }

    public static final EntityDataAccessor<Boolean> DATA_SLEEPING = SynchedEntityData.defineId(Predator.class, EntityDataSerializers.BOOLEAN);

    private static final int ATTACK_ANIMATION_LENGTH = 20;

    public final boolean diurnal;
    private int attackAnimationRemainingTicks = 0;

    public static Predator createDiurnal(EntityType<? extends Predator> type, Level level)
    {
        return new Predator(type, level, true);
    }

    public Predator(EntityType<? extends Predator> type, Level level, boolean diurnal)
    {
        super(type, level);
        this.diurnal = diurnal;
        getNavigation().setCanFloat(true);
    }

    @Override
    protected Brain.Provider<Predator> brainProvider()
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
        super.tick();
        if (level.isClientSide && isSleeping() && getRandom().nextInt(5) == 0)
        {
            level.addParticle(TFCParticles.SLEEP.get(), getX(), getY() + getEyeHeight(), getZ(), 0.04, 0.2, 0.04);
        }
    }

    @Override
    public boolean hurt(DamageSource source, float amount)
    {
        boolean hurt = super.hurt(source, amount);
        if (!level.isClientSide && source instanceof EntityDamageSource entitySource && entitySource.getEntity() instanceof LivingEntity livingEntity && getHealth() > 0)
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
        attackAnimationRemainingTicks = ATTACK_ANIMATION_LENGTH;
        level.broadcastEntityEvent(this, (byte) 4);
        playSound(getAttackSound(), 1.0f, getVoicePitch());

        if (target instanceof Player player)
        {
            pinPlayer(player);
        }

        return hurt;
    }

    @Override
    public void aiStep()
    {
        if (attackAnimationRemainingTicks > 0)
        {
            --attackAnimationRemainingTicks;
        }
        super.aiStep(); // this method is called on both sides of LivingEntity#tick. So the name is rather improper
    }

    @Override
    public void handleEntityEvent(byte id)
    {
        if (id == 4)
        {
            attackAnimationRemainingTicks = 20;
            playSound(getAttackSound(), 1.0F, getVoicePitch());
        }
        super.handleEntityEvent(id);
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType type, @Nullable SpawnGroupData data, @Nullable CompoundTag tag)
    {
        getBrain().setMemory(MemoryModuleType.HOME, GlobalPos.of(level.getLevel().dimension(), blockPosition()));
        return super.finalizeSpawn(level, difficulty, type, data, tag);
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

    public int getAttackTicks()
    {
        return attackAnimationRemainingTicks <= 0 ? 0 : ATTACK_ANIMATION_LENGTH - attackAnimationRemainingTicks;
    }

    public SoundEvent getAttackSound()
    {
        return SoundEvents.POLAR_BEAR_WARNING;
    }

    private void pinPlayer(Player player)
    {
        if (distanceToSqr(player) < 6D)
        {
            if (!player.level.isClientSide)
            {
                player.addEffect(new MobEffectInstance(TFCEffects.PINNED.get(), 35, 0, false, false));
            }
        }
    }
}
