package net.dries007.tfc.util;

import net.minecraft.util.DamageSource;

public class TFCDamageSources
{
    public static final DamageSource GRILL = (new DamageSource("grill")).bypassArmor().setIsFire();
    public static final DamageSource POT = (new DamageSource("pot")).bypassArmor().setIsFire();
}
