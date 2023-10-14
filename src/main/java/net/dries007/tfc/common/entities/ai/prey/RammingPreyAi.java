/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities.ai.prey;

import java.util.Optional;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.*;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.goat.Goat;
import net.minecraft.world.entity.schedule.Activity;

import com.mojang.datafixers.util.Pair;

import net.dries007.tfc.common.entities.ai.FastGateBehavior;
import net.dries007.tfc.common.entities.ai.SetLookTarget;
import net.dries007.tfc.common.entities.prey.Prey;
import net.dries007.tfc.common.entities.prey.RammingPrey;

public class RammingPreyAi
{
    public static final int RAM_PREPARE_TIME = 20;
    public static final int RAM_MIN_DISTANCE = 4;
    public static final int RAM_MAX_DISTANCE = 7;
    private static final float SPEED_MULTIPLIER_WHEN_PREPARING_TO_RAM = 1.25F;
    private static final float SPEED_MULTIPLIER_WHEN_RAMMING = 3.0F;
    public static final float ADULT_RAM_KNOCKBACK_FORCE = 2.5F;
    public static final float BABY_RAM_KNOCKBACK_FORCE = 1.0F;
    private static final UniformInt TIME_BETWEEN_RAMS_MALE = UniformInt.of(100, 150);
    private static final UniformInt TIME_BETWEEN_RAMS_FEMALE = UniformInt.of(200, 300);
    private static final TargetingConditions RAM_TARGET_CONDITIONS = TargetingConditions.forCombat().selector((target) -> {
        //TODO: Better target selection
        return !target.getType().equals(EntityType.GOAT) && target.level().getWorldBorder().isWithinBounds(target.getBoundingBox());
    });

    public static final int AVOID_RANGE = 20 * 20;

    private static final UniformInt RETREAT_DURATION = TimeUtil.rangeOfSeconds(7, 22);

    public static final ImmutableList<SensorType<? extends Sensor<? super RammingPrey>>> SENSOR_TYPES = ImmutableList.of(
        SensorType.NEAREST_LIVING_ENTITIES, SensorType.NEAREST_PLAYERS, SensorType.HURT_BY
    );

    public static final ImmutableList<MemoryModuleType<?>> MEMORY_TYPES = ImmutableList.of(
        MemoryModuleType.LOOK_TARGET, MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES, MemoryModuleType.NEAREST_LIVING_ENTITIES, MemoryModuleType.WALK_TARGET,
        MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, MemoryModuleType.PATH, MemoryModuleType.AVOID_TARGET, MemoryModuleType.NEAREST_VISIBLE_ADULT
    );

    public static Brain<?> makeBrain(Brain<? extends RammingPrey> brain)
    {
        initCoreActivity(brain);
        initIdleActivity(brain);
        initRetreatActivity(brain);
        initRamActivity(brain);

        brain.setCoreActivities(ImmutableSet.of(Activity.CORE));
        brain.setDefaultActivity(Activity.IDLE);
        brain.useDefaultActivity();

        return brain;
    }

    public static void initCoreActivity(Brain<? extends RammingPrey> brain)
    {
        brain.addActivity(Activity.CORE, 0, ImmutableList.of(
            new Swim(0.7F), // float in water
            new AnimalPanic(2.0F), // if memory of being hit, runs away
            new LookAtTargetSink(45, 90), // if memory of look target, looks at that
            new MoveToTargetSink(), // tries to walk to its internal walk target. This could just be a random block.
            new CountDownCooldownTicks(MemoryModuleType.RAM_COOLDOWN_TICKS)
        ));
    }

    public static void initIdleActivity(Brain<? extends RammingPrey> brain)
    {
        brain.addActivity(Activity.IDLE, ImmutableList.of(
            Pair.of(0, SetLookTarget.create(EntityType.PLAYER, 6.0F, UniformInt.of(30, 60))), // looks at player, but its only try it every so often -- "Run Sometimes"
            //TODO: Uncomment
            //Pair.of(1, AvoidPredatorBehavior.create(false)),
            Pair.of(2, BabyFollowAdult.create(UniformInt.of(5, 16), 1.25F)), // babies follow any random adult around
            Pair.of(3, createIdleMovementBehaviors())
        ));
    }

    /**
     * Focuses on retreating from the avoiding target.
     * What the name "addActivityAndRemoveMemoryWhenStopped" does not say is that the erased memory is REQUIRED to start this activity
     * In other words, this is triggered automatically by updateActivity if AVOID_TARGET is present.
     */
    public static void initRetreatActivity(Brain<? extends RammingPrey> brain)
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

    public static FastGateBehavior<RammingPrey> createIdleMovementBehaviors()
    {
        //TODO: Uncomment
        return FastGateBehavior.runOne(ImmutableList.of(
            // Chooses one of these behaviors to run. Notice that all three of these are basically the fallback walking around behaviors, and it doesn't make sense to check them all every time
            //RandomStroll.stroll(1.0F), // picks a random place to walk to
            //SetWalkTargetFromLookTarget.create(1.0F, 3), // walk to what it is looking at
            new DoNothing(30, 60)
        )); // do nothing for a certain period of time
    }

    private static void initRamActivity(Brain<? extends RammingPrey> brain) {
        brain.addActivityWithConditions(Activity.RAM, ImmutableList.of(Pair.of(0, new RamTargetTFC(
            (rammingPrey) -> {
                return rammingPrey.isMale() ? TIME_BETWEEN_RAMS_MALE : TIME_BETWEEN_RAMS_FEMALE;
            }, RAM_TARGET_CONDITIONS, 3.0F, (rammingPrey) -> {
                return rammingPrey.isBaby() ? BABY_RAM_KNOCKBACK_FORCE : ADULT_RAM_KNOCKBACK_FORCE;
            }, (rammingPrey) -> {
                //TODO: Make sounds custom
                return rammingPrey.isMale() ? SoundEvents.GOAT_SCREAMING_RAM_IMPACT : SoundEvents.GOAT_RAM_IMPACT;
        })
        ), Pair.of(1, new PrepareRamNearestTarget<>((rammingPrey) -> {
            return rammingPrey.isMale() ? TIME_BETWEEN_RAMS_MALE.getMinValue() : TIME_BETWEEN_RAMS_FEMALE.getMinValue();
        }, 4, 7, 1.25F, RAM_TARGET_CONDITIONS, 20, (rammingPrey) -> {
            //TODO: Make sounds custom
            return rammingPrey.isMale() ? SoundEvents.GOAT_SCREAMING_PREPARE_RAM : SoundEvents.GOAT_PREPARE_RAM;
        }))), ImmutableSet.of(
            Pair.of(MemoryModuleType.RAM_COOLDOWN_TICKS, MemoryStatus.VALUE_ABSENT)));
    }

    public static void updateActivity(RammingPrey prey)
    {
        prey.getBrain().setActiveActivityToFirstValid(ImmutableList.of(Activity.RAM, Activity.IDLE));
    }
}
