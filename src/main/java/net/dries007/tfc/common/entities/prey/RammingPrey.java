/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities.prey;

import java.util.function.Supplier;
import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Dynamic;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.client.TFCSounds;
import net.dries007.tfc.common.entities.EntityHelpers;
import net.dries007.tfc.common.entities.ai.prey.RammingPreyAi;

public class RammingPrey extends WildAnimal
{
    protected static final ImmutableList<SensorType<? extends Sensor<? super RammingPrey>>> SENSOR_TYPES = ImmutableList.of(
        SensorType.NEAREST_LIVING_ENTITIES,
        SensorType.NEAREST_PLAYERS,
        SensorType.NEAREST_ITEMS,
        SensorType.NEAREST_ADULT,
        SensorType.HURT_BY);
    protected static final ImmutableList<MemoryModuleType<?>> MEMORY_TYPES = ImmutableList.of(
        MemoryModuleType.LOOK_TARGET,
        MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES,
        MemoryModuleType.WALK_TARGET,
        MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE,
        MemoryModuleType.PATH,
        MemoryModuleType.NEAREST_VISIBLE_ADULT,
        MemoryModuleType.RAM_COOLDOWN_TICKS,
        MemoryModuleType.RAM_TARGET,
        MemoryModuleType.IS_PANICKING,
        MemoryModuleType.HURT_BY_ENTITY);

    private final Supplier<SoundEvent> attack;

    public final AnimationState telegraphAnimation = new AnimationState();
    public final AnimationState attackingAnimation = new AnimationState();

    private boolean isTelegraphingAttack;
    private int telegraphAttackTick;
    private float attackDamageMultiplier = 1f;
    private final double rammingReach;

    public static AttributeSupplier.Builder createAttributes()
    {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 20.0D).add(Attributes.MOVEMENT_SPEED, 0.16F).add(Attributes.ATTACK_DAMAGE, 6.0D).add(Attributes.KNOCKBACK_RESISTANCE, 0.8);
    }
    public static AttributeSupplier.Builder createMediumAttributes()
    {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 30.0D).add(Attributes.MOVEMENT_SPEED, 0.16F).add(Attributes.ATTACK_DAMAGE, 6.0D).add(Attributes.KNOCKBACK_RESISTANCE, 0.9);
    }
    public static AttributeSupplier.Builder createLargeAttributes()
    {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 40.0D).add(Attributes.MOVEMENT_SPEED, 0.12F).add(Attributes.ATTACK_DAMAGE, 6.0D).add(Attributes.KNOCKBACK_RESISTANCE, 1.0);
    }

    public final AnimationState walkingAnimation = new AnimationState();

    public RammingPrey(EntityType<? extends RammingPrey> type, Level level, TFCSounds.EntityId sounds, double rammingReach)
    {
        super(type, level, sounds);
        this.attack = sounds.attack().orElseThrow();
        //rammingReach is the amount the entity's bounding box should be expanded when dealing ramming damage. 0.1
        this.rammingReach = rammingReach;
    }

    @Override
    protected Brain.Provider<? extends RammingPrey> brainProvider()
    {
        return Brain.provider(RammingPrey.MEMORY_TYPES, RammingPrey.SENSOR_TYPES);
    }

    @Override
    protected Brain<?> makeBrain(Dynamic<?> dynamic)
    {
        return RammingPreyAi.makeBrain(brainProvider().makeBrain(dynamic));
    }

    @Override
    protected void customServerAiStep()
    {
        getBrain().tick((ServerLevel) level(), this);
        RammingPreyAi.updateActivity(this);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Brain<RammingPrey> getBrain()
    {
        return (Brain<RammingPrey>) super.getBrain();
    }

    @Override
    public boolean hurt(DamageSource source, float amount)
    {
        boolean hurt = super.hurt(source, amount);
        if (!level().isClientSide && isAlive())
        {
            brain.eraseMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
        }
        return hurt;
    }

    @Override
    public void tick()
    {
        if (level().isClientSide)
        {
            EntityHelpers.startOrStop(walkingAnimation, EntityHelpers.isMovingOnLand(this), tickCount);
        }
        super.tick();
    }

    @Override
    public void handleEntityEvent(byte id)
    {
        if (id == 58)
        {
            this.isTelegraphingAttack = true;
        }
        else if (id == 59)
        {
            this.isTelegraphingAttack = false;
        }
        else if (id == 4)
        {
            attackingAnimation.start(tickCount);
        }
        else
        {
            super.handleEntityEvent(id);
        }
    }

    @Override
    public void aiStep()
    {
        if (this.isTelegraphingAttack)
        {
            ++this.telegraphAttackTick;
        }
        else
        {
            this.telegraphAttackTick = 0;
        }

        this.telegraphAttackTick = Mth.clamp(this.telegraphAttackTick, 0, 20);
        super.aiStep();
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData spawnData)
    {
        RammingPreyAi.initMemories(this, level.getRandom());
        return super.finalizeSpawn(level, difficulty, spawnType, spawnData);
    }

    public int getTelegraphAttackTick()
    {
        return telegraphAttackTick;
    }

    public boolean isTelegraphingAttack()
    {
        return isTelegraphingAttack;
    }

    public Supplier<SoundEvent> getAttackSound()
    {
        return attack;
    }

    public void setAttackDamageMultiplier(float multiplier)
    {
        this.attackDamageMultiplier = multiplier;
    }

    public float getAttackDamageMultiplier()
    {
        return this.attackDamageMultiplier;
    }

    public double getRammingReach()
    {
        return this.rammingReach;
    }

}
