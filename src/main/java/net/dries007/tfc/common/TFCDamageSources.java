/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;

import net.dries007.tfc.TerraFirmaCraft;

public class TFCDamageSources
{
    public static final DamageSource GRILL = create("grill").bypassArmor().setIsFire();
    public static final DamageSource POT = create("pot").bypassArmor().setIsFire();
    public static final DamageSource DEHYDRATION = create("dehydration").bypassArmor().bypassMagic();
    public static final DamageSource CORAL = create("coral");
    public static final DamageSource PLUCK = create("pluck");

    private static DamageSource create(String key)
    {
        return new DamageSource(TerraFirmaCraft.MOD_ID + "." + key);
    }

    public static void hotFloor(Entity entity, float amount)
    {
        entity.hurt(DamageSource.HOT_FLOOR, amount);
    }

    public static void pluck(Entity entity, float amount)
    {
        entity.hurt(PLUCK, amount);
    }

    public static void cactus(Entity entity, float amount)
    {
        entity.hurt(DamageSource.CACTUS, amount);
    }

    public static void coral(Entity entity, float amount)
    {
        entity.hurt(CORAL, amount);
    }

    public static void berryBush(Entity entity, float amount)
    {
        entity.hurt(DamageSource.SWEET_BERRY_BUSH, amount);
    }

    public static void dehydration(Entity entity, float amount)
    {
        entity.hurt(DEHYDRATION, amount);
    }
}
