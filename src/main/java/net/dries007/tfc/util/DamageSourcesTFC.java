/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.util;

import net.minecraft.util.DamageSource;

public class DamageSourcesTFC
{
    public static final DamageSource PLUCKING = new DamageSource("plucking").setDamageBypassesArmor();
    public static final DamageSource FOOD_POISON = new DamageSource("food_poison").setDamageBypassesArmor().setDamageIsAbsolute();
    public static final DamageSource DEHYDRATION = (new DamageSource("dehydration")).setDamageBypassesArmor().setDamageIsAbsolute();
    public static final DamageSource GRILL = (new DamageSource("grill")).setDamageBypassesArmor().setFireDamage();
    public static final DamageSource SOUP = (new DamageSource("grill")).setDamageBypassesArmor().setFireDamage();
}
