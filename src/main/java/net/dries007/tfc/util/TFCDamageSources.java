/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util;

import net.minecraft.util.DamageSource;

public class TFCDamageSources
{
    public static final DamageSource GRILL = (new DamageSource("grill")).bypassArmor().setIsFire();
    public static final DamageSource POT = (new DamageSource("pot")).bypassArmor().setIsFire();
}
