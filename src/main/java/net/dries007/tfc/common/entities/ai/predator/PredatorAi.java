/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities.ai.predator;

import java.util.Optional;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.*;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.NearestVisibleLivingEntities;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.schedule.Activity;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.entities.ai.FastGateBehavior;
import net.dries007.tfc.common.entities.ai.SetLookTarget;
import net.dries007.tfc.common.entities.ai.TFCBrain;
import net.dries007.tfc.common.entities.ai.pet.MoveToTargetSinkIfNotSleeping;
import net.dries007.tfc.common.entities.predator.Predator;
import net.dries007.tfc.common.entities.prey.RammingPrey;
import net.dries007.tfc.util.Helpers;

public class PredatorAi
{
    public static final ImmutableList<? extends SensorType<? extends Sensor<? super Predator>>> SENSOR_TYPES = ImmutableList.of(
        SensorType.NEAREST_LIVING_ENTITIES, SensorType.NEAREST_PLAYERS, SensorType.HURT_BY
    );
    public static final ImmutableList<? extends MemoryModuleType<?>> MEMORY_TYPES = ImmutableList.of(
        MemoryModuleType.BREED_TARGET, MemoryModuleType.NEAREST_LIVING_ENTITIES, MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES, MemoryModuleType.NEAREST_VISIBLE_PLAYER,
        MemoryModuleType.NEAREST_VISIBLE_ATTACKABLE_PLAYER, MemoryModuleType.LOOK_TARGET, MemoryModuleType.WALK_TARGET, MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE,
        MemoryModuleType.PATH, MemoryModuleType.ATTACK_TARGET, MemoryModuleType.ATTACK_COOLING_DOWN,
        MemoryModuleType.PACIFIED, MemoryModuleType.HOME, MemoryModuleType.HUNTED_RECENTLY, TFCBrain.WAKEUP_TICKS.get()
    );

    public static final int MAX_WANDER_DISTANCE = 100 * 100;
    public static final int MAX_ATTACK_DISTANCE = 80 * 80;

    public static Brain<?> makeBrain(Brain<? extends Predator> brain, Predator predator)
    {
        initCoreActivity(brain);
        initHuntActivity(brain);
        initRetreatActivity(brain);
        initRestActivity(brain);
        initFightActivity(brain);

        brain.setSchedule(predator.diurnal ? TFCBrain.DIURNAL.get() : TFCBrain.NOCTURNAL.get());
        brain.setCoreActivities(ImmutableSet.of(Activity.CORE));
        brain.setDefaultActivity(TFCBrain.HUNT.get());
        brain.setActiveActivityIfPossible(TFCBrain.HUNT.get());
        brain.updateActivityFromSchedule(predator.level().getDayTime(), predator.level().getGameTime());

        return brain;
    }

    public static void updateActivity(Predator predator)
    {
        Brain<Predator> brain = predator.getBrain();
        brain.setActiveActivityToFirstValid(ImmutableList.of(Activity.AVOID, Activity.FIGHT));
        if (brain.getActiveNonCoreActivity().isPresent())
        {
            Activity current = brain.getActiveNonCoreActivity().get();
            if (current == Activity.FIGHT && !brain.hasMemoryValue(MemoryModuleType.ATTACK_TARGET))
            {
                brain.updateActivityFromSchedule(predator.level().getDayTime(), predator.level().getGameTime());
            }
            else if (current == Activity.AVOID && !brain.hasMemoryValue(MemoryModuleType.PACIFIED))
            {
                brain.updateActivityFromSchedule(predator.level().getDayTime(), predator.level().getGameTime());
            }
        }
        predator.setAggressive(brain.hasMemoryValue(MemoryModuleType.ATTACK_TARGET));
    }

    public static void initCoreActivity(Brain<? extends Predator> brain)
    {
        brain.addActivity(Activity.CORE, 0, ImmutableList.of(
            new AggressiveSwim(0.8F),
            new LookAtTargetSink(45, 90),
            new MoveToTargetSinkIfNotSleeping(),
            new CountDownCooldownTicks(TFCBrain.WAKEUP_TICKS.get())
        ));
    }

    public static void initHuntActivity(Brain<? extends Predator> brain)
    {
        brain.addActivity(TFCBrain.HUNT.get(), 10, ImmutableList.of(
            PredatorBehaviors.becomePassiveIf(p -> p.getHealth() < 5f, 200),
            StartAttacking.create(PredatorAi::getAttackTarget),
            SetLookTarget.create(8.0F, UniformInt.of(30, 60)),
            BabyFollowAdult.create(UniformInt.of(5, 16), 1.25F), // babies follow any random adult around
            createIdleMovementBehaviors(),
            PredatorBehaviors.tickScheduleAndWake()
        ));
    }

    public static void initRetreatActivity(Brain<? extends Predator> brain)
    {
        brain.addActivityAndRemoveMemoryWhenStopped(Activity.AVOID, 10, ImmutableList.of(
            BehaviorBuilder.triggerIf(PredatorAi::hasNearbyAttacker, SetWalkTargetAwayFrom.entity(MemoryModuleType.HURT_BY_ENTITY, 1.2f, 16, true)),
            StrollToPoi.create(MemoryModuleType.HOME, 1.2f, 5, MAX_WANDER_DISTANCE),
            createIdleMovementBehaviors()
        ), MemoryModuleType.PACIFIED);
    }

    public static void initRestActivity(Brain<? extends Predator> brain)
    {
        brain.addActivity(Activity.REST, 10, ImmutableList.of(
            StartAttacking.create(PredatorAi::getDisturbedAttackTarget),
            PredatorBehaviors.findNewHome(),
            StrollToPoi.create(MemoryModuleType.HOME, 1.2F, 5, MAX_WANDER_DISTANCE),
            PredatorBehaviors.startSleeping(),
            PredatorBehaviors.tickScheduleAndWake(),
            PredatorBehaviors.wakeFromDisturbance()
        ));
    }

    public static void initFightActivity(Brain<? extends Predator> brain)
    {
        brain.addActivityAndRemoveMemoryWhenStopped(Activity.FIGHT, 10, ImmutableList.of(
            PredatorBehaviors.becomePassiveIf(p -> p.getHealth() < 5f, 200),
            SetWalkTargetFromAttackTargetIfTargetOutOfReach.create(1.15F),
            MeleeAttack.create(40),
            PredatorBehaviors.stopAttackingIfTooFarFromHome()
        ), MemoryModuleType.ATTACK_TARGET);
    }

    public static FastGateBehavior<Predator> createIdleMovementBehaviors()
    {
        return FastGateBehavior.runOne(ImmutableList.of(
            RandomStroll.stroll(0.4F),
            SetWalkTargetFromLookTarget.create(0.4F, 3),
            new DoNothing(30, 60),
            StrollToPoi.create(MemoryModuleType.HOME, 0.6F, 2, 5),
            StrollAroundPoi.create(MemoryModuleType.HOME, 0.6F, MAX_WANDER_DISTANCE)
        ));
    }

    public static Optional<? extends LivingEntity> getDisturbedAttackTarget(Predator predator)
    {
        return (predator.getBrain().getMemory(TFCBrain.WAKEUP_TICKS.get()).isPresent()) ? getAttackTarget(predator) : Optional.empty();
    }

    public static Optional<? extends LivingEntity> getAttackTarget(Predator predator)
    {
        if (isPacified(predator))
        {
            return Optional.empty();
        }
        Brain<Predator> brain = predator.getBrain();
        if (brain.hasMemoryValue(MemoryModuleType.NEAREST_VISIBLE_ATTACKABLE_PLAYER))
        {
            return brain.getMemory(MemoryModuleType.NEAREST_VISIBLE_ATTACKABLE_PLAYER);
        }
        if (brain.hasMemoryValue(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES))
        {
            NearestVisibleLivingEntities nearestEntities = brain.getMemory(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES).get();
            return nearestEntities.findClosest(e -> Helpers.isEntity(e, TFCTags.Entities.HUNTED_BY_LAND_PREDATORS) && !e.isInWater());
        }
        return Optional.empty();
    }

    private static boolean isPacified(Predator predator)
    {
        return predator.isBaby() || predator.getBrain().hasMemoryValue(MemoryModuleType.PACIFIED) || predator.getBrain().hasMemoryValue(MemoryModuleType.HUNTED_RECENTLY);
    }

    public static double getDistanceFromHomeSqr(LivingEntity predator)
    {
        return predator.blockPosition().distSqr(getHomePos(predator));
    }

    public static BlockPos getHomePos(LivingEntity predator)
    {
        Optional<GlobalPos> memory = predator.getBrain().getMemory(MemoryModuleType.HOME);
        if (memory.isPresent())
        {
            return memory.get().pos();
        }
        else
        {
            predator.getBrain().setMemory(MemoryModuleType.HOME, GlobalPos.of(predator.level().dimension(), predator.blockPosition()));
            return predator.blockPosition();
        }
    }

    public static boolean hasNearbyAttacker(LivingEntity predator)
    {
        return predator.getBrain().getMemory(MemoryModuleType.HURT_BY_ENTITY).map(entity -> entity.distanceToSqr(predator) < 256).orElse(false);
    }
}
