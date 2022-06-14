/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities.ai;

import java.util.Optional;
import java.util.function.Supplier;

import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.entity.schedule.Schedule;
import net.minecraft.world.entity.schedule.ScheduleBuilder;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import com.mojang.serialization.Codec;
import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.common.entities.ai.livestock.DelegatingTemptingSensor;

public class TFCBrain
{
    public static final DeferredRegister<Activity> ACTIVITIES = DeferredRegister.create(ForgeRegistries.ACTIVITIES, TerraFirmaCraft.MOD_ID);
    public static final DeferredRegister<MemoryModuleType<?>> MEMORY_TYPES = DeferredRegister.create(ForgeRegistries.MEMORY_MODULE_TYPES, TerraFirmaCraft.MOD_ID);
    public static final DeferredRegister<Schedule> SCHEDULES = DeferredRegister.create(ForgeRegistries.SCHEDULES, TerraFirmaCraft.MOD_ID);
    public static final DeferredRegister<SensorType<?>> SENSOR_TYPES = DeferredRegister.create(ForgeRegistries.SENSOR_TYPES, TerraFirmaCraft.MOD_ID);

    public static final RegistryObject<Activity> HUNT = registerActivity("hunt");

    public static final RegistryObject<Schedule> DIURNAL = registerSchedule("diurnal", () -> newSchedule().changeActivityAt(0, HUNT.get()).changeActivityAt(11000, Activity.REST).build());
    public static final RegistryObject<Schedule> NOCTURNAL = registerSchedule("nocturnal", () -> newSchedule().changeActivityAt(0, Activity.REST).changeActivityAt(11000, HUNT.get()).build());

    public static final RegistryObject<SensorType<DelegatingTemptingSensor>> TEMPTATION = registerSensorType("tempt", DelegatingTemptingSensor::new);

    public static <T extends Sensor<?>> RegistryObject<SensorType<T>> registerSensorType(String name, Supplier<T> supplier)
    {
        return SENSOR_TYPES.register(name, () -> new SensorType<>(supplier));
    }

    public static RegistryObject<Activity> registerActivity(String name)
    {
        return ACTIVITIES.register(name, () -> new Activity(name));
    }

    public static <T> RegistryObject<MemoryModuleType<T>> registerMemory(String name)
    {
        return MEMORY_TYPES.register(name, () -> new MemoryModuleType<>(Optional.empty()));
    }

    public static <T> RegistryObject<MemoryModuleType<T>> registerMemory(String name, Codec<T> codec)
    {
        return MEMORY_TYPES.register(name, () -> new MemoryModuleType<>(Optional.of(codec)));
    }

    public static ScheduleBuilder newSchedule()
    {
        return new ScheduleBuilder(new Schedule());
    }

    public static RegistryObject<Schedule> registerSchedule(String name, Supplier<Schedule> supplier)
    {
        return SCHEDULES.register(name, supplier);
    }

    public static void registerAll(IEventBus bus)
    {
        ACTIVITIES.register(bus);
        MEMORY_TYPES.register(bus);
        SCHEDULES.register(bus);
        SENSOR_TYPES.register(bus);
    }
}
