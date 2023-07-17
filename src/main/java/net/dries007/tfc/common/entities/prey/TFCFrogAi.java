/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities.prey;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.behavior.Croak;
import net.minecraft.world.entity.ai.behavior.FollowTemptation;
import net.minecraft.world.entity.ai.behavior.RandomStroll;
import net.minecraft.world.entity.ai.behavior.RunOne;
import net.minecraft.world.entity.ai.behavior.SetWalkTargetFromLookTarget;
import net.minecraft.world.entity.ai.behavior.StartAttacking;
import net.minecraft.world.entity.ai.behavior.TryFindLand;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.animal.frog.Frog;
import net.minecraft.world.entity.schedule.Activity;

import net.dries007.tfc.common.entities.ai.SetLookTarget;
import net.dries007.tfc.common.entities.ai.TFCBrain;
import net.dries007.tfc.common.entities.ai.livestock.BreedBehavior;

public class TFCFrogAi
{
    protected static final ImmutableList<SensorType<? extends Sensor<? super Frog>>> SENSOR_TYPES = ImmutableList.of(SensorType.NEAREST_LIVING_ENTITIES, SensorType.HURT_BY, SensorType.FROG_ATTACKABLES, TFCBrain.TEMPTATION_SENSOR.get(), SensorType.IS_IN_WATER);

    @SuppressWarnings("unchecked")
    public static Brain<?> makeBrain(Brain<? extends Frog> brain)
    {
        initIdleActivity((Brain<TFCFrog>) brain);
        return brain;
    }

    public static void initIdleActivity(Brain<TFCFrog> brain)
    {
        brain.addActivityWithConditions(Activity.IDLE, ImmutableList.of(
            Pair.of(0, SetLookTarget.create(EntityType.PLAYER, 6.0F, UniformInt.of(30, 60))),
            Pair.of(0, new BreedBehavior<>(1.0F)),
            Pair.of(1, new FollowTemptation(e -> 1.25F)),
            Pair.of(2, StartAttacking.create(TFCFrogAi::canAttack, frog -> frog.getBrain().getMemory(MemoryModuleType.NEAREST_ATTACKABLE))),
            Pair.of(3, TryFindLand.create(6, 1.0F)),
            Pair.of(4, new RunOne<>(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT), ImmutableList.of(Pair.of(RandomStroll.stroll(1.0F), 1),
                Pair.of(SetWalkTargetFromLookTarget.create(1.0F, 3), 1), Pair.of(new Croak(), 3),
                Pair.of(BehaviorBuilder.triggerIf(Entity::onGround), 2)))
            )), ImmutableSet.of(Pair.of(MemoryModuleType.LONG_JUMP_MID_JUMP, MemoryStatus.VALUE_ABSENT),
            Pair.of(MemoryModuleType.IS_IN_WATER, MemoryStatus.VALUE_ABSENT)));
    }

    private static boolean canAttack(Frog frog)
    {
        return !BehaviorUtils.isBreeding(frog);
    }

}
