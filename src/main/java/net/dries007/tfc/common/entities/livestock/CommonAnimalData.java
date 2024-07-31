/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities.livestock;

import java.lang.reflect.Field;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.ClassTreeIdRegistry;

import net.dries007.tfc.util.Helpers;

/**
 * A collection of properties that are common to (generally) all TFC entities. These are synced, and defined in each class by default (as per
 * how {@link SynchedEntityData} is required to function). Since we cannot perfectly insert into the vanilla class hierarchy, we have to duplicate
 * these across multiple different entities.
 */
public record CommonAnimalData(
    EntityDataAccessor<Boolean> gender,
    EntityDataAccessor<Byte> lastAge,
    EntityDataAccessor<Byte> geneticSize,
    EntityDataAccessor<Integer> uses,
    EntityDataAccessor<Boolean> fertilized,
    EntityDataAccessor<Float> familiarity,
    EntityDataAccessor<Long> lastFamiliarityTick,
    EntityDataAccessor<Long> birthTick,
    EntityDataAccessor<Long> oldTick,
    EntityDataAccessor<Long> lastFedTick,
    EntityDataAccessor<Long> lastMateTick
)
{
    public static CommonAnimalData create(Class<?> clazz)
    {
        return new CommonAnimalData(
            defineId(clazz, EntityDataSerializers.BOOLEAN),
            defineId(clazz, EntityDataSerializers.BYTE),
            defineId(clazz, EntityDataSerializers.BYTE),
            defineId(clazz, EntityDataSerializers.INT),
            defineId(clazz, EntityDataSerializers.BOOLEAN),
            defineId(clazz, EntityDataSerializers.FLOAT),
            defineId(clazz, EntityDataSerializers.LONG),
            defineId(clazz, EntityDataSerializers.LONG),
            defineId(clazz, EntityDataSerializers.LONG),
            defineId(clazz, EntityDataSerializers.LONG),
            defineId(clazz, EntityDataSerializers.LONG)
        );
    }

    /**
     * Since we batch things into helper methods (due to the aforementioned class tree hierarchy problem which vanilla doesn't have),
     * we just access the id registry directly, rather than going through {@link SynchedEntityData#defineId}, because of stack trace checks
     */
    private static final ClassTreeIdRegistry ID_REGISTRY = Helpers.uncheck(() -> {
        final Field field = SynchedEntityData.class.getDeclaredField("ID_REGISTRY");
        field.setAccessible(true);
        return field.get(null);
    });

    /**
     * @see SynchedEntityData#defineId
     */
    private static <T> EntityDataAccessor<T> defineId(Class<?> clazz, EntityDataSerializer<T> serializer)
    {
        final int id = ID_REGISTRY.define(clazz);
        if (id > 254) throw new IllegalArgumentException("Data value id is too big with " + id + "! (Max is 254)");
        return serializer.createAccessor(id);
    }

    public void define(SynchedEntityData.Builder builder)
    {
        builder.define(gender, true)
            .define(lastAge, (byte) Age.CHILD.ordinal())
            .define(geneticSize, (byte) 16)
            .define(uses, 0)
            .define(fertilized, false)
            .define(familiarity, 0f)
            .define(lastFamiliarityTick, Long.MIN_VALUE)
            .define(birthTick, 0L)
            .define(oldTick, -1L)
            .define(lastFedTick, Long.MIN_VALUE)
            .define(lastMateTick, Long.MIN_VALUE);
    }
}
