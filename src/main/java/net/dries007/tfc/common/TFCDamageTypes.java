/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.util.Helpers;

public class TFCDamageTypes
{
    public static final ResourceKey<DamageType> GRILL = ResourceKey.create(Registries.DAMAGE_TYPE, Helpers.identifier("grill"));
    public static final ResourceKey<DamageType> POT = ResourceKey.create(Registries.DAMAGE_TYPE, Helpers.identifier("pot"));
    public static final ResourceKey<DamageType> DEHYDRATION = ResourceKey.create(Registries.DAMAGE_TYPE, Helpers.identifier("dehydration"));
    public static final ResourceKey<DamageType> CORAL = ResourceKey.create(Registries.DAMAGE_TYPE, Helpers.identifier("coral"));
    public static final ResourceKey<DamageType> PLUCK = ResourceKey.create(Registries.DAMAGE_TYPE, Helpers.identifier("pluck"));

    public static void grill(Entity entity, float amount)
    {
        entity.hurt(new DamageSource(fetch(GRILL, entity.level())), amount);
    }

    public static void pot(Entity entity, float amount)
    {
        entity.hurt(new DamageSource(fetch(POT, entity.level())), amount);
    }

    public static void coral(Entity entity, float amount)
    {
        entity.hurt(new DamageSource(fetch(CORAL, entity.level())), amount);
    }

    public static void pluck(Entity entity, float amount, @Nullable Entity plucker)
    {
        entity.hurt(new DamageSource(fetch(PLUCK, entity.level()), plucker), amount);
    }

    public static void dehydration(Entity entity, float amount)
    {
        entity.hurt(new DamageSource(fetch(DEHYDRATION, entity.level())), amount);
    }

    private static Holder<DamageType> fetch(ResourceKey<DamageType> type, Level level)
    {
        return level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(type);
    }

}
