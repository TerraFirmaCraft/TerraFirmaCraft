/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities.ai.pet;

import java.util.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.AnimalPanic;
import net.minecraft.world.entity.ai.behavior.BabyFollowAdult;
import net.minecraft.world.entity.ai.behavior.CountDownCooldownTicks;
import net.minecraft.world.entity.ai.behavior.DoNothing;
import net.minecraft.world.entity.ai.behavior.EntityTracker;
import net.minecraft.world.entity.ai.behavior.EraseMemoryIf;
import net.minecraft.world.entity.ai.behavior.FollowTemptation;
import net.minecraft.world.entity.ai.behavior.LookAtTargetSink;
import net.minecraft.world.entity.ai.behavior.MeleeAttack;

import net.dries007.tfc.common.entities.ai.SetLookTarget;

import net.minecraft.world.entity.ai.behavior.SetWalkTargetAwayFrom;
import net.minecraft.world.entity.ai.behavior.SetWalkTargetFromAttackTargetIfTargetOutOfReach;
import net.minecraft.world.entity.ai.behavior.SetWalkTargetFromLookTarget;
import net.minecraft.world.entity.ai.behavior.StartAttacking;
import net.minecraft.world.entity.ai.behavior.StopAttackingIfTargetInvalid;
import net.minecraft.world.entity.ai.behavior.StrollAroundPoi;
import net.minecraft.world.entity.ai.behavior.StrollToPoi;
import net.minecraft.world.entity.ai.behavior.Swim;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.schedule.Activity;

import net.dries007.tfc.common.entities.ai.FastGateBehavior;
import net.dries007.tfc.common.entities.ai.TFCBrain;
import net.dries007.tfc.common.entities.ai.livestock.BreedBehavior;
import net.dries007.tfc.common.entities.ai.livestock.LivestockAi;
import net.dries007.tfc.common.entities.ai.prey.PreyAi;
import net.dries007.tfc.common.entities.livestock.TFCAnimal;
import net.dries007.tfc.common.entities.livestock.pet.TamableMammal;
import net.dries007.tfc.common.entities.prey.Pest;
import net.dries007.tfc.util.calendar.Calendars;

public class TamableAi
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
        MemoryModuleType.HURT_BY_ENTITY, MemoryModuleType.HURT_BY, MemoryModuleType.HOME, TFCBrain.SLEEP_POS.get(), TFCBrain.SIT_TIME.get(),
        MemoryModuleType.ATTACK_TARGET, MemoryModuleType.ATTACK_COOLING_DOWN, MemoryModuleType.IS_PANICKING
    );

    public static final int HOME_WANDER_DISTANCE = 36;
    public static final int HOME_LOST_DISTANCE = 120;

    public static Brain<?> makeBrain(Brain<? extends TamableMammal> brain)
    {
        initCoreActivity(brain);
        initIdleActivity(brain);
        initIdleAtHomeActivity(brain);
        initRestActivity(brain);
        initRetreatActivity(brain);
        initFollowActivity(brain);
        initHuntActivity(brain);
        initFightActivity(brain);
        initSitActivity(brain);

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
            new MoveToTargetSinkIfNotSleeping(), // tries to walk to its internal walk target. This could just be a random block.
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
            Pair.of(0, SetLookTarget.create(EntityType.PLAYER, 6.0F, UniformInt.of(30, 60))), // looks at player, but its only try it every so often -- "Run Sometimes"
            Pair.of(1, new BreedBehavior<>(0.5f)), // custom TFC breed behavior
            Pair.of(1, new AnimalPanic(1f)), // if memory of being hit, runs away
            Pair.of(2, new FollowTemptation(e -> e.isBaby() ? 1.5F : 1.25F)), // sets the walk and look targets to whomever it has a memory of being tempted by
            Pair.of(3, BabyFollowAdult.create(UniformInt.of(5, 16), 1.25F)), // babies follow any random adult around
            Pair.of(3, StrollToPoi.create(MemoryModuleType.HOME, 1F, 10, HOME_WANDER_DISTANCE - 10)),
            Pair.of(3, StartAttacking.create(TamableAi::getUnwantedAttackTarget)), // rats or attackers only
            Pair.of(4, FastGateBehavior.runOne(ImmutableList.of(
                StrollToPoi.create(MemoryModuleType.HOME, 0.6F, 10, HOME_WANDER_DISTANCE - 10),
                StrollAroundPoi.create(MemoryModuleType.HOME, 0.6F, HOME_WANDER_DISTANCE),
                SetWalkTargetFromLookTarget.create(1.0F, 3), // walk to what it is looking at
                new DoNothing(30, 60)
            )))
        ));
    }

    public static void initRestActivity(Brain<? extends TamableMammal> brain)
    {
        brain.addActivity(Activity.REST, 10, ImmutableList.of(
            StrollToPoi.create(MemoryModuleType.HOME, 1.2F, 5, HOME_WANDER_DISTANCE),
            TamableFindSleepPos.create(),
            new TamableSleepBehavior()
        ));
    }

    public static void initRetreatActivity(Brain<? extends TamableMammal> brain)
    {
        brain.addActivityAndRemoveMemoryWhenStopped(Activity.AVOID, 10, ImmutableList.of(
            SetWalkTargetAwayFrom.entity(MemoryModuleType.AVOID_TARGET, 1.3F, 15, false),
            createIdleMovementBehaviors(),
            SetLookTarget.create(8.0F, UniformInt.of(30, 60)),
            EraseMemoryIf.create(PreyAi::wantsToStopFleeing, MemoryModuleType.AVOID_TARGET) // essentially ends the activity
        ), MemoryModuleType.AVOID_TARGET);
    }

    public static void initHuntActivity(Brain<? extends TamableMammal> brain)
    {
        brain.addActivity(TFCBrain.HUNT.get(), ImmutableList.of(
            Pair.of(0, FollowOwnerBehavior.create()),
            Pair.of(1, StartAttacking.create(TamableAi::getAttackTarget)),
            Pair.of(4, SetLookTarget.create(EntityType.PLAYER, 6.0F, UniformInt.of(30, 60)))
        ));
    }

    public static void initFightActivity(Brain<? extends TamableMammal> brain)
    {
        brain.addActivityAndRemoveMemoryWhenStopped(Activity.FIGHT, 10, ImmutableList.of(
            SetWalkTargetFromAttackTargetIfTargetOutOfReach.create(1.15F),
            MeleeAttack.create(40),
            StopAttackingIfTargetInvalid.create(TamableAi::couldFlee, (t, e) -> t.refreshCommandOnNextTick(), false)
        ), MemoryModuleType.ATTACK_TARGET);
    }

    public static void initFollowActivity(Brain<? extends TamableMammal> brain)
    {
        brain.addActivity(TFCBrain.FOLLOW.get(), ImmutableList.of(
            Pair.of(0, FollowOwnerBehavior.create()),
            Pair.of(1, SetLookTarget.create(EntityType.PLAYER, 6.0F, UniformInt.of(30, 60)))
        ));
    }

    public static void initSitActivity(Brain<? extends TamableMammal> brain)
    {
        brain.addActivity(TFCBrain.SIT.get(), 1, ImmutableList.of(
            SetLookTarget.create(8f, UniformInt.of(30, 60)),
            SetLookTarget.create(EntityType.PLAYER, 8f, UniformInt.of(30, 60)),
            new DoNothing(30, 60)
        ));
    }

    public static FastGateBehavior<TFCAnimal> createIdleMovementBehaviors()
    {
        return LivestockAi.createIdleMovementBehaviors();
    }

    public static boolean isTooFarFromHome(TamableMammal entity)
    {
        return isFarFromHome(entity, HOME_WANDER_DISTANCE);
    }

    public static boolean isExtremelyFarFromHome(TamableMammal entity)
    {
        return isFarFromHome(entity, HOME_LOST_DISTANCE);
    }

    public static boolean isFarFromHome(TamableMammal entity, int distance)
    {
        return entity.getBrain().getMemory(MemoryModuleType.HOME).map(globalPos ->
            globalPos.dimension() != entity.level().dimension() || globalPos.pos().distSqr(entity.blockPosition()) > distance * distance
        ).orElse(true);
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

    public static boolean wantsToStopSleeping(TamableMammal entity)
    {
        var brain = entity.getBrain();
        if (brain.getMemory(MemoryModuleType.HURT_BY_ENTITY).isPresent())
        {
            return true;
        }
        return brain.getMemory(MemoryModuleType.LAST_SLEPT).filter(time -> Calendars.SERVER.getTicks() > time + 2000).isPresent();
    }

    public static void updateActivity(TamableMammal entity, boolean doMoreChecks)
    {
        final var brain = entity.getBrain();
        final Activity current = brain.getActiveNonCoreActivity().orElse(null);
        if (current != null)
        {
            if (brain.hasMemoryValue(MemoryModuleType.AVOID_TARGET) && couldFlee(entity))
            {
                brain.setActiveActivityIfPossible(Activity.AVOID);
                return;
            }
            else if (brain.hasMemoryValue(MemoryModuleType.ATTACK_TARGET))
            {
                brain.setActiveActivityIfPossible(Activity.FIGHT);
            }
            if (doMoreChecks)
            {
                entity.setInterested(brain.getMemory(MemoryModuleType.LOOK_TARGET).filter(lookTarget ->
                    lookTarget instanceof EntityTracker entityTracker && entity.isOwnedBy(entityTracker.getEntity()) && entity.isSitting() && entityTracker.isVisibleBy(entity)
                ).isPresent());
                final boolean farFromHome = isExtremelyFarFromHome(entity);
                if ((current.equals(TFCBrain.HUNT.get()) || current.equals(TFCBrain.FOLLOW.get())) && entity.getOwner() == null)
                {
                    beginIdle(entity, !farFromHome);
                }
                else if (current.equals(TFCBrain.IDLE_AT_HOME.get()))
                {
                    if (farFromHome)
                    {
                        beginIdle(entity, false);
                    }
                    else
                    {
                        brain.getMemory(MemoryModuleType.LAST_SLEPT).ifPresentOrElse(slept -> {
                            if (Calendars.SERVER.getTicks() > slept + 12000)
                            {
                                brain.setActiveActivityIfPossible(Activity.REST);
                            }
                        }, () -> brain.setMemory(MemoryModuleType.LAST_SLEPT, 0L));
                    }
                }
                else if (current.equals(TFCBrain.SIT.get()) && wantsToStopSitting(entity))
                {
                    beginIdle(entity, !farFromHome);
                }
                else if (current.equals(Activity.REST) && entity.isSleeping() && wantsToStopSleeping(entity))
                {
                    beginIdle(entity, !farFromHome);
                }
            }
        }
    }

    private static void beginIdle(TamableMammal entity, boolean home)
    {
        entity.setSitting(false);
        entity.setSleeping(false);
        entity.setCommand(TamableMammal.Command.RELAX);
        entity.getBrain().setActiveActivityIfPossible(home ? TFCBrain.IDLE_AT_HOME.get() : Activity.IDLE);
    }

    private static boolean couldFlee(LivingEntity entity)
    {
        return entity.getHealth() < 5f || entity.isOnFire();
    }

    private static Optional<? extends LivingEntity> getUnwantedAttackTarget(TamableMammal entity)
    {
        final var brain = entity.getBrain();
        if (brain.hasMemoryValue(MemoryModuleType.HURT_BY_ENTITY))
        {
            return brain.getMemory(MemoryModuleType.HURT_BY_ENTITY);
        }
        return brain.getMemory(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES).flatMap(memory -> memory.findClosest(e -> e instanceof Pest));
    }

    private static Optional<? extends LivingEntity> getAttackTarget(TamableMammal entity)
    {
        final var brain = entity.getBrain();
        if (couldFlee(entity))
        {
            return Optional.empty();
        }
        if (entity.getOwner() instanceof ServerPlayer player)
        {
            LivingEntity target = player.getLastHurtByMob();
            if (target != null && entity.canAttack(target) && target.tickCount < player.getLastHurtByMobTimestamp() + 120)
            {
                return Optional.of(target);
            }
            target = player.getLastHurtMob();
            if (target != null && entity.canAttack(target) && target.tickCount < player.getLastHurtMobTimestamp() + 120)
            {
                return Optional.of(target);
            }
        }
        if (brain.hasMemoryValue(MemoryModuleType.HURT_BY_ENTITY))
        {
            return brain.getMemory(MemoryModuleType.HURT_BY_ENTITY);
        }
        return Optional.empty();
    }
}
