/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities.ai.prey;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.BabyFollowAdult;
import net.minecraft.world.entity.ai.behavior.RunSometimes;
import net.minecraft.world.entity.ai.behavior.SetEntityLookTarget;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.schedule.Activity;

import com.mojang.datafixers.util.Pair;
import net.dries007.tfc.common.entities.ai.TFCBrain;
import net.dries007.tfc.common.entities.prey.Pest;

public class PestAi
{
    public static final ImmutableList<SensorType<? extends Sensor<? super Pest>>> SENSOR_TYPES = ImmutableList.of(
        SensorType.NEAREST_LIVING_ENTITIES, SensorType.NEAREST_PLAYERS, SensorType.HURT_BY
    );

    public static final ImmutableList<MemoryModuleType<?>> MEMORY_TYPES = ImmutableList.of(
        MemoryModuleType.LOOK_TARGET, MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES, MemoryModuleType.NEAREST_LIVING_ENTITIES, MemoryModuleType.WALK_TARGET,
        MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, MemoryModuleType.PATH, MemoryModuleType.AVOID_TARGET, MemoryModuleType.NEAREST_VISIBLE_ADULT, TFCBrain.SMELLY_POS.get()
    );

    public static Brain<?> makeBrain(Brain<? extends Pest> brain)
    {
        initCoreActivity(brain);
        initIdleActivity(brain);
        initRetreatActivity(brain);

        brain.setCoreActivities(ImmutableSet.of(Activity.CORE));
        brain.setDefaultActivity(Activity.IDLE);
        brain.useDefaultActivity();

        return brain;
    }

    public static void initCoreActivity(Brain<? extends Pest> brain)
    {
        PreyAi.initCoreActivity(brain);
    }

    public static void initIdleActivity(Brain<? extends Pest> brain)
    {
        brain.addActivity(Activity.IDLE, ImmutableList.of(
            Pair.of(0, new RunSometimes<>(new SetEntityLookTarget(EntityType.PLAYER, 6.0F), UniformInt.of(30, 60))), // looks at player, but its only try it every so often -- "Run Sometimes"
            Pair.of(1, new AvoidPredatorBehavior(false)),
            Pair.of(2, new RunSometimes<>(new PestFeastBehavior(TFCBrain.SMELLY_POS.get(), false), UniformInt.of(30, 60))),
            Pair.of(3, new BabyFollowAdult<>(UniformInt.of(5, 16), 1.25F)), // babies follow any random adult around
            Pair.of(4, PreyAi.createIdleMovementBehaviors())
        ));
    }

    public static void initRetreatActivity(Brain<? extends Pest> brain)
    {
        PreyAi.initRetreatActivity(brain);
    }

    public static void setSmelledPos(Pest pest, BlockPos pos)
    {
        pest.getBrain().setMemory(TFCBrain.SMELLY_POS.get(), GlobalPos.of(pest.level.dimension(), pos));
    }
}
