/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities.ai.amphibian;

import java.util.Optional;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import net.minecraft.core.BlockPos;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.*;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.level.Level;

import com.mojang.datafixers.util.Pair;
import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.entities.ai.BrainObjects;
import net.dries007.tfc.common.entities.aquatic.AmphibiousAnimal;
import net.dries007.tfc.util.calendar.Calendars;

/**
 * Reference implementation of {@link Brain} based on Axolotls.
 * This animal does most of what a default mob does -- look at players and run around.
 */
public class AmphibianAi
{
    public static final ImmutableList<? extends SensorType<? extends Sensor<? super LivingEntity>>> SENSOR_TYPES = ImmutableList.of(SensorType.NEAREST_LIVING_ENTITIES, SensorType.HURT_BY);
    public static final ImmutableList<? extends MemoryModuleType<?>> MEMORY_TYPES = ImmutableList.of(
        MemoryModuleType.NEAREST_LIVING_ENTITIES, MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES, MemoryModuleType.NEAREST_VISIBLE_PLAYER, MemoryModuleType.LOOK_TARGET,
        MemoryModuleType.WALK_TARGET, MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, MemoryModuleType.PATH, BrainObjects.TRAVEL_POS.get(), BrainObjects.NEXT_TRAVEL_TIME.get(),
        MemoryModuleType.PLAY_DEAD_TICKS, MemoryModuleType.HURT_BY_ENTITY
    );

    public static Brain<?> makeBrain(Brain<AmphibiousAnimal> brain)
    {
        initCoreActivity(brain);
        initIdleActivity(brain);
        initPlayDeadActivity(brain);
        initTravelActivity(brain);

        brain.setCoreActivities(ImmutableSet.of(Activity.CORE));
        brain.setDefaultActivity(Activity.IDLE);
        brain.useDefaultActivity();

        brain.setMemory(BrainObjects.NEXT_TRAVEL_TIME.get(), Calendars.SERVER.getTicks());
        return brain;
    }

    /**
     * These activities are always active. So, expect them to run every tick.
     */
    private static void initCoreActivity(Brain<AmphibiousAnimal> brain)
    {
        brain.addActivity(Activity.CORE, 0, ImmutableList.of(
            new LookAtTargetSink(45, 90),
            new MoveToTargetSink(),
            new RunOnceBehavior<>(MemoryModuleType.PLAY_DEAD_TICKS, AmphibianPlayDeadBehavior::update)
        ));
    }

    /**
     * These will run whenever we don't have something better to do. Essentially walk and swim randomly, or do nothing.
     */
    private static void initIdleActivity(Brain<AmphibiousAnimal> brain)
    {
        brain.addActivity(Activity.IDLE, ImmutableList.of(
            Pair.of(0, new RunSometimes<>(new SetEntityLookTarget(TFCTags.Entities.TURTLE_FRIENDS, 6.0F), UniformInt.of(30, 60))),
            Pair.of(1, new TryFindWater(6, 0.15F)),
            Pair.of(2, new GateBehavior<>(
                ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT),
                ImmutableSet.of(),
                GateBehavior.OrderPolicy.ORDERED,
                GateBehavior.RunningPolicy.TRY_ALL,
                ImmutableList.of(
                        Pair.of(new RandomSwim(0.5F), 5),
                        Pair.of(new RandomStroll(0.15F, false), 2),
                        Pair.of(new SetWalkTargetFromLookTarget(AmphibianAi::canSetWalkTargetFromLookTarget, AmphibianAi::getSpeedModifier, 3), 3),
                        Pair.of(new RunIf<>(Entity::isInWaterOrBubble, new DoNothing(30, 60)), 3),
                        Pair.of(new RunIf<>(Entity::isOnGround, new DoNothing(200, 400)), 3)
                    )
            )),
            Pair.of(3, new RunSometimes<>(new RunIf<>(AmphibianTravelBehavior::canGoAway, new RunOnceBehavior<>(AmphibianTravelBehavior::doGoAway)), UniformInt.of(300, 600))),
            Pair.of(4, new RunSometimes<>(new RunIf<>(AmphibianTravelBehavior::canGoHome, new RunOnceBehavior<>(AmphibianTravelBehavior::goHome)), UniformInt.of(300, 600)))
        ));
    }

    /**
     * A simple task that erases itself when finished.
     * First, a prioritized list of behaviors. Then a set of conditions, in this case, the memory being present. Then the memory to be erased.
     */
    private static void initPlayDeadActivity(Brain<AmphibiousAnimal> brain)
    {
        brain.addActivityAndRemoveMemoriesWhenStopped(Activity.PLAY_DEAD,
            ImmutableList.of(Pair.of(0, new AmphibianPlayDeadBehavior())),
            ImmutableSet.of(Pair.of(MemoryModuleType.PLAY_DEAD_TICKS, MemoryStatus.VALUE_PRESENT)),
            ImmutableSet.of(MemoryModuleType.PLAY_DEAD_TICKS));
    }

    public static void initTravelActivity(Brain<AmphibiousAnimal> brain)
    {
        brain.addActivityAndRemoveMemoriesWhenStopped(BrainObjects.TRAVEL.get(),
            ImmutableList.of(Pair.of(0, new AmphibianTravelBehavior())),
            ImmutableSet.of(Pair.of(BrainObjects.TRAVEL_POS.get(), MemoryStatus.VALUE_PRESENT)),
            ImmutableSet.of(BrainObjects.TRAVEL_POS.get())
        );
    }

    public static void updateActivity(AmphibiousAnimal animal)
    {
        Brain<AmphibiousAnimal> brain = animal.getBrain();
        Activity current = brain.getActiveNonCoreActivity().orElse(null);
        if (current != Activity.PLAY_DEAD)
        {
            brain.setActiveActivityToFirstValid(ImmutableList.of(Activity.PLAY_DEAD, BrainObjects.TRAVEL.get(), Activity.IDLE));
        }
    }

    private static boolean canSetWalkTargetFromLookTarget(LivingEntity entity)
    {
        Level level = entity.level;
        Optional<PositionTracker> tracker = entity.getBrain().getMemory(MemoryModuleType.LOOK_TARGET);
        if (tracker.isPresent())
        {
            BlockPos pos = tracker.get().currentBlockPosition();
            return level.isWaterAt(pos) == entity.isInWaterOrBubble();
        }
        return false;
    }

    private static float getSpeedModifier(LivingEntity entity)
    {
        return entity.isInWaterOrBubble() ? 0.5F : 0.15F;
    }
}
