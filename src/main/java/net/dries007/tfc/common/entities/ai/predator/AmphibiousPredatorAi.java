/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities.ai.predator;

import java.util.List;
import java.util.Optional;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.*;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.NearestVisibleLivingEntities;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.level.Level;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.entities.ai.SetLookTarget;
import net.dries007.tfc.common.entities.ai.TFCBrain;
import net.dries007.tfc.common.entities.ai.amphibian.AmphibianAi;
import net.dries007.tfc.common.entities.ai.pet.MoveToTargetSinkIfNotSleeping;
import net.dries007.tfc.common.entities.aquatic.AmphibiousAnimal;
import net.dries007.tfc.common.entities.predator.AmphibiousPredator;
import net.dries007.tfc.common.entities.predator.Predator;
import net.dries007.tfc.util.Helpers;

import static net.dries007.tfc.common.entities.ai.predator.PredatorAi.*;

public class AmphibiousPredatorAi
{
    public static final ImmutableList<? extends SensorType<? extends Sensor<? super Predator>>> SENSOR_TYPES = ImmutableList.of(
        SensorType.NEAREST_LIVING_ENTITIES, SensorType.NEAREST_PLAYERS, SensorType.HURT_BY
    );
    public static final ImmutableList<MemoryModuleType<?>> MEMORY_TYPES = Util.make(() -> {
        List<MemoryModuleType<?>> list = Lists.newArrayList(PredatorAi.MEMORY_TYPES);
        return ImmutableList.copyOf(list);
    });

    public static final int MAX_WANDER_DISTANCE = 100 * 100;
    public static final int MAX_ATTACK_DISTANCE = 80 * 80;

    public static Brain<?> makeBrain(Brain<? extends Predator> brain, Predator predator)
    {
        initCoreActivity(brain);
        initHuntActivity(brain);
        initRetreatActivity(brain);
        PredatorAi.initRestActivity(brain);
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
            new LookAtTargetSink(45, 90),
            new MoveToTargetSinkIfNotSleeping(),
            new CountDownCooldownTicks(TFCBrain.WAKEUP_TICKS.get())
        ));
    }

    public static void initHuntActivity(Brain<? extends Predator> brain)
    {
        brain.addActivity(TFCBrain.HUNT.get(), ImmutableList.of(
            Pair.of(0, PredatorBehaviors.becomePassiveIf(p -> p.getHealth() < 5f, 200)),
            Pair.of(1, StartAttacking.create(PredatorAi::getAttackTarget)),
            Pair.of(2, SetLookTarget.create(8.0F, UniformInt.of(30, 60))),
            Pair.of(3, TryFindWater.create(6, 1F)),
            Pair.of(5, RandomStroll.swim(1.0F)),
            Pair.of(6, RandomStroll.stroll(1F, false)),
            Pair.of(7, SetWalkTargetFromLookTarget.create(AmphibiousPredatorAi::canSetWalkTargetFromLookTarget, AmphibiousPredatorAi::getSpeedModifier, 3)),
            Pair.of(10, BabyFollowAdult.create(UniformInt.of(5, 16), 1.25F)), // babies follow any random adult around
            Pair.of(11, createIdleMovementBehaviors()),
            Pair.of(12, PredatorBehaviors.tickScheduleAndWake())
        ));
    }

    public static void initRetreatActivity(Brain<? extends Predator> brain)
    {
        brain.addActivityAndRemoveMemoryWhenStopped(Activity.AVOID, 10, ImmutableList.of(
            BehaviorBuilder.triggerIf(PredatorAi::hasNearbyAttacker, SetWalkTargetAwayFrom.entity(MemoryModuleType.HURT_BY_ENTITY, 1.2f, 16, true)),
            StrollToPoi.create(MemoryModuleType.HOME, 0.7f, 5, MAX_WANDER_DISTANCE),
            createIdleMovementBehaviors()
        ), MemoryModuleType.PACIFIED);
    }

    public static void initFightActivity(Brain<? extends Predator> brain)
    {
        brain.addActivityAndRemoveMemoryWhenStopped(Activity.FIGHT, 10, ImmutableList.of(
            PredatorBehaviors.becomePassiveIf(p -> p.getHealth() < 5f, 200),
            SetWalkTargetFromAttackTargetIfTargetOutOfReach.create(AmphibiousPredatorAi::getSpeedModifier),
            MeleeAttack.create(40),
            PredatorBehaviors.stopAttackingIfTooFarFromHome()
        ), MemoryModuleType.ATTACK_TARGET);
    }

    private static float getSpeedModifier(LivingEntity entity)
    {
        return entity.isInWaterOrBubble() ? 1.6F : 1.0F;
    }

    private static boolean canSetWalkTargetFromLookTarget(LivingEntity entity)
    {
        Level level = entity.level();
        Optional<PositionTracker> tracker = entity.getBrain().getMemory(MemoryModuleType.LOOK_TARGET);
        if (tracker.isPresent())
        {
            BlockPos pos = tracker.get().currentBlockPosition();
            return level.isWaterAt(pos) == entity.isInWaterOrBubble();
        }
        return false;
    }


}
