/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities.ai.prey;

import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import net.dries007.tfc.common.entities.prey.RammingPrey;

public class RamTargetTFC extends Behavior<RammingPrey>
{
    public static final int TIME_OUT_DURATION = 200;
    public static final float RAM_SPEED_FORCE_FACTOR = 1.65F;
    private final Function<RammingPrey, UniformInt> getTimeBetweenRams;
    private final TargetingConditions ramTargeting;
    private final float speed;
    private final ToDoubleFunction<RammingPrey> getKnockbackForce;
    private Vec3 ramDirection;
    private final Function<RammingPrey, SoundEvent> getImpactSound;

    public RamTargetTFC(Function<RammingPrey, UniformInt> getTimeBetweenRams, TargetingConditions ramTargeting, float speed, ToDoubleFunction<RammingPrey> getKnockbackForce, Function<RammingPrey, SoundEvent> getImpactSound)
    {
        super(ImmutableMap.of(MemoryModuleType.RAM_COOLDOWN_TICKS, MemoryStatus.VALUE_ABSENT, MemoryModuleType.RAM_TARGET, MemoryStatus.VALUE_PRESENT), 200);
        this.getTimeBetweenRams = getTimeBetweenRams;
        this.ramTargeting = ramTargeting;
        this.speed = speed;
        this.getKnockbackForce = getKnockbackForce;
        this.getImpactSound = getImpactSound;
        this.ramDirection = Vec3.ZERO;
    }

    protected boolean checkExtraStartConditions(ServerLevel level, RammingPrey rammingPrey)
    {
        return rammingPrey.getBrain().hasMemoryValue(MemoryModuleType.RAM_TARGET);
    }

    protected boolean canStillUse(ServerLevel level, RammingPrey rammingPrey, long time)
    {
        return rammingPrey.getBrain().hasMemoryValue(MemoryModuleType.RAM_TARGET);
    }

    protected void start(ServerLevel level, RammingPrey rammingPrey, long time)
    {
        BlockPos blockpos = rammingPrey.blockPosition();
        Brain<?> brain = rammingPrey.getBrain();
        Vec3 ramTargetVector = brain.getMemory(MemoryModuleType.RAM_TARGET).get();
        this.ramDirection = (new Vec3((double) blockpos.getX() - ramTargetVector.x(), 0.0D, (double) blockpos.getZ() - ramTargetVector.z())).normalize();
        brain.setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(ramTargetVector, this.speed, 0));
    }

    protected void tick(ServerLevel level, RammingPrey rammingPrey, long time)
    {
        Brain<?> brain = rammingPrey.getBrain();
        //Generate list of nearby entities meeting the Targeting conditions
        List<LivingEntity> list = level.getNearbyEntities(LivingEntity.class, this.ramTargeting, rammingPrey, rammingPrey.getBoundingBox().inflate(rammingPrey.getRammingReach()));
        //Damages any targetable entities that come within the animal's bounding box
        if (!list.isEmpty())
        {
            LivingEntity livingentity = list.get(0);
            livingentity.hurt(level.damageSources().noAggroMobAttack(rammingPrey), ((float) rammingPrey.getAttributeValue(Attributes.ATTACK_DAMAGE)) * rammingPrey.getAttackDamageMultiplier());
            int i = rammingPrey.hasEffect(MobEffects.MOVEMENT_SPEED) ? rammingPrey.getEffect(MobEffects.MOVEMENT_SPEED).getAmplifier() + 1 : 0;
            int j = rammingPrey.hasEffect(MobEffects.MOVEMENT_SLOWDOWN) ? rammingPrey.getEffect(MobEffects.MOVEMENT_SLOWDOWN).getAmplifier() + 1 : 0;
            float f = 0.25F * (float) (i - j);
            float f1 = Mth.clamp(rammingPrey.getSpeed() * 1.65F, 0.2F, 3.0F) + f;
            float f2 = livingentity.isDamageSourceBlocked(level.damageSources().mobAttack(rammingPrey)) ? 0.5F : 1.0F;
            float f3 = rammingPrey.getAttackDamageMultiplier() * 0.6f;
            livingentity.knockback((double) (f1 * f2 * f3) * this.getKnockbackForce.applyAsDouble(rammingPrey), this.ramDirection.x(), this.ramDirection.z());
            this.finishRam(level, rammingPrey);
            level.playSound((Player) null, rammingPrey, this.getImpactSound.apply(rammingPrey), SoundSource.NEUTRAL, 1.0F, 1.0F);
        }
        else
        {
            Optional<WalkTarget> optionalWalkTarget = brain.getMemory(MemoryModuleType.WALK_TARGET);
            Optional<Vec3> optionalRamTarget = brain.getMemory(MemoryModuleType.RAM_TARGET);
            boolean flag1 = optionalWalkTarget.isEmpty() || optionalRamTarget.isEmpty() || optionalWalkTarget.get().getTarget().currentPosition().closerThan(optionalRamTarget.get(), 0.25D);
            if (flag1)
            {
                this.finishRam(level, rammingPrey);
            }
        }

    }

    protected void finishRam(ServerLevel serverLevel, RammingPrey rammingPrey)
    {
        serverLevel.broadcastEntityEvent(rammingPrey, (byte) 59);
        rammingPrey.getBrain().setMemory(MemoryModuleType.RAM_COOLDOWN_TICKS, this.getTimeBetweenRams.apply(rammingPrey).sample(serverLevel.random));
        rammingPrey.getBrain().eraseMemory(MemoryModuleType.RAM_TARGET);
    }
}