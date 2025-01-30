/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities.ai.predator;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.ai.behavior.EntityTracker;
import net.minecraft.world.entity.ai.behavior.OneShot;
import net.minecraft.world.entity.ai.behavior.StopAttackingIfTargetInvalid;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.phys.Vec3;

import net.dries007.tfc.common.entities.ai.TFCBrain;
import net.dries007.tfc.common.entities.predator.Predator;

public final class PredatorBehaviors
{
    /**
     * If the given predicate triggers, stop attacking, and become passive for {@code ticks}
     */
    public static OneShot<Predator> becomePassiveIf(Predicate<Predator> predicate, int ticks)
    {
        return BehaviorBuilder.triggerIf(predicate, BehaviorBuilder.create(instance -> instance.group(
            instance.registered(MemoryModuleType.ATTACK_TARGET),
            instance.absent(MemoryModuleType.PACIFIED)
        ).apply(instance, (attackMemory, passiveMemory) -> (level, predator, time) -> {
            attackMemory.erase();
            passiveMemory.setWithExpiry(true, ticks);
            return true;
        })));
    }

    /**
     * Prevents a predator from wandering too far from their home.
     */
    public static BehaviorControl<Mob> stopAttackingIfTooFarFromHome()
    {
        return stopAttackingIf(
            (predator, target) -> PredatorAi.getDistanceFromHomeSqr(predator) > PredatorAi.MAX_ATTACK_DISTANCE,
            (predator, target) -> {
                if (target.isDeadOrDying())
                {
                    final Brain<?> brain = predator.getBrain();
                    brain.setMemoryWithExpiry(MemoryModuleType.HUNTED_RECENTLY, true, 12000);
                    if (brain.hasMemoryValue(MemoryModuleType.HURT_BY_ENTITY))
                    {
                        brain.eraseMemory(MemoryModuleType.HURT_BY_ENTITY);
                    }
                }
            }
        );
    }

    /**
     * This is similar to {@link StopAttackingIfTargetInvalid} but with a predicate based on the attacker, not just the target.
     *
     * @param canStopAttacking A predicate of (attacker, target) entity, returns {@code true} if it can stop attacking.
     */
    public static <E extends Mob> BehaviorControl<E> stopAttackingIf(BiPredicate<E, LivingEntity> canStopAttacking, BiConsumer<Mob, LivingEntity> onStopAttacking)
    {
        return BehaviorBuilder.create(instance -> instance.group(
            instance.present(MemoryModuleType.ATTACK_TARGET),
            instance.registered(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE)
        ).apply(instance, (attackTarget, cantReachWalkTargetSince) -> (level, attackerMob, gameTime) -> {
            final LivingEntity attackedMob = instance.get(attackTarget);
            if (!attackerMob.canAttack(attackedMob)
                || isTiredOfTryingToReachTarget(attackerMob, instance.tryGet(cantReachWalkTargetSince))
                || !attackedMob.isAlive()
                || attackedMob.level() != attackerMob.level()
                || canStopAttacking.test(attackerMob, attackedMob))
            {
                onStopAttacking.accept(attackerMob, attackedMob);
                attackTarget.erase();
            }
            return true;
        }));
    }

    public static OneShot<Predator> startSleeping()
    {
        return BehaviorBuilder.triggerIf(entity -> PredatorAi.getDistanceFromHomeSqr(entity) < 25 && !entity.isSleeping() && !entity.isInWaterOrBubble(), BehaviorBuilder.create(instance -> instance.group(
            instance.absent(MemoryModuleType.ATTACK_TARGET),
            instance.absent(TFCBrain.WAKEUP_TICKS.get()),
            instance.registered(MemoryModuleType.WALK_TARGET),
            instance.registered(MemoryModuleType.LOOK_TARGET)
        ).apply(instance, (attack, tick, walk, look) -> (level, predator, time) -> {
            walk.erase();
            look.erase();
            predator.setSleeping(true);
            return true;
        })));
    }

    public static OneShot<Predator> tickScheduleAndWake()
    {
        return BehaviorBuilder.create(instance -> instance.group(
            instance.absent(MemoryModuleType.ATTACK_TARGET)
        ).apply(instance, attack -> (level, predator, time) -> {
            Optional<Activity> before = predator.getBrain().getActiveNonCoreActivity();
            predator.getBrain().updateActivityFromSchedule(level.getDayTime(), level.getGameTime());
            Optional<Activity> after = predator.getBrain().getActiveNonCoreActivity();
            if (before.isPresent() && after.isPresent() && before.get() == Activity.REST && after.get() != Activity.REST)
            {
                predator.setSleeping(false);
                return true;
            }
            return false;
        }));
    }

    //Wake if predator is in water, is touched by an entity, or is more than 5 blocks from its home
    public static OneShot<Predator> wakeFromDisturbance()
    {
        return BehaviorBuilder.triggerIf(entity -> (PredatorAi.getDistanceFromHomeSqr(entity) > 25
            || entity.isInWaterOrBubble()
            || !entity.level().getNearbyEntities(LivingEntity.class, TargetingConditions.DEFAULT, entity, entity.getBoundingBox()).isEmpty())
            && entity.isSleeping(),
            BehaviorBuilder.create(instance -> instance.group(
                instance.absent(MemoryModuleType.ATTACK_TARGET)
            ).apply(instance, (attack) -> (level, predator, time) -> {
                predator.setSleeping(false);
                predator.getBrain().setMemory(TFCBrain.WAKEUP_TICKS.get(), 100);
                return true;
        })));
    }

    //Activates if predator wanders outside of territory or tries to sleep in water
    public static OneShot<Predator> findNewHome()
    {
        return BehaviorBuilder.triggerIf(predator -> (PredatorAi.getDistanceFromHomeSqr(predator) > PredatorAi.MAX_WANDER_DISTANCE || (PredatorAi.getDistanceFromHomeSqr(predator) < 9 && predator.isInWaterOrBubble())), BehaviorBuilder.create(instance -> instance.group(
            instance.present(MemoryModuleType.HOME),
            instance.registered(MemoryModuleType.WALK_TARGET)
        ).apply(instance, (homeMemory, walkMemory) -> (level, predator, time) -> {
            final Vec3 found = LandRandomPos.getPos(predator, 10, 5);
            if (found != null)
            {
                homeMemory.set(GlobalPos.of(level.dimension(), BlockPos.containing(found)));
                walkMemory.set(new WalkTarget(found, 1.0f, 16));
                return true;
            }
            return false;
        })));
    }

    public static OneShot<Predator> listenToAlpha()
    {
        return BehaviorBuilder.triggerIf(PackPredatorAi::isNotAlpha, BehaviorBuilder.create(instance -> instance.group(
            instance.registered(MemoryModuleType.WALK_TARGET),
            instance.registered(MemoryModuleType.HOME),
            instance.absent(MemoryModuleType.ATTACK_TARGET)
        ).apply(instance, (walk, home, attackTarget) -> (level, predator, time) -> {
            final LivingEntity alpha = PackPredatorAi.getAlpha(predator);
            final var alphaBrain = alpha.getBrain();

            alphaBrain.getMemory(MemoryModuleType.HOME).ifPresent(home::set);

            if (!alpha.equals(predator))
            {
                walk.set(new WalkTarget(new EntityTracker(alpha, false), 1.1f, 6));
            }
            return true;
        })));
    }

    private static boolean isTiredOfTryingToReachTarget(LivingEntity entity, Optional<Long> timeSinceInvalid)
    {
        return timeSinceInvalid.isPresent() && entity.level().getGameTime() - timeSinceInvalid.get() > 200L;
    }
}
