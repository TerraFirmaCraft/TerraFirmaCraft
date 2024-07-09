/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities.ai;

import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.entity.schedule.Schedule;
import net.minecraft.world.entity.schedule.ScheduleBuilder;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.entities.ai.livestock.DelegatingTemptingSensor;
import net.dries007.tfc.common.entities.ai.livestock.NearestNestBoxSensor;
import net.dries007.tfc.common.entities.ai.predator.PackLeaderSensor;
import net.dries007.tfc.common.entities.ai.predator.PackPredator;

public class TFCBrain
{
    public static final DeferredRegister<Activity> ACTIVITIES = DeferredRegister.create(Registries.ACTIVITY, TerraFirmaCraft.MOD_ID);
    public static final DeferredRegister<MemoryModuleType<?>> MEMORY_TYPES = DeferredRegister.create(Registries.MEMORY_MODULE_TYPE, TerraFirmaCraft.MOD_ID);
    public static final DeferredRegister<Schedule> SCHEDULES = DeferredRegister.create(Registries.SCHEDULE, TerraFirmaCraft.MOD_ID);
    public static final DeferredRegister<SensorType<?>> SENSOR_TYPES = DeferredRegister.create(Registries.SENSOR_TYPE, TerraFirmaCraft.MOD_ID);
    public static final DeferredRegister<PoiType> POI_TYPES = DeferredRegister.create(Registries.POINT_OF_INTEREST_TYPE, TerraFirmaCraft.MOD_ID);

    public static final DeferredHolder<Activity, Activity> HUNT = registerActivity("hunt");
    public static final DeferredHolder<Activity, Activity> IDLE_AT_HOME = registerActivity("idle_at_home");
    public static final DeferredHolder<Activity, Activity> FOLLOW = registerActivity("follow");
    public static final DeferredHolder<Activity, Activity> SIT = registerActivity("sit");

    public static final DeferredHolder<MemoryModuleType<?>, MemoryModuleType<BlockPos>> NEST_BOX_MEMORY = registerMemory("nest");
    public static final DeferredHolder<MemoryModuleType<?>, MemoryModuleType<GlobalPos>> SLEEP_POS = registerMemory("sleep_pos", GlobalPos.CODEC);
    public static final DeferredHolder<MemoryModuleType<?>, MemoryModuleType<GlobalPos>> SMELLY_POS = registerMemory("smelly_pos", GlobalPos.CODEC);
    public static final DeferredHolder<MemoryModuleType<?>, MemoryModuleType<Long>> SIT_TIME = registerMemory("sit_time", Codec.LONG);
    public static final DeferredHolder<MemoryModuleType<?>, MemoryModuleType<PackPredator>> ALPHA = registerMemory("alpha");
    public static final DeferredHolder<MemoryModuleType<?>, MemoryModuleType<Integer>> WAKEUP_TICKS = registerMemory("wakeup_ticks", Codec.INT);

    public static final DeferredHolder<Schedule, Schedule> DIURNAL = registerSchedule("diurnal");
    public static final DeferredHolder<Schedule, Schedule> NOCTURNAL = registerSchedule("nocturnal");

    public static final DeferredHolder<SensorType<?>, SensorType<DelegatingTemptingSensor>> TEMPTATION_SENSOR = registerSensorType("tempt", DelegatingTemptingSensor::new);
    public static final DeferredHolder<SensorType<?>, SensorType<NearestNestBoxSensor>> NEST_BOX_SENSOR = registerSensorType("nearest_nest_box", NearestNestBoxSensor::new);
    public static final DeferredHolder<SensorType<?>, SensorType<PackLeaderSensor>> PACK_LEADER_SENSOR = registerSensorType("pack_leader", PackLeaderSensor::new);

    public static final DeferredHolder<PoiType, PoiType> NEST_BOX_POI = registerPoi("nest_box", () -> new PoiType(getBlockStates(TFCBlocks.NEST_BOX.get()), 1, 1));

    private static Set<BlockState> getBlockStates(Block block)
    {
        return ImmutableSet.copyOf(block.getStateDefinition().getPossibleStates());
    }

    public static DeferredHolder<PoiType, PoiType> registerPoi(String name, Supplier<PoiType> supplier)
    {
        return POI_TYPES.register(name, supplier);
    }

    public static <T extends Sensor<?>> DeferredHolder<SensorType<?>, SensorType<T>> registerSensorType(String name, Supplier<T> supplier)
    {
        return SENSOR_TYPES.register(name, () -> new SensorType<>(supplier));
    }

    public static DeferredHolder<Activity, Activity> registerActivity(String name)
    {
        return ACTIVITIES.register(name, () -> new Activity(name));
    }

    public static <T> DeferredHolder<MemoryModuleType<?>, MemoryModuleType<T>> registerMemory(String name)
    {
        return MEMORY_TYPES.register(name, () -> new MemoryModuleType<>(Optional.empty()));
    }

    public static <T> DeferredHolder<MemoryModuleType<?>, MemoryModuleType<T>> registerMemory(String name, Codec<T> codec)
    {
        return MEMORY_TYPES.register(name, () -> new MemoryModuleType<>(Optional.of(codec)));
    }

    public static DeferredHolder<Schedule, Schedule> registerSchedule(String name)
    {
        return SCHEDULES.register(name, Schedule::new);
    }

    public static void initializeScheduleContents()
    {
        new ScheduleBuilder(DIURNAL.get()).changeActivityAt(0, HUNT.get()).changeActivityAt(11000, Activity.REST).build();
        new ScheduleBuilder(NOCTURNAL.get()).changeActivityAt(0, Activity.REST).changeActivityAt(11000, HUNT.get()).build();
    }
}
