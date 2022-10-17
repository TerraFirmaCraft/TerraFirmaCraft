/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities.ai.livestock;

import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import net.minecraft.Util;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.*;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.schedule.Activity;

import com.mojang.datafixers.util.Pair;
import net.dries007.tfc.common.entities.ai.TFCBrain;
import net.dries007.tfc.common.entities.ai.prey.AvoidPredatorBehavior;
import net.dries007.tfc.common.entities.livestock.OviparousAnimal;

public class OviparousAi
{
    public static final ImmutableList<SensorType<? extends Sensor<? super OviparousAnimal>>> SENSOR_TYPES = Util.make(() -> {
        List<SensorType<? extends Sensor<? super OviparousAnimal>>> list = Lists.newArrayList(LivestockAi.SENSOR_TYPES);
        list.add(TFCBrain.NEST_BOX_SENSOR.get());
        return ImmutableList.copyOf(list);
    });

    public static final ImmutableList<MemoryModuleType<?>> MEMORY_TYPES = Util.make(() -> {
        List<MemoryModuleType<?>> list = Lists.newArrayList(LivestockAi.MEMORY_TYPES);
        list.add(TFCBrain.NEST_BOX_MEMORY.get());
        return ImmutableList.copyOf(list);
    });

    public static Brain<?> makeBrain(Brain<? extends OviparousAnimal> brain)
    {
        initCoreActivity(brain);
        initIdleActivity(brain);
        initRetreatActivity(brain);

        brain.setCoreActivities(ImmutableSet.of(Activity.CORE)); // core activities run all the time
        brain.setDefaultActivity(Activity.IDLE); // the default activity is a useful way to have a fallback activity
        brain.useDefaultActivity();

        return brain;
    }

    public static void initCoreActivity(Brain<? extends OviparousAnimal> brain)
    {
        LivestockAi.initCoreActivity(brain);
    }

    public static void initIdleActivity(Brain<? extends OviparousAnimal> brain)
    {
        brain.addActivity(Activity.IDLE, ImmutableList.of(
            Pair.of(0, new RunSometimes<>(new SetEntityLookTarget(EntityType.PLAYER, 6.0F), UniformInt.of(30, 60))), // looks at player, but its only try it every so often -- "Run Sometimes"
            Pair.of(0, new AvoidPredatorBehavior(true)),
            Pair.of(0, new LayEggBehavior()),
            Pair.of(1, new BreedBehavior(1.0F)), // custom TFC breed behavior
            Pair.of(1, new AnimalPanic(2.0F)), // if memory of being hit, runs away
            Pair.of(2, new FollowTemptation(e -> e.isBaby() ? 1.5F : 1.25F)), // sets the walk and look targets to whomever it has a memory of being tempted by
            Pair.of(3, new BabyFollowAdult<>(UniformInt.of(5, 16), 1.25F)), // babies follow any random adult around
            Pair.of(4, LivestockAi.createIdleMovementBehaviors())
        ));
    }

    public static void initRetreatActivity(Brain<? extends OviparousAnimal> brain)
    {
        LivestockAi.initRetreatActivity(brain);
    }
}
