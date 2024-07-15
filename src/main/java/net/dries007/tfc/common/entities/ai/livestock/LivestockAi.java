/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities.ai.livestock;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.AnimalPanic;
import net.minecraft.world.entity.ai.behavior.BabyFollowAdult;
import net.minecraft.world.entity.ai.behavior.CountDownCooldownTicks;
import net.minecraft.world.entity.ai.behavior.DoNothing;
import net.minecraft.world.entity.ai.behavior.EraseMemoryIf;
import net.minecraft.world.entity.ai.behavior.FollowTemptation;
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
import net.dries007.tfc.common.entities.ai.TFCBrain;
import net.dries007.tfc.common.entities.ai.prey.AvoidPredatorBehavior;
import net.dries007.tfc.common.entities.ai.prey.PreyAi;
import net.dries007.tfc.common.entities.livestock.TFCAnimal;

public class LivestockAi
{
    public static final ImmutableList<SensorType<? extends Sensor<? super TFCAnimal>>> SENSOR_TYPES = ImmutableList.of(
        SensorType.NEAREST_LIVING_ENTITIES, SensorType.NEAREST_PLAYERS, SensorType.NEAREST_ITEMS,
        SensorType.NEAREST_ADULT, SensorType.HURT_BY, TFCBrain.TEMPTATION_SENSOR.get()
    );

    public static final ImmutableList<MemoryModuleType<?>> MEMORY_TYPES = ImmutableList.of(
        MemoryModuleType.LOOK_TARGET, MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES, MemoryModuleType.WALK_TARGET,
        MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, MemoryModuleType.PATH, MemoryModuleType.ATE_RECENTLY,
        MemoryModuleType.BREED_TARGET, MemoryModuleType.TEMPTING_PLAYER, MemoryModuleType.NEAREST_VISIBLE_ADULT,
        MemoryModuleType.TEMPTATION_COOLDOWN_TICKS, MemoryModuleType.IS_TEMPTED, MemoryModuleType.AVOID_TARGET,
        MemoryModuleType.HURT_BY_ENTITY, MemoryModuleType.HURT_BY, MemoryModuleType.IS_PANICKING
    );

    /**
     * Initializes the brain when the entity is made.
     * Activities get re-added to the Brain object each time. This means they can differ per entity instance, if we want them to!
     * Only one non-core Activity can run at once. When an Activity is run, it cycles through each prioritized Behavior and tries to run them all.
     */
    public static Brain<?> makeBrain(Brain<? extends TFCAnimal> brain)
    {
        initCoreActivity(brain);
        initIdleActivity(brain);
        initRetreatActivity(brain);

        brain.setCoreActivities(ImmutableSet.of(Activity.CORE)); // core activities run all the time
        brain.setDefaultActivity(Activity.IDLE); // the default activity is a useful way to have a fallback activity
        brain.useDefaultActivity();

        return brain;
    }


    /**
     * These activities are always active. So, expect them to run every tick.
     */
    public static void initCoreActivity(Brain<? extends TFCAnimal> brain)
    {
        brain.addActivity(Activity.CORE, 0, ImmutableList.of(
            new Swim(0.8F), // float in water
            new LookAtTargetSink(45, 90), // if memory of look target, looks at that
            new MoveToTargetSink(), // tries to walk to its internal walk target. This could just be a random block.
            new CountDownCooldownTicks(MemoryModuleType.TEMPTATION_COOLDOWN_TICKS) // cools down between being tempted if its concentration broke
        ));
    }

    /**
     * These will run whenever we don't have something better to do. Essentially walk and swim randomly, or do nothing.
     */
    public static void initIdleActivity(Brain<? extends TFCAnimal> brain)
    {
        brain.addActivity(Activity.IDLE, 0, ImmutableList.of(
            SetLookTarget.create(EntityType.PLAYER, 6.0F, UniformInt.of(30, 60)), // looks at player, but its only try it every so often -- "Run Sometimes"
            AvoidPredatorBehavior.create(true),
            new BreedBehavior<>(1.0F), // custom TFC breed behavior
            new AnimalPanic<>(2.0F), // if memory of being hit, runs away
            new FollowTemptation(e -> e.isBaby() ? 1.5F : 1.25F), // sets the walk and look targets to whomever it has a memory of being tempted by
            BabyFollowAdult.create(UniformInt.of(5, 16), 1.25F), // babies follow any random adult around
            createIdleMovementBehaviors()
        ));
    }

    public static void initRetreatActivity(Brain<? extends TFCAnimal> brain)
    {
        brain.addActivityAndRemoveMemoryWhenStopped(Activity.AVOID, 10, ImmutableList.of(
            SetWalkTargetAwayFrom.entity(MemoryModuleType.AVOID_TARGET, 1.3F, 15, false),
            createIdleMovementBehaviors(),
            SetLookTarget.create(8.0F, UniformInt.of(30, 60)),
            EraseMemoryIf.create(PreyAi::wantsToStopFleeing, MemoryModuleType.AVOID_TARGET) // essentially ends the activity
            ),
            MemoryModuleType.AVOID_TARGET
        );
    }

    public static FastGateBehavior<TFCAnimal> createIdleMovementBehaviors()
    {
        return FastGateBehavior.runOne(ImmutableList.of(
            // Chooses one of these behaviors to run. Notice that all three of these are basically the fallback walking around behaviors, and it doesn't make sense to check them all every time
            RandomStroll.stroll(1.0F), // picks a random place to walk to
            SetWalkTargetFromLookTarget.create(1.0F, 3), // walk to what it is looking at
            new DoNothing(30, 60)
        )); // do nothing for a certain period of time
    }

    /**
     * This is what lets you switch activities. It should be in reverse order of the importance of the activity.
     */
    public static void updateActivity(TFCAnimal animal)
    {
        animal.getBrain().setActiveActivityToFirstValid(ImmutableList.of(Activity.AVOID, Activity.IDLE));
    }
}
