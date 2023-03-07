/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities.ai.predator;

import java.util.List;
import java.util.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import net.minecraft.Util;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.BabyFollowAdult;
import net.minecraft.world.entity.ai.behavior.CountDownCooldownTicks;
import net.minecraft.world.entity.ai.behavior.FollowTemptation;
import net.minecraft.world.entity.ai.behavior.LookAtTargetSink;
import net.minecraft.world.entity.ai.behavior.MoveToTargetSink;
import net.minecraft.world.entity.ai.behavior.RunIf;
import net.minecraft.world.entity.ai.behavior.RunSometimes;
import net.minecraft.world.entity.ai.behavior.SetEntityLookTarget;
import net.minecraft.world.entity.ai.behavior.StartAttacking;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.schedule.Activity;

import net.dries007.tfc.common.entities.ai.TFCBrain;
import net.dries007.tfc.common.entities.predator.Predator;

public class PackPredatorAi
{
    public static final ImmutableList<SensorType<? extends Sensor<? super PackPredator>>> SENSOR_TYPES = Util.make(() -> {
        List<SensorType<? extends Sensor<? super PackPredator>>> list = Lists.newArrayList(PredatorAi.SENSOR_TYPES);
        list.add(TFCBrain.PACK_LEADER_SENSOR.get());
        list.add(TFCBrain.TEMPTATION_SENSOR.get());
        return ImmutableList.copyOf(list);
    });

    public static final ImmutableList<MemoryModuleType<?>> MEMORY_TYPES = Util.make(() -> {
        List<MemoryModuleType<?>> list = Lists.newArrayList(PredatorAi.MEMORY_TYPES);
        list.add(TFCBrain.ALPHA.get());
        list.add(MemoryModuleType.TEMPTING_PLAYER);
        list.add(MemoryModuleType.TEMPTATION_COOLDOWN_TICKS);
        list.add(MemoryModuleType.IS_TEMPTED);
        return ImmutableList.copyOf(list);
    });

    public static Brain<?> makeBrain(Brain<? extends Predator> brain, Predator predator)
    {
        initCoreActivity(brain);
        initHuntActivity(brain);
        PredatorAi.initRetreatActivity(brain);
        PredatorAi.initRestActivity(brain);
        PredatorAi.initFightActivity(brain);

        brain.setSchedule(predator.diurnal ? TFCBrain.DIURNAL.get() : TFCBrain.NOCTURNAL.get());
        brain.setCoreActivities(ImmutableSet.of(Activity.CORE));
        brain.setDefaultActivity(TFCBrain.HUNT.get());
        brain.setActiveActivityIfPossible(TFCBrain.HUNT.get());
        brain.updateActivityFromSchedule(predator.level.getDayTime(), predator.level.getGameTime());

        return brain;
    }

    public static void initCoreActivity(Brain<? extends Predator> brain)
    {
        brain.addActivity(Activity.CORE, 0, ImmutableList.of(
            new AggressiveSwim(0.8F),
            new LookAtTargetSink(45, 90),
            new MoveToTargetSink(),
            new CountDownCooldownTicks(MemoryModuleType.TEMPTATION_COOLDOWN_TICKS)
        ));
    }

    public static void initHuntActivity(Brain<? extends Predator> brain)
    {
        brain.addActivity(TFCBrain.HUNT.get(), 10, ImmutableList.of(
            new BecomePassiveIfBehavior(p -> p.getHealth() < 5f || isAlphaPassive(p), 200),
            new BecomePassiveIfBehavior(p -> p.getBrain().hasMemoryValue(MemoryModuleType.TEMPTING_PLAYER), 400),
            new StartAttacking<>(PackPredatorAi::getAttackTarget),
            new RunSometimes<>(new SetEntityLookTarget(8.0F), UniformInt.of(30, 60)),
            new RunIf<>(PackPredatorAi::isAlpha, new FindNewHomeBehavior()),
            new ListenToAlphaBehavior(),
            new FollowTemptation(e -> e.isBaby() ? 1.5f : 1.2f),
            new BabyFollowAdult<>(UniformInt.of(5, 16), 1.25F), // babies follow any random adult around
            PredatorAi.createIdleMovementBehaviors(),
            new TickScheduleAndWakeBehavior()
        ));
    }

    public static boolean isAlphaPassive(Predator predator)
    {
        return getAlpha(predator).getBrain().getMemory(MemoryModuleType.PACIFIED).orElse(false);
    }

    public static Optional<? extends LivingEntity> getAttackTarget(Predator predator)
    {
        return isAlpha(predator) ? PredatorAi.getAttackTarget(predator) : getMemoryFromAlpha(predator, MemoryModuleType.ATTACK_TARGET);
    }

    public static <T> Optional<T> getMemoryFromAlpha(Predator predator, MemoryModuleType<T> memory)
    {
        return getAlpha(predator).getBrain().getMemory(memory);
    }

    public static boolean isNotAlpha(Predator predator)
    {
        return !getAlpha(predator).equals(predator);
    }

    public static boolean isAlpha(Predator predator)
    {
        return getAlpha(predator).equals(predator);
    }

    public static void alertOthers(PackPredator predator, LivingEntity target)
    {
        predator.getBrain().getMemory(MemoryModuleType.NEAREST_LIVING_ENTITIES).ifPresent(list -> {
            list.forEach(entity -> {
                if (entity instanceof PackPredator otherPredator)
                {
                    otherPredator.getBrain().setMemory(MemoryModuleType.ATTACK_TARGET, target);
                    otherPredator.getBrain().eraseMemory(MemoryModuleType.HUNTED_RECENTLY);
                    otherPredator.getBrain().setActiveActivityIfPossible(Activity.FIGHT);
                    otherPredator.setSleeping(false);
                }
            });
        });
    }

    public static PackPredator getAlpha(Predator predator)
    {
        return predator.getBrain().getMemory(TFCBrain.ALPHA.get()).orElse((PackPredator) predator);
    }
}
