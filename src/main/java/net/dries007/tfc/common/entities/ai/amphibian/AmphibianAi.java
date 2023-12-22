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
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.*;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.animal.axolotl.Axolotl;
import net.minecraft.world.entity.animal.axolotl.AxolotlAi;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.level.Level;

import com.mojang.datafixers.util.Pair;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.entities.ai.FastGateBehavior;
import net.dries007.tfc.common.entities.ai.SetLookTarget;
import net.dries007.tfc.common.entities.ai.TFCBrain;
import net.dries007.tfc.common.entities.aquatic.AmphibiousAnimal;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.calendar.ICalendar;

/**
 * Reference implementation of {@link Brain} based on Axolotls.
 * This animal does most of what a default mob does -- look at players and run around.
 */
public class AmphibianAi
{
    private static final UniformInt ADULT_FOLLOW_RANGE = UniformInt.of(15, 30);

    public static final ImmutableList<SensorType<? extends Sensor<? super AmphibiousAnimal>>> SENSOR_TYPES = ImmutableList.of(SensorType.NEAREST_LIVING_ENTITIES, SensorType.NEAREST_ADULT, SensorType.HURT_BY, TFCBrain.TEMPTATION_SENSOR.get());
    public static final ImmutableList<? extends MemoryModuleType<?>> MEMORY_TYPES = ImmutableList.of(
        MemoryModuleType.NEAREST_LIVING_ENTITIES, MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES, MemoryModuleType.NEAREST_VISIBLE_PLAYER, MemoryModuleType.LOOK_TARGET, MemoryModuleType.NEAREST_VISIBLE_ADULT,
        MemoryModuleType.WALK_TARGET, MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, MemoryModuleType.PATH, MemoryModuleType.ATTACK_TARGET, MemoryModuleType.ATTACK_COOLING_DOWN, MemoryModuleType.HUNTED_RECENTLY,
        MemoryModuleType.PLAY_DEAD_TICKS, MemoryModuleType.HURT_BY_ENTITY, MemoryModuleType.HOME, MemoryModuleType.TEMPTING_PLAYER, MemoryModuleType.IS_TEMPTED, MemoryModuleType.TEMPTATION_COOLDOWN_TICKS, MemoryModuleType.BREED_TARGET, MemoryModuleType.IS_PANICKING
    );

    public static Brain<?> makeBrain(Brain<? extends AmphibiousAnimal> brain)
    {
        initCoreActivity(brain);
        initIdleActivity(brain);
        initPlayDeadActivity(brain);
        initFightActivity(brain);

        brain.setCoreActivities(ImmutableSet.of(Activity.CORE));
        brain.setDefaultActivity(Activity.IDLE);
        brain.useDefaultActivity();

        return brain;
    }

    /**
     * These activities are always active. So, expect them to run every tick.
     */
    private static void initCoreActivity(Brain<? extends AmphibiousAnimal> brain)
    {
        brain.addActivity(Activity.CORE, 0, ImmutableList.of(
            new LookAtTargetSink(45, 90),
            new MoveToTargetSink(),
            new CountDownCooldownTicks(MemoryModuleType.TEMPTATION_COOLDOWN_TICKS)
        ));
    }

    /**
     * These will run whenever we don't have something better to do. Essentially walk and swim randomly, or do nothing.
     */
    private static void initIdleActivity(Brain<? extends AmphibiousAnimal> brain)
    {
        brain.addActivity(Activity.IDLE, ImmutableList.of(
            Pair.of(0, SetLookTarget.create(TFCTags.Entities.TURTLE_FRIENDS, 6.0F, UniformInt.of(30, 60))),
            Pair.of(2, new RunOne<>(ImmutableList.of(
                Pair.of(BabyFollowAdult.create(ADULT_FOLLOW_RANGE, AmphibianAi::getChasingSpeedModifier), 1),
                Pair.of(new FollowTemptation(AmphibianAi::getSpeedModifier), 1)
            ))),
            Pair.of(3, TryFindWater.create(6, 0.15F)),
            Pair.of(4, new GateBehavior<>(
                ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT),
                ImmutableSet.of(),
                GateBehavior.OrderPolicy.ORDERED,
                GateBehavior.RunningPolicy.TRY_ALL,
                ImmutableList.of(
                    Pair.of(StartAttacking.create(AmphibianAi::getAttackTarget), 2),
                    Pair.of(RandomStroll.swim(0.5F), 2),
                    Pair.of(RandomStroll.stroll(0.15F, false), 2),
                    Pair.of(SetWalkTargetFromLookTarget.create(AmphibianAi::canSetWalkTargetFromLookTarget, AmphibianAi::getSpeedModifier, 3), 3),
                    Pair.of(new DoNothing(30, 60), 3),
                    Pair.of(StrollToPoi.create(MemoryModuleType.HOME, 0.5f, 5, 100), 3)
                )
            ))
        ));
    }

    /**
     * A simple task that erases itself when finished.
     * First, a prioritized list of behaviors. Then a set of conditions, in this case, the memory being present. Then the memory to be erased.
     */
    private static void initPlayDeadActivity(Brain<? extends AmphibiousAnimal> brain)
    {
        brain.addActivityAndRemoveMemoriesWhenStopped(Activity.PLAY_DEAD,
            ImmutableList.of(Pair.of(0, new AmphibianPlayDeadBehavior())),
            ImmutableSet.of(Pair.of(MemoryModuleType.PLAY_DEAD_TICKS, MemoryStatus.VALUE_PRESENT)),
            ImmutableSet.of(MemoryModuleType.PLAY_DEAD_TICKS));
    }

    private static void initFightActivity(Brain<? extends AmphibiousAnimal> brain)
    {
        brain.addActivityAndRemoveMemoryWhenStopped(Activity.FIGHT, 0, ImmutableList.of(
            StopAttackingIfTargetInvalid.create(),
            SetWalkTargetFromAttackTargetIfTargetOutOfReach.create(AmphibianAi::getChasingSpeedModifier),
            MeleeAttack.create(20),
            EraseMemoryIf.create(AmphibianAi::isTempted, MemoryModuleType.ATTACK_TARGET)
        ), MemoryModuleType.ATTACK_TARGET);
    }

    public static void updateActivity(AmphibiousAnimal animal)
    {
        Brain<AmphibiousAnimal> brain = animal.getBrain();
        Activity current = brain.getActiveNonCoreActivity().orElse(null);
        AmphibianPlayDeadBehavior.update(animal);
        if (current != Activity.PLAY_DEAD)
        {
            brain.setActiveActivityToFirstValid(ImmutableList.of(Activity.PLAY_DEAD, Activity.FIGHT, Activity.IDLE));
        }
    }

    private static boolean isTempted(AmphibiousAnimal entity)
    {
        return entity.getBrain().hasMemoryValue(MemoryModuleType.TEMPTING_PLAYER);
    }

    private static Optional<? extends LivingEntity> getAttackTarget(AmphibiousAnimal entity)
    {
        if (entity.getBrain().hasMemoryValue(MemoryModuleType.HUNTED_RECENTLY))
        {
            return Optional.empty();
        }
        return entity.getBrain()
            .getMemory(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES)
            .flatMap(nearest -> nearest.findClosest(p -> Helpers.isEntity(p, TFCTags.Entities.SMALL_FISH) && p.isAlive()));
    }

    private static boolean isDayTime(Entity animal)
    {
        return animal.level().getDayTime() % ICalendar.TICKS_IN_DAY < 12000;
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

    private static float getSpeedModifier(LivingEntity entity)
    {
        return entity.isInWaterOrBubble() ? 0.5F : 0.15F;
    }

    private static float getChasingSpeedModifier(LivingEntity entity)
    {
        return entity.isInWaterOrBubble() ? 0.6F : 0.17F;
    }
}
