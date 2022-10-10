/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common;

import net.minecraft.world.damagesource.DamageSource;

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
}
