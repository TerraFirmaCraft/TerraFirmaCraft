/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities.ai.prey;

import java.util.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.AnimalPanic;
import net.minecraft.world.entity.ai.behavior.BabyFollowAdult;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.behavior.DoNothing;
import net.minecraft.world.entity.ai.behavior.EraseMemoryIf;
import net.minecraft.world.entity.ai.behavior.LookAtTargetSink;
import net.minecraft.world.entity.ai.behavior.MoveToTargetSink;
import net.minecraft.world.entity.ai.behavior.RandomStroll;
import net.minecraft.world.entity.ai.behavior.SetWalkTargetAwayFrom;
import net.minecraft.world.entity.ai.behavior.SetWalkTargetFromLookTarget;
import net.minecraft.world.entity.ai.behavior.Swim;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.schedule.Activity;

import net.dries007.tfc.common.entities.ai.FastGateBehavior;
import net.dries007.tfc.common.entities.ai.SetLookTarget;
import net.dries007.tfc.common.entities.prey.Prey;

public class PreyAi
{
    public static final int AVOID_RANGE = 20 * 20;

    private static final UniformInt RETREAT_DURATION = TimeUtil.rangeOfSeconds(7, 22);

    public static final ImmutableList<SensorType<? extends Sensor<? super Prey>>> SENSOR_TYPES = ImmutableList.of(
        SensorType.NEAREST_LIVING_ENTITIES, SensorType.NEAREST_PLAYERS, SensorType.HURT_BY
    );

    public static final ImmutableList<MemoryModuleType<?>> MEMORY_TYPES = ImmutableList.of(
        MemoryModuleType.LOOK_TARGET, MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES, MemoryModuleType.NEAREST_LIVING_ENTITIES, MemoryModuleType.WALK_TARGET,
        MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, MemoryModuleType.PATH, MemoryModuleType.AVOID_TARGET, MemoryModuleType.NEAREST_VISIBLE_ADULT
    );

    public static Brain<?> makeBrain(Brain<? extends Prey> brain)
    {
        initCoreActivity(brain);
        initIdleActivity(brain);
        initRetreatActivity(brain);

        brain.setCoreActivities(ImmutableSet.of(Activity.CORE));
        brain.setDefaultActivity(Activity.IDLE);
        brain.useDefaultActivity();

        return brain;
    }

    public static void initCoreActivity(Brain<? extends Prey> brain)
    {
        brain.addActivity(Activity.CORE, 0, ImmutableList.of(
            new Swim(0.7F), // float in water
            new AnimalPanic<>(2.0F), // if memory of being hit, runs away
            new LookAtTargetSink(45, 90), // if memory of look target, looks at that
            new MoveToTargetSink() // tries to walk to its internal walk target. This could just be a random block.
        ));
    }

    public static void initIdleActivity(Brain<? extends Prey> brain)
    {
        brain.addActivity(Activity.IDLE, ImmutableList.of(
            Pair.of(0, SetLookTarget.create(EntityType.PLAYER, 6.0F, UniformInt.of(30, 60))), // looks at player, but its only try it every so often -- "Run Sometimes"
            Pair.of(1, AvoidPredatorBehavior.create(false)),
            Pair.of(2, BabyFollowAdult.create(UniformInt.of(5, 16), 1.25F)), // babies follow any random adult around
            Pair.of(3, createIdleMovementBehaviors())
        ));
    }

    /**
     * Focuses on retreating from the avoiding target.
     * What the name "addActivityAndRemoveMemoryWhenStopped" does not say is that the erased memory is REQUIRED to start this activity
     * In other words, this is triggered automatically by updateActivity if AVOID_TARGET is present.
     */
    public static void initRetreatActivity(Brain<? extends Prey> brain)
    {
        brain.addActivityAndRemoveMemoryWhenStopped(Activity.AVOID, 10, ImmutableList.of(
            SetWalkTargetAwayFrom.entity(MemoryModuleType.AVOID_TARGET, 1.1F, 15, false),
            createIdleMovementBehaviors(),
            SetLookTarget.create(8.0F, UniformInt.of(30, 60)),
            EraseMemoryIf.create(PreyAi::wantsToStopFleeing, MemoryModuleType.AVOID_TARGET) // essentially ends the activity
            ),
            MemoryModuleType.AVOID_TARGET
        );
    }

    public static FastGateBehavior<Prey> createIdleMovementBehaviors()
    {
        return FastGateBehavior.runOne(ImmutableList.of(
            // Chooses one of these behaviors to run. Notice that all three of these are basically the fallback walking around behaviors, and it doesn't make sense to check them all every time
            RandomStroll.stroll(1.0F), // picks a random place to walk to
            SetWalkTargetFromLookTarget.create(1.0F, 3), // walk to what it is looking at
            new DoNothing(30, 60)
        )); // do nothing for a certain period of time
    }

    public static void updateActivity(Prey prey)
    {
        prey.getBrain().setActiveActivityToFirstValid(ImmutableList.of(Activity.AVOID, Activity.IDLE));
    }

    public static Optional<LivingEntity> getAvoidTarget(LivingEntity prey)
    {
        return prey.getBrain().hasMemoryValue(MemoryModuleType.AVOID_TARGET) ? prey.getBrain().getMemory(MemoryModuleType.AVOID_TARGET) : Optional.empty();
    }

    public static void wasHurtBy(LivingEntity prey, LivingEntity attacker)
    {
        Brain<?> brain = prey.getBrain();
        getAvoidTarget(prey).ifPresent(avoid -> {
            if (avoid.getType() != attacker.getType())
            {
                brain.eraseMemory(MemoryModuleType.AVOID_TARGET);
                brain.setMemoryWithExpiry(MemoryModuleType.AVOID_TARGET, attacker, RETREAT_DURATION.sample(prey.getRandom()));
                broadcastAvoidTarget(prey, attacker);
            }
        });
    }

    public static void broadcastAvoidTarget(LivingEntity prey, LivingEntity attacker)
    {
        Brain<?> brain = prey.getBrain();
        brain.getMemory(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES).ifPresent(entities -> {
            entities.findAll(e -> e.getType() == prey.getType()).forEach(friend -> {
                LivingEntity closest = BehaviorUtils.getNearestTarget(friend, friend.getBrain().getMemory(MemoryModuleType.AVOID_TARGET), attacker);
                setAvoidTarget(friend, closest);
            });
        });
    }

    public static void setAvoidTarget(LivingEntity prey, LivingEntity attacker)
    {
        Brain<?> brain = prey.getBrain();
        brain.eraseMemory(MemoryModuleType.WALK_TARGET);
        brain.eraseMemory(MemoryModuleType.LOOK_TARGET);
        brain.setMemoryWithExpiry(MemoryModuleType.AVOID_TARGET, attacker, RETREAT_DURATION.sample(prey.getRandom()));
    }

    public static boolean wantsToStopFleeing(LivingEntity prey)
    {
        final Brain<?> brain = prey.getBrain();
        return !brain.hasMemoryValue(MemoryModuleType.AVOID_TARGET) || brain.getMemory(MemoryModuleType.AVOID_TARGET).get().distanceToSqr(prey) > AVOID_RANGE;
    }
}
