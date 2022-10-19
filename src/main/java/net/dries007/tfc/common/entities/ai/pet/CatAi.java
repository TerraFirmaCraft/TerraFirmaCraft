/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities.ai.pet;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import net.minecraft.core.GlobalPos;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.EntityType;

import net.dries007.tfc.common.entities.ai.livestock.LivestockAi;
import net.dries007.tfc.common.entities.livestock.pet.TamableMammal;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.*;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.schedule.Activity;

import com.mojang.datafixers.util.Pair;
import net.dries007.tfc.common.entities.ai.TFCBrain;
import net.dries007.tfc.common.entities.ai.livestock.BreedBehavior;
import net.dries007.tfc.common.entities.ai.prey.PreyAi;
import net.dries007.tfc.util.calendar.Calendars;

public class CatAi
{
    public static final ImmutableList<SensorType<? extends Sensor<? super TamableMammal>>> SENSOR_TYPES = ImmutableList.of(
        SensorType.NEAREST_LIVING_ENTITIES, SensorType.NEAREST_PLAYERS, SensorType.NEAREST_ITEMS,
        SensorType.NEAREST_ADULT, SensorType.HURT_BY, TFCBrain.TEMPTATION_SENSOR.get()
    );

    public static final ImmutableList<MemoryModuleType<?>> MEMORY_TYPES = ImmutableList.of(
        MemoryModuleType.LOOK_TARGET, MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES, MemoryModuleType.WALK_TARGET,
        MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, MemoryModuleType.PATH, MemoryModuleType.LAST_SLEPT,
        MemoryModuleType.BREED_TARGET, MemoryModuleType.TEMPTING_PLAYER, MemoryModuleType.NEAREST_VISIBLE_ADULT,
        MemoryModuleType.TEMPTATION_COOLDOWN_TICKS, MemoryModuleType.IS_TEMPTED, MemoryModuleType.AVOID_TARGET,
        MemoryModuleType.HURT_BY_ENTITY, MemoryModuleType.HURT_BY, MemoryModuleType.HOME, TFCBrain.SLEEP_POS.get(), TFCBrain.SIT_TIME.get()
    );

    public static final int HOME_WANDER_DISTANCE = 36;

    public static Brain<?> makeBrain(Brain<? extends TamableMammal> brain)
    {
        initCoreActivity(brain);
        initIdleActivity(brain);
        initIdleAtHomeActivity(brain);
        initRestActivity(brain);
        initRetreatActivity(brain);

        brain.setCoreActivities(ImmutableSet.of(Activity.CORE)); // core activities run all the time
        brain.setDefaultActivity(Activity.IDLE); // the default activity is a useful way to have a fallback activity
        brain.useDefaultActivity();

        return brain;
    }

    public static void initCoreActivity(Brain<? extends TamableMammal> brain)
    {
        brain.addActivity(Activity.CORE, 0, ImmutableList.of(
            new Swim(0.8F), // float in water
            new LookAtTargetSink(45, 90), // if memory of look target, looks at that
            new MoveToTargetSink(), // tries to walk to its internal walk target. This could just be a random block.
            new CountDownCooldownTicks(MemoryModuleType.TEMPTATION_COOLDOWN_TICKS) // cools down between being tempted if its concentration broke
        ));
    }

    public static void initIdleActivity(Brain<? extends TamableMammal> brain)
    {
        LivestockAi.initIdleActivity(brain);
    }

    public static void initIdleAtHomeActivity(Brain<? extends TamableMammal> brain)
    {
        brain.addActivity(TFCBrain.IDLE_AT_HOME.get(), ImmutableList.of(
            Pair.of(0, new RunSometimes<>(new SetEntityLookTarget(EntityType.PLAYER, 6.0F), UniformInt.of(30, 60))), // looks at player, but its only try it every so often -- "Run Sometimes"
            Pair.of(1, new BreedBehavior(1.0F)), // custom TFC breed behavior
            Pair.of(1, new AnimalPanic(2.0F)), // if memory of being hit, runs away
            Pair.of(2, new FollowTemptation(e -> e.isBaby() ? 1.5F : 1.25F)), // sets the walk and look targets to whomever it has a memory of being tempted by
            Pair.of(3, new BabyFollowAdult<>(UniformInt.of(5, 16), 1.25F)), // babies follow any random adult around
            Pair.of(3, new RunIf<>(CatAi::isTooFarFromHome, new StrollToPoi(MemoryModuleType.HOME, 1F, 10, HOME_WANDER_DISTANCE - 10))),
            Pair.of(4, new RunOne<>(ImmutableList.of(
                Pair.of(new StrollToPoi(MemoryModuleType.HOME, 0.6F, 10, HOME_WANDER_DISTANCE - 10), 2),
                Pair.of(new StrollAroundPoi(MemoryModuleType.HOME, 0.6F, HOME_WANDER_DISTANCE), 3),
                Pair.of(new SetWalkTargetFromLookTarget(1.0F, 3), 2), // walk to what it is looking at
                Pair.of(new DoNothing(30, 60), 1))
            ))
        ));
    }

    public static void initRestActivity(Brain<? extends TamableMammal> brain)
    {
        brain.addActivity(Activity.REST, 10, ImmutableList.of(
            new RunIf<>(e -> !e.isSleeping() && isTooFarFromHome(e), new StrollToPoi(MemoryModuleType.HOME, 1.2F, 5, HOME_WANDER_DISTANCE)),
            new RunIf<>(e -> !e.isSleeping(), new CatFindSleepPos(1f)),
            new CatSleepBehavior()
        ));
    }

    public static void initRetreatActivity(Brain<? extends TamableMammal> brain)
    {
        brain.addActivityAndRemoveMemoryWhenStopped(Activity.AVOID, 10, ImmutableList.of(
                SetWalkTargetAwayFrom.entity(MemoryModuleType.AVOID_TARGET, 1.3F, 15, false),
                createIdleMovementBehaviors(),
                new RunSometimes<>(new SetEntityLookTarget(8.0F), UniformInt.of(30, 60)),
                new EraseMemoryIf<>(PreyAi::wantsToStopFleeing, MemoryModuleType.AVOID_TARGET) // essentially ends the activity
            ),
            MemoryModuleType.AVOID_TARGET
        );
    }

    public static void initHuntActivity(Brain<? extends TamableMammal> brain)
    {
        brain.addActivity(TFCBrain.HUNT.get(), ImmutableList.of(
            Pair.of(0, new FollowOwnerBehavior())
        ));
    }

    public static RunOne<TamableMammal> createIdleMovementBehaviors()
    {
        return new RunOne<>(ImmutableList.of(
            // Chooses one of these behaviors to run. Notice that all three of these are basically the fallback walking around behaviors, and it doesn't make sense to check them all every time
            Pair.of(new RandomStroll(1.0F), 2), // picks a random place to walk to
            Pair.of(new SetWalkTargetFromLookTarget(1.0F, 3), 2), // walk to what it is looking at
            Pair.of(new DoNothing(30, 60), 1))
        ); // do nothing for a certain period of time
    }

    public static boolean isTooFarFromHome(TamableMammal entity)
    {
        final GlobalPos globalPos = entity.getBrain().getMemory(MemoryModuleType.HOME).orElseThrow();
        return globalPos.dimension() != entity.level.dimension() || globalPos.pos().distSqr(entity.blockPosition()) > HOME_WANDER_DISTANCE * HOME_WANDER_DISTANCE;
    }

    public static boolean wantsToStopSitting(TamableMammal entity)
    {
        var brain = entity.getBrain();
        if (brain.getMemory(MemoryModuleType.HURT_BY_ENTITY).isPresent())
        {
            return true;
        }
        return brain.getMemory(TFCBrain.SIT_TIME.get()).filter(time -> Calendars.SERVER.getTicks() > time + 2000).isPresent();
    }

    public static void updateActivity(TamableMammal entity)
    {

    }
}
