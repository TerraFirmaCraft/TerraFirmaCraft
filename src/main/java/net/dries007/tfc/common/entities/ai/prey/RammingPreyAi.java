/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities.ai.prey;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.*;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.schedule.Activity;

import com.mojang.datafixers.util.Pair;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.client.TFCSounds;
import net.dries007.tfc.common.entities.ai.FastGateBehavior;
import net.dries007.tfc.common.entities.ai.SetLookTarget;
import net.dries007.tfc.common.entities.ai.predator.PredatorAi;
import net.dries007.tfc.common.entities.prey.RammingPrey;

public class RammingPreyAi
{
    public static final int RAM_PREPARE_TIME = 20;
    public static final int RAM_MIN_DISTANCE = 3;
    public static final int RAM_MAX_DISTANCE = 9;
    private static final float SPEED_MULTIPLIER_WHEN_PREPARING_TO_RAM = 1.6F;
    private static final float SPEED_MULTIPLIER_WHEN_RAMMING = 3.0F;
    public static final float ADULT_RAM_KNOCKBACK_FORCE = 2.5F;
    public static final float BABY_RAM_KNOCKBACK_FORCE = 1.0F;
    private static final UniformInt TIME_BETWEEN_RAMS_MALE = UniformInt.of(600, 1200);
    private static final UniformInt TIME_BETWEEN_RAMS_FEMALE = UniformInt.of(1000, 2000);
    private static final TargetingConditions RAM_TARGET_CONDITIONS = TargetingConditions.forCombat().selector((target) -> {
        return (target.level().getWorldBorder().isWithinBounds(target.getBoundingBox()));
    });

    private static final UniformInt RETREAT_DURATION = TimeUtil.rangeOfSeconds(7, 22);

    public static void initMemories(RammingPrey rammingPrey, RandomSource random) {
        rammingPrey.getBrain().setMemory(MemoryModuleType.RAM_COOLDOWN_TICKS, (rammingPrey.isMale() ? TIME_BETWEEN_RAMS_MALE : TIME_BETWEEN_RAMS_FEMALE).sample(random));
    }

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
            new LookAtTargetSink(45, 90), // if memory of look target, looks at that
            new MoveToTargetSink(), // tries to walk to its internal walk target. This could just be a random block.
            new CountDownCooldownTicks(MemoryModuleType.RAM_COOLDOWN_TICKS),
            //If the ramming animal has been hurt by a nearby entity, bypasses the ramming cooldown
            EraseMemoryIf.create(PredatorAi::hasNearbyAttacker, MemoryModuleType.RAM_COOLDOWN_TICKS),
            EraseMemoryIf.create(RammingPreyAi::attackerHasLeft, MemoryModuleType.HURT_BY_ENTITY)
        ));
    }

    public static void initIdleActivity(Brain<? extends RammingPrey> brain)
    {
        brain.addActivity(Activity.IDLE, ImmutableList.of(
            Pair.of(0, SetLookTarget.create(EntityType.PLAYER, 6.0F, UniformInt.of(30, 60))), // looks at player, but its only try it every so often -- "Run Sometimes"
            Pair.of(1, AvoidPredatorBehavior.create(true)), //Excludes players, as these animals should not fear the player
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
        return FastGateBehavior.runOne(ImmutableList.of(
            // Chooses one of these behaviors to run. Notice that all three of these are basically the fallback walking around behaviors, and it doesn't make sense to check them all every time
            RandomStroll.stroll(1.0F), // picks a random place to walk to
            SetWalkTargetFromLookTarget.create(1.0F, 3), // walk to what it is looking at
            new DoNothing(30, 60)
        )); // do nothing for a certain period of time
    }

    /**
     * Rams the nearest valid target on a cooldown
     */
    private static void initRamActivity(Brain<? extends RammingPrey> brain) {
        brain.addActivityWithConditions(Activity.RAM, ImmutableList.of(
            Pair.of(0, new RamTargetTFC(
                (rammingPrey) -> {
                    return rammingPrey.isMale() ? TIME_BETWEEN_RAMS_MALE : TIME_BETWEEN_RAMS_FEMALE;
                }, RAM_TARGET_CONDITIONS, 3.0F, (rammingPrey) -> {
                    return rammingPrey.isBaby() ? BABY_RAM_KNOCKBACK_FORCE : ADULT_RAM_KNOCKBACK_FORCE;
                }, (rammingPrey) -> TFCSounds.RAMMING_IMPACT.get())
            ),
            Pair.of(1, new PrepareRamNearestTargetTFC<>((rammingPrey) -> {
                return rammingPrey.isMale() ? TIME_BETWEEN_RAMS_MALE.getMinValue() : TIME_BETWEEN_RAMS_FEMALE.getMinValue();
            }, RAM_MIN_DISTANCE, RAM_MAX_DISTANCE, SPEED_MULTIPLIER_WHEN_PREPARING_TO_RAM, RAM_TARGET_CONDITIONS, RAM_PREPARE_TIME, (rammingPrey) -> {
                return rammingPrey.getAttackSound().get();
            }))
        ), ImmutableSet.of(
            Pair.of(MemoryModuleType.RAM_COOLDOWN_TICKS, MemoryStatus.VALUE_ABSENT)));
    }

    public static void updateActivity(RammingPrey prey)
    {
        prey.getBrain().setActiveActivityToFirstValid(ImmutableList.of(Activity.AVOID, Activity.RAM, Activity.IDLE));
    }

    public static boolean attackerHasLeft(LivingEntity rammingPrey)
    {
        return !rammingPrey.getBrain().getMemory(MemoryModuleType.HURT_BY_ENTITY).map(entity -> entity.distanceToSqr(rammingPrey) < 400).orElse(false);
    }

}
