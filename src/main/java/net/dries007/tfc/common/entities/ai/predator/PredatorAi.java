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
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.*;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.NearestVisibleLivingEntities;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.schedule.Activity;

import com.mojang.datafixers.util.Pair;
import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.entities.ai.TFCBrain;
import net.dries007.tfc.common.entities.predator.Predator;
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
        MemoryModuleType.PACIFIED, MemoryModuleType.HOME, MemoryModuleType.HUNTED_RECENTLY
    );

    public static final int MAX_WANDER_DISTANCE = 100 * 100;
    public static final int MAX_ATTACK_DISTANCE = 80 * 80;

    public static Brain<?> makeBrain(Brain<Predator> brain, Predator predator)
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
        brain.updateActivityFromSchedule(predator.level.getDayTime(), predator.level.getGameTime());

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
                brain.updateActivityFromSchedule(predator.level.getDayTime(), predator.level.getGameTime());
            }
            else if (current == Activity.AVOID && !brain.hasMemoryValue(MemoryModuleType.PACIFIED))
            {
                brain.updateActivityFromSchedule(predator.level.getDayTime(), predator.level.getGameTime());
            }
        }
        predator.setAggressive(brain.hasMemoryValue(MemoryModuleType.ATTACK_TARGET));
    }

    public static void initCoreActivity(Brain<Predator> brain)
    {
        brain.addActivity(Activity.CORE, 0, ImmutableList.of(
            new Swim(0.8F),
            new LookAtTargetSink(45, 90),
            new MoveToTargetSink()
        ));
    }

    public static void initHuntActivity(Brain<Predator> brain)
    {
        brain.addActivity(TFCBrain.HUNT.get(), 10, ImmutableList.of(
            new BecomePassiveIfBehavior(p -> p.getHealth() < 5f, 200),
            new StartAttacking<>(PredatorAi::getAttackTarget),
            new RunSometimes<>(new SetEntityLookTarget(8.0F), UniformInt.of(30, 60)),
            new FindNewHomeBehavior(),
            createIdleMovementBehaviors(),
            new TickScheduleAndWakeBehavior()
        ));
    }

    public static void initRetreatActivity(Brain<Predator> brain)
    {
        brain.addActivityAndRemoveMemoryWhenStopped(Activity.AVOID, 10, ImmutableList.of(
            new RunIf<>(PredatorAi::hasNearbyAttacker, SetWalkTargetAwayFrom.entity(MemoryModuleType.HURT_BY_ENTITY, 1.2f, 16, true)),
            new RunSometimes<>(new StrollToPoi(MemoryModuleType.HOME, 1.2f, 5, MAX_WANDER_DISTANCE), UniformInt.of(30, 60)),
            createIdleMovementBehaviors()
        ), MemoryModuleType.PACIFIED);
    }

    public static void initRestActivity(Brain<Predator> brain)
    {
        brain.addActivity(Activity.REST, 10, ImmutableList.of(
            new RunIf<>(p -> !p.isSleeping(), new StrollToPoi(MemoryModuleType.HOME, 1.2F, 5, MAX_WANDER_DISTANCE)),
            new PredatorSleepBehavior(),
            new TickScheduleAndWakeBehavior(),
            new RunSometimes<>(new HealBehavior(1), UniformInt.of(100, 300))
        ));
    }

    public static void initFightActivity(Brain<Predator> brain)
    {
        brain.addActivityAndRemoveMemoryWhenStopped(Activity.FIGHT, 10, ImmutableList.<Behavior<? super Predator>>of(
            new BecomePassiveIfBehavior(p -> p.getHealth() < 5f, 200),
            new SetWalkTargetFromAttackTargetIfTargetOutOfReach(1.15F),
            new MeleeAttack(40),
            new PredatorStopAttackingBehavior()
        ), MemoryModuleType.ATTACK_TARGET);
    }

    private static RunOne<Predator> createIdleMovementBehaviors()
    {
        return new RunOne<>(ImmutableList.of(
            Pair.of(new RandomStroll(0.4F), 2),
            Pair.of(new SetWalkTargetFromLookTarget(0.4F, 3), 2),
            Pair.of(new DoNothing(30, 60), 1),
            Pair.of(new StrollToPoi(MemoryModuleType.HOME, 0.6F, 2, 5), 2),
            Pair.of(new StrollAroundPoi(MemoryModuleType.HOME, 0.6F, MAX_WANDER_DISTANCE), 3)
        ));
    }

    private static Optional<? extends LivingEntity> getAttackTarget(Predator predator)
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
        return predator.getBrain().hasMemoryValue(MemoryModuleType.PACIFIED) || predator.getBrain().hasMemoryValue(MemoryModuleType.HUNTED_RECENTLY);
    }

    public static double getDistanceFromHomeSqr(LivingEntity predator)
    {
        return predator.blockPosition().distSqr(getHomePos(predator));
    }

    public static BlockPos getHomePos(LivingEntity predator)
    {
        return predator.getBrain().getMemory(MemoryModuleType.HOME).orElseThrow().pos();
    }

    public static boolean hasNearbyAttacker(LivingEntity predator)
    {
        return predator.getBrain().getMemory(MemoryModuleType.HURT_BY_ENTITY).map(entity -> entity.distanceToSqr(predator) < 256).orElse(false);
    }
}
